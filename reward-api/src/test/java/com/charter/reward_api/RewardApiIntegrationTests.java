package com.charter.reward_api;

import com.charter.reward_api.model.Customer;
import com.charter.reward_api.model.Transaction;
import com.charter.reward_api.repository.CustomerRepository;
import com.charter.reward_api.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RewardApiIntegrationTests {

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

        Customer alice = customerRepository.save(new Customer("Alice Johnson"));
        Customer bob = customerRepository.save(new Customer("Bob Smith"));

        transactionRepository.save(new Transaction(alice, new BigDecimal("120.00"), LocalDate.of(2024, 1, 15)));
        transactionRepository.save(new Transaction(alice, new BigDecimal("45.00"), LocalDate.of(2024, 1, 20)));
        transactionRepository.save(new Transaction(alice, new BigDecimal("200.00"), LocalDate.of(2024, 2, 10)));
        transactionRepository.save(new Transaction(bob, new BigDecimal("75.00"), LocalDate.of(2024, 1, 12)));
    }

    @Test
    void testGetAllCustomerRewards_FullIntegration() throws Exception {
        mockMvc.perform(get("/api/rewards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].customerName", is("Alice Johnson")))
                .andExpect(jsonPath("$.content[0].totalPoints", is(340)))
                .andExpect(jsonPath("$.content[1].customerName", is("Bob Smith")))
                .andExpect(jsonPath("$.content[1].totalPoints", is(25)));
    }

    @Test
    void testGetAllCustomerRewards_WithDateFilter() throws Exception {
        mockMvc.perform(get("/api/rewards")
                        .param("from", "2024-01-01")
                        .param("to", "2024-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].totalPoints", is(90)))
                .andExpect(jsonPath("$.content[1].totalPoints", is(25)));
    }

    @Test
    void testGetCustomerRewards_FullIntegration() throws Exception {
        Customer alice = customerRepository.findAll().get(0);

        mockMvc.perform(get("/api/rewards/" + alice.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName", is("Alice Johnson")))
                .andExpect(jsonPath("$.monthlyRewards", hasSize(2)))
                .andExpect(jsonPath("$.monthlyRewards[0].month", is("JANUARY")))
                .andExpect(jsonPath("$.monthlyRewards[0].points", is(90)))
                .andExpect(jsonPath("$.monthlyRewards[1].month", is("FEBRUARY")))
                .andExpect(jsonPath("$.monthlyRewards[1].points", is(250)))
                .andExpect(jsonPath("$.totalPoints", is(340)));
    }

    @Test
    void testGetCustomerRewards_WithDateRange() throws Exception {
        Customer alice = customerRepository.findAll().get(0);

        mockMvc.perform(get("/api/rewards/" + alice.getId())
                        .param("from", "2024-01-01")
                        .param("to", "2024-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPoints", is(90)))
                .andExpect(jsonPath("$.monthlyRewards", hasSize(1)));
    }

    @Test
    void testGetCustomerRewards_NotFound() throws Exception {
        mockMvc.perform(get("/api/rewards/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Customer not found with id: 999")));
    }

    @Test
    void testPagination() throws Exception {
        mockMvc.perform(get("/api/rewards")
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.page", is(0)))
                .andExpect(jsonPath("$.size", is(1)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(2)))
                .andExpect(jsonPath("$.last", is(false)));
    }

    @Test
    void testInvalidDateRange() throws Exception {
        Customer alice = customerRepository.findAll().get(0);

        mockMvc.perform(get("/api/rewards/" + alice.getId())
                        .param("from", "2024-03-01")
                        .param("to", "2024-01-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    void testGetAllCustomerRewards_EmptyResult() throws Exception {
        transactionRepository.deleteAll();
        customerRepository.deleteAll();

        mockMvc.perform(get("/api/rewards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));
    }

    @Test
    void testPointsCalculation_Exactly50Dollars() throws Exception {
        transactionRepository.deleteAll();
        customerRepository.deleteAll();

        Customer customer = customerRepository.save(new Customer("Test Customer"));
        transactionRepository.save(new Transaction(customer, new BigDecimal("50.00"), LocalDate.now()));

        mockMvc.perform(get("/api/rewards/" + customer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPoints", is(0)));
    }

    @Test
    void testPointsCalculation_Exactly100Dollars() throws Exception {
        transactionRepository.deleteAll();
        customerRepository.deleteAll();

        Customer customer = customerRepository.save(new Customer("Test Customer"));
        transactionRepository.save(new Transaction(customer, new BigDecimal("100.00"), LocalDate.now()));

        mockMvc.perform(get("/api/rewards/" + customer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPoints", is(50)));
    }

    @Test
    void testGetAllCustomerRewards_LargePagination() throws Exception {
        mockMvc.perform(get("/api/rewards")
                        .param("page", "0")
                        .param("size", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size", is(100)));
    }

    @Test
    void testGetCustomerRewards_SameDateRange() throws Exception {
        Customer alice = customerRepository.findAll().get(0);

        mockMvc.perform(get("/api/rewards/" + alice.getId())
                        .param("from", "2024-01-15")
                        .param("to", "2024-01-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPoints", is(90)));
    }
}
