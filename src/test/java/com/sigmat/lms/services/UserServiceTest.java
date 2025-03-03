package com.sigmat.lms.services;

import com.sigmat.lms.models.Users;
import com.sigmat.lms.models.UserProfile;
import com.sigmat.lms.repo.UserRepo;
import com.sigmat.lms.repo.UserProfileRepo;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

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

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testProcessUserFileBatchCreate_CSV() throws IOException {
        String csvContent = "username1,password1,email1@example.com,FirstName1,LastName1\n" +
                "username2,password2,email2@example.com,FirstName2,LastName2\n" +
                "username3,password3,email3@example.com,FirstName3,LastName3";
        MultipartFile csvFile = new MockMultipartFile("file", "users.csv", "text/csv", csvContent.getBytes());

        // Mock the behavior of userRepository to avoid actual database calls
        when(userRepository.save(any(Users.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Mock the behavior of userProfileRepository to return a new UserProfile
        UserProfile mockUserProfile = new UserProfile();
        when(userProfileRepository.findByUsers(any(Users.class))).thenReturn(mockUserProfile);

        List<Users> users = userService.processUserFileBatchCreate(csvFile);

        assertEquals(3, users.size(), "Expected 3 users to be processed from CSV");
        assertEquals("username1", users.get(0).getUsername());
        assertEquals("username2", users.get(1).getUsername());
        assertEquals("username3", users.get(2).getUsername());

        // Verify that saveUser  was called for each user
        verify(userRepository, times(3)).save(any(Users.class));
    }

    @Test
    public void testProcessUserFileBatchCreate_Excel() throws IOException {
        // Create a valid Excel file in memory
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Username");
        headerRow.createCell(1).setCellValue("Password");
        headerRow.createCell(2).setCellValue("Email");
        headerRow.createCell(3).setCellValue("First Name");
        headerRow.createCell(4).setCellValue("Last Name");

        // Add user data
        Object[][] userData = {
                {"username1", "password1", "email1@example.com", "FirstName1", "LastName1"},
                {"username2", "password2", "email2@example.com", "FirstName2", "LastName2"},
                {"username3", "password3", "email3@example.com", "FirstName3", "LastName3"}
        };

        int rowNum = 1;
        for (Object[] user : userData) {
            Row row = sheet.createRow(rowNum++);
            for (int i = 0; i < user.length; i++) {
                row.createCell(i).setCellValue((String) user[i]);
            }
        }

        // Write the workbook to a byte array output stream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        // Create a MultipartFile from the byte array
        MultipartFile excelFile = new MockMultipartFile("file", "users.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", outputStream.toByteArray());

        // Mock the behavior of userRepository to avoid actual database calls
        when(userRepository.save(any(Users.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Mock the behavior of userProfileRepository to return a new UserProfile
        UserProfile mockUserProfile = new UserProfile();
        when(userProfileRepository.findByUsers(any(Users.class))).thenReturn(mockUserProfile);

        List<Users> users = userService.processUserFileBatchCreate(excelFile);

        assertEquals(3, users.size(), "Expected 3 users to be processed from Excel");
        assertEquals("username1", users.get(0).getUsername());
        assertEquals("username2", users.get(1).getUsername());
        assertEquals("username3", users.get(2).getUsername());

        // Verify that saveUser  was called for each user
        verify(userRepository, times(3)).save(any(Users.class));
    }
}