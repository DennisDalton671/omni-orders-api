# Omni Orders API

Spring Boot backend project for managing omni-channel orders with full CRUD functionality, validation, and persistent storage.

---

## Overview

This project demonstrates backend development using Spring Boot by implementing a realistic order management system. It focuses on RESTful API design, clean structure, and handling core backend responsibilities.

---

## Features

- Create, read, update, and delete orders (CRUD)
- Persistent H2 database
- Input validation
- Global exception handling
- Pagination and sorting
- Filtering support
- OpenAPI / Swagger configuration
- Health check endpoint

---

## Tech Stack

- Java
- Spring Boot
- Spring Data JPA
- H2 Database
- Maven

---

## Running the Project

1. Clone the repository
2. Open in IntelliJ or your preferred IDE
3. Run the Spring Boot application
4. Access locally: http://localhost:8080

---

## API Documentation

Swagger UI is available at:

http://localhost:8080/swagger-ui.html

---

## Base URL

http://localhost:8080

---

## Example Endpoints

- GET /orders - Retrieve all orders
- GET /orders/{id} - Retrieve an order by ID
- POST /orders - Create a new order
- PUT /orders/{id}/status - Update order status
- DELETE /orders/{id} - Delete an order
- GET /health - API health check

---

## Query Examples

Get All Orders  
GET /orders  

Get Order By ID  
GET /orders/1  

Pagination  
GET /orders?page=0&size=5  

Sorting (createdAt desc)  
GET /orders?sortBy=createdAt&direction=desc  

Sorting (itemName asc)  
GET /orders?sortBy=itemName&direction=asc  

Sorting (quantity desc)  
GET /orders?sortBy=quantity&direction=desc  

Sorting (updatedAt asc)  
GET /orders?sortBy=updatedAt&direction=asc  

Filter by Status (PENDING)  
GET /orders?status=PENDING  

Filter by Status (PROCESSING)  
GET /orders?status=PROCESSING  

Filter by Status (READY_FOR_PICKUP)  
GET /orders?status=READY_FOR_PICKUP  

Filter by Status (COMPLETED)  
GET /orders?status=COMPLETED  

Filter by Status (CANCELLED)  
GET /orders?status=CANCELLED  

Pagination + Sorting  
GET /orders?page=0&size=10&sortBy=updatedAt&direction=desc  

Pagination + Filtering  
GET /orders?page=0&size=5&status=PROCESSING  

Sorting + Filtering  
GET /orders?status=READY_FOR_PICKUP&sortBy=createdAt&direction=asc  

Full Combined Example  
GET /orders?page=1&size=5&sortBy=quantity&direction=desc&status=PENDING  

Health Check  
GET /health  

---

## Example Request Bodies

Create Order

{
  "itemName": "Nike Shoes",
  "quantity": 2
}

Update Order Status

{
  "status": "PROCESSING"
}

Allowed status values:
- PENDING
- PROCESSING
- READY_FOR_PICKUP
- COMPLETED
- CANCELLED

---

## Validation Rules

Create Order:
- itemName must not be blank
- quantity must be at least 1

Query Parameters:
- page must be 0 or greater
- size must be at least 1
- size cannot exceed 50
- sortBy must be one of:
  id, itemName, quantity, status, createdAt, updatedAt
- direction must be asc or desc

---

## Error Handling

The API returns structured JSON responses for:

- 400 Bad Request (validation errors, invalid parameters)
- 404 Not Found (order not found)
- 409 Conflict (invalid status transition)

---

## Business Rules

- New orders default to PENDING
- Status updates use a dedicated endpoint
- Completed orders cannot be modified
- Cancelled orders cannot be modified
- Orders cannot be updated to the same status

---

## Project Structure

- controller - API endpoints
- service - business logic
- repository - database access
- model - entities and enums
- dto - request/response objects
- exceptions - global error handling
- config - Swagger configuration

---

## Why I Built This

I built this project to strengthen my backend development skills and create a portfolio piece that demonstrates practical Spring Boot knowledge, including REST API design, validation, pagination, sorting, filtering, and structured error handling.

---

## Future Improvements

- Authentication and authorization
- Role-based access control
- Replace H2 with PostgreSQL
- Dockerize the application
- Deploy to cloud hosting

---

## API Testing

All endpoints were tested using Postman.

---

## Author

Dennis Dalton
