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
    private String profilePic;
    private Boolean enabled;
    
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
    	this.profilePic= restaurant.getProfilePic();
    	this.enabled= restaurant.getEnabled();
    	
    }
    
}
