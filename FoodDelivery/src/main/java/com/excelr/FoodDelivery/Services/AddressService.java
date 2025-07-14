package com.excelr.FoodDelivery.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.excelr.FoodDelivery.Models.Address;
import com.excelr.FoodDelivery.Models.Customer;
import com.excelr.FoodDelivery.Models.DTO.AddressDTO;
import com.excelr.FoodDelivery.Models.Enum.AddressOwnerType;
import com.excelr.FoodDelivery.Models.Restaurant;
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
		address.setIsActive(true);
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
		address.setIsActive(true);
		return addressRepo.save(address);
	}
	
//	public Address modifyAddress(AddressDTO a) {
//		Address address= addressRepo.findById(a.getId())
//					.orElseThrow(() -> new RuntimeException("Address not found"));
//		
//		address.setStreet(a.getStreet());
//		address.setState(a.getState());
//		address.setCity(a.getCity());
//		address.setPincode(a.getPincode());
//		address.setCountry(a.getCountry());
//		address.setLatitude(a.getLatitude());
//		address.setLongitude(a.getLongitude());
//		address.setAddressName(a.getAddressName());
//				
//		return addressRepo.save(address);
//	
//	}
	
	public List<Address> getAddresses ( Long id) {
		return  addressRepo.findByOwnerId(id);
	}
	
	public void deleteAddress(AddressDTO a) {
		Address address= addressRepo.findById(a.getId())
				.orElseThrow(() -> new RuntimeException("Address not found"));
		
		address.setIsActive(false);
	
	}
	
}
