package com.sigmat.lms.services;

import com.sigmat.lms.models.Users;
import com.sigmat.lms.models.UserProfile;
import com.sigmat.lms.repo.UserRepo;
import com.sigmat.lms.repo.UserProfileRepo;
import com.sigmat.lms.services.JwtService;
import com.sigmat.lms.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepo userRepository;

    @Mock
    private UserProfileRepo userProfileRepository;

    @Mock
    private JwtService jwtUtil;

    @InjectMocks
    private UserService userService;

    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    public void testValidateUser_Success() {
        Users user = new Users();
        user.setUsername("testUser ");
        user.setPassword(passwordEncoder.encode("testPassword"));

        when(userRepository.findByUsername("testUser ")).thenReturn(user);

        boolean isValid = userService.validateUser ("testUser ", "testPassword");

        assertTrue(isValid);
    }

    @Test
    public void testValidateUser_Failure() {
        when(userRepository.findByUsername("testUser ")).thenReturn(null);

        boolean isValid = userService.validateUser ("testUser ", "testPassword");

        assertFalse(isValid);
    }

    @Test
    public void testGenerateToken() {
        when(jwtUtil.generateToken("testUser ")).thenReturn("mockToken");

        String token = userService.generateToken("testUser ");

        assertEquals("mockToken", token);
    }

    @Test
    @Transactional
    public void testSaveUser () {
        Users user = new Users();
        user.setUsername("testUser ");
        user.setPassword("testPassword");
        user.setFirstName("Test");
        user.setLastName("User ");

        UserProfile userProfile = new UserProfile();
        userProfile.setUsers(user);

        when(userRepository.save(any(Users.class))).thenReturn(user);
        when(userProfileRepository.findByUsers(user)).thenReturn(userProfile);

        userService.saveUser (user);

        verify(userRepository).save(user);
        verify(userProfileRepository).save(userProfile);
    }

    @Test
    public void testGetAllUsers() {
        Users user = new Users();
        user.setUsername("testUser ");

        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        List<Users> users = userService.getAllUsers();

        assertEquals(1, users.size());
        assertEquals("testUser ", users.get(0).getUsername());
    }

    @Test
    @Transactional
    public void testDeleteUserByUsername_Success() {
        Users user = new Users();
        user.setUsername("testUser ");

        UserProfile userProfile = new UserProfile();
        userProfile.setUsers(user);

        when(userRepository.findByUsername("testUser ")).thenReturn(user);
        when(userProfileRepository.findByUsers(user)).thenReturn(userProfile);

        userService.deleteUserByUsername("testUser ");

        verify(userProfileRepository).delete(userProfile);
        verify(userRepository).delete(user);
    }

    @Test
    public void testDeleteUserByUsername_UserNotFound() {
        when(userRepository.findByUsername("nonExistentUser ")).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.deleteUserByUsername("nonExistentUser ");
        });

        assertEquals("User  not found: nonExistentUser ", exception.getMessage());
    }
}