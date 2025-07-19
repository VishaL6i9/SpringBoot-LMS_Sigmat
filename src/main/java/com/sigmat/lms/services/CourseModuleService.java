package com.sigmat.lms.services;

import com.sigmat.lms.exceptions.ResourceNotFoundException;
import com.sigmat.lms.models.Course;
import com.sigmat.lms.models.CourseModule;
import com.sigmat.lms.repo.CourseRepo;
import com.sigmat.lms.repository.CourseModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<CourseModule> getModulesForCourse(Long courseId) {
        return courseRepository.findById(courseId).map(Course::getModules).orElseThrow(() -> new ResourceNotFoundException("Course not found"));
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
