package com.sigmat.lms.repository;

import com.sigmat.lms.models.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
}
