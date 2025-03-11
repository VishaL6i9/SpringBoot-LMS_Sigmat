package com.sigmat.lms.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.verify;

public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendVerificationEmail() {
        // Arrange
        String to = "vishalkandakatla@gmail.com";
        String verificationToken = "123456";

        // Act
        emailService.sendVerificationEmail(to, verificationToken);

        // Assert
        SimpleMailMessage expectedMessage = new SimpleMailMessage();
        expectedMessage.setTo(to);
        expectedMessage.setSubject("Email Verification");
        expectedMessage.setText("Please verify your email by clicking the following link: "
                + "http://localhost:8080/verify-email?token=" + verificationToken);

        // Verify that the mailSender's send method was called with the expected message
        verify(mailSender).send(expectedMessage);
    }
}