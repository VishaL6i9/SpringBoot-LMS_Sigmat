package com.sigmat.lms.repository;

import com.sigmat.lms.models.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
}
