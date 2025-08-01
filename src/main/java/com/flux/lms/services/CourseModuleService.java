package com.flux.lms.services;

import com.flux.lms.dtos.CourseModuleDTO;
import com.flux.lms.dtos.LessonDTO;
import com.flux.lms.exceptions.ResourceNotFoundException;
import com.flux.lms.models.*;
import com.flux.lms.repository.CourseModuleRepository;
import com.flux.lms.repository.CourseRepo;
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

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public List<CourseModuleDTO> getModulesForCourseAsDTO(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        
        return course.getModules().stream()
                .map(this::convertToDTO)
                .toList();
    }

    private CourseModuleDTO convertToDTO(CourseModule module) {
        CourseModuleDTO dto = new CourseModuleDTO();
        dto.setId(module.getId());
        dto.setTitle(module.getTitle());
        dto.setDescription(module.getDescription());
        dto.setModuleOrder(module.getModuleOrder());
        
        if (module.getLessons() != null) {
            dto.setLessons(module.getLessons().stream()
                    .map(this::convertLessonToDTO)
                    .toList());
        }
        
        return dto;
    }

    private LessonDTO convertLessonToDTO(Lesson lesson) {
        LessonDTO dto = new LessonDTO();
        dto.setId(lesson.getId());
        dto.setTitle(lesson.getTitle());
        dto.setLessonOrder(lesson.getLessonOrder());
        
        if (lesson instanceof ArticleLesson articleLesson) {
            dto.setType("article");
            dto.setContent(articleLesson.getContent());
        } else if (lesson instanceof VideoLesson videoLesson) {
            dto.setType("video");
            if (videoLesson.getVideo() != null) {
                dto.setVideoId(videoLesson.getVideo().getId());
            }
        } else if (lesson instanceof Quiz) {
            dto.setType("quiz");
        } else if (lesson instanceof Assignment) {
            dto.setType("assignment");
        }
        
        return dto;
    }
}
