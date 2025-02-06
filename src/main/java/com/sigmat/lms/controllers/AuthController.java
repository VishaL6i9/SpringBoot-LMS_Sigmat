package com.sigmat.lms.controllers;

import com.sigmat.lms.models.Role;
import com.sigmat.lms.models.Users;
import com.sigmat.lms.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Users loginUser ) {
        String username = loginUser .getUsername();
        String password = loginUser .getPassword();

        if (userService.validateUser (username, password)) {
            String token = userService.generateToken(username);
            return ResponseEntity.ok().body("Login successful! Token: " + token);
        } else {
            return ResponseEntity.status(401).body("Login failed! Invalid credentials.");
        }
    }

    @PostMapping("/register/user")
    public ResponseEntity<?> register(@RequestBody Users newUser ) {
        Users userToSave = new Users();
        userToSave.setId(newUser.getId());
        userToSave.setUsername(newUser .getUsername());
        userToSave.setPassword(newUser .getPassword());
        userToSave.getRoles().add(Role.LEARNER); 

        try {
            userService.saveUser (userToSave);
            return ResponseEntity.ok().body("User  registered successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Registration failed: " + e.getMessage());
        }
    }
    

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok().body("Test successful");
    }
}