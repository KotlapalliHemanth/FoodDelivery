package com.excelr.FoodDelivery.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.excelr.FoodDelivery.Models.Customer;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT c FROM Customer c WHERE c.username = :input OR c.email = :input OR c.phone = :input")
    Optional<Customer> findByUsernameOrEmailOrPhone(@Param("input") String input);
    
    
    @Query("SELECT c FROM Customer c WHERE (c.username = :input OR c.email = :input OR c.phone = :input) AND c.enabled = true")
    Optional<Customer> findEnabled(@Param("input") String input);
}
