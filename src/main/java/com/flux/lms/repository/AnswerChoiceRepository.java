package com.flux.lms.repository;

import com.flux.lms.models.AnswerChoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerChoiceRepository extends JpaRepository<AnswerChoice, Long> {
}
