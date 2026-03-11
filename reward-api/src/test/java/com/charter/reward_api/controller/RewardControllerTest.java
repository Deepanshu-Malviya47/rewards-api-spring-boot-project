package com.charter.reward_api.controller;

import com.charter.reward_api.dto.CustomerRewardSummaryDTO;
import com.charter.reward_api.dto.MonthlyRewardDTO;
import com.charter.reward_api.dto.PagedRewardSummaryDTO;
import com.charter.reward_api.exception.CustomerNotFoundException;
import com.charter.reward_api.exception.InvalidDateRangeException;
import com.charter.reward_api.service.RewardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for RewardController.
 */
@ExtendWith(MockitoExtension.class)
class RewardControllerTest {

    @Mock
    private RewardService rewardService;

    @InjectMocks
    private RewardController rewardController;

    private CustomerRewardSummaryDTO customerRewardSummary;
    private PagedRewardSummaryDTO pagedRewardSummary;

    @BeforeEach
    void setUp() {
        List<MonthlyRewardDTO> monthlyRewards = List.of(
                new MonthlyRewardDTO(2024, "JANUARY", 90),
                new MonthlyRewardDTO(2024, "FEBRUARY", 250)
        );

        customerRewardSummary = new CustomerRewardSummaryDTO(
                1L,
                "Alice Johnson",
                monthlyRewards,
                340
        );

        pagedRewardSummary = new PagedRewardSummaryDTO(
                List.of(customerRewardSummary),
                0,
                10,
                1,
                1,
                true
        );
    }

    @Test
    void testGetAllCustomerRewards_Success() {
        when(rewardService.getAllCustomerRewards(anyInt(), anyInt(), any(), any()))
                .thenReturn(pagedRewardSummary);

        ResponseEntity<PagedRewardSummaryDTO> response = rewardController.getAllCustomerRewards(0, 10, null, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().content().size());
        assertEquals(0, response.getBody().page());
        assertEquals(10, response.getBody().size());
        verify(rewardService).getAllCustomerRewards(0, 10, null, null);
    }

    @Test
    void testGetAllCustomerRewards_WithDateFilter() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 3, 31);

        when(rewardService.getAllCustomerRewards(0, 10, from, to))
                .thenReturn(pagedRewardSummary);

        ResponseEntity<PagedRewardSummaryDTO> response = rewardController.getAllCustomerRewards(0, 10, from, to);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(rewardService).getAllCustomerRewards(0, 10, from, to);
    }

    @Test
    void testGetAllCustomerRewards_WithPagination() {
        when(rewardService.getAllCustomerRewards(2, 5, null, null))
                .thenReturn(new PagedRewardSummaryDTO(List.of(), 2, 5, 0, 0, true));

        ResponseEntity<PagedRewardSummaryDTO> response = rewardController.getAllCustomerRewards(2, 5, null, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().page());
        assertEquals(5, response.getBody().size());
        verify(rewardService).getAllCustomerRewards(2, 5, null, null);
    }

    @Test
    void testGetAllCustomerRewards_InvalidDateRange() {
        LocalDate from = LocalDate.of(2024, 3, 1);
        LocalDate to = LocalDate.of(2024, 1, 1);

        when(rewardService.getAllCustomerRewards(0, 10, from, to))
                .thenThrow(new InvalidDateRangeException(from, to));

        assertThrows(InvalidDateRangeException.class,
                () -> rewardController.getAllCustomerRewards(0, 10, from, to));
    }

    @Test
    void testGetCustomerRewards_Success() {
        when(rewardService.getCustomerRewards(1L, null, null))
                .thenReturn(customerRewardSummary);

        ResponseEntity<CustomerRewardSummaryDTO> response = rewardController.getCustomerRewards(1L, null, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().customerId());
        assertEquals("Alice Johnson", response.getBody().customerName());
        assertEquals(340, response.getBody().totalPoints());
        assertEquals(2, response.getBody().monthlyRewards().size());
        verify(rewardService).getCustomerRewards(1L, null, null);
    }

    @Test
    void testGetCustomerRewards_WithDateFilter() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 2, 28);

        when(rewardService.getCustomerRewards(1L, from, to))
                .thenReturn(customerRewardSummary);

        ResponseEntity<CustomerRewardSummaryDTO> response = rewardController.getCustomerRewards(1L, from, to);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(rewardService).getCustomerRewards(1L, from, to);
    }

    @Test
    void testGetCustomerRewards_CustomerNotFound() {
        when(rewardService.getCustomerRewards(999L, null, null))
                .thenThrow(new CustomerNotFoundException(999L));

        assertThrows(CustomerNotFoundException.class,
                () -> rewardController.getCustomerRewards(999L, null, null));
    }

    @Test
    void testGetCustomerRewards_InvalidDateRange() {
        LocalDate from = LocalDate.of(2024, 3, 1);
        LocalDate to = LocalDate.of(2024, 1, 1);

        when(rewardService.getCustomerRewards(1L, from, to))
                .thenThrow(new InvalidDateRangeException(from, to));

        assertThrows(InvalidDateRangeException.class,
                () -> rewardController.getCustomerRewards(1L, from, to));
    }

    @Test
    void testGetCustomerRewards_NoRewards() {
        CustomerRewardSummaryDTO emptyRewards = new CustomerRewardSummaryDTO(
                1L,
                "Alice Johnson",
                List.of(),
                0
        );

        when(rewardService.getCustomerRewards(1L, null, null))
                .thenReturn(emptyRewards);

        ResponseEntity<CustomerRewardSummaryDTO> response = rewardController.getCustomerRewards(1L, null, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().totalPoints());
        assertEquals(0, response.getBody().monthlyRewards().size());
    }

    @Test
    void testGetAllCustomerRewards_EmptyResult() {
        PagedRewardSummaryDTO emptyPage = new PagedRewardSummaryDTO(
                List.of(),
                0,
                10,
                0,
                0,
                true
        );

        when(rewardService.getAllCustomerRewards(0, 10, null, null))
                .thenReturn(emptyPage);

        ResponseEntity<PagedRewardSummaryDTO> response = rewardController.getAllCustomerRewards(0, 10, null, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().content().size());
        assertEquals(0, response.getBody().totalElements());
    }

    @Test
    void testGetCustomerRewards_SameDateRange() {
        LocalDate sameDate = LocalDate.of(2024, 1, 15);

        when(rewardService.getCustomerRewards(1L, sameDate, sameDate))
                .thenReturn(customerRewardSummary);

        ResponseEntity<CustomerRewardSummaryDTO> response = rewardController.getCustomerRewards(1L, sameDate, sameDate);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(rewardService).getCustomerRewards(1L, sameDate, sameDate);
    }

    @Test
    void testGetAllCustomerRewards_LastPage() {
        PagedRewardSummaryDTO lastPage = new PagedRewardSummaryDTO(
                List.of(customerRewardSummary),
                2,
                10,
                25,
                3,
                true
        );

        when(rewardService.getAllCustomerRewards(2, 10, null, null))
                .thenReturn(lastPage);

        ResponseEntity<PagedRewardSummaryDTO> response = rewardController.getAllCustomerRewards(2, 10, null, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(true, response.getBody().last());
        assertEquals(3, response.getBody().totalPages());
    }

    @Test
    void testGetCustomerRewards_MultipleMonths() {
        List<MonthlyRewardDTO> multipleMonths = List.of(
                new MonthlyRewardDTO(2024, "JANUARY", 90),
                new MonthlyRewardDTO(2024, "FEBRUARY", 250),
                new MonthlyRewardDTO(2024, "MARCH", 150)
        );

        CustomerRewardSummaryDTO multiMonthSummary = new CustomerRewardSummaryDTO(
                1L,
                "Alice Johnson",
                multipleMonths,
                490
        );

        when(rewardService.getCustomerRewards(1L, null, null))
                .thenReturn(multiMonthSummary);

        ResponseEntity<CustomerRewardSummaryDTO> response = rewardController.getCustomerRewards(1L, null, null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, response.getBody().monthlyRewards().size());
        assertEquals(490, response.getBody().totalPoints());
    }
}
