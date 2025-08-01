package com.flux.lms.controllers;

import com.flux.lms.dtos.InstituteDTO;
import com.flux.lms.services.InstituteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/institutes")
@RequiredArgsConstructor
public class InstituteController {

    private final InstituteService instituteService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<InstituteDTO> createInstitute(@RequestBody InstituteDTO instituteDTO) {
        InstituteDTO createdInstitute = instituteService.createInstitute(instituteDTO);
        return new ResponseEntity<>(createdInstitute, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<InstituteDTO>> getAllInstitutes() {
        List<InstituteDTO> institutes = instituteService.getAllInstitutes();
        return ResponseEntity.ok(institutes);
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    public ResponseEntity<List<InstituteDTO>> getActiveInstitutes() {
        List<InstituteDTO> institutes = instituteService.getActiveInstitutes();
        return ResponseEntity.ok(institutes);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('INSTITUTION') or hasRole('ADMIN')")
    public ResponseEntity<InstituteDTO> getInstituteById(@PathVariable Long id) {
        InstituteDTO institute = instituteService.getInstituteById(id);
        return ResponseEntity.ok(institute);
    }

    @GetMapping("/code/{code}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('INSTITUTION') or hasRole('ADMIN')")
    public ResponseEntity<InstituteDTO> getInstituteByCode(@PathVariable String code) {
        InstituteDTO institute = instituteService.getInstituteByCode(code);
        return ResponseEntity.ok(institute);
    }

    @GetMapping("/admin/{adminId}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('INSTITUTION')")
    public ResponseEntity<InstituteDTO> getInstituteByAdmin(@PathVariable Long adminId) {
        InstituteDTO institute = instituteService.getInstituteByAdmin(adminId);
        return ResponseEntity.ok(institute);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN') or (hasRole('INSTITUTION') and @instituteService.getInstituteByAdmin(authentication.principal.id).instituteId == #id)")
    public ResponseEntity<InstituteDTO> updateInstitute(@PathVariable Long id, @RequestBody InstituteDTO instituteDTO) {
        InstituteDTO updatedInstitute = instituteService.updateInstitute(id, instituteDTO);
        return ResponseEntity.ok(updatedInstitute);
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deactivateInstitute(@PathVariable Long id) {
        instituteService.deactivateInstitute(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> activateInstitute(@PathVariable Long id) {
        instituteService.activateInstitute(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteInstitute(@PathVariable Long id) {
        instituteService.deleteInstitute(id);
        return ResponseEntity.noContent().build();
    }
}