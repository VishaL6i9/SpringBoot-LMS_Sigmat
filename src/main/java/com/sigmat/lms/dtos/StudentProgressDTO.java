package com.sigmat.lms.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProgressDTO {
    private Long studentId;
    private String studentName;
    private String rollNumber;
    private String email;
    
    // Institute and batch info
    private Long instituteId;
    private String instituteName;
    private String batchName;
    private String semester;
    
    // Academic progress
    private Double overallGrade;
    private String gradeLevel; // A+, A, B+, etc.
    private Double attendancePercentage;
    private Integer totalClasses;
    private Integer attendedClasses;
    
    // Course-wise progress
    private List<CourseProgressDTO> courseProgress;
    
    // Recent activities
    private List<AssignmentSubmissionDTO> recentSubmissions;
    private List<QuizResultDTO> recentQuizResults;
    private LocalDateTime lastActivityDate;
    
    // Performance metrics
    private Map<String, Double> subjectWiseGrades;
    private List<String> strengths;
    private List<String> areasForImprovement;
    
    // Behavioral metrics
    private Integer participationScore;
    private String behaviorRating;
    private List<String> achievements;
    private List<String> concerns;
    
    // Parent/Manager visibility
    private boolean visibleToParent;
    private boolean visibleToManager;
    
    // Timestamps
    private LocalDateTime reportGeneratedAt;
    private LocalDateTime lastUpdated;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseProgressDTO {
        private Long courseId;
        private String courseName;
        private String instructorName;
        private Double progressPercentage;
        private Double currentGrade;
        private Integer completedLessons;
        private Integer totalLessons;
        private LocalDateTime lastAccessed;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssignmentSubmissionDTO {
        private Long assignmentId;
        private String assignmentTitle;
        private String courseName;
        private LocalDateTime submittedAt;
        private LocalDateTime dueDate;
        private Double score;
        private String feedback;
        private String status; // SUBMITTED, GRADED, LATE, MISSING
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuizResultDTO {
        private Long quizId;
        private String quizTitle;
        private String courseName;
        private LocalDateTime attemptedAt;
        private Double score;
        private Double maxScore;
        private Double percentage;
        private Integer timeSpent; // in minutes
        private String status; // COMPLETED, IN_PROGRESS, NOT_ATTEMPTED
    }
}