package com.excelr.FoodDelivery.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.excelr.FoodDelivery.Models.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query("SELECT a FROM Address a WHERE a.customer.id = :ownerId OR a.restaurant.id = :ownerId")
    List<Address> findByOwnerId(@Param("ownerId") Long ownerId);

}
