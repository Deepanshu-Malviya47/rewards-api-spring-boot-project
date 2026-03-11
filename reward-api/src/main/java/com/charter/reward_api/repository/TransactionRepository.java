package com.charter.reward_api.repository;

import com.charter.reward_api.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Transaction entity.
 * Provides CRUD operations and custom queries for transaction data.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Finds all transactions for a specific customer within a date range.
     *
     * @param customerId the customer ID
     * @param from the start date (inclusive)
     * @param to the end date (inclusive)
     * @return list of transactions
     */
    @Query("SELECT t FROM Transaction t WHERE t.customer.id = :customerId " +
            "AND t.transactionDate BETWEEN :from AND :to")
    List<Transaction> findByCustomerIdAndDateRange(
            @Param("customerId") Long customerId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );

    /**
     * Finds all transactions within a date range.
     *
     * @param from the start date (inclusive)
     * @param to the end date (inclusive)
     * @return list of transactions
     */
    @Query("SELECT t FROM Transaction t WHERE t.transactionDate BETWEEN :from AND :to")
    List<Transaction> findByDateRange(
            @Param("from") LocalDate from,
            @Param("to") LocalDate to
    );
}
