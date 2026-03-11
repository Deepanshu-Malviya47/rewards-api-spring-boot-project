package com.charter.reward_api.controller;

import com.charter.reward_api.dto.CustomerRewardSummaryDTO;
import com.charter.reward_api.dto.PagedRewardSummaryDTO;
import com.charter.reward_api.service.RewardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;

/**
 * REST controller for managing customer reward points.
 * Calculates points based on purchase transactions: 1 point per dollar over $50, and 2 points per dollar over $100.
 */
@RestController
@RequestMapping("/api/rewards")
@Validated
@Tag(name = "Rewards", description = "Customer reward points API")
public class RewardController {

    private final RewardService rewardService;

    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    /**
     * Retrieves reward summaries for all customers with pagination and optional date filtering.
     *
     * @param page the page number (default: 0, must be >= 0)
     * @param size the page size (default: 10, must be >= 1)
     * @param from optional start date for filtering transactions (ISO-8601 format: yyyy-MM-dd)
     * @param to optional end date for filtering transactions (ISO-8601 format: yyyy-MM-dd)
     * @return paginated list of customer reward summaries with monthly breakdowns and total points
     */
    @GetMapping
    @Operation(summary = "Get all customer rewards with pagination")
    public ResponseEntity<PagedRewardSummaryDTO> getAllCustomerRewards(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page must be >= 0") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Size must be >= 1") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        PagedRewardSummaryDTO result = rewardService.getAllCustomerRewards(page, size, from, to);
        return ResponseEntity.ok(result);
    }

    /**
     * Retrieves reward summary for a specific customer with optional date filtering.
     *
     * @param customerId the customer ID (must be >= 1)
     * @param from optional start date for filtering transactions (ISO-8601 format: yyyy-MM-dd)
     * @param to optional end date for filtering transactions (ISO-8601 format: yyyy-MM-dd)
     * @return customer reward summary with monthly breakdowns and total points
     */
    @GetMapping("/{customerId}")
    @Operation(summary = "Get reward summary for a specific customer")
    public ResponseEntity<CustomerRewardSummaryDTO> getCustomerRewards(
            @PathVariable @Min(value = 1, message = "Customer ID must be >= 1") Long customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        CustomerRewardSummaryDTO result = rewardService.getCustomerRewards(customerId, from, to);
        return ResponseEntity.ok(result);
    }
}
