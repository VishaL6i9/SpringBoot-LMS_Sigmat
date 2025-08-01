package com.flux.lms.services;

import com.flux.lms.models.ProfileImage;
import com.flux.lms.repository.ProfileImageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProfileImageService {
    @Autowired
    private ProfileImageRepo imageRepository;

    public ProfileImage uploadImage(MultipartFile file) throws IOException {
        ProfileImage image = new ProfileImage();
        image.setImageData(file.getBytes());
        image.setImageName(file.getOriginalFilename());
        return imageRepository.save(image);
    }

    public ProfileImage getImage(Long id) {
        return imageRepository.findById(id).orElse(null);
    }

    public List<ProfileImage> getAllImages() {
        return imageRepository.findAll();
    }
}