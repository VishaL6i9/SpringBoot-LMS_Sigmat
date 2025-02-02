package com.sigmat.lms.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Learner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "learner_id", unique = true, nullable = false)
    private Long learnerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNo;
    private LocalDate joinDate;

}
