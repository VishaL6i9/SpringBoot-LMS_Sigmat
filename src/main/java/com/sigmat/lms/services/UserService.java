package com.sigmat.lms.services;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.sigmat.lms.models.Role;
import com.sigmat.lms.models.UserProfile;
import com.sigmat.lms.models.Users;
import com.sigmat.lms.repo.UserProfileRepo;
import com.sigmat.lms.repo.UserRepo;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.logging.Logger;

@Service
public class UserService {

    private final UserRepo userRepository;
    private final UserProfileRepo userProfileRepository;
    private final JwtService jwtUtil;
    private final EmailService emailService; 
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());

    private final Set<String> invalidatedTokens = new HashSet<>();

    public UserService(UserRepo userRepository, UserProfileRepo userProfileRepository, JwtService jwtUtil, EmailService emailService) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService; 
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
        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        user.setVerified(false);
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        try {
            LOGGER.info("Saving user: " + user.getUsername() + ", First Name: " + user.getFirstName() + ", Last Name: " + user.getLastName());

            Users savedUser = userRepository.save(user);

            UserProfile userProfile = userProfileRepository.findByUsers(savedUser);
            if (userProfile == null) {
                userProfile = new UserProfile();
                userProfile.setUsers(savedUser);
            }
            userProfile.setEmail(savedUser.getEmail());
            userProfile.setFirstName(savedUser.getFirstName());
            userProfile.setLastName(savedUser.getLastName());
            userProfile.setLanguage("en");
            userProfile.setTimezone("UTC");
            userProfile.setPassword(savedUser.getPassword());

            userProfileRepository.save(userProfile);

            emailService.sendVerificationEmail(savedUser.getEmail(), verificationToken);

            LOGGER.info("User registered successfully: " + savedUser.getUsername());
        } catch (ObjectOptimisticLockingFailureException e) {
            LOGGER.warning("User was updated by another transaction: " + e.getMessage());
            throw new RuntimeException("Registration failed due to a conflict. Please try again.");
        } catch (Exception e) {
            LOGGER.severe("Error saving user: " + e.getMessage());
            throw new RuntimeException("Registration failed. Please try again.");
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

    public boolean invalidateToken(String token) {
        return invalidatedTokens.add(token);
    }

    public boolean isTokenInvalidated(String token) {
        return invalidatedTokens.contains(token);
    }

    public List<Users> processUserFileBatchCreate(MultipartFile file) {
        List<Users> users = new ArrayList<>();
        try {
            if (file.getOriginalFilename().endsWith(".csv")) {
                try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
                    String[] values;

                    while ((values = csvReader.readNext()) != null) {
                        Users user = new Users();
                        user.setUsername(values[0]);
                        user.setPassword(values[1]);
                        user.setEmail(values[2]);
                        user.setFirstName(values[3]);
                        user.setLastName(values[4]);
                        user.getRoles().add(Role.LEARNER);

                        String verificationToken = UUID.randomUUID().toString();
                        user.setVerificationToken(verificationToken);
                        user.setVerified(false);

                        users.add(user);
                    }
                }
            } else if (file.getOriginalFilename().endsWith(".xlsx") || file.getOriginalFilename().endsWith(".xls")) {
                try (InputStream inputStream = file.getInputStream()) {
                    Workbook workbook = WorkbookFactory.create(inputStream);
                    Sheet sheet = workbook.getSheetAt(0);

                    for (Row row : sheet) {
                        Users user = new Users();
                        user.setUsername(row.getCell(0).getStringCellValue());
                        user.setPassword(row.getCell(1).getStringCellValue());
                        user.setEmail(row.getCell(2).getStringCellValue());
                        user.setFirstName(row.getCell(3).getStringCellValue());
                        user.setLastName(row.getCell(4).getStringCellValue());
                        user.getRoles().add(Role.LEARNER);

                        String verificationToken = UUID.randomUUID().toString();
                        user.setVerificationToken(verificationToken);
                        user.setVerified(false);

                        users.add(user);
                    }
                }
            } else {
                throw new IllegalArgumentException("Invalid file type. Please upload a CSV or Excel file.");
            }

            for (Users user : users) {
                saveUser(user);
            }
        } catch (Exception e) {
            LOGGER.severe("Error processing user file: " + e.getMessage());
            throw new RuntimeException("Failed to process user file: " + e.getMessage());
        }
        return users;
    }

    public List<String> readUsernamesFromCSV(MultipartFile file) throws IOException {
        List<String> usernames = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                usernames.add(nextLine[0]);
            }
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
        return usernames;
    }

    public List<String> readUsernamesFromExcel(MultipartFile file) throws IOException {
        List<String> usernames = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getCell(0) != null) {
                    usernames.add(row.getCell(0).getStringCellValue());
                }
            }
        }
        return usernames;
    }

    @Transactional
    public boolean verifyUser(String verificationToken) {
        Users user = userRepository.findByVerificationToken(verificationToken);
        if (user == null) {
            return false;
        }

        user.setVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);

        return true;
    }

    public Users getUserById(Long userId) {
        return userRepository.findById(userId);
    }
}