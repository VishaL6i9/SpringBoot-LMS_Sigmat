package com.sigmat.lms.services;

import com.sigmat.lms.models.UserProfile;
import com.sigmat.lms.repo.UserProfileRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserProfileServiceTest {

    @InjectMocks
    private UserProfileService userProfileService;

    @Mock
    private UserProfileRepo userProfileRepository;

    private UserProfile userProfile;
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordEncoder = new BCryptPasswordEncoder();
       
        userProfile = new UserProfile();
        userProfile.setId(1L);
        userProfile.setFirstName("John");
        userProfile.setLastName("Doe");
        userProfile.setEmail("john.doe@example.com");
        userProfile.setPhone("1234567890");
        userProfile.setTimezone("UTC");
        userProfile.setLanguage("English");
        userProfile.setProfileImage(new byte[]{});
        userProfile.setPassword("password123");
    }

    @Test
    public void testUpdateUserProfile_Success() {
        // Mock the repository method to return the existing userProfile
        when(userProfileRepository.findById(1L)).thenReturn(Optional.of(userProfile));

        // Create an updated profile
        UserProfile updatedProfile = new UserProfile();
        updatedProfile.setId(1L);
        updatedProfile.setFirstName("Jane");
        updatedProfile.setLastName("Doe");
        updatedProfile.setEmail("jane.doe@example.com");
        updatedProfile.setPhone("0987654321");
        updatedProfile.setTimezone("UTC");
        updatedProfile.setLanguage("English");
        updatedProfile.setProfileImage(new byte[]{});
        updatedProfile.setPassword("newpassword123");

        // Mock the save method to return the updated profile
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(invocation -> {
            UserProfile savedProfile = invocation.getArgument(0);
            savedProfile.setPassword(passwordEncoder.encode(savedProfile.getPassword())); // Simulate password encoding
            return savedProfile;
        });

        // Call the service method
        UserProfile result = userProfileService.updateUserProfile(updatedProfile);

        // Assert that the result is not null and matches the updated values
        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("jane.doe@example.com", result.getEmail());
        assertNotEquals("newpassword123", result.getPassword()); // Ensure the password is hashed
        verify(userProfileRepository, times(1)).save(any(UserProfile.class));
    }

    @Test
    public void testUpdateUserProfile_NotFound() {
        // Mock the repository method to return an empty Optional
        when(userProfileRepository.findById(1L)).thenReturn(Optional.empty());

        // Call the service method and assert that it throws a RuntimeException
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userProfileService.updateUserProfile(userProfile);
        });

        // Assert that the exception message is as expected
        assertEquals("User  Profile not found with id: 1", exception.getMessage());
        verify(userProfileRepository, times(0)).save(any(UserProfile.class));
    }
}