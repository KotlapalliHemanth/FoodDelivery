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

import jakarta.transaction.Transactional;

@Service
public class AddressService {

	@Autowired
	AddressRepository addressRepo;

	public Address createCustomerAddress(Customer c, AddressDTO a) {
		Address address = new Address();

		address.setAddressName(a.getAddressName());
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

	public Address createRestaurentAddress(Restaurant c, AddressDTO a) {
		Address address = new Address();

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
		address.setLandmark(a.getLandmark());
		address.setFulladdress(a.getFulladdress());
		address.setIsActive(true);
		return addressRepo.save(address);
	}

	@Transactional
	public Address modifyAddress(AddressDTO a) {
	    Address oldAddress = addressRepo.findById(a.getId())
	        .orElseThrow(() -> new RuntimeException("Address not found"));

	    // Mark old address as inactive
	    oldAddress.setIsActive(false);
	    addressRepo.save(oldAddress);

	    // Create new address
	    Address newAddress = new Address();
	    newAddress.setStreet(a.getStreet());
	    newAddress.setState(a.getState());
	    newAddress.setCity(a.getCity());
	    newAddress.setPincode(a.getPincode());
	    newAddress.setCountry(a.getCountry());
	    newAddress.setLatitude(a.getLatitude());
	    newAddress.setLongitude(a.getLongitude());
	    newAddress.setAddressName(a.getAddressName());
	    newAddress.setLandmark(a.getLandmark());
	    newAddress.setFulladdress(a.getFulladdress());
	    newAddress.setOwnerType(oldAddress.getOwnerType());
	    newAddress.setIsActive(true);

	    // Link to customer or restaurant
	    if (oldAddress.getRestaurant() != null) {
	        newAddress.setRestaurant(oldAddress.getRestaurant());
	        // Optionally: oldAddress.getRestaurant().getAddresses().add(newAddress);
	    }
	    if (oldAddress.getCustomer() != null) {
	        newAddress.setCustomer(oldAddress.getCustomer());
	        // Optionally: oldAddress.getCustomer().getAddresses().add(newAddress);
	    }

	    return addressRepo.save(newAddress);
	}

	public List<Address> getAddresses(Long id) {
		return addressRepo.findByOwnerId(id);
	}

	public void deleteAddress(AddressDTO a) {
		Address address = addressRepo.findById(a.getId()).orElseThrow(() -> new RuntimeException("Address not found"));

		address.setIsActive(false);
		addressRepo.save(address);
	}

}
