package com.sigmat.lms.config;

import com.sigmat.lms.models.Role;
import com.sigmat.lms.models.Users;
import com.sigmat.lms.repo.UserRepo;
import com.sigmat.lms.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataLoader {


    @Bean
    CommandLineRunner initDatabase(UserRepo userRepo, PasswordEncoder passwordEncoder, UserService userService) {
       
        return args -> {
            // Create Admin User
            if (userRepo.findByUsername("admin") == null) {
                Users adminUser = new Users();
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
                userRepo.save(adminUser);
                System.out.println("Created admin user: " + adminUser.getUsername());
            }

            // Create Instructor User
            if (userRepo.findByUsername("instructor") == null) {
                Users instructorUser = new Users();
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
                userRepo.save(instructorUser);
                System.out.println("Created instructor user: " + instructorUser.getUsername());
            }

            // Create Regular User
            if (userRepo.findByUsername("user") == null) {
                Users regularUser = new Users();
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
                userRepo.save(regularUser);
                System.out.println("Created regular user: " + regularUser.getUsername());
            }
        };
    }
}
