package com.sigmat.lms.dtos;

import lombok.Data;

@Data
public class CourseDTO {
    private Long courseId;
    private String courseName;
    private String courseCode;
    private String courseDescription;
    private Long courseDuration;
    private String courseMode;
    private int maxEnrollments;
    private Long courseFee;
    private String language;
    private String courseCategory;
    private String courseScope; // INSTITUTE_ONLY, GLOBAL, RESTRICTED
    private Long instituteId;
    private String instituteName;

    public CourseDTO() {
    }

    public CourseDTO(Long courseId, String courseName, String courseCode, String courseDescription, Long courseDuration, String courseMode, int maxEnrollments, Long courseFee, String language, String courseCategory) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.courseDescription = courseDescription;
        this.courseDuration = courseDuration;
        this.courseMode = courseMode;
        this.maxEnrollments = maxEnrollments;
        this.courseFee = courseFee;
        this.language = language;
        this.courseCategory = courseCategory;
    }

    public CourseDTO(Long courseId, String courseName, String courseCode, String courseDescription, Long courseDuration, String courseMode, int maxEnrollments, Long courseFee, String language, String courseCategory, String courseScope, Long instituteId, String instituteName) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.courseDescription = courseDescription;
        this.courseDuration = courseDuration;
        this.courseMode = courseMode;
        this.maxEnrollments = maxEnrollments;
        this.courseFee = courseFee;
        this.language = language;
        this.courseCategory = courseCategory;
        this.courseScope = courseScope;
        this.instituteId = instituteId;
        this.instituteName = instituteName;
    }
}
