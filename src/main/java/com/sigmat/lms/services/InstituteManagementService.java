package com.sigmat.lms.services;

import com.sigmat.lms.dtos.CourseDTO;
import com.sigmat.lms.dtos.InstructorDTO;
import com.sigmat.lms.dtos.UserDTO;
import com.sigmat.lms.exceptions.ResourceNotFoundException;
import com.sigmat.lms.models.*;
import com.sigmat.lms.repository.CourseRepo;
import com.sigmat.lms.repository.InstituteRepository;
import com.sigmat.lms.repository.InstructorRepo;
import com.sigmat.lms.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstituteManagementService {
    
    private final InstituteRepository instituteRepository;
    private final UserRepo userRepository;
    private final InstructorRepo instructorRepository;
    private final CourseRepo courseRepository;

    // Student Management
    @Transactional
    public void addStudentToInstitute(Long instituteId, Long studentId) {
        Institute institute = getInstituteById(instituteId);
        Users student = getUserById(studentId);
        
        student.setInstitute(institute);
        userRepository.save(student);
    }

    @Transactional
    public void removeStudentFromInstitute(Long studentId) {
        Users student = getUserById(studentId);
        student.setInstitute(null);
        userRepository.save(student);
    }

    public List<UserDTO> getInstituteStudents(Long instituteId) {
        Institute institute = getInstituteById(instituteId);
        return userRepository.findByInstituteAndRole(institute, Role.USER).stream()
                .map(this::convertUserToDTO)
                .collect(Collectors.toList());
    }

    // Instructor Management
    @Transactional
    public void addInstructorToInstitute(Long instituteId, Long instructorId) {
        Institute institute = getInstituteById(instituteId);
        Instructor instructor = getInstructorById(instructorId);
        
        instructor.setInstitute(institute);
        instructorRepository.save(instructor);
    }

    @Transactional
    public void removeInstructorFromInstitute(Long instructorId) {
        Instructor instructor = getInstructorById(instructorId);
        instructor.setInstitute(null);
        instructorRepository.save(instructor);
    }

    public List<InstructorDTO> getInstituteInstructors(Long instituteId) {
        return instructorRepository.findByInstituteId(instituteId).stream()
                .map(this::convertInstructorToDTO)
                .collect(Collectors.toList());
    }

    // Course Management
    @Transactional
    public void addCourseToInstitute(Long instituteId, Long courseId) {
        Institute institute = getInstituteById(instituteId);
        Course course = getCourseById(courseId);
        
        course.setInstitute(institute);
        courseRepository.save(course);
    }

    @Transactional
    public void removeCourseFromInstitute(Long courseId) {
        Course course = getCourseById(courseId);
        course.setInstitute(null);
        courseRepository.save(course);
    }

    public List<CourseDTO> getInstituteCourses(Long instituteId) {
        return courseRepository.findByInstituteId(instituteId).stream()
                .map(this::convertCourseToDTO)
                .collect(Collectors.toList());
    }

    public List<CourseDTO> getInstituteCoursesByCategory(Long instituteId, String category) {
        Institute institute = getInstituteById(instituteId);
        return courseRepository.findByInstituteAndCategory(institute, category).stream()
                .map(this::convertCourseToDTO)
                .collect(Collectors.toList());
    }

    // Admin Management
    @Transactional
    public void changeInstituteAdmin(Long instituteId, Long newAdminId) {
        Institute institute = getInstituteById(instituteId);
        Users newAdmin = getUserById(newAdminId);
        
        // Remove INSTITUTION role from current admin if exists
        if (institute.getAdmin() != null) {
            Users currentAdmin = institute.getAdmin();
            currentAdmin.getRoles().remove(Role.INSTITUTION);
            currentAdmin.setInstitute(null);
            userRepository.save(currentAdmin);
        }
        
        // Set new admin
        newAdmin.getRoles().add(Role.INSTITUTION);
        newAdmin.setInstitute(institute);
        institute.setAdmin(newAdmin);
        
        userRepository.save(newAdmin);
        instituteRepository.save(institute);
    }

    // Statistics
    public InstituteStatistics getInstituteStatistics(Long instituteId) {
        Institute institute = getInstituteById(instituteId);
        
        long studentCount = userRepository.findByInstituteAndRole(institute, Role.USER).size();
        long instructorCount = instructorRepository.countByInstitute(institute);
        long courseCount = courseRepository.countByInstitute(institute);
        
        return InstituteStatistics.builder()
                .instituteId(instituteId)
                .instituteName(institute.getInstituteName())
                .totalStudents(studentCount)
                .totalInstructors(instructorCount)
                .totalCourses(courseCount)
                .isActive(institute.isActive())
                .build();
    }

    // Helper methods
    private Institute getInstituteById(Long id) {
        return instituteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Institute not found with id: " + id));
    }

    private Users getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private Instructor getInstructorById(Long id) {
        return instructorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with id: " + id));
    }

    private Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }

    private UserDTO convertUserToDTO(Users user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setRoles(user.getRoles());
        return userDTO;
    }

    private InstructorDTO convertInstructorToDTO(Instructor instructor) {
        InstructorDTO instructorDTO = new InstructorDTO();
        instructorDTO.setInstructorId(instructor.getInstructorId());
        instructorDTO.setFirstName(instructor.getFirstName());
        instructorDTO.setLastName(instructor.getLastName());
        instructorDTO.setEmail(instructor.getEmail());
        instructorDTO.setPhoneNo(instructor.getPhoneNo());
        instructorDTO.setDateOfJoining(instructor.getDateOfJoining());
        instructorDTO.setFacebookHandle(instructor.getFacebookHandle());
        instructorDTO.setLinkedinHandle(instructor.getLinkedinHandle());
        instructorDTO.setYoutubeHandle(instructor.getYoutubeHandle());
        return instructorDTO;
    }

    private CourseDTO convertCourseToDTO(Course course) {
        return new CourseDTO(
                course.getCourseId(),
                course.getCourseName(),
                course.getCourseCode(),
                course.getCourseDescription(),
                course.getCourseDuration(),
                course.getCourseMode(),
                course.getMaxEnrollments(),
                course.getCourseFee(),
                course.getLanguage(),
                course.getCourseCategory()
        );
    }

    // Statistics DTO
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class InstituteStatistics {
        private Long instituteId;
        private String instituteName;
        private long totalStudents;
        private long totalInstructors;
        private long totalCourses;
        private boolean isActive;
    }
}