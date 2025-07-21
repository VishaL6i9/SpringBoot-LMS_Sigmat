package com.sigmat.lms.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class VideoLessonDTO extends LessonDTO {
    private Long videoId;
    private String videoTitle;
}
