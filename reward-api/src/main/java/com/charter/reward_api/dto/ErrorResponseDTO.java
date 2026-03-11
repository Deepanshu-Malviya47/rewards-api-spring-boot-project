package com.charter.reward_api.dto;

/**
 * Data Transfer Object for error responses.
 * Provides structured error information to API clients.
 *
 * @param status HTTP status code
 * @param error HTTP status reason phrase
 * @param message detailed error message
 */
public record ErrorResponseDTO(int status, String error, String message) {
}
