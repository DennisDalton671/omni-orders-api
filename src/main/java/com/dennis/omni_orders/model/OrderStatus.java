package com.dennis.omni_orders.model;

/**
 * OrderStatus (Enum)
 *
 * Purpose:
 * Defines all possible states an order can be in.
 *
 * Why this exists:
 * - Restricts status values to a fixed set (no invalid strings)
 * - Improves type safety (no typos like "procesing")
 * - Makes business logic easier to enforce
 *
 * Used in:
 * - Order entity (stored in database)
 * - DTOs (API input/output)
 * - Service layer (business rules)
 *
 * Database behavior:
 * Stored as STRING (because of @Enumerated(EnumType.STRING) in Order entity)
 * Example values in DB:
 * - "PENDING"
 * - "PROCESSING"
 * - "COMPLETED"
 */
public enum OrderStatus {

    /**
     * Order has been created but not yet processed
     *
     * This is the default state when a new order is created
     */
    PENDING,

    /**
     * Order is currently being prepared or processed
     */
    PROCESSING,

    /**
     * Order is ready for pickup by the customer
     */
    READY_FOR_PICKUP,

    /**
     * Order has been completed and fulfilled
     *
     * Business rule:
     * - Cannot be modified after reaching this state
     */
    COMPLETED,

    /**
     * Order has been cancelled
     *
     * Business rule:
     * - Cannot be modified after reaching this state
     */
    CANCELLED
}