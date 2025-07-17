package com.excelr.FoodDelivery.Models.DTO;

import java.time.LocalDateTime;

import com.excelr.FoodDelivery.Models.Restaurant;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RestaurantDetailsforCustomersDTO {
	
	private Long id;

    private String username;
    private String restaurantName;
    private String email;
    private String phone;
    private String profilePic;
    private String description;
    private Double rating;
    private Boolean open;
    
    public RestaurantDetailsforCustomersDTO(Restaurant r) {
    	this.id = r.getId();
    	this.username= r.getUsername();
    	this.restaurantName= r.getRestaurantName();
    	this.email= r.getEmail();
    	this.phone= r.getPhone();
    	this.profilePic= r.getProfilePic();
    	this.open= r.getOpen();
    	this.description= r.getDescription();
    	this.rating= r.getRating();
    	
    }
    
    

}
