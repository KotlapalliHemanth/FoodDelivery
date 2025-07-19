package com.excelr.FoodDelivery.Models.DTO;

import com.excelr.FoodDelivery.Models.Restaurant;
import com.excelr.FoodDelivery.Models.Address;


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
}