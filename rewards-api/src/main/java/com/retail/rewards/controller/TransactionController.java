package com.retail.rewards.controller;

import com.retail.rewards.dto.TransactionDto;
import com.retail.rewards.dto.TransactionResponseDto;
import com.retail.rewards.entity.Transaction;
import com.retail.rewards.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for transaction-related endpoints.
 * Provides APIs to create and manage transactions.
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Creates a new transaction.
     *
     * @param transactionDto the transaction data
     * @return the created transaction
     */
    @PostMapping
    public ResponseEntity<TransactionResponseDto> createTransaction(@Valid @RequestBody TransactionDto transactionDto) {
        Transaction transaction = transactionService.createTransaction(transactionDto);
        TransactionResponseDto response = new TransactionResponseDto(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getTransactionDate()
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
