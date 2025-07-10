package com.sigmat.lms.services;

import com.sigmat.lms.exceptions.DuplicateRelationshipException;
import com.sigmat.lms.exceptions.RelationshipNotFoundException;
import com.sigmat.lms.models.InstructorStudentRelationship;
import com.sigmat.lms.repo.InstructorStudentRepository;
import com.sigmat.lms.repo.UserRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserRelationshipService {

    private final InstructorStudentRepository relationshipRepo;
    private final UserRepo userRepo;

    public UserRelationshipService(InstructorStudentRepository relationshipRepo,
                                   UserRepo userRepo) {
        this.relationshipRepo = relationshipRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public InstructorStudentRelationship createRelationship(Long instructorId, Long studentId) {
        // Validate users exist and have correct roles
        validateUsers(instructorId, studentId);

        // Check if relationship already exists
        if (relationshipRepo.existsByInstructorIdAndStudentIdAndActiveTrue(instructorId, studentId)) {
            throw new DuplicateRelationshipException(
                    "Relationship already exists between instructor " + instructorId + " and student " + studentId);
        }

        // Create new relationship
        InstructorStudentRelationship relationship = new InstructorStudentRelationship();
        relationship.setInstructorId(instructorId);
        relationship.setStudentId(studentId);
        relationship.setActive(true);

        return relationshipRepo.save(relationship);
    }

    @Transactional
    public void deactivateRelationship(Long instructorId, Long studentId) {
        InstructorStudentRelationship relationship = relationshipRepo
                .findByInstructorAndStudent(instructorId, studentId)
                .orElseThrow(() -> new RelationshipNotFoundException(
                        "No active relationship found between instructor " + instructorId + " and student " + studentId));

        relationship.setActive(false);
        relationshipRepo.save(relationship);
    }

    public boolean isStudentOfInstructor(Long studentId, Long instructorId) {
        return relationshipRepo.existsByInstructorIdAndStudentIdAndActiveTrue(instructorId, studentId);
    }

    public List<Long> getStudentsForInstructor(Long instructorId) {
        return relationshipRepo.findStudentIdsByInstructorId(instructorId);
    }

    private void validateUsers(Long instructorId, Long studentId) {
        // Verify instructor exists and has instructor role
        if (!userRepo.existsByIdAndRoles_Name(instructorId, "INSTRUCTOR")) {
            throw new IllegalArgumentException("User " + instructorId + " is not an instructor");
        }

        // Verify student exists and has student role
        if (!userRepo.existsByIdAndRoles_Name(studentId, "USER")) {
            throw new IllegalArgumentException("User " + studentId + " is not a student");
        }
    }
    }