package com.sigmat.lms.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ProfileImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "image_data", columnDefinition = "bytea")
    private byte[] imageData;

    private String imageName;

    private String contentType;
}