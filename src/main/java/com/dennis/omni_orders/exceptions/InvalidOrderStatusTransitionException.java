package com.dennis.omni_orders.exceptions;

/**
 * InvalidOrderStatusTransitionException
 *
 * Purpose:
 * This is a custom exception used to represent invalid changes
 * to an order's status.
 *
 * Why this exists:
 * - Separates business logic errors from generic errors
 * - Allows us to return a specific HTTP status (409 Conflict)
 * - Makes error handling cleaner and more readable
 *
 * Example scenarios:
 * - Trying to change a COMPLETED order
 * - Trying to change a CANCELLED order
 * - Trying to set the same status again
 *
 * Important:
 * This extends RuntimeException, so it does NOT need to be declared
 * in method signatures (no "throws" required).
 *
 * Flow:
 * Service layer throws this →
 * GlobalExceptionHandler catches it →
 * Returns clean JSON error response
 */
public class InvalidOrderStatusTransitionException extends RuntimeException {

    /**
     * Constructor
     *
     * Takes a message describing the specific error.
     *
     * Example:
     * throw new InvalidOrderStatusTransitionException(
     *     "Cannot change status of a completed order"
     * );
     *
     * This message is later returned to the client.
     */
    public InvalidOrderStatusTransitionException(String message) {
        super(message);
    }
}