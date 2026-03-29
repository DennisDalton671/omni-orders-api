package com.dennis.omni_orders;

// OpenAPI classes used to build API documentation
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

// Spring annotations for configuration
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenApiConfig
 *
 * Purpose:
 * This class customizes the Swagger / OpenAPI documentation
 * for your backend API.
 *
 * Why this exists:
 * - Provides a professional title and description
 * - Adds versioning info
 * - Adds contact information
 * - Makes your API look polished in Swagger UI
 *
 * Without this:
 * Swagger would still work, but it would look generic and less professional.
 *
 * With this:
 * Your API looks like a real product.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Defines a custom OpenAPI bean
     *
     * @Bean:
     * - Tells Spring to register this as a managed object
     * - Swagger automatically picks this up and uses it
     *
     * What this config controls:
     * - API title
     * - description
     * - version
     * - contact info
     *
     * This appears at the top of Swagger UI.
     */
    @Bean
    public OpenAPI omniOrdersOpenAPI() {

        return new OpenAPI()
                .info(
                        new Info()

                                // API title shown in Swagger UI
                                .title("Omni Orders API")

                                // Description explaining what your API does
                                .description(
                                        "Spring Boot backend for managing Omni orders with CRUD, " +
                                                "order status workflows, pagination, sorting, filtering, " +
                                                "and structured error handling."
                                )

                                // Version of your API
                                .version("1.0")

                                // Contact info (nice professional touch)
                                .contact(
                                        new Contact()
                                                .name("Dennis Dalton")
                                                .url("https://querydennis.com")
                                )
                );
    }
}