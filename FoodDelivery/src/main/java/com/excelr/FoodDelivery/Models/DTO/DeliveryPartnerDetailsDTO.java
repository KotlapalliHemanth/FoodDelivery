package com.excelr.FoodDelivery.Models.DTO;

import com.excelr.FoodDelivery.Models.DeliveryPartner;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeliveryPartnerDetailsDTO {
	private Long id;

    private String username;
    private String email;
    private String phone;
    private String password;
    private String profilePic;
    private String profilePicPublicId;
    private String googleId;
    private Boolean enabled;
    
    private Double latitude;
    private Double longitude;
    
    public DeliveryPartnerDetailsDTO(DeliveryPartner rider) {
    	this.id= rider.getId();
    	this.username= rider.getUsername();
    	this.email= rider.getEmail();
    	if(rider.getPhone().equals(rider.getGoogleId())) {
			this.phone = "";
		}else {
			this.phone = rider.getPhone();
		}
    	this.profilePic= rider.getProfilePic();
    	this.enabled= rider.getEnabled();
    	this.googleId= rider.getGoogleId();
    	this.latitude= rider.getLatitude();
    	this.longitude= rider.getLongitude();
    	
    }
}
