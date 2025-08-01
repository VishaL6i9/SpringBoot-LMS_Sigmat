package com.flux.lms.dtos;

import com.flux.lms.models.ProfileImage;
import lombok.Data;

@Data
public class UserProfileDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String language;
    private String timezone;
    private ProfileImage profileImage;
    private UserDTO user;
}
