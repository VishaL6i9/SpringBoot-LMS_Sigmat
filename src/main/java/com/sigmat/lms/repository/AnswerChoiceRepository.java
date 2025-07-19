package com.sigmat.lms.repository;

import com.sigmat.lms.models.AnswerChoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerChoiceRepository extends JpaRepository<AnswerChoice, Long> {
}
