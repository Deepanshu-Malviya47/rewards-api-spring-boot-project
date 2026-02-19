package com.retail.rewards.repository;

import com.retail.rewards.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Transaction entity.
 * Provides CRUD operations and custom queries for transactions.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Finds all transactions for a customer within a date range.
     *
     * @param customerId the customer ID
     * @param startDate  the start date
     * @param endDate    the end date
     * @return list of transactions
     */
    @Query("SELECT t FROM Transaction t WHERE t.customer.id = :customerId AND t.transactionDate BETWEEN :startDate AND :endDate")
    List<Transaction> findByCustomerIdAndDateRange(
            @Param("customerId") Long customerId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Finds all transactions within a date range.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return list of transactions
     */
    @Query("SELECT t FROM Transaction t WHERE t.transactionDate BETWEEN :startDate AND :endDate")
    List<Transaction> findByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
