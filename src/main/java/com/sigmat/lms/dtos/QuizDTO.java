package com.sigmat.lms.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class QuizDTO extends LessonDTO {
    private String quizName;
    private Integer durationMinutes;
}
