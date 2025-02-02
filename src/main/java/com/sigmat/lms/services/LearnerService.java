package com.sigmat.lms.services;

import com.sigmat.lms.models.Learner;
import com.sigmat.lms.repo.LearnerRepo;
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

    // Add other methods as needed
}