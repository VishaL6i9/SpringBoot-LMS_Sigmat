package com.flux.lms.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ArticleLessonDTO extends LessonDTO {
    // No 'content' field to avoid exposing large LOBs
}
