package com.sigmat.lms.config;

import com.sigmat.lms.models.*;
import com.sigmat.lms.repo.CourseRepo;
import com.sigmat.lms.repo.InstructorRepo;
import com.sigmat.lms.repo.UserRepo;
import com.sigmat.lms.repo.VideoRepo;
import com.sigmat.lms.services.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(
            UserRepo userRepo, 
            PasswordEncoder passwordEncoder, 
            UserService userService,
            CourseRepo courseRepo,
            InstructorRepo instructorRepo,
            CourseService courseService,
            CourseModuleService moduleService,
            LessonService lessonService,
            QuizService quizService,
            AssignmentService assignmentService,
            VideoRepo videoRepo,
            VideoService videoService) {
       
        return args -> {
            // Create Admin User
            Users adminUser = null;
            if (userRepo.findByUsername("admin") == null) {
                adminUser = new Users();
                adminUser.setUsername("admin");
                adminUser.setPassword("adminpass");
                adminUser.setEmail("admin@example.com");
                adminUser.setFirstName("Admin");
                adminUser.setLastName("User");
                Set<Role> adminRoles = new HashSet<>();
                adminRoles.add(Role.ADMIN);
                adminUser.setRoles(adminRoles);
                userService.saveUser(adminUser);
                adminUser.setVerified(true);
                adminUser.setVerificationToken(null);
                adminUser = userRepo.save(adminUser);
                System.out.println("Created admin user: " + adminUser.getUsername());
            }

            // Create Instructor User
            Users instructorUser = null;
            if (userRepo.findByUsername("instructor") == null) {
                instructorUser = new Users();
                instructorUser.setUsername("instructor");
                instructorUser.setPassword("instructorpass");
                instructorUser.setEmail("instructor@example.com");
                instructorUser.setFirstName("Instructor");
                instructorUser.setLastName("User");
                Set<Role> instructorRoles = new HashSet<>();
                instructorRoles.add(Role.INSTRUCTOR);
                instructorUser.setRoles(instructorRoles);
                userService.saveUser(instructorUser);
                instructorUser.setVerified(true);
                instructorUser.setVerificationToken(null);
                instructorUser = userRepo.save(instructorUser);
                System.out.println("Created instructor user: " + instructorUser.getUsername());
            }

            // Create Regular User
            Users regularUser = null;
            if (userRepo.findByUsername("user") == null) {
                regularUser = new Users();
                regularUser.setUsername("user");
                regularUser.setPassword("userpass");
                regularUser.setEmail("user@example.com");
                regularUser.setFirstName("Regular");
                regularUser.setLastName("User");
                Set<Role> userRoles = new HashSet<>();
                userRoles.add(Role.USER);
                regularUser.setRoles(userRoles);
                userService.saveUser(regularUser);
                regularUser.setVerified(true);
                regularUser.setVerificationToken(null);
                regularUser = userRepo.save(regularUser);
                System.out.println("Created regular user: " + regularUser.getUsername());
            }
            
            // Create Instructor entity if it doesn't exist
            Instructor instructor = null;
            Optional<Instructor> existingInstructor = instructorRepo.findByFirstName("Instructor");
            if (existingInstructor.isEmpty()) {
                instructor = new Instructor();
                instructor.setFirstName("Instructor");
                instructor.setLastName("Demo");
                instructor.setEmail("instructor@example.com");
               // instructor.setBio("Experienced instructor with expertise in programming and software development.");
              //  instructor.setSpecialization("Software Development");
                instructor = instructorRepo.save(instructor);
                System.out.println("Created instructor: " + instructor.getFirstName());
            } else {
                instructor = existingInstructor.get();
            }
            
            // Create a sample Course if it doesn't exist
            Course course = null;
            Optional<Course> existingCourse = courseRepo.findByCourseCode("JAVA101");
            if (existingCourse.isEmpty()) {
                course = new Course();
                course.setCourseName("Introduction to Java Programming");
                course.setCourseCode("JAVA101");
                course.setCourseDescription("A comprehensive introduction to Java programming language and object-oriented concepts.");
                course.setCourseDuration(8L); // 8 weeks
                course.setCourseMode("Online");
                course.setMaxEnrollments(50);
                course.setCourseFee(99L);
                course.setLanguage("English");
                course.setCourseCategory("Programming");
                
                // Add instructor to course
                Set<Instructor> instructors = new HashSet<>();
                instructors.add(instructor);
                course.setInstructors(instructors);
                
                course = courseRepo.save(course);
                System.out.println("Created course: " + course.getCourseName());
                
                // Create a Module for the course
                CourseModule module = new CourseModule();
                module.setTitle("Getting Started with Java");
                module.setDescription("Learn the basics of Java programming language and environment setup.");
                module.setModuleOrder(1);
                module.setCourse(course);
                
                // Create an Article Lesson
                ArticleLesson articleLesson = new ArticleLesson();
                articleLesson.setTitle("Introduction to Java");
                articleLesson.setLessonOrder(1);
                articleLesson.setCourseModule(module);
                articleLesson.setContent("Java is a high-level, class-based, object-oriented programming language that is designed to have as few implementation dependencies as possible. It is a general-purpose programming language intended to let application developers write once, run anywhere (WORA), meaning that compiled Java code can run on all platforms that support Java without the need for recompilation.");
                
                // Create a Video Lesson
                VideoLesson videoLesson = new VideoLesson();
                videoLesson.setTitle("Setting Up Java Development Environment");
                videoLesson.setLessonOrder(2);
                videoLesson.setCourseModule(module);
                
                // Create Video entity
                Video video = new Video();
                video.setTitle("Java Environment Setup");
                video.setDescription("Step-by-step guide to set up Java development environment");
                video.setFilePath("/videos/example.mp4");
                video = videoRepo.save(video);
                
                videoLesson.setVideo(video);
                
                // Create a Quiz
                Quiz quiz = new Quiz();
                quiz.setTitle("Java Basics Quiz");
                quiz.setLessonOrder(3);
                quiz.setCourseModule(module);
                
                // Create Questions for the Quiz
                List<Question> questions = new ArrayList<>();
                
                Question question1 = new Question();
                question1.setQuestionText("What is the main method signature in Java?");
                question1.setQuiz(quiz);
                
                List<AnswerChoice> choices1 = new ArrayList<>();
                
                AnswerChoice choice1a = new AnswerChoice();
                choice1a.setChoiceText("public static void main(String[] args)");
                choice1a.setCorrect(true);
                choice1a.setQuestion(question1);
                choices1.add(choice1a);
                
                AnswerChoice choice1b = new AnswerChoice();
                choice1b.setChoiceText("public void main(String args[])");
                choice1b.setCorrect(false);
                choice1b.setQuestion(question1);
                choices1.add(choice1b);
                
                AnswerChoice choice1c = new AnswerChoice();
                choice1c.setChoiceText("static void main(String[] args)");
                choice1c.setCorrect(false);
                choice1c.setQuestion(question1);
                choices1.add(choice1c);
                
                question1.setAnswerChoices(choices1);
                questions.add(question1);
                
                Question question2 = new Question();
                question2.setQuestionText("Which of the following is not a primitive data type in Java?");
                question2.setQuiz(quiz);
                
                List<AnswerChoice> choices2 = new ArrayList<>();
                
                AnswerChoice choice2a = new AnswerChoice();
                choice2a.setChoiceText("int");
                choice2a.setCorrect(false);
                choice2a.setQuestion(question2);
                choices2.add(choice2a);
                
                AnswerChoice choice2b = new AnswerChoice();
                choice2b.setChoiceText("String");
                choice2b.setCorrect(true);
                choice2b.setQuestion(question2);
                choices2.add(choice2b);
                
                AnswerChoice choice2c = new AnswerChoice();
                choice2c.setChoiceText("boolean");
                choice2c.setCorrect(false);
                choice2c.setQuestion(question2);
                choices2.add(choice2c);
                
                question2.setAnswerChoices(choices2);
                questions.add(question2);
                
                quiz.setQuestions(questions);
                
                // Create an Assignment
                Assignment assignment = new Assignment();
                assignment.setTitle("Java Hello World Program");
                assignment.setLessonOrder(4);
                assignment.setCourseModule(module);
                assignment.setDescription("Create a simple Java program that prints 'Hello, World!' to the console.");
                assignment.setDueDate(LocalDateTime.now().plusDays(7));
                assignment.setMaxPoints(100);
                
                // Save the module with its lessons
                List<Lesson> lessons = new ArrayList<>();
                lessons.add(articleLesson);
                lessons.add(videoLesson);
                lessons.add(quiz);
                lessons.add(assignment);
                module.setLessons(lessons);
                
                List<CourseModule> modules = new ArrayList<>();
                modules.add(module);
                course.setModules(modules);
                
                courseRepo.save(course);
                System.out.println("Created module and lessons for course: " + course.getCourseName());
            }
        };
    }
}
