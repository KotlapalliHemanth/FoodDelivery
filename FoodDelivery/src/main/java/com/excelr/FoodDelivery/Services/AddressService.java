package com.excelr.FoodDelivery.Services;

import java.util.List;

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
		address.setLandmark(a.getLandmark());
		address.setFulladdress(a.getFulladdress());
		address.setAddressName(a.getAddressName());
		if(a.getDefaultAddress()) {
			Address defaultAddress= addressRepo.findByOwnerId(c.getId()).stream().filter(ad->ad.getDefaultAddress()).findFirst()
						.orElseThrow(() -> new RuntimeException("Address not found"));
			defaultAddress.setDefaultAddress(false);
			addressRepo.save(defaultAddress);
			address.setDefaultAddress(a.getDefaultAddress());
		
		}else {
			address.setDefaultAddress(a.getDefaultAddress());
		}
		address.setOwnerType(AddressOwnerType.CUSTOMER);
		address.setCustomer(c);
		address.setIsActive(true);
		return addressRepo.save(address);
	}
	
	public Address createRestaurantAddress(Restaurant c,AddressDTO a) {
		Address address= new Address();
		
		address.setStreet(a.getStreet());
		address.setState(a.getState());
		address.setCity(a.getCity());
		address.setPincode(a.getPincode());
		address.setCountry(a.getCountry());
		address.setLatitude(a.getLatitude());
		address.setLongitude(a.getLongitude());
		address.setFulladdress(a.getFulladdress());
		address.setAddressName(a.getAddressName());
		address.setLandmark(a.getLandmark());
		address.setOwnerType(AddressOwnerType.RESTAURANT);
		address.setDefaultAddress(true);
		address.setRestaurant(c);
		address.setIsActive(true);
		return addressRepo.save(address);
	}
	
	public Address modifyAddress(AddressDTO a) {
		Address address= addressRepo.findById(a.getId())
					.orElseThrow(() -> new RuntimeException("Address not found"));
		address.setIsActive(false);
	 	Address newAddress= new Address();
		newAddress.setStreet(a.getStreet());
		newAddress.setState(a.getState());
		newAddress.setCity(a.getCity());
		newAddress.setPincode(a.getPincode());
		newAddress.setCountry(a.getCountry());
		newAddress.setLatitude(a.getLatitude());
		newAddress.setLongitude(a.getLongitude());
		newAddress.setLandmark(a.getLandmark());
		newAddress.setFulladdress(a.getFulladdress());
		newAddress.setAddressName(a.getAddressName());
	    newAddress.setOwnerType(address.getOwnerType());
	    if(address.getRestaurant() != null) {
		newAddress.setRestaurant(address.getRestaurant());
		}
		if(address.getCustomer() != null) {
		newAddress.setCustomer(address.getCustomer());
		
		if(a.getDefaultAddress()) {
			Address defaultAddress= addressRepo.findByOwnerId(address.getCustomer().getId()).stream().filter(ad->ad.getDefaultAddress()).findFirst()
						.orElseThrow(() -> new RuntimeException("Address not found"));
			defaultAddress.setDefaultAddress(false);
			addressRepo.save(defaultAddress);
			newAddress.setDefaultAddress(a.getDefaultAddress());
		
		}else {
			newAddress.setDefaultAddress(a.getDefaultAddress());
		}
		
		}
		
		
		addressRepo.save(address);		
		return addressRepo.save(newAddress);
	
		}
	
	public List<Address> getAddresses ( Long id) {
		return  addressRepo.findByOwnerId(id);
	}
	
	public void deleteAddress(AddressDTO a) {
		Address address= addressRepo.findById(a.getId())
				.orElseThrow(() -> new RuntimeException("Address not found"));
		
		address.setIsActive(false);
		addressRepo.save(address);
	
	}
	
}
