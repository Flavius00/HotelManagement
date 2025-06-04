package com.hotelchain.apigateway.security;

import jakarta.servlet.http.HttpServletRequest;

public abstract class AuthorizationHandler {
    protected AuthorizationHandler nextHandler;

    public void setNextHandler(AuthorizationHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    public abstract boolean handle(HttpServletRequest request, String userType);
}