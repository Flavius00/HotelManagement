package com.hotelchain.apigateway.security;

import com.hotelchain.apigateway.security.impl.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorizationChainFactory {

    private final ClientAuthorizationHandler clientHandler;
    private final EmployeeAuthorizationHandler employeeHandler;
    private final ManagerAuthorizationHandler managerHandler;
    private final AdministratorAuthorizationHandler administratorHandler;

    public AuthorizationHandler createChain() {
        // Set up the chain of responsibility
        clientHandler.setNextHandler(employeeHandler);
        employeeHandler.setNextHandler(managerHandler);
        managerHandler.setNextHandler(administratorHandler);

        return clientHandler;
    }
}