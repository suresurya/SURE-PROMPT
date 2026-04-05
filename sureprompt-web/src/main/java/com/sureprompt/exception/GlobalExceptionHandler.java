package com.sureprompt.exception;

import com.sureprompt.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleResourceNotFound(ResourceNotFoundException ex, Model model, HttpServletRequest request) {
        if (isApiRequest(request)) {
            return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
        }
        model.addAttribute("error", ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(BadRequestException.class)
    public Object handleBadRequest(BadRequestException ex, Model model, HttpServletRequest request) {
        if (isApiRequest(request)) {
            return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
        }
        model.addAttribute("error", ex.getMessage());
        return "error/400";
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleUnauthorized(UnauthorizedException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "error/401";
    }

    @ExceptionHandler(Exception.class)
    public Object handleGeneralException(Exception ex, Model model, HttpServletRequest request) {
        if (isApiRequest(request)) {
            return buildErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
        model.addAttribute("error", ex.getMessage());
        return "error/500";
    }

    private boolean isApiRequest(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api/");
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(String message, HttpStatus status, HttpServletRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .message(message)
                .status(status.value())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(error, status);
    }
}
