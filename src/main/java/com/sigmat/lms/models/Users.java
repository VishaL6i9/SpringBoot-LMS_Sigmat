package com.sigmat.lms.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String email;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    @JsonIgnore
    @OneToOne(mappedBy = "users", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private UserProfile userProfile;

    @JsonProperty(required = false)
    @Column(name = "verification_token")
    private String verificationToken; 

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified = false;

    @Column(name = "password_reset_token")
    private String passwordResetToken;

    @Column(name = "password_reset_token_expiration")
    private Long passwordResetTokenExpiration;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserSubscription> subscriptions;

    // Institute relationship - for students and institute admins
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institute_id")
    private Institute institute;

    // Batch relationship - for students
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private Batch batch;

    // Parent relationship - for students in educational institutions
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ParentProfile parentProfile;

    // Manager relationship - for employees in industry L&D
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Users manager;

    // Institutional specific attributes
    @Column(name = "roll_number")
    private String rollNumber;        // For students

    @Column(name = "admission_id")
    private String admissionId;       // For students

    @Column(name = "staff_id")
    private String staffId;           // For staff/instructors

    @Column(name = "employee_id")
    private String employeeId;        // For industry L&D

    private String department;        // For industry L&D
    private String jobRole;           // For industry L&D

    // Contact details
    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "parent_contact")
    private String parentContact;     // For educational institutions

    @Column(name = "emergency_contact")
    private String emergencyContact;

    // Academic/Professional details
    @Column(name = "batch_name")
    private String batchName;         // For students (batch name as string)
    private String semester;          // For students
    private String grade;             // For students
    private String division;          // For students

    @Column(name = "course_of_study")
    private String courseOfStudy;     // For students

    // Enrollment details
    @Column(name = "enrollment_date")
    private LocalDateTime enrollmentDate;

    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;

    @PrePersist
    public void prePersist() {
        if (userProfile == null) {
            userProfile = new UserProfile();
            userProfile.setUsers(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Users users = (Users) o;
        return id != null && id.equals(users.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}