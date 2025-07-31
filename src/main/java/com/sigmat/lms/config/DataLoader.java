package com.sigmat.lms.config;

import com.sigmat.lms.models.*;
import com.sigmat.lms.repository.*;
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
            InstructorProfileService instructorProfileService,
            CourseService courseService,
            CourseModuleService moduleService,
            LessonService lessonService,
            QuizService quizService,
            AssignmentService assignmentService,
            VideoRepo videoRepo,
            VideoService videoService,
            InstituteRepository instituteRepository,
            InstituteService instituteService,
            InstituteSubscriptionRepository instituteSubscriptionRepository,
            CourseAccessControlService courseAccessControlService,
            BatchRepository batchRepository,
            BatchService batchService,
            ParentProfileRepository parentProfileRepository,
            AnnouncementRepository announcementRepository) {
       
        return args -> {
            // Create SuperAdmin User
            Users superAdminUser = null;
            if (userRepo.findByUsername("superadmin") == null) {
                superAdminUser = new Users();
                superAdminUser.setUsername("superadmin");
                superAdminUser.setPassword("superadminpass");
                superAdminUser.setEmail("superadmin@example.com");
                superAdminUser.setFirstName("Super");
                superAdminUser.setLastName("Admin");
                Set<Role> superAdminRoles = new HashSet<>();
                superAdminRoles.add(Role.SUPER_ADMIN);
                superAdminUser.setRoles(superAdminRoles);
                userService.saveUser(superAdminUser);
                superAdminUser.setVerified(true);
                superAdminUser.setVerificationToken(null);
                superAdminUser = userRepo.save(superAdminUser);
                System.out.println("Created super admin user: " + superAdminUser.getUsername());
            }

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
            Users instructorUser = userRepo.findByUsername("instructor");
            if (instructorUser == null) {
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

            // Create Institute Admin User
            Users instituteAdminUser = null;
            if (userRepo.findByUsername("instituteadmin") == null) {
                instituteAdminUser = new Users();
                instituteAdminUser.setUsername("instituteadmin");
                instituteAdminUser.setPassword("instituteadminpass");
                instituteAdminUser.setEmail("instituteadmin@techuniversity.edu");
                instituteAdminUser.setFirstName("Institute");
                instituteAdminUser.setLastName("Admin");
                Set<Role> instituteAdminRoles = new HashSet<>();
                instituteAdminRoles.add(Role.INSTITUTION);
                instituteAdminUser.setRoles(instituteAdminRoles);
                userService.saveUser(instituteAdminUser);
                instituteAdminUser.setVerified(true);
                instituteAdminUser.setVerificationToken(null);
                instituteAdminUser = userRepo.save(instituteAdminUser);
                System.out.println("Created institute admin user: " + instituteAdminUser.getUsername());
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

            // Create additional student users
            Users student1 = null;
            if (userRepo.findByUsername("student1") == null) {
                student1 = new Users();
                student1.setUsername("student1");
                student1.setPassword("student1pass");
                student1.setEmail("student1@techuniversity.edu");
                student1.setFirstName("Alice");
                student1.setLastName("Johnson");
                Set<Role> studentRoles = new HashSet<>();
                studentRoles.add(Role.USER);
                student1.setRoles(studentRoles);
                userService.saveUser(student1);
                student1.setVerified(true);
                student1.setVerificationToken(null);
                student1 = userRepo.save(student1);
                System.out.println("Created student user: " + student1.getUsername());
            }

            Users student2 = null;
            if (userRepo.findByUsername("student2") == null) {
                student2 = new Users();
                student2.setUsername("student2");
                student2.setPassword("student2pass");
                student2.setEmail("student2@businesscollege.edu");
                student2.setFirstName("Bob");
                student2.setLastName("Smith");
                Set<Role> studentRoles = new HashSet<>();
                studentRoles.add(Role.USER);
                student2.setRoles(studentRoles);
                userService.saveUser(student2);
                student2.setVerified(true);
                student2.setVerificationToken(null);
                student2 = userRepo.save(student2);
                System.out.println("Created student user: " + student2.getUsername());
            }
            
            // Create Instructor entity if it doesn't exist
            Instructor instructor = null;
            Optional<Instructor> existingInstructor = instructorRepo.findByFirstName("Instructor");
            if (existingInstructor.isEmpty()) {
                instructor = new Instructor();
                instructor.setUser(instructorUser);
                instructor.setFirstName("Instructor");
                instructor.setLastName("Demo");
                instructor.setEmail("instructor@example.com");
                instructor.setPhoneNo("555-0123");
                instructor.setDateOfJoining(java.time.LocalDate.now());
                instructor.setFacebookHandle("instructordemo");
                instructor.setLinkedinHandle("instructor-demo");
                instructor.setYoutubeHandle("instructordemotech");
                instructor = instructorRepo.save(instructor);
                System.out.println("Created instructor: " + instructor.getFirstName());
                
                // Create instructor profile
                try {
                    InstructorProfile instructorProfile = instructorProfileService.createInstructorProfile(instructor.getInstructorId());
                    
                    // Update profile with additional information
                    instructorProfile.setBio("Experienced instructor with expertise in programming and software development.");
                    instructorProfile.setSpecialization("Software Development");
                    instructorProfile.setAddress("123 Education Street, Tech City, TC 12345");
                    instructorProfile.setTimezone("UTC");
                    instructorProfile.setLanguage("English");
                    instructorProfile.setBankName("Demo Bank");
                    instructorProfile.setAccountNumber("1234567890");
                    instructorProfile.setRoutingNumber("123456789");
                    instructorProfile.setAccountHolderName("Instructor Demo");
                    
                    instructorProfileService.updateInstructorProfile(instructor.getInstructorId(), instructorProfile);
                    System.out.println("Created instructor profile for: " + instructor.getFirstName());
                } catch (Exception e) {
                    System.out.println("Note: Could not create instructor profile - may already exist or service unavailable");
                }
            } else {
                instructor = existingInstructor.get();
            }

            // Create Institutes
            Institute techUniversity = null;
            Optional<Institute> existingTechUniversity = instituteRepository.findByInstituteName("Tech University");
            if (existingTechUniversity.isEmpty()) {
                techUniversity = Institute.builder()
                        .instituteName("Tech University")
                        .instituteCode("TECH001")
                        .description("Leading technology university specializing in computer science and engineering")
                        .address("123 Tech Street")
                        .city("Tech City")
                        .state("California")
                        .country("USA")
                        .postalCode("90210")
                        .email("info@techuniversity.edu")
                        .phoneNumber("+1-555-0100")
                        .website("https://www.techuniversity.edu")
                        .establishedDate(LocalDateTime.of(1995, 9, 1, 0, 0))
                        .admin(instituteAdminUser)
                        .isActive(true)
                        .build();
                techUniversity = instituteRepository.save(techUniversity);
                System.out.println("Created institute: " + techUniversity.getInstituteName());
            } else {
                techUniversity = existingTechUniversity.get();
            }

            Institute businessCollege = null;
            Optional<Institute> existingBusinessCollege = instituteRepository.findByInstituteName("Business College");
            if (existingBusinessCollege.isEmpty()) {
                businessCollege = Institute.builder()
                        .instituteName("Business College")
                        .instituteCode("BIZ001")
                        .description("Premier business education institution")
                        .address("456 Business Ave")
                        .city("Commerce City")
                        .state("New York")
                        .country("USA")
                        .postalCode("10001")
                        .email("info@businesscollege.edu")
                        .phoneNumber("+1-555-0200")
                        .website("https://www.businesscollege.edu")
                        .establishedDate(LocalDateTime.of(1980, 1, 15, 0, 0))
                        .isActive(true)
                        .build();
                businessCollege = instituteRepository.save(businessCollege);
                System.out.println("Created institute: " + businessCollege.getInstituteName());
            } else {
                businessCollege = existingBusinessCollege.get();
            }

            // Assign users to institutes and add institutional attributes
            if (instituteAdminUser != null && instituteAdminUser.getInstitute() == null) {
                instituteAdminUser.setInstitute(techUniversity);
                instituteAdminUser.setStaffId("STAFF001");
                instituteAdminUser.setDepartment("Administration");
                instituteAdminUser.setJobRole("Institute Administrator");
                instituteAdminUser.setPhoneNumber("+1-555-0101");
                instituteAdminUser.setEnrollmentDate(LocalDateTime.now());
                userRepo.save(instituteAdminUser);
                System.out.println("Assigned institute admin to Tech University");
            }

            if (student1 != null && student1.getInstitute() == null) {
                student1.setInstitute(techUniversity);
                student1.setRollNumber("CS2024001");
                student1.setAdmissionId("ADM2024001");
                student1.setSemester("Fall 2024");
                student1.setGrade("Freshman");
                student1.setDivision("A");
                student1.setCourseOfStudy("Computer Science");
                student1.setBatchName("CS 2024 Batch A");
                student1.setPhoneNumber("+1-555-0201");
                student1.setParentContact("+1-555-0301");
                student1.setEmergencyContact("+1-555-0401");
                student1.setEnrollmentDate(LocalDateTime.of(2024, 8, 15, 9, 0));
                userRepo.save(student1);
                System.out.println("Assigned student1 to Tech University with institutional attributes");
            }

            if (student2 != null && student2.getInstitute() == null) {
                student2.setInstitute(businessCollege);
                student2.setRollNumber("BIZ2024001");
                student2.setAdmissionId("ADM2024002");
                student2.setSemester("Fall 2024");
                student2.setGrade("Sophomore");
                student2.setDivision("B");
                student2.setCourseOfStudy("Business Administration");
                student2.setBatchName("BIZ 2024 Batch B");
                student2.setPhoneNumber("+1-555-0202");
                student2.setParentContact("+1-555-0302");
                student2.setEmergencyContact("+1-555-0402");
                student2.setEnrollmentDate(LocalDateTime.of(2024, 8, 20, 9, 0));
                userRepo.save(student2);
                System.out.println("Assigned student2 to Business College with institutional attributes");
            }

            if (instructorUser != null && instructor != null && instructor.getInstitute() == null) {
                instructor.setInstitute(techUniversity);
                instructorRepo.save(instructor);
                
                // Update instructor user with institutional attributes
                instructorUser.setInstitute(techUniversity);
                instructorUser.setStaffId("INST001");
                instructorUser.setDepartment("Computer Science");
                instructorUser.setJobRole("Senior Instructor");
                instructorUser.setPhoneNumber("+1-555-0123");
                instructorUser.setEnrollmentDate(LocalDateTime.now().minusYears(2));
                userRepo.save(instructorUser);
                System.out.println("Assigned instructor to Tech University with institutional attributes");
            }
            
            // Create Institute-specific Course
            Course instituteCourse = null;
            Optional<Course> existingInstituteCourse = courseRepo.findByCourseCode("JAVA101");
            if (existingInstituteCourse.isEmpty()) {
                instituteCourse = new Course();
                instituteCourse.setCourseName("Introduction to Java Programming");
                instituteCourse.setCourseCode("JAVA101");
                instituteCourse.setCourseDescription("A comprehensive introduction to Java programming language and object-oriented concepts.");
                instituteCourse.setCourseDuration(8L); // 8 weeks
                instituteCourse.setCourseMode("Online");
                instituteCourse.setMaxEnrollments(50);
                instituteCourse.setCourseFee(99L);
                instituteCourse.setLanguage("English");
                instituteCourse.setCourseCategory("Programming");
                instituteCourse.setCourseScope(CourseScope.INSTITUTE_ONLY);
                instituteCourse.setInstitute(techUniversity);
                
                // Add instructor to course
                Set<Instructor> instructors = new HashSet<>();
                instructors.add(instructor);
                instituteCourse.setInstructors(instructors);
                
                instituteCourse = courseRepo.save(instituteCourse);
                System.out.println("Created institute course: " + instituteCourse.getCourseName());

                // Create course content for institute course
                createCourseContent(instituteCourse, videoRepo);
            } else {
                instituteCourse = existingInstituteCourse.get();
            }

            // Create Global Course
            Course globalCourse = null;
            Optional<Course> existingGlobalCourse = courseRepo.findByCourseCode("WEB101");
            if (existingGlobalCourse.isEmpty()) {
                globalCourse = new Course();
                globalCourse.setCourseName("Web Development Fundamentals");
                globalCourse.setCourseCode("WEB101");
                globalCourse.setCourseDescription("Learn the fundamentals of web development including HTML, CSS, and JavaScript.");
                globalCourse.setCourseDuration(10L); // 10 weeks
                globalCourse.setCourseMode("Online");
                globalCourse.setMaxEnrollments(100);
                globalCourse.setCourseFee(149L);
                globalCourse.setLanguage("English");
                globalCourse.setCourseCategory("Web Development");
                globalCourse.setCourseScope(CourseScope.GLOBAL);
                // Global courses don't belong to a specific institute
                
                // Add instructor to course
                Set<Instructor> globalInstructors = new HashSet<>();
                globalInstructors.add(instructor);
                globalCourse.setInstructors(globalInstructors);
                
                globalCourse = courseRepo.save(globalCourse);
                System.out.println("Created global course: " + globalCourse.getCourseName());

                // Create course content for global course
                createWebCourseContent(globalCourse, videoRepo);
            } else {
                globalCourse = existingGlobalCourse.get();
            }

            // Create Institute Subscription to Global Course
            if (techUniversity != null && globalCourse != null) {
                boolean subscriptionExists = instituteSubscriptionRepository
                        .existsByInstituteAndCourseAndIsActiveTrue(techUniversity, globalCourse);
                
                if (!subscriptionExists) {
                    try {
                        courseAccessControlService.subscribeInstituteToGlobalCourse(
                                techUniversity.getInstituteId(),
                                globalCourse.getCourseId(),
                                true,  // auto-enroll students
                                true   // auto-enroll instructors
                        );
                        System.out.println("Tech University subscribed to global course: " + globalCourse.getCourseName());
                    } catch (Exception e) {
                        System.out.println("Note: Could not create institute subscription - may already exist");
                    }
                }
            }

            // Create Batches for Institutional Features
            createBatches(batchRepository, techUniversity, businessCollege, instructor, instituteCourse, globalCourse, student1, student2, userRepo);

            // Create Parent Profiles for Educational Institutions
            createParentProfiles(parentProfileRepository, techUniversity, businessCollege, student1, student2);

            // Create Sample Announcements
            createAnnouncements(announcementRepository, techUniversity, businessCollege, instituteAdminUser, instructorUser);

            System.out.println("###############DATALOADER SUCCESS WITH INSTITUTIONAL FEATURES!#####################");
        };
    }

    private void createCourseContent(Course course, VideoRepo videoRepo) {
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
        video.setFilePath("/videos/java-setup.mp4");
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
        
        System.out.println("Created module and lessons for course: " + course.getCourseName());
    }

    private void createWebCourseContent(Course course, VideoRepo videoRepo) {
        // Create a Module for the web development course
        CourseModule module = new CourseModule();
        module.setTitle("Web Development Basics");
        module.setDescription("Learn the fundamentals of HTML, CSS, and JavaScript.");
        module.setModuleOrder(1);
        module.setCourse(course);
        
        // Create an Article Lesson
        ArticleLesson articleLesson = new ArticleLesson();
        articleLesson.setTitle("Introduction to Web Development");
        articleLesson.setLessonOrder(1);
        articleLesson.setCourseModule(module);
        articleLesson.setContent("Web development is the process of creating websites and web applications. It involves both front-end development (what users see and interact with) and back-end development (server-side logic and databases). This course will cover HTML for structure, CSS for styling, and JavaScript for interactivity.");
        
        // Create a Video Lesson
        VideoLesson videoLesson = new VideoLesson();
        videoLesson.setTitle("Setting Up Your Development Environment");
        videoLesson.setLessonOrder(2);
        videoLesson.setCourseModule(module);
        
        // Create Video entity
        Video video = new Video();
        video.setTitle("Web Development Environment Setup");
        video.setDescription("Learn how to set up VS Code, browser dev tools, and other essential tools");
        video.setFilePath("/videos/web-dev-setup.mp4");
        video = videoRepo.save(video);
        
        videoLesson.setVideo(video);
        
        // Create a Quiz
        Quiz quiz = new Quiz();
        quiz.setTitle("Web Development Fundamentals Quiz");
        quiz.setLessonOrder(3);
        quiz.setCourseModule(module);
        
        // Create Questions for the Quiz
        List<Question> questions = new ArrayList<>();
        
        Question question1 = new Question();
        question1.setQuestionText("What does HTML stand for?");
        question1.setQuiz(quiz);
        
        List<AnswerChoice> choices1 = new ArrayList<>();
        
        AnswerChoice choice1a = new AnswerChoice();
        choice1a.setChoiceText("HyperText Markup Language");
        choice1a.setCorrect(true);
        choice1a.setQuestion(question1);
        choices1.add(choice1a);
        
        AnswerChoice choice1b = new AnswerChoice();
        choice1b.setChoiceText("High Tech Modern Language");
        choice1b.setCorrect(false);
        choice1b.setQuestion(question1);
        choices1.add(choice1b);
        
        AnswerChoice choice1c = new AnswerChoice();
        choice1c.setChoiceText("Home Tool Markup Language");
        choice1c.setCorrect(false);
        choice1c.setQuestion(question1);
        choices1.add(choice1c);
        
        question1.setAnswerChoices(choices1);
        questions.add(question1);
        
        Question question2 = new Question();
        question2.setQuestionText("Which language is used for styling web pages?");
        question2.setQuiz(quiz);
        
        List<AnswerChoice> choices2 = new ArrayList<>();
        
        AnswerChoice choice2a = new AnswerChoice();
        choice2a.setChoiceText("HTML");
        choice2a.setCorrect(false);
        choice2a.setQuestion(question2);
        choices2.add(choice2a);
        
        AnswerChoice choice2b = new AnswerChoice();
        choice2b.setChoiceText("CSS");
        choice2b.setCorrect(true);
        choice2b.setQuestion(question2);
        choices2.add(choice2b);
        
        AnswerChoice choice2c = new AnswerChoice();
        choice2c.setChoiceText("JavaScript");
        choice2c.setCorrect(false);
        choice2c.setQuestion(question2);
        choices2.add(choice2c);
        
        question2.setAnswerChoices(choices2);
        questions.add(question2);
        
        quiz.setQuestions(questions);
        
        // Create an Assignment
        Assignment assignment = new Assignment();
        assignment.setTitle("Create Your First Web Page");
        assignment.setLessonOrder(4);
        assignment.setCourseModule(module);
        assignment.setDescription("Create a simple HTML page with basic styling using CSS. Include a header, paragraph, and at least one image.");
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
        
        System.out.println("Created module and lessons for course: " + course.getCourseName());
    }

    private void createBatches(BatchRepository batchRepository, Institute techUniversity, Institute businessCollege, 
                              Instructor instructor, Course instituteCourse, Course globalCourse, 
                              Users student1, Users student2, UserRepo userRepo) {
        try {
            // Create Computer Science Batch for Tech University
            Optional<Batch> existingCSBatch = batchRepository.findByBatchCode("CS2024A");
            if (existingCSBatch.isEmpty()) {
                Batch csBatch = Batch.builder()
                        .batchName("Computer Science 2024 - Section A")
                        .batchCode("CS2024A")
                        .description("First year computer science students - Fall 2024 intake")
                        .institute(techUniversity)
                        .instructor(instructor)
                        .course(instituteCourse)
                        .startDate(LocalDateTime.of(2024, 9, 1, 9, 0))
                        .endDate(LocalDateTime.of(2024, 12, 15, 17, 0))
                        .maxStudents(30)
                        .semester("Fall 2024")
                        .academicYear("2024-2025")
                        .grade("Freshman")
                        .division("A")
                        .status(Batch.BatchStatus.ACTIVE)
                        .isActive(true)
                        .build();
                csBatch = batchRepository.save(csBatch);
                
                // Assign student1 to this batch
                if (student1 != null) {
                    student1.setBatch(csBatch);
                    userRepo.save(student1);
                }
                
                System.out.println("Created CS batch: " + csBatch.getBatchName());
            }

            // Create Business Batch for Business College
            Optional<Batch> existingBizBatch = batchRepository.findByBatchCode("BIZ2024B");
            if (existingBizBatch.isEmpty()) {
                Batch bizBatch = Batch.builder()
                        .batchName("Business Administration 2024 - Section B")
                        .batchCode("BIZ2024B")
                        .description("Second year business administration students - Fall 2024")
                        .institute(businessCollege)
                        .course(globalCourse)
                        .startDate(LocalDateTime.of(2024, 9, 5, 9, 0))
                        .endDate(LocalDateTime.of(2024, 12, 20, 17, 0))
                        .maxStudents(25)
                        .semester("Fall 2024")
                        .academicYear("2024-2025")
                        .grade("Sophomore")
                        .division("B")
                        .status(Batch.BatchStatus.ACTIVE)
                        .isActive(true)
                        .build();
                bizBatch = batchRepository.save(bizBatch);
                
                // Assign student2 to this batch
                if (student2 != null) {
                    student2.setBatch(bizBatch);
                    userRepo.save(student2);
                }
                
                System.out.println("Created Business batch: " + bizBatch.getBatchName());
            }
        } catch (Exception e) {
            System.out.println("Note: Could not create batches - " + e.getMessage());
        }
    }

    private void createParentProfiles(ParentProfileRepository parentProfileRepository, Institute techUniversity, 
                                    Institute businessCollege, Users student1, Users student2) {
        try {
            // Create parent profile for student1
            if (student1 != null && !parentProfileRepository.existsByParentEmail("alice.parent@example.com")) {
                ParentProfile parent1 = ParentProfile.builder()
                        .parentName("John Johnson")
                        .parentEmail("alice.parent@example.com")
                        .phoneNumber("+1-555-0301")
                        .relationship("Father")
                        .institute(techUniversity)
                        .canViewReports(true)
                        .canViewAttendance(true)
                        .canViewGrades(true)
                        .canViewAnnouncements(true)
                        .canReceiveNotifications(true)
                        .emailNotifications(true)
                        .smsNotifications(false)
                        .preferredLanguage("en")
                        .isActive(true)
                        .build();
                parent1 = parentProfileRepository.save(parent1);
                
                // Link student to parent
                student1.setParentProfile(parent1);
                
                System.out.println("Created parent profile for student1: " + parent1.getParentName());
            }

            // Create parent profile for student2
            if (student2 != null && !parentProfileRepository.existsByParentEmail("bob.parent@example.com")) {
                ParentProfile parent2 = ParentProfile.builder()
                        .parentName("Mary Smith")
                        .parentEmail("bob.parent@example.com")
                        .phoneNumber("+1-555-0302")
                        .relationship("Mother")
                        .institute(businessCollege)
                        .canViewReports(true)
                        .canViewAttendance(true)
                        .canViewGrades(true)
                        .canViewAnnouncements(true)
                        .canReceiveNotifications(true)
                        .emailNotifications(true)
                        .smsNotifications(true)
                        .preferredLanguage("en")
                        .isActive(true)
                        .build();
                parent2 = parentProfileRepository.save(parent2);
                
                // Link student to parent
                student2.setParentProfile(parent2);
                
                System.out.println("Created parent profile for student2: " + parent2.getParentName());
            }
        } catch (Exception e) {
            System.out.println("Note: Could not create parent profiles - " + e.getMessage());
        }
    }

    private void createAnnouncements(AnnouncementRepository announcementRepository, Institute techUniversity, 
                                   Institute businessCollege, Users instituteAdminUser, Users instructorUser) {
        try {
            // Create welcome announcement for Tech University
            if (techUniversity != null && instituteAdminUser != null) {
                Announcement welcomeAnnouncement = Announcement.builder()
                        .title("Welcome to Fall 2024 Semester!")
                        .content("Welcome to the Fall 2024 semester at Tech University! We're excited to have you join us for another year of learning and growth. Please check your course schedules and prepare for an amazing semester ahead.")
                        .type(Announcement.AnnouncementType.GENERAL)
                        .priority(Announcement.AnnouncementPriority.HIGH)
                        .author(instituteAdminUser)
                        .institute(techUniversity)
                        .targetRoles(Set.of(Role.USER, Role.INSTRUCTOR))
                        .publishDate(LocalDateTime.now().minusDays(1))
                        .expiryDate(LocalDateTime.now().plusDays(30))
                        .isPublished(true)
                        .isPinned(true)
                        .sendNotification(true)
                        .sendEmail(true)
                        .isActive(true)
                        .build();
                announcementRepository.save(welcomeAnnouncement);
                System.out.println("Created welcome announcement for Tech University");
            }

            // Create exam announcement for instructors
            if (techUniversity != null && instructorUser != null) {
                Announcement examAnnouncement = Announcement.builder()
                        .title("Mid-term Examination Schedule")
                        .content("Mid-term examinations will be conducted from October 15-20, 2024. Please check your individual course schedules for specific dates and times. Good luck with your preparations!")
                        .type(Announcement.AnnouncementType.EXAM)
                        .priority(Announcement.AnnouncementPriority.URGENT)
                        .author(instructorUser)
                        .institute(techUniversity)
                        .targetRoles(Set.of(Role.USER))
                        .publishDate(LocalDateTime.now())
                        .expiryDate(LocalDateTime.of(2024, 10, 21, 23, 59))
                        .isPublished(true)
                        .isPinned(false)
                        .sendNotification(true)
                        .sendEmail(true)
                        .isActive(true)
                        .build();
                announcementRepository.save(examAnnouncement);
                System.out.println("Created exam announcement for Tech University");
            }

            // Create holiday announcement for Business College
            if (businessCollege != null && instituteAdminUser != null) {
                Announcement holidayAnnouncement = Announcement.builder()
                        .title("Thanksgiving Break Notice")
                        .content("The college will be closed for Thanksgiving break from November 25-29, 2024. Classes will resume on Monday, December 2nd. Have a wonderful holiday with your families!")
                        .type(Announcement.AnnouncementType.HOLIDAY)
                        .priority(Announcement.AnnouncementPriority.MEDIUM)
                        .author(instituteAdminUser)
                        .institute(businessCollege)
                        .targetRoles(Set.of(Role.USER, Role.INSTRUCTOR))
                        .publishDate(LocalDateTime.now())
                        .expiryDate(LocalDateTime.of(2024, 12, 2, 23, 59))
                        .isPublished(true)
                        .isPinned(false)
                        .sendNotification(true)
                        .sendEmail(false)
                        .isActive(true)
                        .build();
                announcementRepository.save(holidayAnnouncement);
                System.out.println("Created holiday announcement for Business College");
            }
        } catch (Exception e) {
            System.out.println("Note: Could not create announcements - " + e.getMessage());
        }
    }
}
