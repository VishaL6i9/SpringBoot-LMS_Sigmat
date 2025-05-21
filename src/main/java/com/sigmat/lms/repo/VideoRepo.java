package com.sigmat.lms.repo;

import com.sigmat.lms.models.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoRepo extends JpaRepository<Video, Long> {
    Optional<Video> findByTitle(String title);  
}