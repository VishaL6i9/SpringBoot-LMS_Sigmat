package com.sigmat.lms.services;

import com.sigmat.lms.models.Users;
import com.sigmat.lms.models.UserProfile;
import com.sigmat.lms.repo.UserRepo;
import com.sigmat.lms.repo.UserProfileRepo;
import jakarta.transaction.Transactional;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.logging.Logger;

@Service
public class UserService {

    private final UserRepo userRepository;
    private final UserProfileRepo userProfileRepository;
    private final JwtService jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());

    public UserService(UserRepo userRepository, UserProfileRepo userProfileRepository, JwtService jwtUtil) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.jwtUtil = jwtUtil;
    }

    public boolean validateUser(String username, String password) {
        Users user = userRepository.findByUsername(username);
        return user != null && passwordEncoder.matches(password, user.getPassword());
    }

    public String generateToken(String username) {
        return jwtUtil.generateToken(username);
    }

    @Transactional
    public void saveUser(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        try {
            Users savedUser = userRepository.save(user);
            UserProfile userProfile = userProfileRepository.findByUsers(savedUser);

            if (userProfile == null) {
                userProfile = new UserProfile();
                userProfile.setUsers(savedUser);
                userProfile.setEmail(savedUser.getUsername() + "@gmail.com"); 
                userProfile.setPassword(savedUser.getPassword()); 
            } else {
                
                if (userProfile.getEmail() == null || userProfile.getEmail().isEmpty()) {
                    userProfile.setEmail(savedUser.getUsername() + "@gmail.com");
                }
                
                if (userProfile.getPassword() == null ||
                        !userProfile.getPassword().equals(savedUser.getPassword())) {
                    userProfile.setPassword(savedUser.getPassword()); 
                }
            }
            
            LOGGER.info("Final Email set before save: " + userProfile.getEmail());
            LOGGER.info("Final Password set before save: " + (userProfile.getPassword() != null ? "ENCRYPTED" : "NULL"));

            userProfileRepository.save(userProfile);
            LOGGER.info("User and profile saved successfully: " + user.getUsername());
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new RuntimeException("User was updated by another transaction. Please try again.");
        }
    }
    
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void deleteUserByUsername(String username) {
        Users user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found: " + username);
        }
        
        UserProfile userProfile = userProfileRepository.findByUsers(user);
        if (userProfile != null) {
            userProfileRepository.delete(userProfile);
        }

        userRepository.delete(user);
        LOGGER.info("User deleted: " + username);
    }
}
