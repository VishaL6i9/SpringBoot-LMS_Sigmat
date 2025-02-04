package com.sigmat.lms.services;

import com.sigmat.lms.models.Users;
import com.sigmat.lms.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private JwtService jwtUtil;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public boolean validateUser (String username, String password) {
        Users user = userRepository.findByUsername(username);
        return user != null && passwordEncoder.matches(password, user.getPassword());
    }

    public String generateToken(String username) {
        return jwtUtil.generateToken(username);
    }

    public void saveUser (Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); 
        userRepository.save(user);
    }

    public List<Users> getAllUsers() {
        return userRepository.findAll(); 
    }
}