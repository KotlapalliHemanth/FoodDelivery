package com.excelr.FoodDelivery.Models;

import com.excelr.FoodDelivery.Models.Enum.AddressOwnerType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table(name = "addresses")
@ToString(exclude = {"customer","restaurant"})
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fulladdress;
    private String street;
    private String city;
    private String state;
    private String pincode;
    private String country;
    private String AddressName;
    private String landmark;
    private Double latitude;
    private Double longitude;

    @Enumerated(EnumType.STRING)
    private AddressOwnerType ownerType; // CUSTOMER or RESTAURANT

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonIgnore
    private Customer customer;

    @OneToOne
    @JoinColumn(name = "restaurant_id")
    @JsonIgnore
    private Restaurant restaurant;

    private Boolean isActive = false;
    private Boolean defaultAddress = false;
}



