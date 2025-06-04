package com.hotelchain.usermanagement.repository;

import com.hotelchain.usermanagement.entity.User;
import com.hotelchain.usermanagement.entity.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByUserType(UserType userType);
    List<User> findByIsActive(Boolean isActive);

    @Query("SELECT u FROM User u WHERE u.userType = :userType AND u.isActive = :isActive")
    List<User> findByUserTypeAndIsActive(@Param("userType") UserType userType,
                                         @Param("isActive") Boolean isActive);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}