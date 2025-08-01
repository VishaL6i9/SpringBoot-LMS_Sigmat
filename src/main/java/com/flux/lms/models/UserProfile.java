package com.flux.lms.models;

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

    // Helper method to get current active subscription through the Users entity
    public UserSubscription getCurrentSubscription() {
        if (users == null || users.getSubscriptions() == null || users.getSubscriptions().isEmpty()) {
            return null;
        }
        return users.getSubscriptions().stream()
                .filter(sub -> sub.getStatus() == SubscriptionStatus.ACTIVE)
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfile that = (UserProfile) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}