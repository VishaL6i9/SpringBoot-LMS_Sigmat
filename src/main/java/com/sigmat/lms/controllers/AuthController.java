package com.sigmat.lms.controllers;

import com.sigmat.lms.dtos.*;
import com.sigmat.lms.exceptions.DuplicateEmailException;
import com.sigmat.lms.models.Role;
import com.sigmat.lms.models.Users;
import com.sigmat.lms.services.JwtService;
import com.sigmat.lms.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/public")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private com.sigmat.lms.services.AttendanceService attendanceService;

    private static final Logger LOGGER = Logger.getLogger(AuthController.class.getName());

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        try {
            List<UserDTO> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            LOGGER.severe("Failed to fetch users: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginUser ) {
        String username = loginUser .getUsername();
        String password = loginUser .getPassword();

        if (userService.validateUser (username, password)) {
            String token = userService.generateToken(username);
            Users user = userService.findByUsername(username);
            if (user != null) {
                attendanceService.recordAttendance(user);
            }
            return ResponseEntity.ok().body(token);
        } else {
            return ResponseEntity.status(401).body(Map.of("message", "Login failed! Invalid credentials."));
        }
    }

    @PostMapping("/register/user")
    public ResponseEntity<?> register(@RequestBody Users newUser) {
        Users userToSave = new Users();

        userToSave.setUsername(newUser.getUsername());
        userToSave.setPassword(newUser.getPassword());
        userToSave.setEmail(newUser.getEmail());
        userToSave.setFirstName(newUser.getFirstName());
        userToSave.setLastName(newUser.getLastName());

        userToSave.getRoles().clear();
        if (newUser.getRoles() != null && !newUser.getRoles().isEmpty()) {
            userToSave.getRoles().addAll(newUser.getRoles());
        } else {
            userToSave.getRoles().add(Role.USER);
        }

        try {
            userService.saveUser(userToSave);
            return ResponseEntity.ok().body("User registered successfully!");
            
        } catch (DataIntegrityViolationException e) {
            String errorMessage = handleDatabaseConstraintViolation(e);
            LOGGER.warning("Database constraint violation during user registration: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
            
        } catch (DuplicateEmailException e) {
            LOGGER.warning("Duplicate email registration attempt: " + newUser.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid user registration data: " + e.getMessage());
            return ResponseEntity.badRequest().body("Invalid input: " + e.getMessage());
            
        } catch (Exception e) {
            LOGGER.severe("Unexpected error during user registration: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred. Please try again later.");
        }
    }

    @PostMapping("/register/instructor")
    public ResponseEntity<?> registerInstructor(@RequestBody InstructorRegistrationDTO instructorDTO) {
        try {
            userService.registerInstructor(instructorDTO);
            return ResponseEntity.ok().body("Instructor registered successfully!");
            
        } catch (DataIntegrityViolationException e) {
            String errorMessage = handleDatabaseConstraintViolation(e);
            LOGGER.warning("Database constraint violation during instructor registration: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
            
        } catch (DuplicateEmailException e) {
            LOGGER.warning("Duplicate email registration attempt: " + instructorDTO.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            
        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid instructor registration data: " + e.getMessage());
            return ResponseEntity.badRequest().body("Invalid input: " + e.getMessage());
            
        } catch (Exception e) {
            LOGGER.severe("Unexpected error during instructor registration: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred. Please try again later.");
        }
    }

    @PostMapping("/register/batch")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('INSTITUTION')")
    public ResponseEntity<?> registerBatch(@RequestParam("file") MultipartFile file) {
        // Validate file input
        if (file == null || file.isEmpty()) {
            LOGGER.warning("Batch registration attempted with empty file");
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "File is required and cannot be empty"
            ));
        }

        // Validate file type
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.toLowerCase().endsWith(".csv") && 
            !filename.toLowerCase().endsWith(".xlsx") && 
            !filename.toLowerCase().endsWith(".xls"))) {
            LOGGER.warning("Batch registration attempted with invalid file type: " + filename);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Invalid file type. Please upload a CSV or Excel file (.csv, .xlsx, .xls)"
            ));
        }

        // Validate file size (10MB limit)
        if (file.getSize() > 10 * 1024 * 1024) {
            LOGGER.warning("Batch registration attempted with file too large: " + file.getSize() + " bytes");
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "File size exceeds 10MB limit"
            ));
        }

        try {
            LOGGER.info("Starting batch user registration with file: " + filename);
            UserService.BatchProcessResult result = userService.processUserFileBatchCreate(file);
            
            // Prepare response based on results
            Map<String, Object> response = new HashMap<>();
            response.put("filename", filename);
            response.put("totalProcessed", result.getSuccessCount() + result.getErrorCount());
            response.put("successCount", result.getSuccessCount());
            response.put("errorCount", result.getErrorCount());
            
            if (result.hasErrors()) {
                response.put("success", result.getSuccessCount() > 0); // Partial success if some users created
                response.put("message", result.getSuccessCount() > 0 ? 
                    "Batch registration completed with some errors" : 
                    "Batch registration failed - no users were created");
                response.put("errors", result.getErrors());
                
                // Return appropriate status code
                HttpStatus status = result.getSuccessCount() > 0 ? 
                    HttpStatus.PARTIAL_CONTENT : HttpStatus.BAD_REQUEST;
                
                LOGGER.warning("Batch registration completed with errors. Success: " + 
                    result.getSuccessCount() + ", Errors: " + result.getErrorCount());
                
                return ResponseEntity.status(status).body(response);
            } else {
                response.put("success", true);
                response.put("message", "All users registered successfully");
                
                LOGGER.info("Batch registration successful: " + result.getSuccessCount() + " users created");
                return ResponseEntity.ok().body(response);
            }

        } catch (IllegalArgumentException e) {
            LOGGER.warning("Invalid batch registration data: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Invalid file format or data: " + e.getMessage()
            ));
            
        } catch (Exception e) {
            LOGGER.severe("Unexpected error during batch registration: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Batch registration failed due to an unexpected error. Please try again later.",
                "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        boolean isInvalidated = userService.invalidateToken(token);

        if (isInvalidated) {
            return ResponseEntity.ok().body("Logout successful!");
        } else {
            return ResponseEntity.status(400).body("Logout failed! Invalid token.");
        }
    }

    @GetMapping("/role")
    public ResponseEntity<?> getRole(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7);
            String username = jwtService.extractUserName(jwt);
            Users user = userService.findByUsername(username);
            if (user != null) {
                Set<Role> roles = user.getRoles();
                return ResponseEntity.ok().body(roles);
            } else {
                return ResponseEntity.status(404).body("User not found");
            }
        } catch (Exception e) {
            LOGGER.severe("Failed to fetch user role: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok().body("Test successful");
    }

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam String token) {
        boolean isVerified = userService.verifyUser(token);
        return isVerified ? "Email verified successfully!" : "Invalid or expired token.";
    }

    @PostMapping("/password-reset/request")
    public ResponseEntity<?> requestPasswordReset(@RequestBody PasswordResetRequestDTO request) {
        try {
            userService.requestPasswordReset(request.getEmail());
            return ResponseEntity.ok().body("Password reset email sent.");
        } catch (Exception e) {
            LOGGER.severe("Password reset request failed: " + e.getMessage());
            return ResponseEntity.status(400).body("Password reset request failed: " + e.getMessage());
        }
    }

    @PostMapping("/password-reset/reset")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetDTO request) {
        try {
            userService.resetPassword(request.getEmail(), request.getToken(), request.getNewPassword());
            return ResponseEntity.ok().body("Password reset successful.");
        } catch (Exception e) {
            LOGGER.severe("Password reset failed: " + e.getMessage());
            return ResponseEntity.status(400).body("Password reset failed: " + e.getMessage());
        }
    }

    private String handleDatabaseConstraintViolation(DataIntegrityViolationException e) {
        String errorMessage = e.getMessage();
        
        if (errorMessage != null) {
            if (errorMessage.contains("users_email_key") || 
                errorMessage.contains("duplicate key value violates unique constraint") && 
                errorMessage.contains("email")) {
                return "This email address is already registered in our system.";
            }
            
            if (errorMessage.contains("users_username_key") || 
                errorMessage.contains("username")) {
                return "This username is already taken. Please choose a different username.";
            }
            
            if (errorMessage.contains("instructors_phone_key") || 
                errorMessage.contains("phone")) {
                return "This phone number is already registered. Please use a different phone number.";
            }
        }
        
        return "This information is already registered in our system. Please check your details.";
    }
}
