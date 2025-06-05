package com.hotelchain.userservice.strategy;

/**
 * Strategy Pattern Interface for User Validation
 */
public interface UserValidationStrategy {
    ValidationResult validate(Object userRequest);
}