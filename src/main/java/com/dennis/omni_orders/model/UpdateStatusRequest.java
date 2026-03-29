package com.dennis.omni_orders.model;

// Swagger annotation for documenting request body in API docs
import io.swagger.v3.oas.annotations.media.Schema;

// Validation annotation to ensure required field is present
import jakarta.validation.constraints.NotNull;

/**
 * UpdateStatusRequest (DTO)
 *
 * Purpose:
 * This class represents the request body used when updating
 * the status of an existing order.
 *
 * Why this exists:
 * - Restricts the update operation to ONLY status changes
 * - Prevents accidental modification of other fields (itemName, quantity, etc.)
 * - Keeps API endpoints focused and intentional
 *
 * Example usage:
 * PUT /orders/{id}/status
 *
 * Request body:
 * {
 *   "status": "PROCESSING"
 * }
 *
 * Important:
 * This is a focused DTO for a specific action (status update),
 * not a full order update.
 */
@Schema(description = "Request body for updating an order's status")
public class UpdateStatusRequest {

    /**
     * New status for the order
     *
     * Validation:
     * - Must not be null
     *
     * Enum behavior:
     * - Only allows valid OrderStatus values
     * - Invalid values (e.g., "BANANA") will trigger a parsing error
     *   handled by GlobalExceptionHandler
     *
     * Swagger:
     * - Displays allowed values in UI
     */
    @Schema(
            description = "New status to apply to the order",
            example = "PROCESSING",
            allowableValues = {
                    "PENDING",
                    "PROCESSING",
                    "READY_FOR_PICKUP",
                    "COMPLETED",
                    "CANCELLED"
            }
    )
    @NotNull(message = "Status is required")
    private OrderStatus status;

    /**
     * Default constructor
     *
     * Required by:
     * - Jackson (for JSON → Java object mapping)
     */
    public UpdateStatusRequest() {
    }

    /**
     * Getter for status
     *
     * Used by:
     * - Controller → Service layer
     */
    public OrderStatus getStatus() {
        return status;
    }

    /**
     * Setter for status
     *
     * Used by:
     * - Jackson during request body parsing
     */
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}