package com.sigmat.lms.repository;

import com.sigmat.lms.models.AssignmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {
}
