package com.sigmat.lms.services;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.sigmat.lms.dtos.EnrollmentDTO;
import com.sigmat.lms.dtos.InstructorRegistrationDTO;
import com.sigmat.lms.dtos.SubscriptionRequestDTO;
import com.sigmat.lms.dtos.UserDTO;
import com.sigmat.lms.exceptions.DuplicateEmailException;
import com.sigmat.lms.models.Instructor;
import com.sigmat.lms.models.InstructorProfile;
import com.sigmat.lms.models.Role;
import com.sigmat.lms.models.UserProfile;
import com.sigmat.lms.models.Users;
import com.sigmat.lms.repository.UserProfileRepo;
import com.sigmat.lms.repository.UserRepo;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.logging.Logger;

@Service
public class UserService {

    private final UserRepo userRepository;
    private final UserProfileRepo userProfileRepository;
    private final JwtService jwtUtil;
    private final EmailService emailService; 
    private final EnrollmentService enrollmentService; // Inject EnrollmentService
    private final InstructorService instructorService; // Inject InstructorService
    private final InstructorProfileService instructorProfileService; // Inject InstructorProfileService
    private final SubscriptionService subscriptionService; // Inject SubscriptionService
    private PasswordEncoder passwordEncoder;
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());

    private final Set<String> invalidatedTokens = new HashSet<>();

    public UserService(UserRepo userRepository, UserProfileRepo userProfileRepository, JwtService jwtUtil, EmailService emailService, PasswordEncoder passwordEncoder, EnrollmentService enrollmentService, InstructorService instructorService, InstructorProfileService instructorProfileService, SubscriptionService subscriptionService) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService; 
        this.passwordEncoder = passwordEncoder;
        this.enrollmentService = enrollmentService; // Initialize EnrollmentService
        this.instructorService = instructorService; // Initialize InstructorService
        this.instructorProfileService = instructorProfileService; // Initialize InstructorProfileService
        this.subscriptionService = subscriptionService; // Initialize SubscriptionService
    }

    public boolean validateUser(String username, String password) {
        Users user = userRepository.findByUsername(username);
        return user != null && passwordEncoder.matches(password, user.getPassword());
    }

    public String generateToken(String username) {
        return jwtUtil.generateToken(username);
    }

    @Transactional
    public void saveUser(Users user) throws DuplicateEmailException {
        try {
            // Check if email already exists before attempting to save
            if (userRepository.existsByEmail(user.getEmail())) {
                LOGGER.warning("Attempted registration with existing email: " + user.getEmail());
                throw new DuplicateEmailException(user.getEmail());
            }
            
            // Check if username already exists
            if (userRepository.existsByUsername(user.getUsername())) {
                LOGGER.warning("Attempted registration with existing username: " + user.getUsername());
                throw new RuntimeException("This username is already taken. Please choose a different username.");
            }
            
            String verificationToken = UUID.randomUUID().toString();
            user.setVerificationToken(verificationToken);
            user.setVerified(false);
            
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            LOGGER.info("Saving user: " + user.getUsername() + ", First Name: " + user.getFirstName() + ", Last Name: " + user.getLastName());

            Users savedUser = userRepository.save(user);

            UserProfile userProfile = userProfileRepository.findByUsers(savedUser);
            if (userProfile == null) {
                userProfile = new UserProfile();
                userProfile.setUsers(savedUser);
            }
            userProfile.setEmail(savedUser.getEmail());
            userProfile.setFirstName(savedUser.getFirstName());
            userProfile.setLastName(savedUser.getLastName());
            userProfile.setLanguage("en");
            userProfile.setTimezone("UTC");
            userProfile.setPassword(savedUser.getPassword());

            userProfileRepository.save(userProfile);

            emailService.sendVerificationEmail(savedUser.getEmail(), verificationToken);

            LOGGER.info("User registered successfully: " + savedUser.getUsername());
            
        } catch (DuplicateEmailException e) {
            throw e; // Re-throw custom exception
            
        } catch (DataIntegrityViolationException e) {
            LOGGER.severe("Database constraint violation while saving user: " + e.getMessage());
            String constraintMessage = handleDatabaseConstraintViolation(e);
            throw new RuntimeException(constraintMessage);
            
        } catch (ObjectOptimisticLockingFailureException e) {
            LOGGER.warning("User was updated by another transaction: " + e.getMessage());
            throw new RuntimeException("Registration failed due to a conflict. Please try again.");
            
        } catch (Exception e) {
            LOGGER.severe("Unexpected error saving user: " + e.getMessage());
            throw new RuntimeException("An unexpected error occurred during registration. Please try again.");
        }
    }

    private String handleDatabaseConstraintViolation(DataIntegrityViolationException e) {
        String errorMessage = e.getMessage();
        
        if (errorMessage != null && errorMessage.contains("users_email_key")) {
            return "This email address is already registered in our system.";
        }
        if (errorMessage != null && errorMessage.contains("users_username_key")) {
            return "This username is already taken. Please choose a different username.";
        }
        
        return "This information is already registered in our system. Please check your details.";
    }

    public List<UserDTO> getAllUsers() {
        List<Users> users = userRepository.findAll();
        List<UserDTO> userDTOs = new ArrayList<>();
        for (Users user : users) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setUsername(user.getUsername());
            userDTO.setEmail(user.getEmail());
            userDTO.setFirstName(user.getFirstName());
            userDTO.setLastName(user.getLastName());
            userDTO.setRoles(user.getRoles()); // Map roles
            userDTOs.add(userDTO);
        }
        return userDTOs;
    }

    @Transactional
    public void deleteUserByUsername(String username) {
        Users user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found: " + username);
        }

        // Delete associated enrollments first
        List<EnrollmentDTO> enrollments = enrollmentService.getEnrollmentsByUserId(user.getId());
        enrollments.forEach(enrollment -> enrollmentService.deleteEnrollment(enrollment.getId()));

        UserProfile userProfile = userProfileRepository.findByUsers(user);
        if (userProfile != null) {
            userProfileRepository.delete(userProfile);
        }

        userRepository.delete(user);
        LOGGER.info("User deleted: " + username);
    }

    public boolean invalidateToken(String token) {
        return invalidatedTokens.add(token);
    }

    public boolean isTokenInvalidated(String token) {
        return invalidatedTokens.contains(token);
    }

    public List<Users> processUserFileBatchCreate(MultipartFile file) {
        List<Users> users = new ArrayList<>();
        try {
            if (file.getOriginalFilename().endsWith(".csv")) {
                try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
                    String[] values;

                    while ((values = csvReader.readNext()) != null) {
                        Users user = new Users();
                        user.setUsername(values[0]);
                        user.setPassword(values[1]);
                        user.setEmail(values[2]);
                        user.setFirstName(values[3]);
                        user.setLastName(values[4]);
                        user.getRoles().add(Role.USER);

                        String verificationToken = UUID.randomUUID().toString();
                        user.setVerificationToken(verificationToken);
                        user.setVerified(false);

                        users.add(user);
                    }
                }
            } else if (file.getOriginalFilename().endsWith(".xlsx") || file.getOriginalFilename().endsWith(".xls")) {
                try (InputStream inputStream = file.getInputStream()) {
                    Workbook workbook = WorkbookFactory.create(inputStream);
                    Sheet sheet = workbook.getSheetAt(0);

                    for (Row row : sheet) {
                        Users user = new Users();
                        user.setUsername(row.getCell(0).getStringCellValue());
                        user.setPassword(row.getCell(1).getStringCellValue());
                        user.setEmail(row.getCell(2).getStringCellValue());
                        user.setFirstName(row.getCell(3).getStringCellValue());
                        user.setLastName(row.getCell(4).getStringCellValue());
                        user.getRoles().add(Role.USER);

                        String verificationToken = UUID.randomUUID().toString();
                        user.setVerificationToken(verificationToken);
                        user.setVerified(false);

                        users.add(user);
                    }
                }
            } else {
                throw new IllegalArgumentException("Invalid file type. Please upload a CSV or Excel file.");
            }

            for (Users user : users) {
                saveUser(user);
            }
        } catch (Exception e) {
            LOGGER.severe("Error processing user file: " + e.getMessage());
            throw new RuntimeException("Failed to process user file: " + e.getMessage());
        }
        return users;
    }

    public List<String> readUsernamesFromCSV(MultipartFile file) throws IOException {
        List<String> usernames = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                usernames.add(nextLine[0]);
            }
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
        return usernames;
    }

    public List<String> readUsernamesFromExcel(MultipartFile file) throws IOException {
        List<String> usernames = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getCell(0) != null) {
                    usernames.add(row.getCell(0).getStringCellValue());
                }
            }
        }
        return usernames;
    }

    @Transactional
    public boolean verifyUser(String verificationToken) {
        Users user = userRepository.findByVerificationToken(verificationToken);
        if (user == null) {
            return false;
        }

        user.setVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);

        return true;
    }

    public Users getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public Users findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public void updateUserRole(Long userId, String newRole) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        try {
            Role role = Role.valueOf(newRole.toUpperCase());
            user.getRoles().clear();
            user.getRoles().add(role);
            userRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role specified: " + newRole);
        }
    }

    public List<UserDTO> getUsersByRole(Role role) {
        List<Users> users = userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(role))
                .toList();
        
        List<UserDTO> userDTOs = new ArrayList<>();
        for (Users user : users) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setUsername(user.getUsername());
            userDTO.setEmail(user.getEmail());
            userDTO.setFirstName(user.getFirstName());
            userDTO.setLastName(user.getLastName());
            userDTO.setRoles(user.getRoles());
            userDTOs.add(userDTO);
        }
        return userDTOs;
    }

    public void deleteUserById(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
        } else {
            throw new RuntimeException("User not found with id: " + userId);
        }
    }

    public Map<String, Object> getSystemStats() {
        long totalUsers = userRepository.count();
        long adminCount = userRepository.findAll().stream()
                .mapToLong(user -> user.getRoles().contains(Role.ADMIN) ? 1 : 0)
                .sum();
        long superAdminCount = userRepository.findAll().stream()
                .mapToLong(user -> user.getRoles().contains(Role.SUPER_ADMIN) ? 1 : 0)
                .sum();
        long instructorCount = userRepository.findAll().stream()
                .mapToLong(user -> user.getRoles().contains(Role.INSTRUCTOR) ? 1 : 0)
                .sum();
        long regularUserCount = userRepository.findAll().stream()
                .mapToLong(user -> user.getRoles().contains(Role.USER) && 
                                  !user.getRoles().contains(Role.ADMIN) && 
                                  !user.getRoles().contains(Role.SUPER_ADMIN) && 
                                  !user.getRoles().contains(Role.INSTRUCTOR) ? 1 : 0)
                .sum();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("adminCount", adminCount);
        stats.put("superAdminCount", superAdminCount);
        stats.put("instructorCount", instructorCount);
        stats.put("regularUserCount", regularUserCount);
        stats.put("timestamp", java.time.LocalDateTime.now());
        
        return stats;
    }

    public List<EnrollmentDTO> getUserEnrollments(Long userId) {
        return enrollmentService.getEnrollmentsByUserId(userId);
    }

    @Transactional
    public void requestPasswordReset(String email) {
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found with email: " + email);
        }

        String token = UUID.randomUUID().toString();
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiration(System.currentTimeMillis() + 3600000); // 1 hour
        userRepository.save(user);

        emailService.sendPasswordResetEmail(email, token);
    }

    @Transactional
    public void resetPassword(String email, String token, String newPassword) {
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found with email: " + email);
        }

        if (user.getPasswordResetToken() == null || !user.getPasswordResetToken().equals(token)) {
            throw new RuntimeException("Invalid password reset token.");
        }

        if (System.currentTimeMillis() > user.getPasswordResetTokenExpiration()) {
            throw new RuntimeException("Password reset token has expired.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiration(null);
        userRepository.save(user);
    }

    @Transactional
    public void registerInstructor(InstructorRegistrationDTO instructorDTO) throws DuplicateEmailException {
        try {
            // Check for duplicate email before creating user
            if (userRepository.existsByEmail(instructorDTO.getEmail())) {
                LOGGER.warning("Attempted instructor registration with existing email: " + instructorDTO.getEmail());
                throw new DuplicateEmailException(instructorDTO.getEmail());
            }
            
            // Check for duplicate username
            if (userRepository.existsByUsername(instructorDTO.getUsername())) {
                LOGGER.warning("Attempted instructor registration with existing username: " + instructorDTO.getUsername());
                throw new RuntimeException("This username is already taken. Please choose a different username.");
            }
            
            Users newUser = new Users();
            newUser.setUsername(instructorDTO.getUsername());
            newUser.setPassword(instructorDTO.getPassword()); 
            newUser.setEmail(instructorDTO.getEmail());
            newUser.setFirstName(instructorDTO.getFirstName());
            newUser.setLastName(instructorDTO.getLastName());
            newUser.getRoles().add(Role.INSTRUCTOR);

            saveUser(newUser); 

            Instructor instructor = new Instructor();
            instructor.setUser(newUser);
            instructor.setFirstName(instructorDTO.getFirstName());
            instructor.setLastName(instructorDTO.getLastName());
            instructor.setEmail(instructorDTO.getEmail());
            instructor.setPhoneNo(instructorDTO.getPhoneNo());
            instructor.setDateOfJoining(java.time.LocalDate.now());

            Instructor savedInstructor = instructorService.saveInstructor(instructor);

            // Create instructor profile
            InstructorProfile instructorProfile = instructorProfileService.createInstructorProfile(savedInstructor.getInstructorId());
            
            // Update profile with banking information
            instructorProfile.setBankName(instructorDTO.getBankName());
            instructorProfile.setAccountNumber(instructorDTO.getAccountNumber());
            instructorProfile.setRoutingNumber(instructorDTO.getRoutingNumber());
            instructorProfile.setAccountHolderName(instructorDTO.getAccountHolderName());
            
            instructorProfileService.updateInstructorProfile(savedInstructor.getInstructorId(), instructorProfile);

            // Auto-assign to faculty Free Tier
            SubscriptionRequestDTO subscriptionRequest = new SubscriptionRequestDTO();
            subscriptionRequest.setPlanId(6L);
            subscriptionRequest.setDurationMonths(1);
            subscriptionRequest.setAutoRenew(false);
            subscriptionRequest.setPaymentReference("auto_assigned_faculty_plan");

            subscriptionService.subscribeUser(newUser.getId(), subscriptionRequest);
            
        } catch (DuplicateEmailException e) {
            throw e; // Re-throw custom exception
            
        } catch (DataIntegrityViolationException e) {
            LOGGER.severe("Database constraint violation during instructor registration: " + e.getMessage());
            String constraintMessage = handleDatabaseConstraintViolation(e);
            throw new RuntimeException(constraintMessage);
            
        } catch (Exception e) {
            LOGGER.severe("Unexpected error during instructor registration: " + e.getMessage());
            throw new RuntimeException("An unexpected error occurred during instructor registration. Please try again.");
        }
    }
}