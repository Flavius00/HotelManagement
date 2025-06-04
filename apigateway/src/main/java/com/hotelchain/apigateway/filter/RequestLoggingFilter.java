package com.hotelchain.apigateway.filter;

import com.hotelchain.apigateway.service.GatewayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestLoggingFilter implements Filter {

    private final GatewayService gatewayService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        long startTime = System.currentTimeMillis();
        String endpoint = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();
        String sessionId = httpRequest.getSession().getId();

        try {
            chain.doFilter(request, response);
        } finally {
            long responseTime = System.currentTimeMillis() - startTime;
            int statusCode = httpResponse.getStatus();

            // Log the request
            gatewayService.logRequest(sessionId, endpoint, method, statusCode, responseTime, null);

            log.info("Request logged: {} {} - Status: {} - Response Time: {}ms",
                    method, endpoint, statusCode, responseTime);
        }
    }
}