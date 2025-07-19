package com.sigmat.lms.repository;

import com.sigmat.lms.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
