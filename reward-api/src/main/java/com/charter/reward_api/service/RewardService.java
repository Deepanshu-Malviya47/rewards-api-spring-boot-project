package com.charter.reward_api.service;

import com.charter.reward_api.dto.CustomerRewardSummaryDTO;
import com.charter.reward_api.dto.PagedRewardSummaryDTO;

import java.time.LocalDate;

/**
 * Service interface for calculating and retrieving customer reward points.
 */
public interface RewardService {

    /**
     * Retrieves paginated reward summaries for all customers within an optional date range.
     *
     * @param page the page number
     * @param size the page size
     * @param from optional start date for filtering transactions
     * @param to optional end date for filtering transactions
     * @return paginated reward summaries
     */
    PagedRewardSummaryDTO getAllCustomerRewards(
            int page,
            int size,
            LocalDate from,
            LocalDate to
    );

    /**
     * Retrieves reward summary for a specific customer within an optional date range.
     *
     * @param customerId the customer ID
     * @param from optional start date for filtering transactions
     * @param to optional end date for filtering transactions
     * @return customer reward summary
     */
    CustomerRewardSummaryDTO getCustomerRewards(
            Long customerId,
            LocalDate from,
            LocalDate to
    );
}
