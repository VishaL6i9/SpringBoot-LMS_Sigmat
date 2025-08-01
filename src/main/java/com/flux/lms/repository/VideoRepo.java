package com.flux.lms.repository;

import com.flux.lms.models.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoRepo extends JpaRepository<Video, Long> {
    Optional<Video> findByTitle(String title);  
}