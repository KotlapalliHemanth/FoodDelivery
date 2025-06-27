package com.excelr.FoodDelivery.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.excelr.FoodDelivery.Models.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long>{

}
