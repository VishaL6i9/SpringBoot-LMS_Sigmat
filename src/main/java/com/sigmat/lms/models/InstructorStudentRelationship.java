package com.sigmat.lms.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "instructor_student")
@Data
public class InstructorStudentRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "instructor_id", nullable = false)
    private Long instructorId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "active", nullable = false)
    private boolean active = true;
}