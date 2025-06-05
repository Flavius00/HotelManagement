package com.hotelchain.userservice.dto;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String role;
    private Long hotelId;
    private boolean active;
}