package com.excelr.FoodDelivery.Models.DTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class CreateOrderDTO {
    private List<Long> dishIds;
    private Double amount;
    private Long deliveryAddressId;
    private Long restaurantId;
    private String customerNote;
    private LocalDateTime deliveredAt; // Set by backend when order is delivered
    private LocalDateTime updatedAt;   // Set by backend when order is updated
    private Map<Long, Integer> dishQuantities; // dishId -> quantity

    // Constructors, getters, and setters (Lombok @Data provides these)
}