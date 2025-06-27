package com.excelr.FoodDelivery.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.excelr.FoodDelivery.Models.Dish;

public interface DishRepository extends JpaRepository<Dish, Long>{

}
