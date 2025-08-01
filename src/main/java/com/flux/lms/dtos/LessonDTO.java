package com.flux.lms.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonDTO {
    private Long id;
    private String title;
    private Integer lessonOrder;
    private String type;
    private String content; // For article lessons
    private Long videoId; // For video lessons
}