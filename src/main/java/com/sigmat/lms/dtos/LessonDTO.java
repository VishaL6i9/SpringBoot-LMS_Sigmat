package com.sigmat.lms.dtos;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@Data
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = VideoLessonDTO.class, name = "video"),
        @JsonSubTypes.Type(value = ArticleLessonDTO.class, name = "article"),
        @JsonSubTypes.Type(value = QuizDTO.class, name = "quiz"),
        @JsonSubTypes.Type(value = AssignmentDTO.class, name = "assignment")
})
public abstract class LessonDTO {
    private Long id;
    private String title;
    private Integer lessonOrder;
}
