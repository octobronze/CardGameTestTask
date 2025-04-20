package com.example.CardGame.security;

import com.example.CardGame.security.jwt.JwtException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        ExceptionResponseDto responseDto = new ExceptionResponseDto();
        if (exception instanceof JwtException) {
            responseDto.setMessage("Invalid JWT");
            response.getWriter().write(objectMapper.writeValueAsString(responseDto));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else if (exception instanceof InsufficientAuthenticationException){
            responseDto.setMessage("Unauthorized. Is token provided?");
            response.getWriter().write(objectMapper.writeValueAsString(responseDto));
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception) throws IOException, ServletException {
        ExceptionResponseDto responseDto = new ExceptionResponseDto();
        if (exception instanceof AuthorizationDeniedException) {
            responseDto.setMessage("Access denied. Does user have permission to access resource?");
           response.getWriter().write(objectMapper.writeValueAsString(responseDto));
           response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
