package com.sigmat.lms.services;

import com.sigmat.lms.models.UserProfile;
import com.sigmat.lms.models.Users;
import com.sigmat.lms.repo.UserRepo;
import com.sigmat.lms.repo.UserProfileRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepo userRepository;

    @Mock
    private UserProfileRepo userProfileRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    private Users user;
    private UserProfile userProfile;

    @BeforeEach
    void setUp() {
        user = new Users();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password");

        userProfile = new UserProfile();
        userProfile.setUsers(user);
        userProfile.setEmail("testuser@example.com");
    }

    @Test
    void testSaveUser_CreatesUserProfile() {
        when(userRepository.save(any(Users.class))).thenReturn(user);
        when(userProfileRepository.findByUsers(user)).thenReturn(null);
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(userProfile);

        userService.saveUser(user);

        verify(userRepository, times(1)).save(user);
        verify(userProfileRepository, times(1)).save(any(UserProfile.class));
    }

    @Test
    void testValidateUser_Success() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode("password"));

        when(userRepository.findByUsername("testuser")).thenReturn(user);

        boolean isValid = userService.validateUser("testuser", "password");
        assertTrue(isValid);
    }

    @Test
    void testValidateUser_Failure() {
        when(userRepository.findByUsername("testuser")).thenReturn(null);
        boolean isValid = userService.validateUser("testuser", "password");
        assertFalse(isValid);
    }

    @Test
    void testGenerateToken() {
        when(jwtService.generateToken("testuser")).thenReturn("mockedToken");

        String token = userService.generateToken("testuser");

        assertNotNull(token);
        assertEquals("mockedToken", token);
    }

    @Test
    void testDeleteUserByUsername() {
        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(userProfileRepository.findByUsers(user)).thenReturn(userProfile);

        userService.deleteUserByUsername("testuser");

        verify(userProfileRepository, times(1)).delete(userProfile);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void testDeleteUserByUsername_UserNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.deleteUserByUsername("testuser");
        });

        assertEquals("User not found: testuser", exception.getMessage());
    }
}
