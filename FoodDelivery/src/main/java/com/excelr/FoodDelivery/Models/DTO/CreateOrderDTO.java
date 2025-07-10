package com.excelr.FoodDelivery.Models.DTO;

import java.util.List;

import lombok.Data;
@Data
public class CreateOrderDTO {
    private List<Long> dishIds;
    private Double amount;

    // Constructors, getters, and setters

 

}