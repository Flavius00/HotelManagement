package com.hotelchain.apigateway.security.impl;

import com.hotelchain.apigateway.security.AuthorizationHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;

@Component
@Slf4j
public class ManagerAuthorizationHandler extends AuthorizationHandler {

    @Override
    public boolean handle(HttpServletRequest request, String userType) {
        if ("MANAGER".equals(userType)) {
            String uri = request.getRequestURI();

            // Managers can view all data and statistics
            if ("GET".equals(request.getMethod()) || uri.contains("/statistics")) {
                log.info("Authorization granted for MANAGER user");
                return true;
            }

            log.warn("Authorization denied for MANAGER user accessing: {}", uri);
            return false;
        }

        if (nextHandler != null) {
            return nextHandler.handle(request, userType);
        }

        return false;
    }
}