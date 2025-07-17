package com.excelr.FoodDelivery.Models.DTO;

import com.excelr.FoodDelivery.Models.Restaurant;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RestaurantDetailsDTO {

	private Long id;

    private String username;
    private String restaurantName;
    private String email;
    private String phone;
    private String restaurantPic;
    private Boolean enabled;
    private Boolean open;
    private String googleId;
    private String description;
    private Double rating;
    private Double lat;
    private Double lon;
    
    public RestaurantDetailsDTO(Restaurant restaurant) {
    	this.id= restaurant.getId();
    	this.username= restaurant.getUsername();
    	this.restaurantName= restaurant.getRestaurantName();
    	this.email= restaurant.getEmail();
    	if(restaurant.getPhone()==restaurant.getGoogleId()) {
			this.phone = "";
		}else {
			this.phone = restaurant.getPhone();
		}
    	this.restaurantPic= restaurant.getProfilePic();
    	this.googleId=restaurant.getGoogleId();
    	this.enabled= restaurant.getEnabled();
    	this.description= restaurant.getDescription();
    	this.rating= restaurant.getRating();
    	
    	this.lat=restaurant.getAddresses().getLatitude();
    	this.lon= restaurant.getAddresses().getLongitude();
    }
    
}
