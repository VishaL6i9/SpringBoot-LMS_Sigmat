package com.sigmat.lms.controllers;

import com.sigmat.lms.models.Assignment;
import com.sigmat.lms.models.AssignmentSubmission;
import com.sigmat.lms.services.AssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    @Autowired
    private AssignmentService assignmentService;

    @PostMapping
    public ResponseEntity<Assignment> createAssignment(@RequestBody Assignment assignment) {
        return ResponseEntity.ok(assignmentService.createAssignment(assignment));
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
