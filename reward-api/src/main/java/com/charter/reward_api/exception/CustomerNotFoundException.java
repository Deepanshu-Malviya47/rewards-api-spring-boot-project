package com.charter.reward_api.exception;

/**
 * Exception thrown when a customer is not found in the database.
 */
public class CustomerNotFoundException extends RuntimeException {
    /**
     * Constructs a new CustomerNotFoundException with a message containing the customer ID.
     *
     * @param customerId the ID of the customer that was not found
     */
    public CustomerNotFoundException(Long customerId) {
        super("Customer not found with id: " + customerId);
    }
}
