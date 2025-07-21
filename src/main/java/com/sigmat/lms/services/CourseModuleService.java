package com.sigmat.lms.services;

import com.sigmat.lms.exceptions.ResourceNotFoundException;
import com.sigmat.lms.models.ArticleLesson;
import com.sigmat.lms.models.Course;
import com.sigmat.lms.models.CourseModule;
import com.sigmat.lms.repo.CourseRepo;
import com.sigmat.lms.repository.CourseModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseModuleService {

    @Autowired
    private CourseModuleRepository courseModuleRepository;

    @Autowired
    private CourseRepo courseRepository;

    public CourseModule addModuleToCourse(Long courseId, CourseModule module) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        module.setCourse(course);
        return courseModuleRepository.save(module);
    }

    @Transactional
    public List<CourseModule> getModulesForCourse(Long courseId) {
        Course course = courseRepository.findByCourseIdWithModulesAndLessonsAndArticleContent(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        // Explicitly initialize lazy collections and LOBs within the transaction
        course.getModules().forEach(module -> {
            module.getLessons().forEach(lesson -> {
                if (lesson instanceof ArticleLesson) {
                    // Access the content to initialize the LOB
                    ((ArticleLesson) lesson).getContent();
                }
            });
        });
        return course.getModules();
    }

    public CourseModule updateModule(Long moduleId, CourseModule moduleDetails) {
        CourseModule module = courseModuleRepository.findById(moduleId).orElseThrow(() -> new ResourceNotFoundException("Module not found"));
        module.setTitle(moduleDetails.getTitle());
        module.setDescription(moduleDetails.getDescription());
        module.setModuleOrder(moduleDetails.getModuleOrder());
        return courseModuleRepository.save(module);
    }

    public void deleteModule(Long moduleId) {
        courseModuleRepository.deleteById(moduleId);
    }
}
