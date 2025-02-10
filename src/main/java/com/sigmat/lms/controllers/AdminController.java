package com.sigmat.lms.controllers;

import com.sigmat.lms.models.Users;
import com.sigmat.lms.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<Users>> getUsers() {
        List<Users> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    @DeleteMapping("/delete/user/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        try {
            userService.deleteUserByUsername(username);
            return ResponseEntity.ok().body("User deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(404).body("User not found or deletion failed: " + e.getMessage());
        }
    }
}
