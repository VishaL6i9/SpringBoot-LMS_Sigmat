package com.flux.lms.repository;

import com.flux.lms.models.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileImageRepo extends JpaRepository<ProfileImage, Long> {
}