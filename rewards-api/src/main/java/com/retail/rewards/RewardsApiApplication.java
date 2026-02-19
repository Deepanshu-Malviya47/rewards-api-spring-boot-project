package com.retail.rewards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Rewards API.
 * This Spring Boot application provides REST APIs to calculate customer reward points
 * based on their purchase transactions.
 */
@SpringBootApplication
public class RewardsApiApplication {

	/**
	 * Main method to start the Spring Boot application.
	 *
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(RewardsApiApplication.class, args);
	}
}
