package com.retail.rewards.service;

import com.retail.rewards.dto.CustomerRewardsDto;
import com.retail.rewards.entity.Customer;
import com.retail.rewards.entity.Transaction;
import com.retail.rewards.exception.ResourceNotFoundException;
import com.retail.rewards.repository.CustomerRepository;
import com.retail.rewards.repository.TransactionRepository;
import com.retail.rewards.util.RewardsCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for calculating customer rewards.
 * Handles business logic for reward points calculation.
 */
@Service
@RequiredArgsConstructor
public class RewardsService {

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;

    /**
     * Calculates rewards for a specific customer over the last three months.
     *
     * @param customerId the customer ID
     * @return CustomerRewardsDto containing monthly and total points
     */
    public CustomerRewardsDto calculateRewardsForCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(3);

        List<Transaction> transactions = transactionRepository.findByCustomerIdAndDateRange(
                customerId, startDate, endDate
        );

        return buildCustomerRewardsDto(customer, transactions);
    }

    /**
     * Calculates rewards for all customers over the last three months.
     *
     * @return list of CustomerRewardsDto for all customers
     */
    public List<CustomerRewardsDto> calculateRewardsForAllCustomers() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(3);

        List<Transaction> transactions = transactionRepository.findByDateRange(startDate, endDate);

        Map<Customer, List<Transaction>> transactionsByCustomer = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getCustomer));

        return transactionsByCustomer.entrySet().stream()
                .map(entry -> buildCustomerRewardsDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**
     * Builds CustomerRewardsDto from customer and their transactions.
     *
     * @param customer     the customer
     * @param transactions list of transactions
     * @return CustomerRewardsDto
     */
    private CustomerRewardsDto buildCustomerRewardsDto(Customer customer, List<Transaction> transactions) {
        Map<String, Integer> monthlyPoints = new LinkedHashMap<>();
        int totalPoints = 0;

        Map<YearMonth, List<Transaction>> transactionsByMonth = transactions.stream()
                .collect(Collectors.groupingBy(t -> YearMonth.from(t.getTransactionDate())));

        List<YearMonth> sortedMonths = transactionsByMonth.keySet().stream()
                .sorted()
                .collect(Collectors.toList());

        for (YearMonth month : sortedMonths) {
            int monthPoints = transactionsByMonth.get(month).stream()
                    .mapToInt(t -> RewardsCalculator.calculatePoints(t.getAmount()))
                    .sum();

            String monthKey = month.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            monthlyPoints.put(monthKey, monthPoints);
            totalPoints += monthPoints;
        }

        return new CustomerRewardsDto(
                customer.getId(),
                customer.getName(),
                monthlyPoints,
                totalPoints
        );
    }
}
