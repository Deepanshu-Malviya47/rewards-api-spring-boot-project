package com.charter.reward_api.dto;

import java.util.List;

/**
 * Data Transfer Object representing a customer's reward summary.
 * Contains customer information, monthly reward breakdowns, and total points earned.
 *
 * @param customerId the unique identifier of the customer
 * @param customerName the name of the customer
 * @param monthlyRewards list of monthly reward breakdowns
 * @param totalPoints total reward points earned across all months
 */
public record CustomerRewardSummaryDTO(
        Long customerId,
        String customerName,
        List<MonthlyRewardDTO> monthlyRewards,
        long totalPoints
) {
}
