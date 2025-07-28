package com.sigmat.lms.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseSubscriptionRequestDTO {
    private Long instituteId;
    private Long courseId;
    private boolean autoEnrollStudents = true;
    private boolean autoEnrollInstructors = false;
}