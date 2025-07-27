package com.sigmat.lms.controllers;

import com.sigmat.lms.models.Instructor;
import com.sigmat.lms.services.InstructorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/instructors")
public class InstructorController {

    private final InstructorService instructorService;

    @Autowired
    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<Instructor> createInstructor(@RequestBody Instructor instructor) {
        try {
            Instructor savedInstructor = instructorService.saveInstructor(instructor);
            return new ResponseEntity<>(savedInstructor, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null); // Or a more specific error response
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'INSTRUCTOR', 'USER')")
    public ResponseEntity<List<Instructor>> getAllInstructors() {
        List<Instructor> instructors = instructorService.getAllInstructors();
        return new ResponseEntity<>(instructors, HttpStatus.OK);
    }

    @GetMapping("/{instructorId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'INSTRUCTOR', 'USER')")
    public ResponseEntity<Instructor> getInstructorById(@PathVariable Long instructorId) {
        Optional<Instructor> instructor = instructorService.getInstructorById(instructorId);
        return instructor.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/{instructorId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<Instructor> updateInstructor(@PathVariable Long instructorId, @RequestBody Instructor instructorDetails) {
        // For update, we need to ensure the ID in the path matches the ID in the body if present
        if (instructorDetails.getInstructorId() == null || !instructorDetails.getInstructorId().equals(instructorId)) {
            instructorDetails.setInstructorId(instructorId);
        }
        try {
            Instructor updatedInstructor = instructorService.saveInstructor(instructorDetails);
            return new ResponseEntity<>(updatedInstructor, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null); // Or a more specific error response
        }
    }

    @DeleteMapping("/{instructorId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<Void> deleteInstructor(@PathVariable Long instructorId) {
        try {
            instructorService.deleteInstructor(instructorId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Or a more specific error response
        }
    }
}