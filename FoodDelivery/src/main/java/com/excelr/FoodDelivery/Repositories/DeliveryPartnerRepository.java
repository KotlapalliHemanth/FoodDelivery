package com.excelr.FoodDelivery.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.excelr.FoodDelivery.Models.DeliveryPartner;

import java.util.Optional;

public interface DeliveryPartnerRepository extends JpaRepository<DeliveryPartner, Long> {

    @Query("SELECT d FROM DeliveryPartner d WHERE d.username = :input OR d.email = :input OR d.phone = :input")
    Optional<DeliveryPartner> findByUsernameOrEmailOrPhone(@Param("input") String input);

    @Query("SELECT d FROM DeliveryPartner d WHERE (d.username = :input OR d.email = :input OR d.phone = :input) AND d.enabled = true")
    Optional<DeliveryPartner> findEnabled(@Param("input") String input);
}
