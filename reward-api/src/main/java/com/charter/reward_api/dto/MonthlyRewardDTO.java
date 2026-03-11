package com.charter.reward_api.dto;

/**
 * Data Transfer Object representing monthly reward points for a customer.
 *
 * @param year the year of the reward period
 * @param month the month name (e.g., "JANUARY", "FEBRUARY")
 * @param points reward points earned in this month
 */
public record MonthlyRewardDTO(int year, String month, long points) {
}
