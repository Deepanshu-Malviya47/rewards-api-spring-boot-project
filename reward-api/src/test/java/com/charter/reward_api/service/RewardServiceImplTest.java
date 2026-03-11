package com.charter.reward_api.service;

import com.charter.reward_api.dto.CustomerRewardSummaryDTO;
import com.charter.reward_api.dto.PagedRewardSummaryDTO;
import com.charter.reward_api.exception.CustomerNotFoundException;
import com.charter.reward_api.exception.InvalidDateRangeException;
import com.charter.reward_api.model.Customer;
import com.charter.reward_api.model.Transaction;
import com.charter.reward_api.repository.CustomerRepository;
import com.charter.reward_api.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RewardServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private RewardServiceImpl rewardService;

    private Customer customer;
    private List<Transaction> transactions;

    @BeforeEach
    void setUp() {
        customer = new Customer("Alice Johnson");
        customer.setId(1L);

        transactions = List.of(
                new Transaction(customer, new BigDecimal("120.00"), LocalDate.of(2024, 1, 15)),
                new Transaction(customer, new BigDecimal("45.00"), LocalDate.of(2024, 1, 20)),
                new Transaction(customer, new BigDecimal("200.00"), LocalDate.of(2024, 2, 10))
        );
    }

    @Test
    void testGetCustomerRewards_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(transactionRepository.findByCustomerIdAndDateRange(anyLong(), any(), any()))
                .thenReturn(transactions);

        CustomerRewardSummaryDTO result = rewardService.getCustomerRewards(1L, null, null);

        assertNotNull(result);
        assertEquals(1L, result.customerId());
        assertEquals("Alice Johnson", result.customerName());
        assertEquals(2, result.monthlyRewards().size());
        assertEquals(90, result.monthlyRewards().get(0).points());
        assertEquals(250, result.monthlyRewards().get(1).points());
        assertEquals(340, result.totalPoints());
    }

    @Test
    void testGetCustomerRewards_CustomerNotFound() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class,
                () -> rewardService.getCustomerRewards(999L, null, null));
    }

    @Test
    void testGetCustomerRewards_InvalidDateRange() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        LocalDate from = LocalDate.of(2024, 3, 1);
        LocalDate to = LocalDate.of(2024, 1, 1);

        assertThrows(InvalidDateRangeException.class,
                () -> rewardService.getCustomerRewards(1L, from, to));
    }

    @Test
    void testGetAllCustomerRewards_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> customerPage = new PageImpl<>(List.of(customer), pageable, 1);

        when(customerRepository.findAll(pageable)).thenReturn(customerPage);
        when(transactionRepository.findByDateRange(any(), any())).thenReturn(transactions);

        PagedRewardSummaryDTO result = rewardService.getAllCustomerRewards(0, 10, null, null);

        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(0, result.page());
        assertEquals(10, result.size());
        assertEquals(1, result.totalElements());
    }

    @Test
    void testGetAllCustomerRewards_WithDateFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> customerPage = new PageImpl<>(List.of(customer), pageable, 1);

        when(customerRepository.findAll(pageable)).thenReturn(customerPage);
        when(transactionRepository.findByDateRange(any(), any()))
                .thenReturn(transactions.subList(0, 2));

        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);

        PagedRewardSummaryDTO result = rewardService.getAllCustomerRewards(0, 10, from, to);

        assertNotNull(result);
        assertEquals(1, result.content().size());
        verify(transactionRepository).findByDateRange(from, to);
    }

    @Test
    void testPointsCalculation_Under50() {
        Transaction transaction = new Transaction(customer, new BigDecimal("45.00"), LocalDate.now());
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(transactionRepository.findByCustomerIdAndDateRange(anyLong(), any(), any()))
                .thenReturn(List.of(transaction));

        CustomerRewardSummaryDTO result = rewardService.getCustomerRewards(1L, null, null);

        assertEquals(0, result.totalPoints());
    }

    @Test
    void testPointsCalculation_Between50And100() {
        Transaction transaction = new Transaction(customer, new BigDecimal("75.00"), LocalDate.now());
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(transactionRepository.findByCustomerIdAndDateRange(anyLong(), any(), any()))
                .thenReturn(List.of(transaction));

        CustomerRewardSummaryDTO result = rewardService.getCustomerRewards(1L, null, null);

        assertEquals(25, result.totalPoints());
    }

    @Test
    void testPointsCalculation_Over100() {
        Transaction transaction = new Transaction(customer, new BigDecimal("120.00"), LocalDate.now());
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(transactionRepository.findByCustomerIdAndDateRange(anyLong(), any(), any()))
                .thenReturn(List.of(transaction));

        CustomerRewardSummaryDTO result = rewardService.getCustomerRewards(1L, null, null);

        assertEquals(90, result.totalPoints());
    }

    @Test
    void testPointsCalculation_CentsTruncated() {
        Transaction transaction = new Transaction(customer, new BigDecimal("120.99"), LocalDate.now());
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(transactionRepository.findByCustomerIdAndDateRange(anyLong(), any(), any()))
                .thenReturn(List.of(transaction));

        CustomerRewardSummaryDTO result = rewardService.getCustomerRewards(1L, null, null);

        assertEquals(90, result.totalPoints());
    }

    @Test
    void testPointsCalculation_Exactly50() {
        Transaction transaction = new Transaction(customer, new BigDecimal("50.00"), LocalDate.now());
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(transactionRepository.findByCustomerIdAndDateRange(anyLong(), any(), any()))
                .thenReturn(List.of(transaction));

        CustomerRewardSummaryDTO result = rewardService.getCustomerRewards(1L, null, null);

        assertEquals(0, result.totalPoints());
    }

    @Test
    void testPointsCalculation_Exactly100() {
        Transaction transaction = new Transaction(customer, new BigDecimal("100.00"), LocalDate.now());
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(transactionRepository.findByCustomerIdAndDateRange(anyLong(), any(), any()))
                .thenReturn(List.of(transaction));

        CustomerRewardSummaryDTO result = rewardService.getCustomerRewards(1L, null, null);

        assertEquals(50, result.totalPoints());
    }

    @Test
    void testGetCustomerRewards_NoTransactions() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(transactionRepository.findByCustomerIdAndDateRange(anyLong(), any(), any()))
                .thenReturn(List.of());

        CustomerRewardSummaryDTO result = rewardService.getCustomerRewards(1L, null, null);

        assertEquals(0, result.totalPoints());
        assertEquals(0, result.monthlyRewards().size());
    }

    @Test
    void testGetAllCustomerRewards_EmptyCustomerList() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(customerRepository.findAll(pageable)).thenReturn(emptyPage);
        when(transactionRepository.findByDateRange(any(), any())).thenReturn(List.of());

        PagedRewardSummaryDTO result = rewardService.getAllCustomerRewards(0, 10, null, null);

        assertEquals(0, result.content().size());
        assertEquals(0, result.totalElements());
    }

    @Test
    void testGetAllCustomerRewards_LargePagination() {
        Pageable pageable = PageRequest.of(10, 100);
        Page<Customer> customerPage = new PageImpl<>(List.of(), pageable, 0);

        when(customerRepository.findAll(pageable)).thenReturn(customerPage);
        when(transactionRepository.findByDateRange(any(), any())).thenReturn(List.of());

        PagedRewardSummaryDTO result = rewardService.getAllCustomerRewards(10, 100, null, null);

        assertEquals(10, result.page());
        assertEquals(100, result.size());
    }

    @Test
    void testGetCustomerRewards_SameDateRange() {
        LocalDate sameDate = LocalDate.of(2024, 1, 15);
        Transaction transaction = new Transaction(customer, new BigDecimal("120.00"), sameDate);
        
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(transactionRepository.findByCustomerIdAndDateRange(anyLong(), any(), any()))
                .thenReturn(List.of(transaction));

        CustomerRewardSummaryDTO result = rewardService.getCustomerRewards(1L, sameDate, sameDate);

        assertEquals(90, result.totalPoints());
    }
}
