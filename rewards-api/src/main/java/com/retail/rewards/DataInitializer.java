package com.retail.rewards;

import com.retail.rewards.entity.Customer;
import com.retail.rewards.entity.Transaction;
import com.retail.rewards.repository.CustomerRepository;
import com.retail.rewards.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Component to initialize sample data for demonstration.
 * Populates the database with customers and transactions on application startup.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public void run(String... args) {
        if (customerRepository.count() == 0) {
            initializeData();
        }
    }

    /**
     * Initializes sample customers and transactions.
     */
    private void initializeData() {
        Customer customer1 = new Customer();
        customer1.setName("Deepanshu");
        customer1.setEmail("deepanshu@example.com");

        Customer customer2 = new Customer();
        customer2.setName("Raj");
        customer2.setEmail("raj@example.com");

        Customer customer3 = new Customer();
        customer3.setName("Gagan");
        customer3.setEmail("gagan@example.com");

        List<Customer> customers = customerRepository.saveAll(Arrays.asList(customer1, customer2, customer3));

        LocalDate today = LocalDate.now();

        transactionRepository.saveAll(Arrays.asList(
                createTransaction(customers.get(0), 120.00, today.minusDays(5)),
                createTransaction(customers.get(0), 75.50, today.minusDays(15)),
                createTransaction(customers.get(0), 200.00, today.minusDays(45)),
                createTransaction(customers.get(0), 50.00, today.minusDays(60)),
                createTransaction(customers.get(0), 150.00, today.minusDays(75)),

                createTransaction(customers.get(1), 89.99, today.minusDays(10)),
                createTransaction(customers.get(1), 110.00, today.minusDays(20)),
                createTransaction(customers.get(1), 45.00, today.minusDays(35)),
                createTransaction(customers.get(1), 250.00, today.minusDays(50)),
                createTransaction(customers.get(1), 95.00, today.minusDays(70)),

                createTransaction(customers.get(2), 30.00, today.minusDays(8)),
                createTransaction(customers.get(2), 180.00, today.minusDays(25)),
                createTransaction(customers.get(2), 65.00, today.minusDays(40)),
                createTransaction(customers.get(2), 300.00, today.minusDays(55)),
                createTransaction(customers.get(2), 125.00, today.minusDays(80))
        ));
    }

    /**
     * Helper method to create a transaction.
     *
     * @param customer        the customer
     * @param amount          the transaction amount
     * @param transactionDate the transaction date
     * @return the created transaction
     */
    private Transaction createTransaction(Customer customer, Double amount, LocalDate transactionDate) {
        Transaction transaction = new Transaction();
        transaction.setCustomer(customer);
        transaction.setAmount(amount);
        transaction.setTransactionDate(transactionDate);
        return transaction;
    }
}
