package com.hotelchain.apigateway.security.impl;

import com.hotelchain.apigateway.security.AuthorizationHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;

@Component
@Slf4j
public class AdministratorAuthorizationHandler extends AuthorizationHandler {

    @Override
    public boolean handle(HttpServletRequest request, String userType) {
        if ("ADMINISTRATOR".equals(userType)) {
            // Administrators have full access
            log.info("Authorization granted for ADMINISTRATOR user");
            return true;
        }

        if (nextHandler != null) {
            return nextHandler.handle(request, userType);
        }

        log.warn("Authorization denied for unknown user type: {}", userType);
        return false;
    }
}