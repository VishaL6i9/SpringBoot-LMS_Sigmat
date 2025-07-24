package com.sigmat.lms.controllers;

import com.sigmat.lms.dtos.AssignmentDTO;
import com.sigmat.lms.models.Assignment;
import com.sigmat.lms.models.AssignmentSubmission;
import com.sigmat.lms.services.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    @PostMapping
    public ResponseEntity<AssignmentDTO> createAssignment(@RequestBody AssignmentDTO assignmentDTO) {
        Assignment createdAssignment = assignmentService.createAssignment(assignmentDTO);
        return new ResponseEntity<>(convertToDto(createdAssignment), HttpStatus.CREATED);
    }

    private AssignmentDTO convertToDto(Assignment assignment) {
        AssignmentDTO dto = new AssignmentDTO();
        dto.setId(assignment.getId());
        dto.setTitle(assignment.getTitle()); // Maps to LessonDTO's title
        dto.setDescription(assignment.getDescription());
        dto.setDueDate(assignment.getDueDate() != null ? assignment.getDueDate().toString() : null); // Convert LocalDateTime to String for DTO
        dto.setLessonOrder(assignment.getLessonOrder());
        dto.setType("assignment"); // Explicitly set type for AssignmentDTO
        return dto;
    }

    @PostMapping("/{assignmentId}/submit")
    public ResponseEntity<AssignmentSubmission> submitAssignment(
            @PathVariable Long assignmentId,
            @RequestParam Long userId, // In a real app, get this from security context
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String filePath) {
        return ResponseEntity.ok(assignmentService.submitAssignment(assignmentId, userId, content, filePath));
    }

    @GetMapping("/{assignmentId}/submissions")
    public ResponseEntity<List<AssignmentSubmission>> getSubmissions(@PathVariable Long assignmentId) {
        return ResponseEntity.ok(assignmentService.getSubmissionsForAssignment(assignmentId));
    }

    @PostMapping("/submissions/{submissionId}/grade")
    public ResponseEntity<AssignmentSubmission> gradeSubmission(
            @PathVariable Long submissionId,
            @RequestParam Double grade,
            @RequestParam String feedback) {
        return ResponseEntity.ok(assignmentService.gradeSubmission(submissionId, grade, feedback));
    }
}
