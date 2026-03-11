package com.charter.reward_api.repository;
import com.charter.reward_api.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Customer entity.
 * Provides CRUD operations for customer data.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
