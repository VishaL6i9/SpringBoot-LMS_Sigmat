package com.sigmat.lms.services;

import com.sigmat.lms.models.UserProfile;
import com.sigmat.lms.models.Users;
import com.sigmat.lms.repo.UserProfileRepo;
import com.sigmat.lms.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserProfileServiceTest {

    @InjectMocks
    private UserProfileService userProfileService;

    @Mock
    private UserProfileRepo userProfileRepo;

    @Mock
    private UserRepo userRepo;

    private UserProfile userProfile;
    private Users user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userProfile = new UserProfile();
        userProfile.setId(1L);
        userProfile.setFirstName("John");
        userProfile.setLastName("Doe");
        userProfile.setEmail("john.doe@example.com");
        userProfile.setPhone("1234567890");
        userProfile.setTimezone("UTC");
        userProfile.setLanguage("EN");
        userProfile.setProfileImage("image.png".getBytes());

        user = new Users();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password");
    }

    @Test
    public void testGetUserProfile_Success() {
        when(userProfileRepo.findByUsersId(1L)).thenReturn(userProfile);

        UserProfile result = userProfileService.getUserProfile(1L);
        assertNotNull(result);
        assertEquals("John", result.getFirstName());
    }

    @Test
    public void testGetUserProfile_UserProfileNotFound() {
        when(userProfileRepo.findByUsersId(1L)).thenReturn(null);

        UserProfile result = userProfileService.getUserProfile(1L);
        assertNull(result);
    }

    @Test
    public void testUpdateUserProfile_Success() {
        when(userProfileRepo.findByUsersId(1L)).thenReturn(userProfile);
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(userProfileRepo.save(any(UserProfile.class))).thenReturn(userProfile);
        when(userRepo.save(any(Users.class))).thenReturn(user);

        UserProfile updatedProfile = userProfileService.updateUserProfile(userProfile);

        assertNotNull(updatedProfile);
        assertEquals("John", updatedProfile.getFirstName());
        verify(userProfileRepo).save(userProfile);
        verify(userRepo).save(user);
    }

    @Test
    public void testUpdateUserProfile_UserProfileNotFound() {
        when(userProfileRepo.findByUsersId(1L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userProfileService.updateUserProfile(userProfile);
        });

        assertEquals("User  Profile not found with id: 1", exception.getMessage());
    }

    @Test
    public void testUpdateUserPassword_Success() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(userProfileRepo.findByUsersId(1L)).thenReturn(userProfile);
        when(userRepo.save(any(Users.class))).thenReturn(user);
        when(userProfileRepo.save(any(UserProfile.class))).thenReturn(userProfile);

        userProfileService.updateUserPassword(1L, "newPassword");

        assertNotNull(user.getPassword());
        assertNotEquals("password", user.getPassword());
        verify(userRepo).save(user);
        verify(userProfileRepo).save(userProfile);
    }

    @Test
    public void testUpdateUserPassword_UserNotFound() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userProfileService.updateUserPassword(1L, "newPassword");
        });

        assertEquals("User  not found with id: 1", exception.getMessage());
    }

    @Test
    public void testUpdateUserPassword_UserProfileNotFound() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(userProfileRepo.findByUsersId(1L)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userProfileService.updateUserPassword(1L, "newPassword");
        });

        assertEquals("User  Profile not found with id: 1", exception.getMessage());
    }
}