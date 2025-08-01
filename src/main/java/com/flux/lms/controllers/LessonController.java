package com.flux.lms.controllers;

import com.flux.lms.models.Lesson;
import com.flux.lms.services.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    @Autowired
    private LessonService lessonService;

    @PostMapping("/module/{moduleId}")
    public ResponseEntity<Lesson> addLessonToModule(@PathVariable Long moduleId, @RequestBody Lesson lesson) {
        // This endpoint is tricky because Lesson is abstract.
        // The JSON will need to specify the concrete type.
        // e.g. {"@type": "VideoLesson", "title": "Intro", ...}
        // This requires Jackson's polymorphic deserialization.
        return ResponseEntity.ok(lessonService.addLessonToModule(moduleId, lesson));
    }

    @DeleteMapping("/{lessonId}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long lessonId) {
        lessonService.deleteLesson(lessonId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{lessonId}")
    public ResponseEntity<Lesson> getLessonById(@PathVariable Long lessonId) {
        return ResponseEntity.ok(lessonService.getLessonById(lessonId));
    }
}
