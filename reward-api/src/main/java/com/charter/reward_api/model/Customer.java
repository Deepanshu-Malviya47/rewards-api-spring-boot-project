package com.charter.reward_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA entity representing a customer in the rewards program.
 */
@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Default constructor for JPA.
     */
    public Customer() {
    }

    /**
     * Constructs a new Customer with the specified name.
     *
     * @param name the customer name
     */
    public Customer(String name) {
        this.name = name;
    }

    /**
     * Gets the customer ID.
     *
     * @return the customer ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the customer ID.
     *
     * @param id the customer ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the customer name.
     *
     * @return the customer name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the customer name.
     *
     * @param name the customer name
     */
    public void setName(String name) {
        this.name = name;
    }
}
