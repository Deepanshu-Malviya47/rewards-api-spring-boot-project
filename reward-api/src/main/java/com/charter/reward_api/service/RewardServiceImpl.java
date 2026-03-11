package com.charter.reward_api.service;

import com.charter.reward_api.dto.CustomerRewardSummaryDTO;
import com.charter.reward_api.dto.MonthlyRewardDTO;
import com.charter.reward_api.dto.PagedRewardSummaryDTO;
import com.charter.reward_api.exception.CustomerNotFoundException;
import com.charter.reward_api.exception.InvalidDateRangeException;
import com.charter.reward_api.model.Customer;
import com.charter.reward_api.model.Transaction;
import com.charter.reward_api.repository.CustomerRepository;
import com.charter.reward_api.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Implementation of RewardService that calculates customer reward points based on transaction amounts.
 * Points calculation: 1 point per dollar over $50, and 2 points per dollar over $100.
 */
@Service
public class RewardServiceImpl implements RewardService {

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;

    public RewardServiceImpl(TransactionRepository transactionRepository, CustomerRepository customerRepository) {
        this.transactionRepository = transactionRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    /**
     * Retrieves paginated reward summaries for all customers within the specified date range.
     * Validates date range and groups transactions by customer to calculate monthly and total points.
     *
     * @param page the page number
     * @param size the page size
     * @param from optional start date (defaults to 1900-01-01 if null)
     * @param to optional end date (defaults to 2100-12-31 if null)
     * @return paginated reward summaries with monthly breakdowns
     * @throws InvalidDateRangeException if start date is after end date
     */
    public PagedRewardSummaryDTO getAllCustomerRewards(int page, int size, LocalDate from, LocalDate to) {
        LocalDate startDate = from != null ? from : LocalDate.of(1900, 1, 1);
        LocalDate endDate = to != null ? to : LocalDate.of(2100, 12, 31);

        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException(startDate, endDate);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> customerPage = customerRepository.findAll(pageable);

        List<Transaction> transactions = transactionRepository.findByDateRange(startDate, endDate);
        Map<Long, List<Transaction>> transactionsByCustomer = transactions.stream()
                .collect(Collectors.groupingBy(t -> t.getCustomer().getId()));

        List<CustomerRewardSummaryDTO> summaries = customerPage.getContent().stream()
                .map(customer -> buildCustomerSummary(customer, transactionsByCustomer.getOrDefault(customer.getId(), List.of())))
                .toList();

        return new PagedRewardSummaryDTO(
                summaries,
                customerPage.getNumber(),
                customerPage.getSize(),
                customerPage.getTotalElements(),
                customerPage.getTotalPages(),
                customerPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    /**
     * Retrieves reward summary for a specific customer within the specified date range.
     * Validates customer existence and date range, then calculates monthly and total points.
     *
     * @param customerId the customer ID
     * @param from optional start date (defaults to 1900-01-01 if null)
     * @param to optional end date (defaults to 2100-12-31 if null)
     * @return customer reward summary with monthly breakdowns
     * @throws CustomerNotFoundException if customer does not exist
     * @throws InvalidDateRangeException if start date is after end date
     */
    public CustomerRewardSummaryDTO getCustomerRewards(Long customerId, LocalDate from, LocalDate to) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));

        LocalDate startDate = from != null ? from : LocalDate.of(1900, 1, 1);
        LocalDate endDate = to != null ? to : LocalDate.of(2100, 12, 31);

        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException(startDate, endDate);
        }

        List<Transaction> transactions = transactionRepository.findByCustomerIdAndDateRange(customerId, startDate, endDate);
        return buildCustomerSummary(customer, transactions);
    }

    /**
     * Builds a customer reward summary by grouping transactions by month and calculating points.
     *
     * @param customer the customer entity
     * @param transactions list of customer transactions
     * @return customer reward summary with monthly breakdowns and total points
     */
    private CustomerRewardSummaryDTO buildCustomerSummary(Customer customer, List<Transaction> transactions) {
        Map<YearMonth, Long> pointsByMonth = new TreeMap<>();

        for (Transaction transaction : transactions) {
            YearMonth yearMonth = YearMonth.from(transaction.getTransactionDate());
            long points = calculatePoints(transaction.getAmount());
            pointsByMonth.merge(yearMonth, points, Long::sum);
        }

        List<MonthlyRewardDTO> monthlyRewards = new ArrayList<>();
        long totalPoints = 0;

        for (Map.Entry<YearMonth, Long> entry : pointsByMonth.entrySet()) {
            YearMonth yearMonth = entry.getKey();
            long points = entry.getValue();
            monthlyRewards.add(new MonthlyRewardDTO(yearMonth.getYear(), yearMonth.getMonth().name(), points));
            totalPoints += points;
        }

        return new CustomerRewardSummaryDTO(customer.getId(), customer.getName(), monthlyRewards, totalPoints);
    }

    /**
     * Calculates reward points for a transaction amount.
     * Rules: 0 points for $0-$50, 1 point per dollar over $50, 2 points per dollar over $100.
     *
     * @param amount the transaction amount
     * @return calculated reward points
     */
    private long calculatePoints(BigDecimal amount) {
        long dollars = amount.longValue();

        if (dollars <= 50) {
            return 0;
        } else if (dollars <= 100) {
            return dollars - 50;
        } else {
            return 50 + (dollars - 100) * 2;
        }
    }
}
