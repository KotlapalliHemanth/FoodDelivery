package com.excelr.FoodDelivery.Models;

import java.time.LocalDateTime;

import com.excelr.FoodDelivery.Models.Enum.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table(name = "transactions")
@ToString(exclude= {"order"})
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String transactionId;

    private Double amount;

    private String typeOfPay; // e.g. CARD, CASH, UPI

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @OneToOne(mappedBy = "transaction")
    @JsonIgnore
    private Order order;

    private LocalDateTime paidAt;
}
