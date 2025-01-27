package com.sigmat.lms.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Users {
    public Users(int id,String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;

    }
    @Id
    private int id;
    @Column(nullable = false)
    private String username;
    private String password;

    public Users() {
        
    }
}