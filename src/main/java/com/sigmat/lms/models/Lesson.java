package com.sigmat.lms.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = VideoLesson.class, name = "video"),
        @JsonSubTypes.Type(value = ArticleLesson.class, name = "article"),
        @JsonSubTypes.Type(value = Quiz.class, name = "quiz"),
        @JsonSubTypes.Type(value = Assignment.class, name = "assignment")
})
public abstract class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private Integer lessonOrder;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private CourseModule courseModule;
}
