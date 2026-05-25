package com.example.CardGame.controllers;

import com.example.CardGame.exceptions.BadRequestException;
import com.example.CardGame.security.ExceptionResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<ExceptionResponseDto> handleBadRequestException(BadRequestException exception) {
        return convertExceptionToHttp(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<ExceptionResponseDto> handleAccessDeniedException(AccessDeniedException exception) {
        return convertExceptionToHttp(exception, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ExceptionResponseDto> handleException(Exception exception) {
        var response = convertExceptionToHttp(exception, HttpStatus.INTERNAL_SERVER_ERROR);
        exception.printStackTrace();
        return response;
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        var response = new ExceptionResponseDto();
        response.setMessage(extractDefaultMessage(exception));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private ResponseEntity<ExceptionResponseDto> convertExceptionToHttp(Exception exception, HttpStatus status) {
        var response = new ExceptionResponseDto();
        response.setMessage(exception.getMessage());
        return ResponseEntity.status(status).body(response);
    }

    private String extractDefaultMessage(MethodArgumentNotValidException exception) {
        return exception.getBindingResult().getFieldError().getDefaultMessage();
    }
}