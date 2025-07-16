package com.excelr.FoodDelivery.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.excelr.FoodDelivery.Models.Restaurant;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant,Long> {

    @Query("SELECT r FROM Restaurant r WHERE r.username = :input OR r.email = :input OR r.phone = :input")
    Optional<Restaurant> findByUsernameOrEmailOrPhone(@Param("input") String input);

    @Query("SELECT r FROM Restaurant r WHERE (r.username = :input OR r.email = :input OR r.phone = :input) AND r.enabled = true")
    Optional<Restaurant> findEnabled(@Param("input") String input);

    @Query(value = "SELECT r FROM Restaurant r JOIN r.addresses a WHERE  r.enabled = true AND " +
           "6371 * acos(cos(radians(:latitude)) * cos(radians(a.latitude)) * " +
           "cos(radians(a.longitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(a.latitude))) <= :radius")
    List<Restaurant> findRestaurantsWithinRadius(@Param("latitude") Double latitude,@Param("longitude") Double longitude,@Param("radius") Double radius);
}
