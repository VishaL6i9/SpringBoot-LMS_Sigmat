package com.sigmat.lms.controllers;

import com.sigmat.lms.models.ProfileImage;
import com.sigmat.lms.services.ProfileImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/public")
public class ProfileImageController {
    @Autowired
    private ProfileImageService imageService;
    @Autowired
    private ProfileImageService profileImageService;

    @PostMapping("/image-upload")
    public ResponseEntity<ProfileImage> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            ProfileImage image = imageService.uploadImage(file);
            return ResponseEntity.ok(image);
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/get-image/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        ProfileImage image = imageService.getImage(id);
        if (image != null) {
            String contentType = "image/jpeg";
            if (image.getImageName() != null) {
                if (image.getImageName().endsWith(".png")) {
                    contentType = "image/png";
                } else if (image.getImageName().endsWith(".gif")) {
                    contentType = "image/gif";
                }
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(image.getImageData());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/images")
    public ResponseEntity<List<ProfileImage>> getImages() {
        List<ProfileImage> images = profileImageService.getAllImages();
        return ResponseEntity.ok(images);
    }
}