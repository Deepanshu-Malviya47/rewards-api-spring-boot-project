package com.retail.rewards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.retail.rewards.dto.TransactionDto;
import com.retail.rewards.entity.Customer;
import com.retail.rewards.repository.CustomerRepository;
import com.retail.rewards.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for TransactionController.
 */
@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Customer customer;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        customerRepository.deleteAll();

        customer = new Customer();
        customer.setName("Test Customer");
        customer.setEmail("test@example.com");
        customer = customerRepository.save(customer);
    }

    @Test
    void testCreateTransaction_ValidData_ReturnsCreated() throws Exception {
        TransactionDto dto = new TransactionDto();
        dto.setCustomerId(customer.getId());
        dto.setAmount(120.00);
        dto.setTransactionDate(LocalDate.now());

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.amount").value(120.00));
    }

    @Test
    void testCreateTransaction_InvalidCustomer_ReturnsNotFound() throws Exception {
        TransactionDto dto = new TransactionDto();
        dto.setCustomerId(999L);
        dto.setAmount(120.00);
        dto.setTransactionDate(LocalDate.now());

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not found with id: 999"));
    }

    @Test
    void testCreateTransaction_NullAmount_ReturnsBadRequest() throws Exception {
        TransactionDto dto = new TransactionDto();
        dto.setCustomerId(customer.getId());
        dto.setTransactionDate(LocalDate.now());

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void testCreateTransaction_NegativeAmount_ReturnsBadRequest() throws Exception {
        TransactionDto dto = new TransactionDto();
        dto.setCustomerId(customer.getId());
        dto.setAmount(-50.00);
        dto.setTransactionDate(LocalDate.now());

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateTransaction_NullCustomerId_ReturnsBadRequest() throws Exception {
        TransactionDto dto = new TransactionDto();
        dto.setAmount(120.00);
        dto.setTransactionDate(LocalDate.now());

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
