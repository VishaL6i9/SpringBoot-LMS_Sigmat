package com.flux.lms.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseModuleDTO {
    private Long id;
    private String title;
    private String description;
    private Integer moduleOrder;
    private List<LessonDTO> lessons;
}