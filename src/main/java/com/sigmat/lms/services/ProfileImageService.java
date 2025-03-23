package com.sigmat.lms.services;

import com.sigmat.lms.models.ProfileImage;
import com.sigmat.lms.repo.ProfileImageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

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
}