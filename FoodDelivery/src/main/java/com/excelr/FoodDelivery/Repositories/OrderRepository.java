package com.excelr.FoodDelivery.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.excelr.FoodDelivery.Models.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
