package com.charter.reward_api.exception;

import java.time.LocalDate;

/**
 * Exception thrown when an invalid date range is provided (start date after end date).
 */
public class InvalidDateRangeException extends RuntimeException {
    /**
     * Constructs a new InvalidDateRangeException with a message containing the invalid date range.
     *
     * @param from the start date
     * @param to the end date
     */
    public InvalidDateRangeException(LocalDate from, LocalDate to) {
        super("'from' date (" + from + ") must not be after 'to' date (" + to + ")");
    }
}
