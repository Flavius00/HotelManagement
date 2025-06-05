package com.hotelchain.userservice.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String role;
    private Long userId;
    private Long hotelId;
    private String username;
}