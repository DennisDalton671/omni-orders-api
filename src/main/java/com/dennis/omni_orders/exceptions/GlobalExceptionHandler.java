package com.dennis.omni_orders.exceptions;

// Spring HTTP classes used to control response status + body
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

// Exception thrown when Spring/Jackson cannot parse JSON properly
import org.springframework.http.converter.HttpMessageNotReadableException;

// Exception thrown when @Valid request validation fails
import org.springframework.web.bind.MethodArgumentNotValidException;

// Spring annotations for global exception handling
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler
 *
 * Purpose:
 * This class catches exceptions thrown anywhere in the API and converts them
 * into clean, structured JSON responses.
 *
 * Why this exists:
 * - Prevents ugly default Spring error pages / stacktrace-style responses
 * - Standardizes error response format across the app
 * - Makes the API easier for clients and frontends to consume
 *
 * Important concept:
 * @RestControllerAdvice makes this apply globally to all controllers.
 * So if a controller or service throws one of these exceptions,
 * Spring will route it here automatically.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles OrderNotFoundException
     *
     * When this happens:
     * - An order ID was requested but no matching order exists
     *
     * Example:
     * GET /orders/99999
     *
     * Response:
     * - HTTP 404 Not Found
     * - JSON body with message + custom errorCode
     */
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleOrderNotFound(OrderNotFoundException ex) {
        Map<String, Object> body = new LinkedHashMap<>();

        // Timestamp helps identify when the error happened
        body.put("timestamp", LocalDateTime.now().toString());

        // Numeric HTTP status
        body.put("status", 404);

        // Human-readable status label
        body.put("error", "Not Found");

        // Exception message from the custom exception class
        body.put("message", ex.getMessage());

        // Stable custom application error code
        body.put("errorCode", "ORDER_NOT_FOUND");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Handles validation failures from @Valid request bodies
     *
     * When this happens:
     * - itemName is blank
     * - quantity is less than 1
     * - status is missing in an update request
     *
     * Example:
     * POST /orders with invalid request body
     *
     * Why fieldErrors is included:
     * - Gives a field-by-field explanation of what failed
     * - Useful for frontend forms and debugging
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        // Loop through all validation errors and store:
        // field name -> validation message
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", 400);
        body.put("error", "Bad Request");
        body.put("message", "Validation failed");
        body.put("errorCode", "VALIDATION_FAILED");

        // Include detailed field-level validation errors
        body.put("fieldErrors", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Handles JSON parsing / request body conversion problems
     *
     * When this happens:
     * - malformed JSON
     * - invalid enum value
     * - wrong type sent in JSON
     *
     * Example malformed JSON:
     * {
     *   "status": "PROCESSING"
     *   // missing closing brace
     * }
     *
     * Example invalid enum:
     * {
     *   "status": "BANANA"
     * }
     *
     * Why this matters:
     * Without this handler, Spring tends to return a long ugly default error response.
     * This keeps it clean and API-friendly.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(HttpMessageNotReadableException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", 400);
        body.put("error", "Bad Request");

        // Default assumption: request body JSON is malformed
        String message = "Malformed JSON request";
        String errorCode = "MALFORMED_JSON";

        // Special case:
        // if Spring failed while trying to parse OrderStatus enum,
        // return a more specific message
        if (ex.getMessage() != null && ex.getMessage().contains("OrderStatus")) {
            message = "Invalid status value. Allowed values: PENDING, PROCESSING, READY_FOR_PICKUP, COMPLETED, CANCELLED";
            errorCode = "INVALID_ORDER_STATUS";
        }

        body.put("message", message);
        body.put("errorCode", errorCode);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Handles business-rule violations for status changes
     *
     * When this happens:
     * - trying to update a COMPLETED order
     * - trying to update a CANCELLED order
     * - trying to set the same status again
     *
     * Example:
     * PUT /orders/1/status with invalid transition
     *
     * Why 409 Conflict:
     * - The request itself is valid JSON
     * - But it conflicts with application business rules
     */
    @ExceptionHandler(InvalidOrderStatusTransitionException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidStatusTransition(
            InvalidOrderStatusTransitionException ex) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", 409);
        body.put("error", "Conflict");
        body.put("message", ex.getMessage());
        body.put("errorCode", "ORDER_STATUS_TRANSITION_INVALID");

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Handles generic invalid request parameter problems
     *
     * When this happens:
     * - invalid page number
     * - invalid page size
     * - invalid sort field
     * - invalid sort direction
     *
     * Example:
     * GET /orders?page=-1
     * GET /orders?sortBy=banana
     *
     * Why this exists:
     * Service layer throws IllegalArgumentException for bad query params,
     * and this handler turns that into a clean 400 response.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", 400);
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());
        body.put("errorCode", "INVALID_REQUEST_PARAMETER");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}