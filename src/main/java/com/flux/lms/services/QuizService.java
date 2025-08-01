package com.flux.lms.services;

import com.flux.lms.exceptions.ResourceNotFoundException;
import com.flux.lms.models.Question;
import com.flux.lms.models.Quiz;
import com.flux.lms.repository.QuestionRepository;
import com.flux.lms.repository.QuizRepository;
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

    public void deleteQuestion(Long questionId) {
        questionRepository.deleteById(questionId);
    }
}
