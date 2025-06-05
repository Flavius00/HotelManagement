package com.hotelchain.userservice.strategy;

import com.hotelchain.userservice.dto.CreateUserRequest;
import com.hotelchain.userservice.dto.RegisterRequest;
import org.springframework.stereotype.Component;

/**
 * Context class that uses the validation strategies
 */
@Component
public class UserValidationContext {

    public ValidationResult validateUserRegistration(RegisterRequest request) {
        UserValidationStrategy strategy = new ClientRegistrationValidationStrategy();
        return strategy.validate(request);
    }

    public ValidationResult validateAdminUserCreation(CreateUserRequest request) {
        UserValidationStrategy strategy = new AdminUserCreationValidationStrategy();
        return strategy.validate(request);
    }

    public ValidationResult validateEmployeeUpdate(Object request) {
        UserValidationStrategy strategy = new EmployeeUpdateValidationStrategy();
        return strategy.validate(request);
    }

    // Factory method to get strategy based on operation type
    public UserValidationStrategy getValidationStrategy(String operationType) {
        switch (operationType.toLowerCase()) {
            case "register":
            case "client_registration":
                return new ClientRegistrationValidationStrategy();

            case "admin_create":
            case "admin_user_creation":
                return new AdminUserCreationValidationStrategy();

            case "employee_update":
            case "profile_update":
                return new EmployeeUpdateValidationStrategy();

            default:
                return new ClientRegistrationValidationStrategy(); // Default strategy
        }
    }
}