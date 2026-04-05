package com.sureprompt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Principal;

@Component
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);
        } finally {
            if (request.getRequestURI().startsWith("/api/")) {
                long duration = System.currentTimeMillis() - startTime;
                
                String user = "ANONYMOUS";
                Principal principal = request.getUserPrincipal();
                if (principal != null) {
                    user = principal.getName();
                }

                log.info("API REQUEST => User: {} | Method: {} | Path: {} | Status: {} | Latency: {}ms",
                        user,
                        request.getMethod(),
                        request.getRequestURI(),
                        response.getStatus(),
                        duration);
            }
        }
    }
}
