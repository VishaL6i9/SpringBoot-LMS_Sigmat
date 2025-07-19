package com.sigmat.lms.services;

import com.sigmat.lms.exceptions.ResourceNotFoundException;
import com.sigmat.lms.models.CourseModule;
import com.sigmat.lms.models.Lesson;
import com.sigmat.lms.repository.CourseModuleRepository;
import com.sigmat.lms.repository.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LessonService {

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private CourseModuleRepository moduleRepository;

    public Lesson addLessonToModule(Long moduleId, Lesson lesson) {
        CourseModule module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found with id: " + moduleId));
        lesson.setCourseModule(module);
        return lessonRepository.save(lesson);
    }

    public void deleteLesson(Long lessonId) {
        lessonRepository.deleteById(lessonId);
    }

    public Lesson getLessonById(Long lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));
    }
}
