package com.excelr.FoodDelivery.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.excelr.FoodDelivery.Models.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    
    @Query("SELECT DISTINCT o FROM Order o JOIN o.dishes d WHERE o.status = 'CREATED' AND d.restaurant.id = :restaurantId")
    List<Order> findCreatedOrdersByRestaurantId(@Param("restaurantId") Long restaurantId);
    
    @Query("SELECT DISTINCT o FROM Order o JOIN o.dishes d WHERE o.status = 'PREPARING' AND d.restaurant.id = :restaurantId")
    List<Order> findAcceptedOrdersByRestaurantId(@Param("restaurantId") Long restaurantId);

    @Query("SELECT DISTINCT o FROM Order o JOIN o.dishes d WHERE o.status = 'DELIVERED' AND d.restaurant.id = :restaurantId")
    List<Order> findDeliveredOrdersByRestaurantId(@Param("restaurantId") Long restaurantId);
}
