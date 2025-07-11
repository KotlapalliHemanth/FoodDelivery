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
	private boolean isEnabled;
	
	public CustomerDetailsDTO(Customer customer) {
		this.id = customer.getId();
		this.firstName = customer.getFirstName();
		this.lastName = customer.getLastName();
		this.username = customer.getUsername();
		this.email = customer.getEmail();
		this.phone = customer.getPhone();
		this.profilePic = customer.getProfilePic();
		this.isEnabled= customer.getEnabled();
	}
}
