package com.retail.rewards.controller;

import com.retail.rewards.entity.Customer;
import com.retail.rewards.entity.Transaction;
import com.retail.rewards.repository.CustomerRepository;
import com.retail.rewards.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for RewardsController.
 */
@SpringBootTest
@AutoConfigureMockMvc
class RewardsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        customerRepository.deleteAll();

        Customer customer = new Customer();
        customer.setName("Test Customer");
        customer.setEmail("test@example.com");
        customer = customerRepository.save(customer);

        LocalDate today = LocalDate.now();
        transactionRepository.saveAll(Arrays.asList(
                createTransaction(customer, 120.00, today.minusDays(5)),
                createTransaction(customer, 75.00, today.minusDays(35)),
                createTransaction(customer, 200.00, today.minusDays(65))
        ));
    }

    @Test
    void testGetRewardsForCustomer_ValidCustomer_ReturnsRewards() throws Exception {
        Customer customer = customerRepository.findAll().get(0);

        mockMvc.perform(get("/api/rewards/customer/" + customer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(customer.getId()))
                .andExpect(jsonPath("$.customerName").value("Test Customer"))
                .andExpect(jsonPath("$.totalPoints").isNumber())
                .andExpect(jsonPath("$.monthlyPoints").isMap());
    }

    @Test
    void testGetRewardsForCustomer_InvalidCustomer_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/rewards/customer/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not found with id: 999"));
    }

    @Test
    void testGetRewardsForAllCustomers_ReturnsAllRewards() throws Exception {
        mockMvc.perform(get("/api/rewards/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].customerId").isNumber())
                .andExpect(jsonPath("$[0].customerName").isString())
                .andExpect(jsonPath("$[0].totalPoints").isNumber());
    }

    private Transaction createTransaction(Customer customer, Double amount, LocalDate date) {
        Transaction transaction = new Transaction();
        transaction.setCustomer(customer);
        transaction.setAmount(amount);
        transaction.setTransactionDate(date);
        return transaction;
    }
}
