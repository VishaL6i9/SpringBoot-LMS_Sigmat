package com.sigmat.lms.repo;

import com.sigmat.lms.models.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileImageRepo extends JpaRepository<ProfileImage, Long> {
}