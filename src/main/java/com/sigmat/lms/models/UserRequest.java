package com.sigmat.lms.models;

import lombok.Data;

@Data
public class UserRequest {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String address;
}
