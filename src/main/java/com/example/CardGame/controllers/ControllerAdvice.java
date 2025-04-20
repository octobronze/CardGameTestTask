package com.example.CardGame.controllers;

import com.example.CardGame.exceptions.BadRequestException;
import com.example.CardGame.security.ExceptionResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<ExceptionResponseDto> handleBadRequestException(BadRequestException exception) {
        ExceptionResponseDto response = new ExceptionResponseDto();

        response.setMessage(exception.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ExceptionResponseDto> handleException(Exception exception) {
        ExceptionResponseDto response = new ExceptionResponseDto();

        exception.printStackTrace();
        response.setMessage(exception.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        ExceptionResponseDto response = new ExceptionResponseDto();

        response.setMessage(extractDefaultMessageFromValidationException(exception));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<ExceptionResponseDto> handleAccessDeniedException(AccessDeniedException exception) {
        ExceptionResponseDto response = new ExceptionResponseDto();

        response.setMessage(exception.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    private String extractDefaultMessageFromValidationException(MethodArgumentNotValidException exception) {
        return Objects.requireNonNull(exception.getBindingResult().getFieldError()).getDefaultMessage();
    }
}