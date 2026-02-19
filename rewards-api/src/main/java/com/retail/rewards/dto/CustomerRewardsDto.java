package com.retail.rewards.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Data Transfer Object for customer rewards response.
 * Contains monthly points breakdown and total points for a customer.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRewardsDto {

    private Long customerId;
    private String customerName;
    private Map<String, Integer> monthlyPoints;
    private Integer totalPoints;
}
