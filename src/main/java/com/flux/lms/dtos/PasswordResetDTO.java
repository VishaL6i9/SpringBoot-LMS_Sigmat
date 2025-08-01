package com.flux.lms.dtos;

import lombok.Data;

@Data
public class PasswordResetDTO {
    private String email;
    private String token;
    private String newPassword;
}
