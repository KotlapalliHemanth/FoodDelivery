package com.excelr.FoodDelivery.Models;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Data
@Entity
@Table(name = "delivery_partners")
public class DeliveryPartner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String phone;
    private String password;
    private String profilePic;
     private String profilePicPublicId;
    private String googleId;
    private Boolean isEnabled = true;
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "deliveryPartner")
    private List<Order> deliveries;
}
