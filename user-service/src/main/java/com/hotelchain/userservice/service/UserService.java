package com.hotelchain.userservice.service;

import com.hotelchain.userservice.dto.*;
import com.hotelchain.userservice.entity.User;
import com.hotelchain.userservice.entity.UserRole;
import com.hotelchain.userservice.repository.UserRepository;
import com.hotelchain.userservice.strategy.UserValidationContext;
import com.hotelchain.userservice.strategy.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserValidationContext validationContext;

    @Autowired
    private NotificationService notificationService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (!user.isActive()) {
            throw new RuntimeException("Account is deactivated");
        }

        String token = jwtService.generateToken(user.getUsername(),
                user.getRole().name(),
                user.getId(),
                user.getHotelId());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setRole(user.getRole().name());
        response.setUserId(user.getId());
        response.setHotelId(user.getHotelId());
        response.setUsername(user.getUsername());

        return response;
    }

    public UserDto register(RegisterRequest request) {
        // Use Strategy Pattern for validation
        ValidationResult validation = validationContext.validateUserRegistration(request);
        if (!validation.isValid()) {
            throw new RuntimeException("Validation failed: " + String.join(", ", validation.getErrors()));
        }

        // Verifică dacă username-ul există deja
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRole(UserRole.CLIENT); // Doar clienți prin înregistrare publică

        user = userRepository.save(user);
        return convertToDto(user);
    }

    public TokenValidationResponse validateToken(String token) {
        TokenValidationResponse response = new TokenValidationResponse();

        try {
            var claims = jwtService.extractClaims(token);
            response.setValid(true);
            response.setUsername(claims.getSubject());
            response.setRole(claims.get("role", String.class));
            response.setUserId(claims.get("userId", Long.class));
            response.setHotelId(claims.get("hotelId", Long.class));
        } catch (Exception e) {
            response.setValid(false);
        }

        return response;
    }

    public List<UserDto> getAllUsers(String token) {
        validateAdminRole(token);
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> getUsersByRole(String role, String token) {
        validateAdminRole(token);
        try {
            UserRole userRole = UserRole.valueOf(role.toUpperCase());
            return userRepository.findByRole(userRole).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role: " + role);
        }
    }

    public UserDto getUserById(Long id, String token) {
        validateAdminRole(token);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDto(user);
    }

    public UserDto updateUser(Long id, UpdateUserRequest request, String token) {
        validateAdminRole(token);

        // Use Strategy Pattern for validation
        ValidationResult validation = validationContext.validateEmployeeUpdate(request);
        if (!validation.isValid()) {
            throw new RuntimeException("Validation failed: " + String.join(", ", validation.getErrors()));
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Store original values for change notification
        Map<String, Object> changes = new HashMap<>();
        String originalEmail = user.getEmail();
        String originalPhone = user.getPhone();

        // Update fields if provided
        if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
            // Check if username already exists for another user
            userRepository.findByUsername(request.getUsername())
                    .ifPresent(existingUser -> {
                        if (!existingUser.getId().equals(id)) {
                            throw new RuntimeException("Username already exists");
                        }
                    });
            if (!user.getUsername().equals(request.getUsername().trim())) {
                changes.put("username", request.getUsername().trim());
                user.setUsername(request.getUsername().trim());
            }
        }

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            if (!user.getEmail().equals(request.getEmail().trim())) {
                changes.put("email", request.getEmail().trim());
                user.setEmail(request.getEmail().trim());
            }
        }

        if (request.getPhone() != null) {
            String newPhone = request.getPhone().trim().isEmpty() ? null : request.getPhone().trim();
            if (!java.util.Objects.equals(user.getPhone(), newPhone)) {
                changes.put("phone", newPhone);
                user.setPhone(newPhone);
            }
        }

        if (request.getRole() != null && !request.getRole().trim().isEmpty()) {
            try {
                UserRole role = UserRole.valueOf(request.getRole().toUpperCase());
                if (!user.getRole().equals(role)) {
                    changes.put("role", role.name());
                    user.setRole(role);
                }
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid role: " + request.getRole());
            }
        }

        if (request.getHotelId() != null) {
            if (!java.util.Objects.equals(user.getHotelId(), request.getHotelId())) {
                changes.put("hotelId", request.getHotelId());
                user.setHotelId(request.getHotelId());
            }
        }

        if (request.getActive() != null) {
            if (user.isActive() != request.getActive()) {
                changes.put("active", request.getActive());
                user.setActive(request.getActive());
            }
        }

        // Update password if provided
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            changes.put("password", "updated");
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        user = userRepository.save(user);

        // Send notifications if there were changes
        if (!changes.isEmpty()) {
            String changeDescription = notificationService.generateChangeDescription(changes);
            notificationService.sendUserUpdateNotifications(
                    user.getEmail(),
                    user.getPhone(),
                    user.getUsername(),
                    changeDescription
            );
        }

        return convertToDto(user);
    }

    public UserDto createUser(CreateUserRequest request, String token) {
        validateAdminRole(token);

        // Use Strategy Pattern for validation
        ValidationResult validation = validationContext.validateAdminUserCreation(request);
        if (!validation.isValid()) {
            throw new RuntimeException("Validation failed: " + String.join(", ", validation.getErrors()));
        }

        // Check if username already exists
        if (userRepository.findByUsername(request.getUsername().trim()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail().trim());
        user.setPhone(request.getPhone() != null ? request.getPhone().trim() : null);

        // Set role (default to CLIENT if not specified)
        UserRole role = UserRole.CLIENT;
        if (request.getRole() != null && !request.getRole().trim().isEmpty()) {
            try {
                role = UserRole.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid role: " + request.getRole());
            }
        }
        user.setRole(role);

        user.setHotelId(request.getHotelId());
        user.setActive(true);

        user = userRepository.save(user);

        // Send welcome notification
        notificationService.sendUserUpdateNotifications(
                user.getEmail(),
                user.getPhone(),
                user.getUsername(),
                "Account created successfully"
        );

        return convertToDto(user);
    }

    public void deleteUser(Long id, String token) {
        validateAdminRole(token);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Soft delete - just set active to false
        user.setActive(false);
        userRepository.save(user);

        // Send deactivation notification
        notificationService.sendUserUpdateNotifications(
                user.getEmail(),
                user.getPhone(),
                user.getUsername(),
                "Account has been deactivated"
        );
    }

    public UserDto reactivateUser(Long id, String token) {
        validateAdminRole(token);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setActive(true);
        user = userRepository.save(user);

        // Send reactivation notification
        notificationService.sendUserUpdateNotifications(
                user.getEmail(),
                user.getPhone(),
                user.getUsername(),
                "Account has been reactivated"
        );

        return convertToDto(user);
    }

    // Additional method to demonstrate strategy pattern usage
    public ValidationResult validateUserData(Object userRequest, String operationType) {
        return validationContext.getValidationStrategy(operationType).validate(userRequest);
    }

    private void validateAdminRole(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        var claims = jwtService.extractClaims(token);
        String role = claims.get("role", String.class);

        if (!"ADMIN".equals(role)) {
            throw new RuntimeException("Access denied - Admin role required");
        }
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole().name());
        dto.setHotelId(user.getHotelId());
        dto.setActive(user.isActive());
        return dto;
    }
}