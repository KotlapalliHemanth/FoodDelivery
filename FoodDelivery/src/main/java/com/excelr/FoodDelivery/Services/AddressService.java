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
        address.setOwnerType(AddressOwnerType.CUSTOMER);
        address.setCustomer(c);
        address.setIsActive(true);

        // Null-safe default address logic
        if (Boolean.TRUE.equals(a.getDefaultAddress())) {
            addressRepo.findByOwnerId(c.getId())
                .stream()
                .filter(ad -> Boolean.TRUE.equals(ad.getDefaultAddress()))
                .findFirst()
                .ifPresent(defaultAddress -> {
                    defaultAddress.setDefaultAddress(false);
                    addressRepo.save(defaultAddress);
                });
            address.setDefaultAddress(true);
        } else {
            address.setDefaultAddress(false);
        }

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
        address.setIsActive(true);
        return addressRepo.save(address);
    }

    @Transactional
    public Address modifyAddress(AddressDTO a) {
        Address address = addressRepo.findById(a.getId())
            .orElseThrow(() -> new RuntimeException("Address not found"));
        address.setIsActive(false);

        Address newAddress = new Address();
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

        if (address.getRestaurant() != null) {
            newAddress.setRestaurant(address.getRestaurant());
        }
        if (address.getCustomer() != null) {
            newAddress.setCustomer(address.getCustomer());

            if (Boolean.TRUE.equals(a.getDefaultAddress())) {
                addressRepo.findByOwnerId(address.getCustomer().getId())
                    .stream()
                    .filter(ad -> Boolean.TRUE.equals(ad.getDefaultAddress()))
                    .findFirst()
                    .ifPresent(defaultAddress -> {
                        defaultAddress.setDefaultAddress(false);
                        addressRepo.save(defaultAddress);
                    });
                newAddress.setDefaultAddress(true);
            } else {
                newAddress.setDefaultAddress(false);
            }
        }
        newAddress.setIsActive(true);
        addressRepo.save(address);
        return addressRepo.save(newAddress);
    }

    public List<Address> getAddresses(Long id) {
        return addressRepo.findByOwnerId(id);
    }
    
    @Transactional
    public void setDefaultAddress(Long customerId, Long addressId) {
        // Set all addresses for this customer to defaultAddress = false
        List<Address> addresses = addressRepo.findByOwnerId(customerId);
        for (Address addr : addresses) {
            if (Boolean.TRUE.equals(addr.getDefaultAddress())) {
                addr.setDefaultAddress(false);
                addressRepo.save(addr);
            }
        }
        // Set the selected address to defaultAddress = true
        Address address = addressRepo.findById(addressId)
            .orElseThrow(() -> new RuntimeException("Address not found"));
        address.setDefaultAddress(true);
        addressRepo.save(address);
    }

    public void deleteAddress(AddressDTO a) {
        Address address = addressRepo.findById(a.getId())
            .orElseThrow(() -> new RuntimeException("Address not found"));
        address.setIsActive(false);
        addressRepo.save(address);
    }
}