package com.sigmat.lms.dtos;

import lombok.Data;

import java.util.List;

@Data
public class CourseModuleDTO {
    private Long id;
    private String title;
    private String description;
    private Integer moduleOrder;
    private List<LessonDTO> lessons;
}
