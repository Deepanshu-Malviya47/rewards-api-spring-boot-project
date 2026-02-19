package com.retail.rewards.service;

import com.retail.rewards.dto.TransactionDto;
import com.retail.rewards.entity.Customer;
import com.retail.rewards.entity.Transaction;
import com.retail.rewards.exception.ResourceNotFoundException;
import com.retail.rewards.repository.CustomerRepository;
import com.retail.rewards.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing transactions.
 * Handles transaction creation and retrieval.
 */
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;

    /**
     * Creates a new transaction for a customer.
     *
     * @param transactionDto the transaction data
     * @return the created transaction
     */
    @Transactional
    public Transaction createTransaction(TransactionDto transactionDto) {
        Customer customer = customerRepository.findById(transactionDto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + transactionDto.getCustomerId()));

        Transaction transaction = new Transaction();
        transaction.setCustomer(customer);
        transaction.setAmount(transactionDto.getAmount());
        transaction.setTransactionDate(transactionDto.getTransactionDate());

        return transactionRepository.save(transaction);
    }
}
