package com.dennis.omni_orders.dto;

// Swagger annotation: used to describe this DTO in API documentation
import io.swagger.v3.oas.annotations.media.Schema;

// Validation annotations to enforce rules on incoming data
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * CreateOrderRequest (DTO)
 *
 * Purpose:
 * This class represents the request body for creating a new order.
 *
 * Why this exists (IMPORTANT):
 * - Prevents clients from sending unwanted fields (id, timestamps, status, etc.)
 * - Defines exactly what input is allowed
 * - Acts as a contract between client and API
 *
 * This replaces using the Order entity directly in the request.
 */
@Schema(description = "Request body for creating a new order")
public class CreateOrderRequest {

    /**
     * Item name
     *
     * Validation:
     * - Must not be null
     * - Must not be empty or blank
     *
     * If invalid → triggers MethodArgumentNotValidException
     * → handled by your GlobalExceptionHandler → returns 400
     */
    @Schema(description = "Name of the ordered item", example = "Nike Shoes")
    @NotBlank(message = "Item name is required")
    private String itemName;

    /**
     * Quantity
     *
     * Validation:
     * - Must be at least 1
     *
     * Prevents:
     * - zero quantity
     * - negative values
     */
    @Schema(description = "Quantity ordered", example = "2")
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    /**
     * Default constructor
     *
     * Required by:
     * - Jackson (JSON → Java object conversion)
     */
    public CreateOrderRequest() {
    }

    /**
     * Getter for itemName
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * Getter for quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Setter for itemName
     *
     * Used by:
     * - Jackson during request body parsing
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    /**
     * Setter for quantity
     *
     * Used by:
     * - Jackson during request body parsing
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}