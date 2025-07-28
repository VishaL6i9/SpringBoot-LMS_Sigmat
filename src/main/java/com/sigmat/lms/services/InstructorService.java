package com.sigmat.lms.services;

import com.sigmat.lms.models.Instructor;
import com.sigmat.lms.models.Users;
import com.sigmat.lms.repository.InstructorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class InstructorService {

    @Autowired
    private InstructorRepo instructorRepo;
    
    private static final Logger LOGGER = Logger.getLogger(InstructorService.class.getName());

    public List<Instructor> getAllInstructors() {
        return instructorRepo.findAll();
    }

    public Instructor saveInstructor(Instructor instructor) {
        try {
            // Check for duplicate email before attempting to save
            if (instructor.getEmail() != null && instructorRepo.existsByEmail(instructor.getEmail())) {
                LOGGER.warning("Attempted instructor registration with existing email: " + instructor.getEmail());
                throw new RuntimeException("This email address is already registered as an instructor.");
            }
            
            // Check for duplicate phone number
            if (instructor.getPhoneNo() != null && instructorRepo.existsByPhoneNo(instructor.getPhoneNo())) {
                LOGGER.warning("Attempted instructor registration with existing phone: " + instructor.getPhoneNo());
                throw new RuntimeException("This phone number is already registered. Please use a different phone number.");
            }
            
            // Check if user is already an instructor
            if (instructor.getUser() != null && instructorRepo.existsByUser(instructor.getUser())) {
                LOGGER.warning("Attempted instructor registration with existing user: " + instructor.getUser().getUsername());
                throw new RuntimeException("This user is already registered as an instructor.");
            }
            
            LOGGER.info("Saving instructor: " + instructor.getEmail() + ", Phone: " + instructor.getPhoneNo());
            return instructorRepo.save(instructor);
            
        } catch (DataIntegrityViolationException e) {
            LOGGER.severe("Database constraint violation while saving instructor: " + e.getMessage());
            String constraintMessage = handleDatabaseConstraintViolation(e);
            throw new RuntimeException(constraintMessage);
            
        } catch (Exception e) {
            LOGGER.severe("Unexpected error saving instructor: " + e.getMessage());
            throw new RuntimeException("An unexpected error occurred while saving instructor. Please try again.");
        }
    }

    private String handleDatabaseConstraintViolation(DataIntegrityViolationException e) {
        String errorMessage = e.getMessage();
        
        if (errorMessage != null) {
            if (errorMessage.contains("instructors_email_key") || 
                errorMessage.contains("duplicate key value violates unique constraint") && 
                errorMessage.contains("email")) {
                return "This email address is already registered as an instructor.";
            }
            
            if (errorMessage.contains("instructors_phone_key") || 
                errorMessage.contains("phone")) {
                return "This phone number is already registered. Please use a different phone number.";
            }
            
            if (errorMessage.contains("instructors_user_id_key") || 
                errorMessage.contains("user_id")) {
                return "This user is already registered as an instructor.";
            }
        }
        
        return "This instructor information is already registered in our system. Please check your details.";
    }

    public void deleteInstructor(Long instructorId) {
        instructorRepo.deleteById(instructorId);
    }

    public Optional<Instructor> getInstructorById(Long instructorId) {
        return instructorRepo.findByInstructorId(instructorId);
    }

    public Instructor getInstructorByUser(Users user) {
        return instructorRepo.findByUser(user);
    }

    public Optional<Instructor> getInstructorByUserId(Long userId) {
        return instructorRepo.findByUser_Id(userId);
    }

    public boolean isEmailAlreadyRegistered(String email) {
        return instructorRepo.existsByEmail(email);
    }

    public boolean isPhoneNumberAlreadyRegistered(String phoneNo) {
        return instructorRepo.existsByPhoneNo(phoneNo);
    }

    public boolean isUserAlreadyInstructor(Users user) {
        return instructorRepo.existsByUser(user);
    }

    public Optional<Instructor> findInstructorByEmail(String email) {
        return instructorRepo.findByEmail(email);
    }

    public Optional<Instructor> findInstructorByPhoneNo(String phoneNo) {
        return instructorRepo.findByPhoneNo(phoneNo);
    }
}