package com.retail.rewards.service;

import com.retail.rewards.dto.CustomerRewardsDto;
import com.retail.rewards.entity.Customer;
import com.retail.rewards.entity.Transaction;
import com.retail.rewards.exception.ResourceNotFoundException;
import com.retail.rewards.repository.CustomerRepository;
import com.retail.rewards.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit tests for RewardsService.
 */
@ExtendWith(MockitoExtension.class)
class RewardsServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private RewardsService rewardsService;

    private Customer customer;
    private List<Transaction> transactions;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("Ajit");
        customer.setEmail("ajit@example.com");

        LocalDate today = LocalDate.now();
        transactions = Arrays.asList(
                createTransaction(1L, customer, 120.00, today.minusDays(5)),
                createTransaction(2L, customer, 75.00, today.minusDays(35)),
                createTransaction(3L, customer, 200.00, today.minusDays(65))
        );
    }

    @Test
    void testCalculateRewardsForCustomer_ValidCustomer_ReturnsRewards() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(transactionRepository.findByCustomerIdAndDateRange(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(transactions);

        CustomerRewardsDto result = rewardsService.calculateRewardsForCustomer(1L);

        assertNotNull(result);
        assertEquals(1L, result.getCustomerId());
        assertEquals("Ajit", result.getCustomerName());
        assertTrue(result.getTotalPoints() > 0);
        assertFalse(result.getMonthlyPoints().isEmpty());
    }

    @Test
    void testCalculateRewardsForCustomer_CustomerNotFound_ThrowsException() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            rewardsService.calculateRewardsForCustomer(999L);
        });
    }

    @Test
    void testCalculateRewardsForCustomer_NoTransactions_ReturnsZeroPoints() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(transactionRepository.findByCustomerIdAndDateRange(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Arrays.asList());

        CustomerRewardsDto result = rewardsService.calculateRewardsForCustomer(1L);

        assertNotNull(result);
        assertEquals(0, result.getTotalPoints());
        assertTrue(result.getMonthlyPoints().isEmpty());
    }

    @Test
    void testCalculateRewardsForAllCustomers_MultipleCustomers_ReturnsAllRewards() {
        Customer customer2 = new Customer();
        customer2.setId(2L);
        customer2.setName("Karan");
        customer2.setEmail("karan@example.com");

        LocalDate today = LocalDate.now();
        List<Transaction> allTransactions = Arrays.asList(
                createTransaction(1L, customer, 120.00, today.minusDays(5)),
                createTransaction(2L, customer2, 150.00, today.minusDays(10))
        );

        when(transactionRepository.findByDateRange(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(allTransactions);

        List<CustomerRewardsDto> results = rewardsService.calculateRewardsForAllCustomers();

        assertNotNull(results);
        assertEquals(2, results.size());
    }

    @Test
    void testCalculateRewardsForAllCustomers_NoTransactions_ReturnsEmptyList() {
        when(transactionRepository.findByDateRange(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Arrays.asList());

        List<CustomerRewardsDto> results = rewardsService.calculateRewardsForAllCustomers();

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    private Transaction createTransaction(Long id, Customer customer, Double amount, LocalDate date) {
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setCustomer(customer);
        transaction.setAmount(amount);
        transaction.setTransactionDate(date);
        return transaction;
    }
}
