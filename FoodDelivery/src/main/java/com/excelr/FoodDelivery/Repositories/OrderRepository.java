package com.excelr.FoodDelivery.Repositories;

import java.time.LocalDateTime;
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
    
    @Query("SELECT DISTINCT o FROM Order o " +
           "JOIN o.dishes d JOIN d.restaurant r JOIN r.addresses a " +
           "WHERE o.status = 'PREPARING' AND " +
           "(6371 * acos(cos(radians(:latitude)) * cos(radians(a.latitude)) * " +
           "cos(radians(a.longitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(a.latitude)))) <= 4.0")
    List<Order> findPreparingOrders(@Param("latitude") Double latitude, @Param("longitude") Double longitude);
    
    @Query("SELECT o FROM Order o WHERE o.deliveryPartner.id = :deliveryPartnerId AND o.status = 'PREPARING'")
    List<Order> findPreparingOrdersByDeliveryPartnerId(@Param("deliveryPartnerId") Long deliveryPartnerId);

    @Query("SELECT o FROM Order o WHERE o.deliveryPartner.id = :deliveryPartnerId AND o.status = 'DELIVERED' AND o.createdAt >= :startOfDay AND o.createdAt < :endOfDay")
    List<Order> findDeliveredOrdersByRiderForDateRange(@Param("deliveryPartnerId") Long deliveryPartnerId, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
}
