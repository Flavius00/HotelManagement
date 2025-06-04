package com.hotelchain.usermanagement.dto;

import lombok.*;
import com.hotelchain.usermanagement.entity.UserType;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastName;

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phoneNumber;

    @NotNull(message = "User type is required")
    private UserType userType;

    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<NotificationPreferenceDTO> notificationPreferences;
}