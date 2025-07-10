package com.excelr.FoodDelivery.Models.DTO;

import java.util.List;

public class CreateOrderDTO {
    public List<Long> dishIds;
    public Double amount;

    // Constructors, getters, and setters

    public CreateOrderDTO() {
    }

    public CreateOrderDTO(List<Long> dishIds, Double amount) {
        this.dishIds = dishIds;
        this.amount = amount;
    }

    public List<Long> getDishIds() {
        return dishIds;
    }

    public Double getAmount() {
        return amount;
    }

}