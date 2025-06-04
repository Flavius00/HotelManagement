package com.hotelchain.usermanagement.service;

import com.hotelchain.usermanagement.dto.*;
import com.hotelchain.usermanagement.entity.User;
import com.hotelchain.usermanagement.entity.UserType;
import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDTO createUser(UserRegistrationDTO registrationDTO);
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
    Optional<UserDTO> getUserById(Long id);
    Optional<UserDTO> getUserByUsername(String username);
    List<UserDTO> getAllUsers();
    List<UserDTO> getUsersByType(UserType userType);
    List<UserDTO> getActiveUsers();
    boolean authenticateUser(LoginDTO loginDTO);
    void deactivateUser(Long id);
    void activateUser(Long id);
}