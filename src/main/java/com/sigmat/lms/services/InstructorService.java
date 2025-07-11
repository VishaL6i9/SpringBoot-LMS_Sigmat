package com.sigmat.lms.services;

import com.sigmat.lms.models.Instructor;
import com.sigmat.lms.repo.InstructorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InstructorService {

    @Autowired
    private InstructorRepo instructorRepo;

    public List<Instructor> getAllInstructors() {
        return instructorRepo.findAll();
    }

    public Instructor saveInstructor(Instructor instructor) {
        return instructorRepo.save(instructor);
    }

    public void deleteInstructor(Long instructorId) {
        instructorRepo.deleteById(instructorId);
    }

    public Optional<Instructor> getInstructorById(Long instructorId) {
        return instructorRepo.findByInstructorId(instructorId);
    }

}