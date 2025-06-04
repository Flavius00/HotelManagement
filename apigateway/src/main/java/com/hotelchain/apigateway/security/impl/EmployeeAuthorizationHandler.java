package com.hotelchain.apigateway.security.impl;

import com.hotelchain.apigateway.security.AuthorizationHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;

@Component
@Slf4j
public class EmployeeAuthorizationHandler extends AuthorizationHandler {

    @Override
    public boolean handle(HttpServletRequest request, String userType) {
        if ("EMPLOYEE".equals(userType)) {
            String uri = request.getRequestURI();

            // Employees can do CRUD on rooms, bookings, and client users
            if (uri.contains("/rooms") || uri.contains("/bookings") ||
                    (uri.contains("/users") && !uri.contains("/admin"))) {
                log.info("Authorization granted for EMPLOYEE user");
                return true;
            }

            log.warn("Authorization denied for EMPLOYEE user accessing: {}", uri);
            return false;
        }

        if (nextHandler != null) {
            return nextHandler.handle(request, userType);
        }

        return false;
    }
}