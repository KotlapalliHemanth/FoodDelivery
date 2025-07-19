package com.excelr.FoodDelivery.Models.DTO;

import com.excelr.FoodDelivery.Models.Customer;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CustomerDetailsDTO {
	private Long id;
	private String firstName;
	private String lastName;
	private String username;
	private String email;
	private String phone;
	private String profilePic;
	private String googleId;
	private Boolean isEnabled;
	
	public CustomerDetailsDTO(Customer customer) {
		this.id = customer.getId();
		this.firstName = customer.getFirstName();
		this.lastName = customer.getLastName();
		this.username = customer.getUsername();
		this.email = customer.getEmail();
		if(customer.getPhone().equals(customer.getGoogleId())) {
			this.phone = "";
		}else {
			this.phone = customer.getPhone();
		}
		this.profilePic = customer.getProfilePic();
		this.googleId= customer.getGoogleId();
		this.isEnabled= customer.getEnabled();
	}
}
