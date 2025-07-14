package com.sigmat.lms.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_profile")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users users;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String timezone;
    private String language;

    @OneToOne(cascade = CascadeType.ALL) 
    @JoinColumn(name = "profile_image_id")
    private ProfileImage profileImage; 

    private String password;
}