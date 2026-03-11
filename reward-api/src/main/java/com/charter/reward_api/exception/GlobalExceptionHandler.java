package com.charter.reward_api.exception;

import com.charter.reward_api.dto.ErrorResponseDTO;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Global exception handler for the Reward API.
 * Handles all exceptions and converts them to structured error responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles CustomerNotFoundException and returns a 404 Not Found response.
     *
     * @param ex the exception
     * @return error response with 404 status
     */
    @ExceptionHandler(CustomerNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDTO handleNotFound(CustomerNotFoundException ex) {
        return errorBody(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Handles ConstraintViolationException for validation errors and returns a 400 Bad Request response.
     *
     * @param ex the exception
     * @return error response with 400 status
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .map(cv -> cv.getPropertyPath() + " " + cv.getMessage())
                .findFirst()
                .orElse("Invalid request parameter");
        return errorBody(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * Handles MethodArgumentTypeMismatchException for invalid parameter types and returns a 400 Bad Request response.
     *
     * @param ex the exception
     * @return error response with 400 status
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return errorBody(HttpStatus.BAD_REQUEST, "Invalid parameter: " + ex.getName());
    }

    /**
     * Handles InvalidDateRangeException and returns a 400 Bad Request response.
     *
     * @param ex the exception
     * @return error response with 400 status
     */
    @ExceptionHandler(com.charter.reward_api.exception.InvalidDateRangeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleInvalidDateRange(com.charter.reward_api.exception.InvalidDateRangeException ex) {
        return errorBody(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handles all unhandled exceptions and returns a 500 Internal Server Error response.
     *
     * @param ex the exception
     * @return error response with 500 status
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDTO handleGeneric(Exception ex) {
        log.error("Unexpected error", ex);
        return errorBody(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    /**
     * Creates a standardized error response body.
     *
     * @param status HTTP status
     * @param message error message
     * @return error response DTO
     */
    private ErrorResponseDTO errorBody(HttpStatus status, String message) {
        return new ErrorResponseDTO(status.value(), status.getReasonPhrase(), message);
    }
}
