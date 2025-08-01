package com.flux.lms.dtos;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AssignmentDTO extends LessonDTO {
    private String assignmentName;
    private String description;
    private String dueDate;
}
