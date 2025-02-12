package com.sigmat.lms.controllers;

import com.sigmat.lms.models.Role;
import com.sigmat.lms.models.UserDTO;
import com.sigmat.lms.models.Users;
import com.sigmat.lms.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test

    public void testLogin_Success() {

        UserDTO userDTO = new UserDTO();

        userDTO.setUsername("testUser ");

        userDTO.setPassword("testPassword");


        when(userService.validateUser ("testUser ", "testPassword")).thenReturn(true);

        when(userService.generateToken("testUser ")).thenReturn("mockToken");


        ResponseEntity<?> response = authController.login(userDTO);


        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(Map.of("token", "mockToken", "message", "Login successful!"), response.getBody());

    }


    @Test

    public void testLogin_Failure() {

        UserDTO userDTO = new UserDTO();

        userDTO.setUsername("testUser ");

        userDTO.setPassword("wrongPassword");


        when(userService.validateUser ("testUser ", "wrongPassword")).thenReturn(false);


        ResponseEntity<?> response = authController.login(userDTO);


        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        assertEquals(Map.of("message", "Login failed! Invalid credentials."), response.getBody());

    }

    @Test
    public void testRegister_Success() {
        Users newUser  = new Users();
        newUser .setUsername("newUser ");
        newUser .setPassword("newPassword");
        newUser .getRoles().add(Role.LEARNER); 
        
        doNothing().when(userService).saveUser (any(Users.class));

        ResponseEntity<?> response = authController.register(newUser );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User  registered successfully!", response.getBody());
    }

    @Test
    public void testRegister_Failure() {
        Users newUser  = new Users();
        newUser .setUsername("newUser");
        newUser .setPassword("newPassword");

        doThrow(new RuntimeException("User already exists")).when(userService).saveUser (any(Users.class));

        ResponseEntity<?> response = authController.register(newUser );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Registration failed: User already exists", response.getBody());
    }

    @Test
    public void testTestEndpoint() {
        ResponseEntity<?> response = authController.test();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test successful", response.getBody());
    }
}