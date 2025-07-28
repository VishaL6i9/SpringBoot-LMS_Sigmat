package com.sigmat.lms.services;

import com.sigmat.lms.dtos.CoursePurchaseDTO;
import com.sigmat.lms.exceptions.ResourceNotFoundException;
import com.sigmat.lms.models.*;
import com.sigmat.lms.repository.CoursePurchaseRepository;
import com.sigmat.lms.repository.CourseRepo;
import com.sigmat.lms.repository.EnrollmentRepo;
import com.sigmat.lms.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoursePurchaseService {

    private final CoursePurchaseRepository coursePurchaseRepository;
    private final CourseRepo courseRepository;
    private final UserRepo userRepository;
    private final EnrollmentRepo enrollmentRepository;

    @Transactional
    public CoursePurchaseDTO createPurchase(Long userId, Long courseId, BigDecimal discountApplied) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        // Check if user already purchased this course
        if (coursePurchaseRepository.existsByUserAndCourseAndStatus(user, course, PurchaseStatus.COMPLETED)) {
            throw new IllegalStateException("User has already purchased this course");
        }

        // Calculate final amount
        BigDecimal coursePrice = BigDecimal.valueOf(course.getCourseFee());
        BigDecimal discount = discountApplied != null ? discountApplied : BigDecimal.ZERO;
        BigDecimal finalAmount = coursePrice.subtract(discount);

        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }

        CoursePurchase purchase = CoursePurchase.builder()
                .user(user)
                .course(course)
                .purchasePrice(coursePrice)
                .discountApplied(discount)
                .finalAmount(finalAmount)
                .status(PurchaseStatus.PENDING)
                .build();

        CoursePurchase savedPurchase = coursePurchaseRepository.save(purchase);
        log.info("Created course purchase for user {} and course {}", userId, courseId);
        
        return convertToDTO(savedPurchase);
    }

    @Transactional
    public CoursePurchaseDTO completePurchase(String stripeSessionId, String paymentReference) {
        CoursePurchase purchase = coursePurchaseRepository.findByStripeSessionId(stripeSessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found for session: " + stripeSessionId));

        purchase.setStatus(PurchaseStatus.COMPLETED);
        purchase.setPaymentReference(paymentReference);
        purchase.setAccessGrantedDate(LocalDateTime.now());

        CoursePurchase savedPurchase = coursePurchaseRepository.save(purchase);

        // Auto-enroll user in the course
        enrollUserInCourse(purchase.getUser(), purchase.getCourse());

        log.info("Completed course purchase for user {} and course {}", 
                purchase.getUser().getId(), purchase.getCourse().getCourseId());
        
        return convertToDTO(savedPurchase);
    }

    @Transactional
    public void updatePurchaseSessionId(Long purchaseId, String stripeSessionId) {
        CoursePurchase purchase = coursePurchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found with id: " + purchaseId));

        purchase.setStripeSessionId(stripeSessionId);
        coursePurchaseRepository.save(purchase);
    }

    public List<CoursePurchaseDTO> getUserPurchases(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return coursePurchaseRepository.findByUserOrderByPurchaseDateDesc(user)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CoursePurchaseDTO> getCoursePurchases(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        return coursePurchaseRepository.findByCourseOrderByPurchaseDateDesc(course)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public boolean hasUserPurchasedCourse(Long userId, Long courseId) {
        Users user = userRepository.findById(userId).orElse(null);
        Course course = courseRepository.findById(courseId).orElse(null);
        
        if (user == null || course == null) {
            return false;
        }

        return coursePurchaseRepository.existsByUserAndCourseAndStatus(user, course, PurchaseStatus.COMPLETED);
    }

    public Optional<CoursePurchaseDTO> getUserCoursePurchase(Long userId, Long courseId) {
        Users user = userRepository.findById(userId).orElse(null);
        Course course = courseRepository.findById(courseId).orElse(null);
        
        if (user == null || course == null) {
            return Optional.empty();
        }

        return coursePurchaseRepository.findByUserAndCourse(user, course)
                .map(this::convertToDTO);
    }

    public Long getCourseRevenue(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        return coursePurchaseRepository.findCompletedPurchasesByCourse(course)
                .stream()
                .map(CoursePurchase::getFinalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .longValue();
    }

    public Long getCourseEnrollmentCount(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        return coursePurchaseRepository.countCompletedPurchasesByCourse(course);
    }

    @Transactional
    public void failPurchase(String stripeSessionId, String reason) {
        Optional<CoursePurchase> purchaseOpt = coursePurchaseRepository.findByStripeSessionId(stripeSessionId);
        
        if (purchaseOpt.isPresent()) {
            CoursePurchase purchase = purchaseOpt.get();
            purchase.setStatus(PurchaseStatus.FAILED);
            coursePurchaseRepository.save(purchase);
            
            log.warn("Failed course purchase for session {}: {}", stripeSessionId, reason);
        }
    }

    private void enrollUserInCourse(Users user, Course course) {
        // Check if user is already enrolled
        boolean alreadyEnrolled = enrollmentRepository.findByUserAndCourse(user, course).isPresent();
        
        if (!alreadyEnrolled) {
            Enrollment enrollment = Enrollment.builder()
                    .user(user)
                    .course(course)
                    .enrollmentDate(LocalDate.now())
                    .build();
            
            enrollmentRepository.save(enrollment);
            log.info("Auto-enrolled user {} in course {} after purchase", user.getId(), course.getCourseId());
        }
    }

    private CoursePurchaseDTO convertToDTO(CoursePurchase purchase) {
        return CoursePurchaseDTO.builder()
                .id(purchase.getId())
                .userId(purchase.getUser().getId())
                .username(purchase.getUser().getUsername())
                .courseId(purchase.getCourse().getCourseId())
                .courseName(purchase.getCourse().getCourseName())
                .courseCode(purchase.getCourse().getCourseCode())
                .purchasePrice(purchase.getPurchasePrice())
                .discountApplied(purchase.getDiscountApplied())
                .finalAmount(purchase.getFinalAmount())
                .status(purchase.getStatus())
                .paymentReference(purchase.getPaymentReference())
                .stripeSessionId(purchase.getStripeSessionId())
                .purchaseDate(purchase.getPurchaseDate())
                .accessGrantedDate(purchase.getAccessGrantedDate())
                .createdAt(purchase.getCreatedAt())
                .updatedAt(purchase.getUpdatedAt())
                .build();
    }
}