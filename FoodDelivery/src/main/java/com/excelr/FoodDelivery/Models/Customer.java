package com.excelr.FoodDelivery.Models;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;


@Data
@Entity
@Table(name = "customers")
@ToString(exclude = {"addresses","orders"})

public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
   
    private String firstName;   // Add this
 
    private String lastName;    // Add this
  
    private String username;
   
    private String email;
  
    private String phone;
    
    private String password;
    
    private String profilePic; // image URL (Cloudinary or Google)
    
    private String profilePicPublicId; // Cloudinary public_id, null if Google
    
    private String googleId;
    
   
    private Boolean enabled = true;
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Order> orders;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    
    private List<Address> addresses;
}
