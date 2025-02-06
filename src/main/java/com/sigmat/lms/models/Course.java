package com.sigmat.lms.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

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
    private String courseDescription;
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalModules;

    @ManyToOne
    @JoinColumn(name = "instructor_id", referencedColumnName = "instructor_id")
    private Instructor instructor;

}
