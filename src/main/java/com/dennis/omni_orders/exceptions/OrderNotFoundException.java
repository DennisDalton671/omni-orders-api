package com.dennis.omni_orders.exceptions;

/**
 * OrderNotFoundException
 *
 * Purpose:
 * This custom exception is thrown when an order cannot be found
 * in the database using a given ID.
 *
 * Why this exists:
 * - Provides a clear, meaningful error instead of returning null
 * - Allows the GlobalExceptionHandler to return a proper 404 response
 * - Keeps service logic clean and readable
 *
 * Example scenario:
 * - Client requests: GET /orders/99999
 * - No order exists with that ID
 * - This exception is thrown
 *
 * Flow:
 * Service layer throws this →
 * GlobalExceptionHandler catches it →
 * Returns HTTP 404 with structured JSON response
 *
 * Why extend RuntimeException:
 * - No need to declare "throws" everywhere
 * - This is a programming/business error, not something to recover from
 */
public class OrderNotFoundException extends RuntimeException {

    /**
     * Constructor
     *
     * Takes the missing order ID and builds a helpful error message.
     *
     * Example output:
     * "Order not found with id: 99999"
     *
     * This message is later returned in the API response.
     */
    public OrderNotFoundException(Long id) {
        super("Order not found with id: " + id);
    }
}