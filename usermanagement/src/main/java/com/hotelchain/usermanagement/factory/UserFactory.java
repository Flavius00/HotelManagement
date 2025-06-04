package com.hotelchain.usermanagement.factory;

import com.hotelchain.usermanagement.entity.User;
import com.hotelchain.usermanagement.entity.UserType;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {

    public User createUser(String username, String email, String password, UserType userType) {
        return User.builder()
                .username(username)
                .email(email)
                .password(password)
                .userType(userType)
                .build();
    }

    public User createClient(String username, String email, String password) {
        return createUser(username, email, password, UserType.CLIENT);
    }

    public User createEmployee(String username, String email, String password) {
        return createUser(username, email, password, UserType.EMPLOYEE);
    }

    public User createManager(String username, String email, String password) {
        return createUser(username, email, password, UserType.MANAGER);
    }

    public User createAdministrator(String username, String email, String password) {
        return createUser(username, email, password, UserType.ADMINISTRATOR);
    }
}