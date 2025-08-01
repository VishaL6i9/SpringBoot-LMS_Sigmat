package com.flux.lms.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "institutes")
public class Institute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "institute_id")
    private Long instituteId;

    @Column(nullable = false, unique = true)
    private String instituteName;

    @Column(unique = true)
    private String instituteCode;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String address;

    private String city;
    private String state;
    private String country;
    private String postalCode;

    @Column(unique = true)
    private String email;

    private String phoneNumber;
    private String website;

    @Column(name = "established_date")
    private LocalDateTime establishedDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(name = "is_active")
    private boolean isActive = true;

    // Institute Admin - One institute can have one primary admin
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_user_id", unique = true)
    private Users admin;

    // Students belonging to this institute
    @JsonIgnore
    @OneToMany(mappedBy = "institute", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Users> students;

    // Instructors belonging to this institute
    @JsonIgnore
    @OneToMany(mappedBy = "institute", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Instructor> instructors;

    // Courses offered by this institute
    @JsonIgnore
    @OneToMany(mappedBy = "institute", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Course> courses;

    // Global course subscriptions
    @JsonIgnore
    @OneToMany(mappedBy = "institute", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InstituteSubscription> courseSubscriptions;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Institute institute = (Institute) o;
        return instituteId != null && instituteId.equals(institute.instituteId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}