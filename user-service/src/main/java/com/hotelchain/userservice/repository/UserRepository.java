package com.hotelchain.userservice.repository;

import com.hotelchain.userservice.entity.User;
import com.hotelchain.userservice.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findByRole(UserRole role);
    List<User> findByActive(boolean active);
    List<User> findByHotelId(Long hotelId);
}