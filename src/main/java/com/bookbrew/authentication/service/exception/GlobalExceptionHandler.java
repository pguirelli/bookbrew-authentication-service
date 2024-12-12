package com.bookbrew.authentication.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
                        ResourceNotFoundException ex, WebRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                                "Resource Not Found",
                                ex.getMessage(),
                                request.getDescription(false));
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ErrorResponse> handleBadRequestException(
                        BadRequestException ex, WebRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                                "Bad Request",
                                ex.getMessage(),
                                request.getDescription(false));
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationExceptions(
                        MethodArgumentNotValidException ex, WebRequest request) {
                StringBuilder errorMessage = new StringBuilder("Validation failed for fields: ");

                if (ex.getBindingResult().getFieldErrors().size() > 1) {
                        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
                                errorMessage.append("['")
                                                .append(error.getField())
                                                .append("' - ")
                                                .append(error.getDefaultMessage())
                                                .append("] ");
                        }
                } else {
                        FieldError error = ex.getBindingResult().getFieldErrors().get(0);
                        errorMessage.append("'")
                                        .append(error.getField())
                                        .append("' - ")
                                        .append(error.getDefaultMessage());
                }

                ErrorResponse errorResponse = new ErrorResponse(
                                "Validation Error",
                                errorMessage.toString(),
                                request.getDescription(false));
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGlobalException(
                        Exception ex, WebRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                                "Internal Server Error",
                                ex.getMessage(),
                                request.getDescription(false));
                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @ExceptionHandler(DuplicateNameException.class)
        public ResponseEntity<ErrorResponse> handleDuplicateNameException(
                        DuplicateNameException ex, WebRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(
                                "Duplicate Name",
                                ex.getMessage(),
                                request.getDescription(false));
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
}
