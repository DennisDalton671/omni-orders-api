package com.dennis.omni_orders.model;

// Controls JSON field order when returning responses
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

// Swagger annotation for documentation
import io.swagger.v3.oas.annotations.media.Schema;

// JPA annotations for database mapping
import jakarta.persistence.*;

// Validation annotations (still useful if entity is ever used directly)
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

/**
 * Order (Entity)
 *
 * Purpose:
 * This class represents the "orders" table in the database.
 *
 * Important:
 * - This is NOT used as API input/output anymore (DTOs handle that)
 * - This is purely the internal database model
 *
 * Relationship:
 * - Mapped directly to the "orders" table via JPA
 * - Managed by Hibernate
 * - Schema controlled by Flyway
 */
@JsonPropertyOrder({
        "id",
        "itemName",
        "quantity",
        "status",
        "createdAt",
        "updatedAt"
})
@Schema(description = "Represents an Omni order")
@Entity
@Table(name = "orders")
public class Order {

    /**
     * Primary key (ID)
     *
     * @Id → marks this as the primary key
     * @GeneratedValue → database auto-generates the ID
     *
     * IDENTITY:
     * - lets the database handle ID creation
     */
    @Schema(description = "Unique order ID", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Item name
     *
     * Validation:
     * - must not be blank
     */
    @Schema(description = "Name of the ordered item", example = "Nike Shoes")
    @NotBlank(message = "Item name is required")
    private String itemName;

    /**
     * Quantity
     *
     * Validation:
     * - must be at least 1
     */
    @Schema(description = "Quantity ordered", example = "2")
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    /**
     * Order status (enum)
     *
     * @Enumerated(EnumType.STRING):
     * - stores enum as a readable string in DB (e.g., "PENDING")
     * - safer than ordinal (numbers)
     *
     * @Column(nullable = false):
     * - DB will not allow null values
     */
    @Schema(description = "Current order status", example = "PENDING")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    /**
     * Created timestamp
     *
     * @Column(nullable = false, updatable = false):
     * - cannot be null
     * - cannot be modified after creation
     *
     * Set automatically in @PrePersist
     */
    @Schema(description = "Timestamp when the order was created", example = "2026-03-28T18:45:00", accessMode = Schema.AccessMode.READ_ONLY)
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Updated timestamp
     *
     * Updated every time the entity is modified
     */
    @Schema(description = "Timestamp when the order was last updated", example = "2026-03-28T19:00:00", accessMode = Schema.AccessMode.READ_ONLY)
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Default constructor
     *
     * Required by:
     * - JPA (Hibernate)
     * - Jackson (if ever used)
     */
    public Order() {
    }

    /**
     * Convenience constructor
     *
     * Used when manually creating orders in code
     */
    public Order(String itemName, int quantity, OrderStatus status) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.status = status;
    }

    /**
     * @PrePersist
     *
     * Runs automatically BEFORE inserting into the database
     *
     * Responsibilities:
     * - sets createdAt
     * - sets updatedAt
     * - assigns default status if not set
     */
    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;

        // Default status logic
        if (this.status == null) {
            this.status = OrderStatus.PENDING;
        }
    }

    /**
     * @PreUpdate
     *
     * Runs automatically BEFORE updating an existing record
     *
     * Responsibility:
     * - updates the updatedAt timestamp
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ----------------------
    // Getters (used for reading data / JSON output)
    // ----------------------

    public Long getId() {
        return id;
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // ----------------------
    // Setters (used for updating entity fields)
    // ----------------------

    public void setId(Long id) {
        this.id = id;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}