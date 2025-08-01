package com.flux.lms.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileImageUploadResponseDTO {
    private Long profileImageId;
    private String imageName;
    private String contentType;
    private String message;
    private boolean success;
}