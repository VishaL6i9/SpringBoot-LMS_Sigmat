package com.sigmat.lms.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@Entity
public class Course {
    @Id
    @Column(name = "course_id",unique = true,nullable = false)
    private int courseID;
    private String courseName;
    private String courseDescription;
    private Date startDate;
    private Date endDate;
    private int totalModules;

    @ManyToOne
    @JoinColumn(name = "instructor_id", referencedColumnName = "instructor_id")
    private Instructor instructor;

    public Course() {
        
    }
}
