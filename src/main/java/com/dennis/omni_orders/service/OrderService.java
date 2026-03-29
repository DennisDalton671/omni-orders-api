package com.dennis.omni_orders.service;

// DTOs used for API input/output
import com.dennis.omni_orders.dto.CreateOrderRequest;
import com.dennis.omni_orders.dto.OrderResponse;

// Custom exceptions used for business and not-found errors
import com.dennis.omni_orders.exceptions.InvalidOrderStatusTransitionException;
import com.dennis.omni_orders.exceptions.OrderNotFoundException;

// Internal database model + enum
import com.dennis.omni_orders.model.Order;
import com.dennis.omni_orders.model.OrderStatus;

// Repository used for database access
import com.dennis.omni_orders.repository.OrderRepository;

// Spring Data pagination + sorting support
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

// Marks this class as a Spring service component
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * OrderService
 *
 * Purpose:
 * This class contains the main business logic for the Omni Orders API.
 *
 * Responsibilities:
 * - validates request parameters
 * - applies business rules
 * - interacts with the repository
 * - maps entities to DTO responses
 * - throws meaningful custom exceptions
 *
 * Architecture role:
 * Controller → Service → Repository → Database
 *
 * Important:
 * This is where the "real logic" lives.
 * The controller only receives requests and returns responses.
 * The repository only talks to the database.
 * This class decides what should happen.
 */
@Service
public class OrderService {

    /**
     * Allowed fields that clients are permitted to sort by.
     *
     * Why this exists:
     * - prevents invalid field names
     * - prevents clients from trying to sort by fields that do not exist
     * - keeps sorting controlled and predictable
     */
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id",
            "itemName",
            "quantity",
            "status",
            "createdAt",
            "updatedAt"
    );

    /**
     * Repository dependency
     *
     * Used to read/write Order entities from/to the database.
     */
    private final OrderRepository orderRepository;

    /**
     * Constructor injection
     *
     * Spring automatically provides OrderRepository here.
     */
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Retrieves a paginated list of orders with optional filtering and sorting.
     *
     * Parameters:
     * - page: page number (0-based)
     * - size: number of results per page
     * - sortBy: field to sort on
     * - direction: asc or desc
     * - status: optional status filter
     *
     * Flow:
     * 1. validate query parameters
     * 2. build Sort object
     * 3. build PageRequest object
     * 4. fetch from repository
     * 5. map entities to OrderResponse DTOs
     *
     * Returns:
     * Page<OrderResponse>
     */
    public Page<OrderResponse> getOrders(int page, int size, String sortBy, String direction, OrderStatus status) {

        // Page number must be 0 or higher
        if (page < 0) {
            throw new IllegalArgumentException("Page must be 0 or greater");
        }

        // Must request at least 1 result
        if (size < 1) {
            throw new IllegalArgumentException("Size must be at least 1");
        }

        // Hard cap to prevent excessively large requests
        if (size > 50) {
            throw new IllegalArgumentException("Size cannot exceed 50");
        }

        // Only allow sorting by approved fields
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException(
                    "Invalid sort field: " + sortBy +
                            ". Allowed values: id, itemName, quantity, status, createdAt, updatedAt"
            );
        }

        // Only allow "asc" or "desc"
        if (!direction.equalsIgnoreCase("asc") && !direction.equalsIgnoreCase("desc")) {
            throw new IllegalArgumentException(
                    "Invalid sort direction: " + direction + ". Allowed values: asc, desc"
            );
        }

        /**
         * Build Sort object based on direction.
         *
         * Example:
         * sortBy=createdAt, direction=desc
         */
        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        /**
         * Build pageable request using page number, size, and sort order.
         *
         * Example:
         * page=0, size=5, sort=createdAt desc
         */
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        /**
         * Fetch data from repository.
         *
         * If status filter exists:
         * - use custom repository method findByStatus(...)
         *
         * Otherwise:
         * - return all orders with pagination/sorting
         */
        Page<Order> ordersPage;
        if (status != null) {
            ordersPage = orderRepository.findByStatus(status, pageRequest);
        } else {
            ordersPage = orderRepository.findAll(pageRequest);
        }

        /**
         * Map each Order entity into an OrderResponse DTO.
         *
         * Why:
         * - keeps entity internal
         * - returns clean API response objects
         */
        return ordersPage.map(this::toResponse);
    }

    /**
     * Creates a new order from a CreateOrderRequest DTO.
     *
     * Flow:
     * 1. create new Order entity
     * 2. copy allowed input fields from DTO
     * 3. set default status
     * 4. save to database
     * 5. convert saved entity to OrderResponse
     *
     * Important:
     * - client cannot set ID or timestamps
     * - client cannot choose initial status
     * - system controls those values
     */
    public OrderResponse createOrder(CreateOrderRequest request) {
        Order order = new Order();

        // Copy request fields into internal entity
        order.setItemName(request.getItemName());
        order.setQuantity(request.getQuantity());

        // New orders always start as PENDING
        order.setStatus(OrderStatus.PENDING);

        // Save entity to DB
        Order savedOrder = orderRepository.save(order);

        // Convert entity → DTO response
        return toResponse(savedOrder);
    }

    /**
     * Updates the status of an existing order.
     *
     * Flow:
     * 1. find order by ID
     * 2. throw 404 if missing
     * 3. validate business rules
     * 4. update status
     * 5. save entity
     * 6. return DTO response
     *
     * Business rules:
     * - cannot set same status again
     * - cannot modify COMPLETED orders
     * - cannot modify CANCELLED orders
     */
    public OrderResponse updateStatus(Long id, OrderStatus newStatus) {

        // Find existing order or throw custom 404 exception
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        // Current status used for business-rule checks
        OrderStatus currentStatus = order.getStatus();

        // Prevent updating to the same status
        if (currentStatus == newStatus) {
            throw new InvalidOrderStatusTransitionException(
                    "Order is already in status " + currentStatus
            );
        }

        // Completed orders are final and cannot be changed
        if (currentStatus == OrderStatus.COMPLETED) {
            throw new InvalidOrderStatusTransitionException(
                    "Cannot change status of a completed order"
            );
        }

        // Cancelled orders are final and cannot be changed
        if (currentStatus == OrderStatus.CANCELLED) {
            throw new InvalidOrderStatusTransitionException(
                    "Cannot change status of a cancelled order"
            );
        }

        // Apply new status
        order.setStatus(newStatus);

        // Save updated entity
        Order updatedOrder = orderRepository.save(order);

        // Convert entity → DTO response
        return toResponse(updatedOrder);
    }

    /**
     * Retrieves a single order by ID.
     *
     * Flow:
     * 1. look up order
     * 2. throw 404 if not found
     * 3. map entity to DTO
     */
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        return toResponse(order);
    }

    /**
     * Deletes an order by ID.
     *
     * Flow:
     * 1. find order
     * 2. throw 404 if not found
     * 3. delete it
     */
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        orderRepository.delete(order);
    }

    /**
     * Private mapper method
     *
     * Purpose:
     * Converts internal Order entity into OrderResponse DTO.
     *
     * Why this exists:
     * - avoids repeating mapping logic in every method
     * - centralizes response conversion
     * - makes DTO usage cleaner and easier to maintain
     */
    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getItemName(),
                order.getQuantity(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}