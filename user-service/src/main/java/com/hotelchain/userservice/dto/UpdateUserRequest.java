package com.hotelchain.userservice.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String username;
    private String email;
    private String phone;
    private String role;
    private Long hotelId;
    private Boolean active;
    private String password;
}