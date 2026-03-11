package com.charter.reward_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration class for Swagger/OpenAPI documentation.
 * Defines API metadata, contact information, and server details.
 */
@Configuration
public class SwaggerApiConfig {

    /**
     * Configures OpenAPI documentation for the Reward Calculator API.
     * Includes API description, version, contact details, license, and server information.
     *
     * @return configured OpenAPI instance
     */
    @Bean
    public OpenAPI rewardCalculatorOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Retailer Rewards Calculator API")
                        .description("""
                                REST API for calculating customer reward points based on purchase transactions.
                                
                                **Business Context:**
                                A retailer offers a rewards program where customers earn points based on their purchases.
                                This API calculates and tracks reward points for each customer over time.

                                **Points Calculation Rules:**
                                - $0 - $50: 0 points
                                - $50.01 - $100: 1 point per dollar over $50
                                - Over $100: 2 points per dollar over $100 + 50 points for the $50-$100 tier

                                **Example:** A $120 purchase earns (20 × 2) + 50 = 90 points.

                                **Key Features:**
                                - Retrieve rewards for all customers or specific customer
                                - Monthly breakdown of reward points
                                - Pagination support (page, size parameters)
                                - Date range filtering (from, to parameters in ISO-8601 format: yyyy-MM-dd)
                                - Comprehensive error handling with structured responses
                                - Input validation for all parameters
                                
                                **Technology Stack:**
                                - Spring Boot 3.x
                                - MySQL / H2 Database
                                - Spring Data JPA
                                - OpenAPI 3.0
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Deepanshu")
                                .email("deepanshumalviya58@gmail.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081")
                                .description("Development Server")
                ));
    }
}
