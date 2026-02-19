package com.retail.rewards.repository;

import com.retail.rewards.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Customer entity.
 * Provides CRUD operations for customers.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
