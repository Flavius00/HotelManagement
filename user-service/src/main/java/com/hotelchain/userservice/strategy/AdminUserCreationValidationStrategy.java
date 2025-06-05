package com.hotelchain.userservice.strategy;

import com.hotelchain.userservice.dto.CreateUserRequest;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Strategy for admin user creation validation
 */
public class AdminUserCreationValidationStrategy implements UserValidationStrategy {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");

    @Override
    public ValidationResult validate(Object userRequest) {
        if (!(userRequest instanceof CreateUserRequest)) {
            return new ValidationResult(false, List.of("Invalid request type for admin user creation"));
        }

        CreateUserRequest request = (CreateUserRequest) userRequest;
        ValidationResult result = new ValidationResult(true);

        // Username validation - stricter for admin creation
        if (request.getUsername() == null || request.getUsername().trim().length() < 4) {
            result.addError("Username must be at least 4 characters long");
        }

        // Check for prohibited usernames
        if (request.getUsername() != null && isProhibitedUsername(request.getUsername())) {
            result.addError("Username is not allowed");
        }

        // Password validation - stricter
        if (request.getPassword() == null || request.getPassword().length() < 8) {
            result.addError("Password must be at least 8 characters long");
        }

        if (request.getPassword() != null && !isStrongPassword(request.getPassword())) {
            result.addError("Password must contain uppercase, lowercase, digit, and special character");
        }

        // Email validation
        if (request.getEmail() == null || !EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            result.addError("Valid email address is required");
        }

        // Role validation
        if (request.getRole() != null) {
            if (!isValidRole(request.getRole())) {
                result.addError("Invalid role specified");
            }
        }

        // Hotel ID validation for employees/managers
        if (("EMPLOYEE".equalsIgnoreCase(request.getRole()) || "MANAGER".equalsIgnoreCase(request.getRole()))
                && request.getHotelId() == null) {
            result.addError("Hotel ID is required for employee/manager accounts");
        }

        return result;
    }

    private boolean isProhibitedUsername(String username) {
        String[] prohibited = {"admin", "administrator", "root", "system", "test", "demo"};
        String lowerUsername = username.toLowerCase();
        for (String p : prohibited) {
            if (lowerUsername.contains(p)) {
                return true;
            }
        }
        return false;
    }

    private boolean isStrongPassword(String password) {
        return password.chars().anyMatch(Character::isUpperCase) &&
                password.chars().anyMatch(Character::isLowerCase) &&
                password.chars().anyMatch(Character::isDigit) &&
                password.chars().anyMatch(ch -> "!@#$%^&*()_+-=[]{}|;':\",./<>?".indexOf(ch) >= 0);
    }

    private boolean isValidRole(String role) {
        try {
            return role.matches("^(CLIENT|EMPLOYEE|MANAGER|ADMIN)$");
        } catch (Exception e) {
            return false;
        }
    }
}