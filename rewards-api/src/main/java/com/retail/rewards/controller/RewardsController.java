package com.retail.rewards.controller;

import com.retail.rewards.dto.CustomerRewardsDto;
import com.retail.rewards.service.RewardsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for rewards-related endpoints.
 * Provides APIs to calculate and retrieve customer reward points.
 */
@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
public class RewardsController {

    private final RewardsService rewardsService;

    /**
     * Retrieves rewards for a specific customer.
     *
     * @param customerId the customer ID
     * @return CustomerRewardsDto with monthly and total points
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<CustomerRewardsDto> getRewardsForCustomer(@PathVariable Long customerId) {
        CustomerRewardsDto rewards = rewardsService.calculateRewardsForCustomer(customerId);
        return ResponseEntity.ok(rewards);
    }

    /**
     * Retrieves rewards for all customers.
     *
     * @return list of CustomerRewardsDto for all customers
     */
    @GetMapping("/customers")
    public ResponseEntity<List<CustomerRewardsDto>> getRewardsForAllCustomers() {
        List<CustomerRewardsDto> rewards = rewardsService.calculateRewardsForAllCustomers();
        return ResponseEntity.ok(rewards);
    }
}
