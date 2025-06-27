package com.excelr.FoodDelivery.Models;

import java.time.LocalDateTime;

import com.excelr.FoodDelivery.Models.Enum.PaymentStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;

    private String typeOfPay; // e.g. CARD, CASH, UPI

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private LocalDateTime paidAt = LocalDateTime.now();
}
