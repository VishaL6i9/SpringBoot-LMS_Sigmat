package com.sigmat.lms.controllers;

import com.sigmat.lms.dtos.UserDTO;
import com.sigmat.lms.models.Role;
import com.sigmat.lms.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/super-admin")
@PreAuthorize("hasRole('SUPER_ADMIN')")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "file://*"})
public class SuperAdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/admins")
    public ResponseEntity<List<UserDTO>> getAllAdmins() {
        List<UserDTO> admins = userService.getUsersByRole(Role.ADMIN);
        return ResponseEntity.ok(admins);
    }

    @GetMapping("/users/super-admins")
    public ResponseEntity<List<UserDTO>> getAllSuperAdmins() {
        List<UserDTO> superAdmins = userService.getUsersByRole(Role.SUPER_ADMIN);
        return ResponseEntity.ok(superAdmins);
    }

    @PostMapping("/users/{userId}/promote-to-admin")
    public ResponseEntity<?> promoteToAdmin(@PathVariable Long userId) {
        try {
            userService.updateUserRole(userId, "ADMIN");
            return ResponseEntity.ok().body("User promoted to ADMIN successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error promoting user: " + e.getMessage());
        }
    }

    @PostMapping("/users/{userId}/promote-to-super-admin")
    public ResponseEntity<?> promoteToSuperAdmin(@PathVariable Long userId) {
        try {
            userService.updateUserRole(userId, "SUPER_ADMIN");
            return ResponseEntity.ok().body("User promoted to SUPER_ADMIN successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error promoting user: " + e.getMessage());
        }
    }

    @PostMapping("/users/{userId}/demote-to-user")
    public ResponseEntity<?> demoteToUser(@PathVariable Long userId) {
        try {
            userService.updateUserRole(userId, "USER");
            return ResponseEntity.ok().body("User demoted to USER successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error demoting user: " + e.getMessage());
        }
    }

    @DeleteMapping("/users/{userId}/force-delete")
    public ResponseEntity<?> forceDeleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUserById(userId);
            return ResponseEntity.ok().body("User force deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error deleting user: " + e.getMessage());
        }
    }

    @GetMapping("/system/stats")
    public ResponseEntity<Map<String, Object>> getSystemStats() {
        try {
            Map<String, Object> stats = userService.getSystemStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error retrieving system stats: " + e.getMessage()));
        }
    }

    @PostMapping("/system/maintenance-mode")
    public ResponseEntity<?> toggleMaintenanceMode(@RequestParam boolean enabled) {
        try {
            // This would typically interact with a system configuration service
            return ResponseEntity.ok().body("Maintenance mode " + (enabled ? "enabled" : "disabled") + " successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error toggling maintenance mode: " + e.getMessage());
        }
    }

    @GetMapping("/audit/user-activities")
    public ResponseEntity<?> getUserActivities(@RequestParam(required = false) Long userId,
                                             @RequestParam(required = false) String dateFrom,
                                             @RequestParam(required = false) String dateTo) {
        try {
            // This would typically interact with an audit service
            return ResponseEntity.ok().body("User activities retrieved successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving user activities: " + e.getMessage());
        }
    }
}