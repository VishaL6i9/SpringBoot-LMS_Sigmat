package com.sigmat.lms.services;

import com.sigmat.lms.exceptions.ResourceNotFoundException;
import com.sigmat.lms.models.Question;
import com.sigmat.lms.models.Quiz;
import com.sigmat.lms.repository.QuestionRepository;
import com.sigmat.lms.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    public Quiz createQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    public Question addQuestionToQuiz(Long quizId, Question question) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));
        question.setQuiz(quiz);

        // Set the back-reference for each answer choice
        if (question.getAnswerChoices() != null) {
            for (var answerChoice : question.getAnswerChoices()) {
                answerChoice.setQuestion(question);
            }
        }

        return questionRepository.save(question);
    }

    public Quiz getQuizById(Long quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));
    }
}
