package com.sigmat.lms.controllers;

import com.sigmat.lms.models.Role;
import com.sigmat.lms.models.Users;
import com.sigmat.lms.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.logging.Logger;
import com.sigmat.lms.models.UserDTO;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class AuthController {

    @Autowired
    private UserService userService;
    private static final Logger LOGGER = Logger.getLogger(AuthController.class.getName());
    
    
    @PostMapping("/login")

    public ResponseEntity<?> login(@RequestBody UserDTO loginUser ) {

        String username = loginUser .getUsername();

        String password = loginUser .getPassword();


        if (userService.validateUser (username, password)) {

            String token = userService.generateToken(username);

            return ResponseEntity.ok().body(token);

        } else {

            return ResponseEntity.status(401).body(Map.of("message", "Login failed! Invalid credentials."));

        }

    }
    
    @PostMapping("/register/user")
    public ResponseEntity<?> register(@RequestBody Users newUser ) {
        
        Users userToSave = new Users();
        
        userToSave.setUsername(newUser .getUsername());
        userToSave.setPassword(newUser .getPassword()); 
        userToSave.setEmail(newUser .getEmail());
        userToSave.setFirstName(newUser .getFirstName());
        userToSave.setLastName(newUser .getLastName());
    
        // Clear existing roles and add new roles from the input
        userToSave.getRoles().clear();
        if (newUser .getRoles() != null && !newUser .getRoles().isEmpty()) {
            userToSave.getRoles().addAll(newUser .getRoles());
        } else {
            userToSave.getRoles().add(Role.LEARNER); 
        }
    
        try {
            userService.saveUser (userToSave);
            return ResponseEntity.ok().body("User  registered successfully!");
        } catch (Exception e) {
            
            LOGGER.severe("Registration failed: " + e.getMessage());
            return ResponseEntity.status(400).body("Registration failed: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok().body("Test successful");
    }
    
}