package com.sigmat.lms.controllers;

import com.sigmat.lms.models.CourseModule;
import com.sigmat.lms.services.CourseModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/modules")
public class CourseModuleController {

    @Autowired
    private CourseModuleService courseModuleService;

    @PutMapping("/{moduleId}")
    public ResponseEntity<CourseModule> updateModule(@PathVariable Long moduleId, @RequestBody CourseModule moduleDetails) {
        return ResponseEntity.ok(courseModuleService.updateModule(moduleId, moduleDetails));
    }

    @DeleteMapping("/{moduleId}")
    public ResponseEntity<Void> deleteModule(@PathVariable Long moduleId) {
        courseModuleService.deleteModule(moduleId);
        return ResponseEntity.noContent().build();
    }
}
