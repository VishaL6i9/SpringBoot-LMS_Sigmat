package com.flux.lms.services;

import com.flux.lms.dtos.AssignmentDTO;
import com.flux.lms.exceptions.ResourceNotFoundException;
import com.flux.lms.models.Assignment;
import com.flux.lms.models.AssignmentSubmission;
import com.flux.lms.models.Users;
import com.flux.lms.repository.AssignmentRepository;
import com.flux.lms.repository.AssignmentSubmissionRepository;
import com.flux.lms.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private AssignmentSubmissionRepository submissionRepository;

    @Autowired
    private UserRepo userRepository;

    public Assignment createAssignment(AssignmentDTO assignmentDTO) {
        Assignment assignment = new Assignment();
        assignment.setTitle(assignmentDTO.getTitle());
        assignment.setDescription(assignmentDTO.getDescription());
        assignment.setDueDate(LocalDateTime.parse(assignmentDTO.getDueDate()));
        // Set other fields as necessary
        return assignmentRepository.save(assignment);
    }

    public AssignmentSubmission submitAssignment(Long assignmentId, Long userId, String content, String filePath) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id: " + assignmentId));
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        AssignmentSubmission submission = new AssignmentSubmission();
        submission.setAssignment(assignment);
        submission.setStudent(user);
        submission.setContent(content);
        submission.setFilePath(filePath);
        submission.setSubmissionDate(LocalDateTime.now());

        return submissionRepository.save(submission);
    }

    public List<AssignmentSubmission> getSubmissionsForAssignment(Long assignmentId) {
        return submissionRepository.findAll().stream()
                .filter(s -> s.getAssignment().getId().equals(assignmentId))
                .toList();
    }

    public AssignmentSubmission gradeSubmission(Long submissionId, Double grade, String feedback) {
        AssignmentSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with id: " + submissionId));
        submission.setGrade(grade);
        submission.setFeedback(feedback);
        return submissionRepository.save(submission);
    }
}
