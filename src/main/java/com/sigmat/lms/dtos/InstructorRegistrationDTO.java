package com.sigmat.lms.dtos;

import lombok.Data;

@Data
public class InstructorRegistrationDTO {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNo;
    private String bankName;
    private String accountNumber;
    private String routingNumber;
    private String accountHolderName;
}
