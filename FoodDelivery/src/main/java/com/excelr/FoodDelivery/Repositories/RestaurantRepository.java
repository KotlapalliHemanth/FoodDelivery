package com.excelr.FoodDelivery.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.excelr.FoodDelivery.Models.Restaurant;

import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant,Long> {

    @Query("SELECT r FROM Restaurant r WHERE r.username = :input OR r.email = :input OR r.phone = :input")
    Optional<Restaurant> findByUsernameOrEmailOrPhone(@Param("input") String input);

    @Query("SELECT r FROM Restaurant r WHERE (r.username = :input OR r.email = :input OR r.phone = :input) AND r.isEnabled = true")
    Optional<Restaurant> findEnabled(@Param("input") String input);

}
