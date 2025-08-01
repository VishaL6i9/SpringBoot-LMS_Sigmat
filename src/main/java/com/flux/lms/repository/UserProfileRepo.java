package com.flux.lms.repository;

import com.flux.lms.models.UserProfile;
import com.flux.lms.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepo extends JpaRepository<UserProfile, Long> {
    UserProfile findByUsers(Users users);
    UserProfile findByUsersId(Long userId);
}

