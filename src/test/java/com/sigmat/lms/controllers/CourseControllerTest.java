package com.sigmat.lms.controllers;

import com.sigmat.lms.models.Course;
import com.sigmat.lms.services.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CourseControllerTest {

    @InjectMocks
    private CourseController courseController;

    @Mock
    private CourseService courseService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateCourse() {
        Course course = new Course();
        course.setCourseId(1L);
        course.setCourseName("Test Course");

        when(courseService.saveCourse(any(Course.class))).thenReturn(course);

        ResponseEntity<Course> response = courseController.createCourse(course);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(course, response.getBody());
    }

    @Test
    public void testGetAllCourses() {
        Course course1 = new Course();
        course1.setCourseId(1L);
        course1.setCourseName("Course 1");

        Course course2 = new Course();
        course2.setCourseId(2L);
        course2.setCourseName("Course 2");

        List<Course> courses = Arrays.asList(course1, course2);
        when(courseService.getAllCourses()).thenReturn(courses);

        ResponseEntity<List<Course>> response = courseController.getAllCourses();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(courses, response.getBody());
    }

    @Test
    public void testGetCourseById_Found() {
        Course course = new Course();
        course.setCourseId(1L);
        course.setCourseName("Test Course");

        when(courseService.getCourseById(1L)).thenReturn(Optional.of(course));

        ResponseEntity<Course> response = courseController.getCourseById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(course, response.getBody());
    }

    @Test
    public void testGetCourseById_NotFound() {
        when(courseService.getCourseById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Course> response = courseController.getCourseById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDeleteCourse() {
        Long courseId = 1L;

        doNothing().when(courseService).deleteCourse(courseId);

        ResponseEntity<Void> response = courseController.deleteCourse(courseId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(courseService, times(1)).deleteCourse(courseId);
    }
}