package com.hotelchain.usermanagement.service.impl;

import com.hotelchain.usermanagement.dto.*;
import com.hotelchain.usermanagement.entity.User;
import com.hotelchain.usermanagement.entity.UserType;
import com.hotelchain.usermanagement.repository.UserRepository;
import com.hotelchain.usermanagement.service.UserService;
import com.hotelchain.usermanagement.service.NotificationService;
import com.hotelchain.usermanagement.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    @Override
    public UserDTO createUser(UserRegistrationDTO registrationDTO) {
        log.info("Creating new user with username: {}", registrationDTO.getUsername());

        if (userRepository.existsByUsername(registrationDTO.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .username(registrationDTO.getUsername())
                .password(passwordEncoder.encode(registrationDTO.getPassword()))
                .email(registrationDTO.getEmail())
                .firstName(registrationDTO.getFirstName())
                .lastName(registrationDTO.getLastName())
                .phoneNumber(registrationDTO.getPhoneNumber())
                .userType(registrationDTO.getUserType())
                .build();

        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());

        return userMapper.toDTO(savedUser);
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        log.info("Updating user with ID: {}", id);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User oldUser = User.builder()
                .id(existingUser.getId())
                .username(existingUser.getUsername())
                .email(existingUser.getEmail())
                .build();

        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());
        existingUser.setPhoneNumber(userDTO.getPhoneNumber());
        existingUser.setEmail(userDTO.getEmail());

        User updatedUser = userRepository.save(existingUser);

        // Send notification if email or username changed
        if (!oldUser.getEmail().equals(updatedUser.getEmail()) ||
                !oldUser.getUsername().equals(updatedUser.getUsername())) {
            notificationService.notifyUserInfoChange(updatedUser.getId(), "Profile information updated");
        }

        log.info("User updated successfully with ID: {}", updatedUser.getId());
        return userMapper.toDTO(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);

        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }

        userRepository.deleteById(id);
        log.info("User deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByType(UserType userType) {
        return userRepository.findByUserType(userType).stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getActiveUsers() {
        return userRepository.findByIsActive(true).stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean authenticateUser(LoginDTO loginDTO) {
        Optional<User> userOpt = userRepository.findByUsername(loginDTO.getUsername());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return user.getIsActive() &&
                    passwordEncoder.matches(loginDTO.getPassword(), user.getPassword());
        }

        return false;
    }

    @Override
    public void deactivateUser(Long id) {
        log.info("Deactivating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsActive(false);
        userRepository.save(user);

        notificationService.notifyUserInfoChange(id, "Your account has been deactivated");
        log.info("User deactivated successfully with ID: {}", id);
    }

    @Override
    public void activateUser(Long id) {
        log.info("Activating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setIsActive(true);
        userRepository.save(user);

        notificationService.notifyUserInfoChange(id, "Your account has been activated");
        log.info("User activated successfully with ID: {}", id);
    }
}