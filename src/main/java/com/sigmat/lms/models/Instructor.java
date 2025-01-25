package com.sigmat.lms.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@Entity
public class Instructor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "instructor_id", unique = true, nullable = false)
    private Long instructorId; 

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNo; 
    private LocalDate dateOfJoining; 

    
    //@Column
    //private List<Course> assignedCourses;
}