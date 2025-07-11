package com.excelr.FoodDelivery.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.excelr.FoodDelivery.Models.Address;
import com.excelr.FoodDelivery.Models.Customer;
import com.excelr.FoodDelivery.Models.Restaurant;
import com.excelr.FoodDelivery.Models.DTO.AddressDTO;
import com.excelr.FoodDelivery.Models.Enum.AddressOwnerType;
import com.excelr.FoodDelivery.Repositories.AddressRepository;


@Service
public class AddressService {

	@Autowired
	AddressRepository addressRepo;
	
	
	
	public Address createCustomerAddress(Customer c,AddressDTO a) {
		Address address= new Address();
		
		address.setStreet(a.getStreet());
		address.setState(a.getState());
		address.setCity(a.getCity());
		address.setPincode(a.getPincode());
		address.setCountry(a.getCountry());
		address.setLatitude(a.getLatitude());
		address.setLongitude(a.getLongitude());
		address.setOwnerType(AddressOwnerType.CUSTOMER);
		address.setCustomer(c);
		
		return addressRepo.save(address);
	}
	
	public Address createRestaurentAddress(Restaurant c,AddressDTO a) {
		Address address= new Address();
		
		address.setStreet(a.getStreet());
		address.setState(a.getState());
		address.setCity(a.getCity());
		address.setPincode(a.getPincode());
		address.setCountry(a.getCountry());
		address.setLatitude(a.getLatitude());
		address.setLongitude(a.getLongitude());
		address.setOwnerType(AddressOwnerType.RESTAURANT);
		address.setRestaurant(c);
		
		return addressRepo.save(address);
	}
	
}
