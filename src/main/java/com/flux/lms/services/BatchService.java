package com.flux.lms.services;

import com.flux.lms.dtos.BatchRegistrationDTO;
import com.flux.lms.dtos.InstitutionalUserDTO;
import com.flux.lms.exceptions.ResourceNotFoundException;
import com.flux.lms.models.*;
import com.flux.lms.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final BatchRepository batchRepository;
    private final InstituteRepository instituteRepository;
    private final InstructorRepo instructorRepository;
    private final CourseRepo courseRepository;
    private final UserRepo userRepository;

    @Transactional
    public BatchRegistrationDTO createBatch(BatchRegistrationDTO batchDTO) {
        // Validate institute
        Institute institute = instituteRepository.findById(batchDTO.getInstituteId())
                .orElseThrow(() -> new ResourceNotFoundException("Institute not found"));

        // Validate instructor if provided
        Instructor instructor = null;
        if (batchDTO.getInstructorId() != null) {
            instructor = instructorRepository.findById(batchDTO.getInstructorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));
        }

        // Validate course if provided
        Course course = null;
        if (batchDTO.getCourseId() != null) {
            course = courseRepository.findById(batchDTO.getCourseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        }

        Batch batch = Batch.builder()
                .batchName(batchDTO.getBatchName())
                .batchCode(batchDTO.getBatchCode())
                .description(batchDTO.getDescription())
                .institute(institute)
                .instructor(instructor)
                .course(course)
                .startDate(batchDTO.getStartDate())
                .endDate(batchDTO.getEndDate())
                .maxStudents(batchDTO.getMaxStudents())
                .semester(batchDTO.getSemester())
                .academicYear(batchDTO.getAcademicYear())
                .status(Batch.BatchStatus.valueOf(batchDTO.getStatus() != null ? batchDTO.getStatus() : "PLANNED"))
                .isActive(true)
                .build();

        Batch savedBatch = batchRepository.save(batch);
        return convertToDTO(savedBatch);
    }

    public List<BatchRegistrationDTO> getBatchesByInstitute(Long instituteId) {
        Institute institute = instituteRepository.findById(instituteId)
                .orElseThrow(() -> new ResourceNotFoundException("Institute not found"));

        return batchRepository.findByInstituteAndIsActiveTrue(institute).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<BatchRegistrationDTO> getBatchesByInstructor(Long instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));

        return batchRepository.findByInstructorAndIsActiveTrue(instructor).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public BatchRegistrationDTO getBatchById(Long batchId) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));
        return convertToDTO(batch);
    }

    public BatchRegistrationDTO getBatchByCode(String batchCode) {
        Batch batch = batchRepository.findByBatchCode(batchCode)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with code: " + batchCode));
        return convertToDTO(batch);
    }

    @Transactional
    public BatchRegistrationDTO updateBatch(Long batchId, BatchRegistrationDTO batchDTO) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));

        batch.setBatchName(batchDTO.getBatchName());
        batch.setDescription(batchDTO.getDescription());
        batch.setStartDate(batchDTO.getStartDate());
        batch.setEndDate(batchDTO.getEndDate());
        batch.setMaxStudents(batchDTO.getMaxStudents());
        batch.setSemester(batchDTO.getSemester());
        batch.setAcademicYear(batchDTO.getAcademicYear());

        if (batchDTO.getInstructorId() != null) {
            Instructor instructor = instructorRepository.findById(batchDTO.getInstructorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));
            batch.setInstructor(instructor);
        }

        if (batchDTO.getCourseId() != null) {
            Course course = courseRepository.findById(batchDTO.getCourseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
            batch.setCourse(course);
        }

        if (batchDTO.getStatus() != null) {
            batch.setStatus(Batch.BatchStatus.valueOf(batchDTO.getStatus()));
        }

        Batch updatedBatch = batchRepository.save(batch);
        return convertToDTO(updatedBatch);
    }

    @Transactional
    public void assignStudentToBatch(Long batchId, Long studentId) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));

        Users student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        // Check if batch has available slots
        Integer currentStudents = batchRepository.countStudentsByBatchId(batchId);
        if (batch.getMaxStudents() != null && currentStudents >= batch.getMaxStudents()) {
            throw new RuntimeException("Batch is full. Cannot assign more students.");
        }

        // Check if student belongs to the same institute
        if (!student.getInstitute().getInstituteId().equals(batch.getInstitute().getInstituteId())) {
            throw new RuntimeException("Student must belong to the same institute as the batch.");
        }

        student.setBatch(batch);
        userRepository.save(student);
    }

    @Transactional
    public void removeStudentFromBatch(Long studentId) {
        Users student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        student.setBatch(null);
        userRepository.save(student);
    }

    @Transactional
    public void assignInstructorToBatch(Long batchId, Long instructorId) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));

        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found"));

        batch.setInstructor(instructor);
        batchRepository.save(batch);
    }

    @Transactional
    public void updateBatchStatus(Long batchId, String status) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));

        batch.setStatus(Batch.BatchStatus.valueOf(status.toUpperCase()));
        batchRepository.save(batch);
    }

    @Transactional
    public void deactivateBatch(Long batchId) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));

        batch.setActive(false);
        batchRepository.save(batch);
    }

    public List<InstitutionalUserDTO> getStudentsInBatch(Long batchId) {
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found"));

        return batch.getStudents().stream()
                .map(this::convertUserToInstitutionalDTO)
                .collect(Collectors.toList());
    }

    public List<BatchRegistrationDTO> getAvailableBatches() {
        return batchRepository.findBatchesWithAvailableSlots().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private BatchRegistrationDTO convertToDTO(Batch batch) {
        BatchRegistrationDTO.BatchRegistrationDTOBuilder builder = BatchRegistrationDTO.builder()
                .batchId(batch.getBatchId())
                .batchName(batch.getBatchName())
                .batchCode(batch.getBatchCode())
                .description(batch.getDescription())
                .instituteId(batch.getInstitute().getInstituteId())
                .instituteName(batch.getInstitute().getInstituteName())
                .startDate(batch.getStartDate())
                .endDate(batch.getEndDate())
                .maxStudents(batch.getMaxStudents())
                .semester(batch.getSemester())
                .academicYear(batch.getAcademicYear())
                .status(batch.getStatus().name())
                .isActive(batch.isActive())
                .createdAt(batch.getCreatedAt())
                .updatedAt(batch.getUpdatedAt());

        if (batch.getInstructor() != null) {
            builder.instructorId(batch.getInstructor().getInstructorId())
                   .instructorName(batch.getInstructor().getFirstName() + " " + batch.getInstructor().getLastName())
                   .instructorEmail(batch.getInstructor().getEmail());
        }

        if (batch.getCourse() != null) {
            builder.courseId(batch.getCourse().getCourseId())
                   .courseName(batch.getCourse().getCourseName());
        }

        // Count current students
        Integer currentStudents = batchRepository.countStudentsByBatchId(batch.getBatchId());
        builder.currentStudents(currentStudents);

        return builder.build();
    }

    private InstitutionalUserDTO convertUserToInstitutionalDTO(Users user) {
        return InstitutionalUserDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(user.getRoles())
                .rollNumber(user.getRollNumber())
                .admissionId(user.getAdmissionId())
                .staffId(user.getStaffId())
                .employeeId(user.getEmployeeId())
                .department(user.getDepartment())
                .jobRole(user.getJobRole())
                .phoneNumber(user.getPhoneNumber())
                .parentContact(user.getParentContact())
                .emergencyContact(user.getEmergencyContact())
                .instituteId(user.getInstitute() != null ? user.getInstitute().getInstituteId() : null)
                .instituteName(user.getInstitute() != null ? user.getInstitute().getInstituteName() : null)
                .isActive(true) // Assuming active if in batch
                .isVerified(user.isVerified())
                .enrollmentDate(user.getEnrollmentDate())
                .lastLoginDate(user.getLastLoginDate())
                .batch(user.getBatchName())
                .semester(user.getSemester())
                .grade(user.getGrade())
                .division(user.getDivision())
                .courseOfStudy(user.getCourseOfStudy())
                .build();
    }
}