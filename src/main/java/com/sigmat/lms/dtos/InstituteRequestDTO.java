package com.sigmat.lms.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstituteRequestDTO {
    private String instituteName;
    private String instituteCode;
    private String description;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String email;
    private String phoneNumber;
    private String website;
    private LocalDateTime establishedDate;
    private Long adminId; // ID of the user who will be the institute admin
}