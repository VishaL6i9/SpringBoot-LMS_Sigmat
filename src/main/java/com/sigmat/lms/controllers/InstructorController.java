package com.sigmat.lms.controllers;

import com.sigmat.lms.dtos.InstructorDTO;
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
    public ResponseEntity<List<InstructorDTO>> getAllInstructors() {
        try {
            List<InstructorDTO> instructors = instructorService.getAllInstructorsDTO();
            return new ResponseEntity<>(instructors, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{instructorId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'INSTRUCTOR', 'USER')")
    public ResponseEntity<InstructorDTO> getInstructorById(@PathVariable Long instructorId) {
        try {
            InstructorDTO instructor = instructorService.getInstructorDTOById(instructorId);
            if (instructor != null) {
                return ResponseEntity.ok(instructor);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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