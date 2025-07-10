package com.excelr.FoodDelivery.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.excelr.FoodDelivery.Models.Admin;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    @Query("SELECT a FROM Admin a WHERE a.username = :input OR a.email = :input OR a.phone = :input")
    Optional<Admin> findByUsernameOrEmailOrPhone(@Param("input") String input);
 

    @Query("SELECT a FROM Admin a WHERE (a.username = :input OR a.email = :input OR a.phone = :input) AND a.enabled = true")
    Optional<Admin> findEnabled(@Param("input") String input);
}
