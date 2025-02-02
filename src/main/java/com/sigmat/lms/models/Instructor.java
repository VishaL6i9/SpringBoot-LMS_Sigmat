package com.sigmat.lms.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@Entity
@NoArgsConstructor
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