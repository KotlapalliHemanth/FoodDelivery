package com.excelr.FoodDelivery.Models;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;


@Data
@Entity
@Table(name = "restaurants")
@ToString(exclude= {"dishes","addresses"})
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String restaurantName;
    private String email;
    private String phone;
    private String password;
    private String resturantPic;
    private String resturantPicPublicId;
    private String googleId;
    private String description;
    private Boolean open;
    private Double rating;
    
    private Boolean enabled = true;
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "restaurant")
    private List<Dish> dishes;

    @OneToOne(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private Address addresses;

}