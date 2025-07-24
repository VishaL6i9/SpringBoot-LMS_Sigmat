package com.sigmat.lms.controllers;

import com.sigmat.lms.dtos.UserDTO;
import com.sigmat.lms.models.Users;
import com.sigmat.lms.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/delete/user/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        try {
            userService.deleteUserByUsername(username);
            return ResponseEntity.ok().body("User  deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(404).body("User  not found or deletion failed: " + e.getMessage());
        }
    }

    @PostMapping("/delete/users/batch")
    public ResponseEntity<?> deleteUsersBatch(@RequestParam("file") MultipartFile file) {
        List<String> usernames;
        try {
            if (file.getOriginalFilename().endsWith(".csv")) {
                usernames = userService.readUsernamesFromCSV(file);
            } else if (file.getOriginalFilename().endsWith(".xlsx") || file.getOriginalFilename().endsWith(".xls")) {
                usernames = userService.readUsernamesFromExcel(file);
            } else {
                return ResponseEntity.badRequest().body("Invalid file type. Please upload a CSV or Excel file.");
            }
            for (String username : usernames) {
                userService.deleteUserByUsername(username);
            }
            return ResponseEntity.ok().body("Users deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error occurred during deletion: " + e.getMessage());
        }
    }

    @PutMapping("/user/{userId}/role")
    public ResponseEntity<?> changeUserRole(@PathVariable Long userId, @RequestParam String newRole) {
        try {
            userService.updateUserRole(userId, newRole);
            return ResponseEntity.ok().body("User role updated successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error updating user role: " + e.getMessage());
        }
    }
}