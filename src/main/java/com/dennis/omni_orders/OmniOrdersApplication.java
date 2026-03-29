package com.dennis.omni_orders;

// Spring Boot core class used to start the application
import org.springframework.boot.SpringApplication;

// Enables auto-configuration and component scanning
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * OmniOrdersApplication
 *
 * Purpose:
 * This is the main entry point for the Spring Boot application.
 *
 * What happens when this runs:
 * - Starts the embedded server (Tomcat by default)
 * - Initializes Spring context
 * - Scans for components (controllers, services, repositories)
 * - Applies auto-configuration (database, JPA, Flyway, etc.)
 *
 * Think of this as:
 * 👉 "Boot up the entire backend system"
 */
@SpringBootApplication
public class OmniOrdersApplication {

	/**
	 * Main method
	 *
	 * This is the standard Java entry point.
	 *
	 * SpringApplication.run(...) does all the heavy lifting:
	 * - creates application context
	 * - wires dependencies
	 * - starts web server
	 * - runs Flyway migrations
	 * - initializes everything
	 *
	 * After this runs:
	 * 👉 Your API is live on http://localhost:8080
	 */
	public static void main(String[] args) {
		SpringApplication.run(OmniOrdersApplication.class, args);
	}
}