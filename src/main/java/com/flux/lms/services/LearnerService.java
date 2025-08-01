package com.flux.lms.services;

import com.flux.lms.models.Learner;
import com.flux.lms.repository.LearnerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LearnerService {

    @Autowired
    private LearnerRepo learnerRepo;

    public List<Learner> getAllLearners() {
        return learnerRepo.findAll();
    }

    public Learner saveLearner(Learner learner) {
        return learnerRepo.save(learner);
    }

    public void deleteLearner(Long learnerId) {
        learnerRepo.deleteById(learnerId);
    }

    public Optional<Learner> getLearnerById(Long learnerId) {
        return learnerRepo.findByLearnerId(learnerId);
    }
    
}