package com.central.reward_service.exception;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.openapitools.model.ErrorResponse;

/**
 * Global exception handler for the application.
 * Centralizes exception handling across all @Controller components.
 * Converts exceptions into appropriate HTTP responses with standardized error formats.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation errors and invalid input parameters.
     *
     * @param ex the caught IllegalArgumentException
     * @return ResponseEntity with HTTP 400 Bad Request status and error message
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalStateException ex) {
        Double errorCode = 400.06;
        String description = "Transaction has already been awarded a reward";
        String errorType = HttpStatus.BAD_REQUEST.getReasonPhrase();
        String errorMessage = ex.getMessage();
        return generateErrorResponse(errorCode, description, errorType, errorMessage, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles database integrity violations (e.g., unique constraint violations).
     *
     * @param ex the caught DataIntegrityViolationException
     * @return ResponseEntity with HTTP 409 Conflict status and error message
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleConflict(DataIntegrityViolationException ex) {
        Double errorCode = 400.02;
        String description = "Data integrity violation";
        String errorType = HttpStatus.CONFLICT.getReasonPhrase();
        String errorMessage = ex.getMostSpecificCause().getMessage();
        return generateErrorResponse(errorCode, description, errorType, errorMessage, HttpStatus.CONFLICT);
    }

    /**
     * Handles cases where a requested user cannot be found.
     *
     * @param ex the caught UserDoesNotExistException
     * @return ResponseEntity with HTTP 404 Not Found status and error details
     */
    @ExceptionHandler(RewardClaimException.class)
    public ResponseEntity<ErrorResponse> handleUserDoesNotExistException(RewardClaimException ex) {
        Double errorCode = 404.06;
        String description = "Reward Already claimed or Reward expired";
        String errorType = HttpStatus.IM_USED.getReasonPhrase();
        String errorMessage = ex.getMessage();

        return generateErrorResponse(errorCode, description, errorType, errorMessage, HttpStatus.NOT_FOUND);
    }

    /**
     * Helper method to generate a standardized error response.
     *
     * @param errorCode the HTTP status code
     * @param description a brief description of the error type
     * @param errorType the HTTP status reason phrase
     * @param errorMessage detailed error message for debugging
     * @return ResponseEntity containing the error details
     */
    private ResponseEntity<ErrorResponse> generateErrorResponse(
            Double errorCode,
            String description,
            String errorType,
            String errorMessage,
            HttpStatus httpStatus) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(errorCode)
                .description(description)
                .errorType(errorType)
                .errorMessage(errorMessage)
                .build();

        return ResponseEntity.status(httpStatus).body(errorResponse);
    }
}
