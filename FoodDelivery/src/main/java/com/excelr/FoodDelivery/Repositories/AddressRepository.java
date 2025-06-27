package com.excelr.FoodDelivery.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.excelr.FoodDelivery.Models.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {

}
