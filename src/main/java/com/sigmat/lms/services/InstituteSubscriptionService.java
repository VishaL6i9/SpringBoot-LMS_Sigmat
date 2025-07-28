package com.sigmat.lms.services;

import com.sigmat.lms.dtos.CourseSubscriptionRequestDTO;
import com.sigmat.lms.dtos.InstituteSubscriptionDTO;
import com.sigmat.lms.exceptions.ResourceNotFoundException;
import com.sigmat.lms.models.Course;
import com.sigmat.lms.models.Institute;
import com.sigmat.lms.models.InstituteSubscription;
import com.sigmat.lms.repository.InstituteSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstituteSubscriptionService {
    
    private final InstituteSubscriptionRepository instituteSubscriptionRepository;
    private final CourseAccessControlService courseAccessControlService;

    @Transactional
    public InstituteSubscriptionDTO subscribeToGlobalCourse(CourseSubscriptionRequestDTO request) {
        InstituteSubscription subscription = courseAccessControlService.subscribeInstituteToGlobalCourse(
                request.getInstituteId(),
                request.getCourseId(),
                request.isAutoEnrollStudents(),
                request.isAutoEnrollInstructors()
        );
        
        return convertToDTO(subscription);
    }

    @Transactional
    public void unsubscribeFromGlobalCourse(Long instituteId, Long courseId) {
        courseAccessControlService.unsubscribeInstituteFromGlobalCourse(instituteId, courseId);
    }

    public List<InstituteSubscriptionDTO> getInstituteSubscriptions(Long instituteId) {
        return instituteSubscriptionRepository.findActiveByInstituteId(instituteId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<InstituteSubscriptionDTO> getCourseSubscriptions(Long courseId) {
        return instituteSubscriptionRepository.findActiveByCourseId(courseId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public InstituteSubscriptionDTO getSubscription(Long subscriptionId) {
        InstituteSubscription subscription = instituteSubscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + subscriptionId));
        
        return convertToDTO(subscription);
    }

    @Transactional
    public InstituteSubscriptionDTO updateSubscription(Long subscriptionId, InstituteSubscriptionDTO dto) {
        InstituteSubscription subscription = instituteSubscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + subscriptionId));
        
        subscription.setAutoEnrollStudents(dto.isAutoEnrollStudents());
        subscription.setAutoEnrollInstructors(dto.isAutoEnrollInstructors());
        subscription.setExpiryDate(dto.getExpiryDate());
        
        InstituteSubscription updatedSubscription = instituteSubscriptionRepository.save(subscription);
        return convertToDTO(updatedSubscription);
    }

    @Transactional
    public void deactivateExpiredSubscriptions() {
        courseAccessControlService.deactivateExpiredSubscriptions();
    }

    private InstituteSubscriptionDTO convertToDTO(InstituteSubscription subscription) {
        Institute institute = subscription.getInstitute();
        Course course = subscription.getCourse();
        
        return InstituteSubscriptionDTO.builder()
                .id(subscription.getId())
                .instituteId(institute.getInstituteId())
                .instituteName(institute.getInstituteName())
                .courseId(course.getCourseId())
                .courseName(course.getCourseName())
                .courseCode(course.getCourseCode())
                .subscriptionPrice(subscription.getSubscriptionPrice())
                .discountApplied(subscription.getDiscountApplied())
                .finalAmount(subscription.getFinalAmount())
                .subscriptionDate(subscription.getSubscriptionDate())
                .expiryDate(subscription.getExpiryDate())
                .isActive(subscription.isActive())
                .autoEnrollStudents(subscription.isAutoEnrollStudents())
                .autoEnrollInstructors(subscription.isAutoEnrollInstructors())
                .createdAt(subscription.getCreatedAt())
                .updatedAt(subscription.getUpdatedAt())
                .build();
    }
}