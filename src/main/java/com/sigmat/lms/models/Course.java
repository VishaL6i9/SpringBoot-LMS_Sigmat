package com.sigmat.lms.models;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Course {
    @Id
    @Column(name = "courseId", unique = true,nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;
    private String courseName;
    private String courseCode;
    private String courseDescription;
    private Long courseDuration;
    private String courseMode;
    private int maxEnrollments;
    private Long courseFee;
    private String language;
    private String courseCategory;
    @ManyToOne
    @JoinColumn(name = "instructor_id", referencedColumnName = "instructor_id")
    private Instructor instructor;

}
