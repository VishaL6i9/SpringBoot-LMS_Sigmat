package com.sigmat.lms.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    // Social media handles
    private String facebookHandle;
    private String linkedinHandle;
    private String youtubeHandle;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Users user;

    // Institute relationship - instructors belong to an institute
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institute_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Institute institute;
    
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