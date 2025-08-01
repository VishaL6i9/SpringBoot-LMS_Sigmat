package com.flux.lms.repository;

import com.flux.lms.models.Learner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LearnerRepo extends JpaRepository<Learner, Long> {
    Optional<Learner> findByFirstName(String name);
    Optional<Learner> findByLearnerId(Long learnerId);
    
}
