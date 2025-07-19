package com.excelr.FoodDelivery.Models.DTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.excelr.FoodDelivery.Models.Enum.OrderStatus;
import com.excelr.FoodDelivery.Models.Enum.PaymentStatus;

import lombok.Data;

@Data
public class CreateOrderDTO {
    private List<Long> dishIds;
    private Double amount;
    private Long deliveryAddressId;
    private Long restaurantId;
    private Map<Long, Integer> dishQuantities;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private Boolean riderAssigned;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deliveredAt;
}