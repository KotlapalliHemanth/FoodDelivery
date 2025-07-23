package com.excelr.FoodDelivery.Models.DTO;

import java.time.LocalDateTime;
import java.util.List;
import com.excelr.FoodDelivery.Models.Enum.OrderStatus;
import com.excelr.FoodDelivery.Models.Transaction;
import com.excelr.FoodDelivery.Models.DTO.CustomerDetailsDTO;
import com.excelr.FoodDelivery.Models.DTO.DeliveryPartnerDetailsDTO;
import com.excelr.FoodDelivery.Models.Order;
import com.excelr.FoodDelivery.Models.OrderDish;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderDTO {
    private Long id;
    private CustomerDetailsDTO customer;
    private DeliveryPartnerDetailsDTO deliveryPartner;
    private Transaction transaction;
    private List<OrderDish> orderDishes;
    private Double amount;
    private OrderStatus status;
    private Boolean riderAssigned;
    private LocalDateTime updatedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime createdAt;

    public OrderDTO(Order order) {
        this.id = order.getId();
        this.customer = order.getCustomer() != null ? new CustomerDetailsDTO(order.getCustomer()) : null;
        this.deliveryPartner = order.getDeliveryPartner() != null ? new DeliveryPartnerDetailsDTO(order.getDeliveryPartner()) : null;
        this.transaction = order.getTransaction();
        this.orderDishes = order.getOrderDishes();
        this.amount = order.getAmount();
        this.status = order.getStatus();
        this.riderAssigned = order.getRiderAssigned();
        this.updatedAt = order.getUpdatedAt();
        this.deliveredAt = order.getDeliveredAt();
        this.createdAt = order.getCreatedAt();
    }
} 