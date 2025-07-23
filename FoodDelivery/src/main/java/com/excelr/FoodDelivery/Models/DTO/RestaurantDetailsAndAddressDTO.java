package com.excelr.FoodDelivery.Models.DTO;

import com.excelr.FoodDelivery.Models.Address;
import com.excelr.FoodDelivery.Models.Restaurant;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class RestaurantDetailsAndAddressDTO {
	private RestaurantDetailsDTO rDto;
	private AddressDTO aDto;
	public RestaurantDetailsAndAddressDTO (Restaurant res, Address ad) {
		this.rDto= new RestaurantDetailsDTO(res);
		this.aDto= new AddressDTO(ad);
	}
	
	public RestaurantDetailsAndAddressDTO (Restaurant res) {
		this.rDto= new RestaurantDetailsDTO(res);
		this.aDto= new AddressDTO(res.getAddresses());
	}
}
