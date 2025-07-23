package com.sigmat.lms.controllers;

import com.sigmat.lms.dtos.CourseModuleDTO;
import com.sigmat.lms.models.CourseModule;
import com.sigmat.lms.services.CourseModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses/{courseId}/modules")
public class CourseAllotmentController {

    @Autowired
    private CourseModuleService courseModuleService;

    @PostMapping
    public ResponseEntity<CourseModule> addModuleToCourse(@PathVariable Long courseId, @RequestBody CourseModule module) {
        return ResponseEntity.ok(courseModuleService.addModuleToCourse(courseId, module));
    }

    @GetMapping
    public ResponseEntity<List<CourseModuleDTO>> getModulesForCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseModuleService.getModulesForCourseAsDTO(courseId));
    }
}
