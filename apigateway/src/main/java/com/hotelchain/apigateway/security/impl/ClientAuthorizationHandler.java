package com.hotelchain.apigateway.security.impl;

import com.hotelchain.apigateway.security.AuthorizationHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;

@Component
@Slf4j
public class ClientAuthorizationHandler extends AuthorizationHandler {

    @Override
    public boolean handle(HttpServletRequest request, String userType) {
        if ("CLIENT".equals(userType)) {
            String uri = request.getRequestURI();
            String method = request.getMethod();

            // Clients can only add reviews (POST to /reviews) and view data (GET requests)
            if ("GET".equals(method) ||
                    ("POST".equals(method) && uri.contains("/reviews"))) {
                log.info("Authorization granted for CLIENT user");
                return true;
            }

            log.warn("Authorization denied for CLIENT user accessing: {} {}", method, uri);
            return false;
        }

        if (nextHandler != null) {
            return nextHandler.handle(request, userType);
        }

        return false;
    }
}