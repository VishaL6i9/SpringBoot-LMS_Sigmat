package com.sigmat.lms.controllers;

import com.sigmat.lms.models.Users;
import com.sigmat.lms.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Users newUser ) {
        userService.saveUser (newUser );
        return ResponseEntity.ok().body("User  registered successfully!");
    }
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok().body("Test successful");
    }
    
    
}