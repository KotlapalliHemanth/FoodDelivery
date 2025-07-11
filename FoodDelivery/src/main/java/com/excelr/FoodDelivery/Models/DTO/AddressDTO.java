package com.excelr.FoodDelivery.Models.DTO;

import com.excelr.FoodDelivery.Models.Address;
import com.excelr.FoodDelivery.Models.Customer;
import com.excelr.FoodDelivery.Models.Restaurant;
import com.excelr.FoodDelivery.Models.Enum.AddressOwnerType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddressDTO {

	private Long id;

    private String street;
    private String city;
    private String state;
    private String pincode;
    private String country;
    private String AddressName;
    private Double latitude;
    private Double longitude;
    private AddressOwnerType ownerType;
    
	public AddressDTO(Address a) {
		this.id= a.getId();
		this.street= a.getStreet();
		this.city= a.getCity();
		this.state= a.getState();
		this.pincode= a.getPincode();
		this.country= a.getCountry();
		this.latitude= a.getLatitude();
		this.longitude= a.getLongitude();
		this.ownerType= a.getOwnerType();
		this.AddressName= a.getAddressName();
	}
}
