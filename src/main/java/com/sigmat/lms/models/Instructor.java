package com.sigmat.lms.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Objects;

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

    // Social media handles
    private String facebookHandle;
    private String linkedinHandle;
    private String youtubeHandle;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private Users user;

    
    //@Column
    //private List<Course> assignedCourses;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instructor that = (Instructor) o;
        return instructorId != null && instructorId.equals(that.instructorId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}