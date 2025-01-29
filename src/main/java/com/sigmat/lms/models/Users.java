package com.sigmat.lms.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id; 

    @Column(nullable = false, unique = true) 
    private String username;

    @Column(nullable = false) 
    private String password;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER) 
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    public Users(String username, String password) {
        this.username = username;
        this.password = password;
    }
}