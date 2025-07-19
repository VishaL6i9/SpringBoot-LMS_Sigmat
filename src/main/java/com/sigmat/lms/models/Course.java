package com.sigmat.lms.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

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

    @ManyToMany
    @JoinTable(
        name = "course_instructors",
        joinColumns = @JoinColumn(name = "course_id"),
        inverseJoinColumns = @JoinColumn(name = "instructor_id")
    )
    private Set<Instructor> instructors;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<CourseModule> modules;
}
