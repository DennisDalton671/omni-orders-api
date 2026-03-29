package com.dennis.omni_orders.repository;

// Your Order entity (database model)
import com.dennis.omni_orders.model.Order;
import com.dennis.omni_orders.model.OrderStatus;

// Spring Data pagination support
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

// Core Spring Data JPA interface
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * OrderRepository
 *
 * Purpose:
 * This interface provides database access for Order entities.
 *
 * What it does:
 * - Handles all CRUD operations automatically
 * - Allows custom queries using method naming conventions
 *
 * Important:
 * You DO NOT implement this class yourself.
 * Spring Data JPA generates the implementation at runtime.
 *
 * Think of this as:
 * 👉 "The bridge between your service layer and the database"
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Custom query method
     *
     * Purpose:
     * Retrieves a paginated list of orders filtered by status.
     *
     * How this works:
     * Spring reads the method name:
     * "findByStatus"
     *
     * And automatically generates SQL like:
     * SELECT * FROM orders WHERE status = ?
     *
     * Parameters:
     * - status → filter condition
     * - pageable → pagination + sorting info
     *
     * Returns:
     * Page<Order>
     *
     * Why Page:
     * - includes metadata (total pages, total elements, etc.)
     * - supports pagination cleanly
     *
     * Example usage:
     * orderRepository.findByStatus(PENDING, PageRequest.of(0, 5));
     */
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
}