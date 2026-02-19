package com.retail.rewards.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for RewardsCalculator utility class.
 */
class RewardsCalculatorTest {

    @Test
    void testCalculatePoints_ExactlyHundredTwenty_ReturnsNinetyPoints() {
        Double amount = 120.00;
        int points = RewardsCalculator.calculatePoints(amount);
        assertEquals(90, points);
    }

    @ParameterizedTest
    @CsvSource({
            "120.00, 90",
            "75.50, 25",
            "50.00, 0",
            "100.00, 50",
            "200.00, 250",
            "89.99, 39",
            "110.00, 70",
            "45.00, 0",
            "250.00, 350",
            "0.00, 0",
            "49.99, 0",
            "50.01, 0",
            "100.01, 50",
            "150.00, 150"
    })
    void testCalculatePoints_VariousAmounts(Double amount, int expectedPoints) {
        int points = RewardsCalculator.calculatePoints(amount);
        assertEquals(expectedPoints, points);
    }

    @Test
    void testCalculatePoints_NullAmount_ReturnsZero() {
        int points = RewardsCalculator.calculatePoints(null);
        assertEquals(0, points);
    }

    @Test
    void testCalculatePoints_NegativeAmount_ReturnsZero() {
        Double amount = -50.00;
        int points = RewardsCalculator.calculatePoints(amount);
        assertEquals(0, points);
    }

    @Test
    void testCalculatePoints_ZeroAmount_ReturnsZero() {
        Double amount = 0.0;
        int points = RewardsCalculator.calculatePoints(amount);
        assertEquals(0, points);
    }

    @Test
    void testCalculatePoints_BelowFifty_ReturnsZero() {
        Double amount = 30.00;
        int points = RewardsCalculator.calculatePoints(amount);
        assertEquals(0, points);
    }

    @Test
    void testCalculatePoints_BetweenFiftyAndHundred_ReturnsCorrectPoints() {
        Double amount = 75.00;
        int points = RewardsCalculator.calculatePoints(amount);
        assertEquals(25, points);
    }

    @Test
    void testCalculatePoints_AboveHundred_ReturnsCorrectPoints() {
        Double amount = 150.00;
        int points = RewardsCalculator.calculatePoints(amount);
        assertEquals(150, points);
    }
}
