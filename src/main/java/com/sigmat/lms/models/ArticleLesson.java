package com.sigmat.lms.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class ArticleLesson extends Lesson {

    @Lob
    private String content;
}
