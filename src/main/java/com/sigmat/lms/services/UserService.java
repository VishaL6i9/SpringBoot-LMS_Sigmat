package com.sigmat.lms.services;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.sigmat.lms.dtos.EnrollmentDTO;
import com.sigmat.lms.dtos.InstructorRegistrationDTO;
import com.sigmat.lms.dtos.SubscriptionRequestDTO;
import com.sigmat.lms.dtos.UserDTO;
import com.sigmat.lms.exceptions.DuplicateEmailException;
import com.sigmat.lms.models.*;
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

    @Transactional
    public BatchProcessResult processUserFileBatchCreate(MultipartFile file) {
        List<Users> successfulUsers = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        Set<String> processedEmails = new HashSet<>();
        Set<String> processedUsernames = new HashSet<>();
        
        try {
            List<UserBatchData> userDataList = parseUserFile(file);
            
            if (userDataList.isEmpty()) {
                throw new IllegalArgumentException("File contains no valid user data");
            }
            
            LOGGER.info("Processing batch of " + userDataList.size() + " users");
            
            for (int i = 0; i < userDataList.size(); i++) {
                UserBatchData userData = userDataList.get(i);
                int rowNumber = i + 1;
                
                try {
                    // Validate user data
                    String validationError = validateUserData(userData, rowNumber);
                    if (validationError != null) {
                        errors.add(validationError);
                        continue;
                    }
                    
                    // Check for duplicates within the batch
                    if (processedEmails.contains(userData.email.toLowerCase())) {
                        errors.add("Row " + rowNumber + ": Duplicate email within batch - " + userData.email);
                        continue;
                    }
                    if (processedUsernames.contains(userData.username.toLowerCase())) {
                        errors.add("Row " + rowNumber + ": Duplicate username within batch - " + userData.username);
                        continue;
                    }
                    
                    // Check for existing users in database
                    if (userRepository.existsByEmail(userData.email)) {
                        errors.add("Row " + rowNumber + ": Email already exists - " + userData.email);
                        continue;
                    }
                    if (userRepository.existsByUsername(userData.username)) {
                        errors.add("Row " + rowNumber + ": Username already exists - " + userData.username);
                        continue;
                    }
                    
                    // Create and save user using the existing saveUser method
                    Users user = createUserFromBatchData(userData);
                    saveUser(user); // This handles encoding, validation, profile creation, and email sending
                    
                    successfulUsers.add(user);
                    processedEmails.add(userData.email.toLowerCase());
                    processedUsernames.add(userData.username.toLowerCase());
                    
                    LOGGER.info("Successfully processed user: " + userData.username + " (row " + rowNumber + ")");
                    
                } catch (Exception e) {
                    String errorMsg = "Row " + rowNumber + " (" + userData.username + "): " + e.getMessage();
                    errors.add(errorMsg);
                    LOGGER.warning("Failed to process user at row " + rowNumber + ": " + e.getMessage());
                }
            }
            
            LOGGER.info("Batch processing completed. Success: " + successfulUsers.size() + ", Errors: " + errors.size());
            return new BatchProcessResult(successfulUsers, errors);
            
        } catch (Exception e) {
            LOGGER.severe("Critical error during batch processing: " + e.getMessage());
            throw new RuntimeException("Failed to process user file: " + e.getMessage(), e);
        }
    }
    
    private List<UserBatchData> parseUserFile(MultipartFile file) throws IOException {
        List<UserBatchData> userDataList = new ArrayList<>();
        String filename = file.getOriginalFilename();
        
        if (filename == null) {
            throw new IllegalArgumentException("File name is null");
        }
        
        if (filename.toLowerCase().endsWith(".csv")) {
            userDataList = parseCSVFile(file);
        } else if (filename.toLowerCase().endsWith(".xlsx") || filename.toLowerCase().endsWith(".xls")) {
            userDataList = parseExcelFile(file);
        } else {
            throw new IllegalArgumentException("Invalid file type. Please upload a CSV or Excel file.");
        }
        
        return userDataList;
    }
    
    private List<UserBatchData> parseCSVFile(MultipartFile file) throws IOException {
        List<UserBatchData> userDataList = new ArrayList<>();
        
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] values;
            boolean isFirstRow = true;
            
            while ((values = csvReader.readNext()) != null) {
                // Skip header row if it looks like headers
                if (isFirstRow && isHeaderRow(values)) {
                    isFirstRow = false;
                    continue;
                }
                isFirstRow = false;
                
                // Skip empty rows
                if (isEmptyRow(values)) {
                    continue;
                }
                
                if (values.length < 5) {
                    continue; // Skip rows with insufficient data
                }
                
                UserBatchData userData = new UserBatchData();
                userData.username = trimToNull(values[0]);
                userData.password = trimToNull(values[1]);
                userData.email = trimToNull(values[2]);
                userData.firstName = trimToNull(values[3]);
                userData.lastName = trimToNull(values[4]);
                
                userDataList.add(userData);
            }
        } catch (CsvValidationException e) {
            throw new IOException("Invalid CSV format: " + e.getMessage(), e);
        }
        
        return userDataList;
    }
    
    private List<UserBatchData> parseExcelFile(MultipartFile file) throws IOException {
        List<UserBatchData> userDataList = new ArrayList<>();
        
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            
            boolean isFirstRow = true;
            
            for (Row row : sheet) {
                // Skip header row
                if (isFirstRow) {
                    isFirstRow = false;
                    if (isExcelHeaderRow(row)) {
                        continue;
                    }
                }
                
                // Skip empty rows
                if (isExcelEmptyRow(row)) {
                    continue;
                }
                
                if (row.getLastCellNum() < 5) {
                    continue; // Skip rows with insufficient data
                }
                
                UserBatchData userData = new UserBatchData();
                userData.username = getCellValueAsString(row.getCell(0));
                userData.password = getCellValueAsString(row.getCell(1));
                userData.email = getCellValueAsString(row.getCell(2));
                userData.firstName = getCellValueAsString(row.getCell(3));
                userData.lastName = getCellValueAsString(row.getCell(4));
                
                userDataList.add(userData);
            }
        }
        
        return userDataList;
    }
    
    private String validateUserData(UserBatchData userData, int rowNumber) {
        if (userData.username == null || userData.username.trim().isEmpty()) {
            return "Row " + rowNumber + ": Username is required";
        }
        if (userData.password == null || userData.password.trim().isEmpty()) {
            return "Row " + rowNumber + ": Password is required";
        }
        if (userData.email == null || userData.email.trim().isEmpty()) {
            return "Row " + rowNumber + ": Email is required";
        }
        if (userData.firstName == null || userData.firstName.trim().isEmpty()) {
            return "Row " + rowNumber + ": First name is required";
        }
        if (userData.lastName == null || userData.lastName.trim().isEmpty()) {
            return "Row " + rowNumber + ": Last name is required";
        }
        
        // Basic email validation
        if (!userData.email.contains("@") || !userData.email.contains(".")) {
            return "Row " + rowNumber + ": Invalid email format - " + userData.email;
        }
        
        // Username validation
        if (userData.username.length() < 3) {
            return "Row " + rowNumber + ": Username must be at least 3 characters long";
        }
        
        // Password validation
        if (userData.password.length() < 6) {
            return "Row " + rowNumber + ": Password must be at least 6 characters long";
        }
        
        return null; // No validation errors
    }
    
    private Users createUserFromBatchData(UserBatchData userData) {
        Users user = new Users();
        user.setUsername(userData.username.trim());
        user.setPassword(userData.password); // Don't encode here - saveUser method handles encoding
        user.setEmail(userData.email.trim().toLowerCase());
        user.setFirstName(userData.firstName.trim());
        user.setLastName(userData.lastName.trim());
        user.getRoles().add(Role.USER);
        
        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        user.setVerified(false);
        
        return user;
    }
    

    
    // Helper methods
    private boolean isHeaderRow(String[] values) {
        if (values.length == 0) return false;
        String firstValue = values[0].toLowerCase().trim();
        return firstValue.equals("username") || firstValue.equals("user") || firstValue.equals("name");
    }
    
    private boolean isEmptyRow(String[] values) {
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isExcelHeaderRow(Row row) {
        if (row.getCell(0) == null) return false;
        String firstValue = getCellValueAsString(row.getCell(0)).toLowerCase();
        return firstValue.equals("username") || firstValue.equals("user") || firstValue.equals("name");
    }
    
    private boolean isExcelEmptyRow(Row row) {
        for (int i = 0; i < 5; i++) {
            if (row.getCell(i) != null && !getCellValueAsString(row.getCell(i)).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    private String getCellValueAsString(org.apache.poi.ss.usermodel.Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
    
    private String trimToNull(String str) {
        if (str == null) return null;
        str = str.trim();
        return str.isEmpty() ? null : str;
    }
    
    // Inner classes for batch processing
    public static class UserBatchData {
        public String username;
        public String password;
        public String email;
        public String firstName;
        public String lastName;
    }
    
    public static class BatchProcessResult {
        private final List<Users> successfulUsers;
        private final List<String> errors;
        
        public BatchProcessResult(List<Users> successfulUsers, List<String> errors) {
            this.successfulUsers = successfulUsers;
            this.errors = errors;
        }
        
        public List<Users> getSuccessfulUsers() { return successfulUsers; }
        public List<String> getErrors() { return errors; }
        public int getSuccessCount() { return successfulUsers.size(); }
        public int getErrorCount() { return errors.size(); }
        public boolean hasErrors() { return !errors.isEmpty(); }
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