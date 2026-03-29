package com.dennis.omni_orders.controller;

import com.dennis.omni_orders.dto.CreateOrderRequest;
import com.dennis.omni_orders.dto.OrderResponse;
import com.dennis.omni_orders.model.OrderStatus;
import com.dennis.omni_orders.model.UpdateStatusRequest;
import com.dennis.omni_orders.service.OrderService;

// Swagger annotations for documentation
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

// Validation annotation to enforce DTO constraints
import jakarta.validation.Valid;

// Spring Data pagination support
import org.springframework.data.domain.Page;

// Used to control HTTP responses (status codes + body)
import org.springframework.http.ResponseEntity;

// Spring REST annotations
import org.springframework.web.bind.annotation.*;

/**
 * OrderController
 *
 * Purpose:
 * This is the main REST controller for managing orders.
 *
 * Responsibilities:
 * - Handles incoming HTTP requests
 * - Validates input (DTOs)
 * - Delegates logic to OrderService
 * - Returns properly formatted responses
 *
 * Important:
 * This class does NOT contain business logic.
 * It only coordinates requests and responses.
 *
 * All endpoints are prefixed with /orders
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    /**
     * Service layer dependency
     *
     * Why:
     * - Controller should not contain business logic
     * - Service handles validation, rules, and DB interaction
     */
    private final OrderService orderService;

    /**
     * Constructor injection (recommended approach)
     *
     * Spring automatically injects OrderService here
     */
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * GET /orders
     *
     * Purpose:
     * Retrieves a paginated list of orders.
     *
     * Features:
     * - Pagination (page, size)
     * - Sorting (sortBy, direction)
     * - Optional filtering by status
     *
     * Returns:
     * Page<OrderResponse>
     * (Spring Data pagination wrapper)
     *
     * Example:
     * GET /orders?page=0&size=5&sortBy=createdAt&direction=desc
     */
    @Operation(
            summary = "Get all orders",
            description = "Returns a paginated list of orders with optional sorting and status filtering."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid paging, sorting, or filter parameters")
    })
    @GetMapping
    public Page<OrderResponse> getOrders(

            // Page number (0-based)
            @Parameter(description = "Page number starting at 0", example = "0")
            @RequestParam(defaultValue = "0") int page,

            // Number of results per page
            @Parameter(description = "Number of orders per page", example = "5")
            @RequestParam(defaultValue = "5") int size,

            // Field used for sorting
            @Parameter(description = "Field to sort by: id, itemName, quantity, status, createdAt, updatedAt", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sortBy,

            // Sorting direction
            @Parameter(description = "Sort direction: asc or desc", example = "desc")
            @RequestParam(defaultValue = "desc") String direction,

            // Optional filter by order status
            @Parameter(description = "Optional order status filter", example = "PENDING")
            @RequestParam(required = false) OrderStatus status
    ) {
        // Delegate logic to service layer
        return orderService.getOrders(page, size, sortBy, direction, status);
    }

    /**
     * GET /orders/{id}
     *
     * Purpose:
     * Retrieves a single order by its ID.
     *
     * Behavior:
     * - Returns 200 if found
     * - Throws exception → handled globally → returns 404 if not found
     */
    @Operation(
            summary = "Get order by ID",
            description = "Returns a single order using its unique ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(

            // Path variable (order ID)
            @Parameter(description = "Unique order ID", example = "1")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    /**
     * POST /orders
     *
     * Purpose:
     * Creates a new order.
     *
     * Input:
     * CreateOrderRequest DTO
     *
     * Important:
     * - Only allows itemName + quantity (DTO controlled)
     * - Prevents clients from setting internal fields (id, timestamps, etc.)
     *
     * Validation:
     * @Valid ensures:
     * - itemName is not blank
     * - quantity >= 1
     */
    @Operation(
            summary = "Create a new order",
            description = "Creates a new Omni order. New orders default to PENDING status."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed")
    })
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(

            // Validates request body against DTO constraints
            @Valid @RequestBody CreateOrderRequest request
    ) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    /**
     * PUT /orders/{id}/status
     *
     * Purpose:
     * Updates the status of an existing order.
     *
     * Input:
     * UpdateStatusRequest DTO
     *
     * Business rules enforced in service:
     * - Cannot update COMPLETED orders
     * - Cannot update CANCELLED orders
     * - Cannot set same status again
     *
     * Possible responses:
     * - 200 → success
     * - 400 → invalid request
     * - 404 → order not found
     * - 409 → invalid status transition
     */
    @Operation(
            summary = "Update order status",
            description = "Updates the status of an existing order. Business rules may block invalid status transitions."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or invalid status value"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "409", description = "Invalid order status transition")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(

            @Parameter(description = "Unique order ID", example = "1")
            @PathVariable Long id,

            @Valid @RequestBody UpdateStatusRequest request
    ) {
        return ResponseEntity.ok(orderService.updateStatus(id, request.getStatus()));
    }

    /**
     * DELETE /orders/{id}
     *
     * Purpose:
     * Deletes an order by ID.
     *
     * Behavior:
     * - Returns 204 (no content) on success
     * - Returns 404 if order does not exist
     *
     * Why 204:
     * - Standard REST practice for successful delete
     * - No response body needed
     */
    @Operation(
            summary = "Delete an order",
            description = "Deletes an order by its unique ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(

            @Parameter(description = "Unique order ID", example = "1")
            @PathVariable Long id
    ) {
        orderService.deleteOrder(id);

        // 204 No Content = success with no response body
        return ResponseEntity.noContent().build();
    }
}