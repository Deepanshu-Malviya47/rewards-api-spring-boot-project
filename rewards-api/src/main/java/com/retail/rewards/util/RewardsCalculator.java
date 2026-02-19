package com.retail.rewards.util;

/**
 * Utility class for calculating reward points based on transaction amounts.
 * Points calculation rules:
 * - 2 points for every dollar spent over $100
 * - 1 point for every dollar spent between $50 and $100
 */
public class RewardsCalculator {

    private static final double TIER_ONE_THRESHOLD = 50.0;
    private static final double TIER_TWO_THRESHOLD = 100.0;
    private static final int TIER_ONE_POINTS = 1;
    private static final int TIER_TWO_POINTS = 2;

    /**
     * Calculates reward points for a given transaction amount.
     *
     * @param amount the transaction amount
     * @return the calculated reward points
     */
    public static int calculatePoints(Double amount) {
        if (amount == null || amount <= 0) {
            return 0;
        }

        int points = 0;

        if (amount > TIER_TWO_THRESHOLD) {
            double amountOverHundred = amount - TIER_TWO_THRESHOLD;
            points += (int) amountOverHundred * TIER_TWO_POINTS;
            points += (int) (TIER_TWO_THRESHOLD - TIER_ONE_THRESHOLD) * TIER_ONE_POINTS;
        } else if (amount > TIER_ONE_THRESHOLD) {
            double amountOverFifty = amount - TIER_ONE_THRESHOLD;
            points += (int) amountOverFifty * TIER_ONE_POINTS;
        }

        return points;
    }
}
