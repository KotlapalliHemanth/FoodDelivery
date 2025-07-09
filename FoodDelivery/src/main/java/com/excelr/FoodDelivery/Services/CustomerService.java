package com.excelr.FoodDelivery.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.excelr.FoodDelivery.Models.Customer;
import com.excelr.FoodDelivery.Models.Dish;
import com.excelr.FoodDelivery.Models.Order;
import com.excelr.FoodDelivery.Models.DTO.CreateOrderDTO;
import com.excelr.FoodDelivery.Repositories.CustomerRepository;
import com.excelr.FoodDelivery.Repositories.DishRepository;
import com.excelr.FoodDelivery.Repositories.OrderRepository;
import com.excelr.FoodDelivery.Services.Utilities.CloudinaryUtil;

@Service
public class CustomerService {

	@Autowired
	CustomerRepository customerRepo;
	
	@Autowired
	DishRepository dishRepo;
	
	@Autowired
	OrderRepository orderRepo;
	
	
	@Autowired
	private CloudinaryUtil cloudinaryUtil;

	public Customer updateCustomerDetails(Customer customer, Customer update, MultipartFile newProfilePic) throws Exception {
        // Only update fields if provided (not null), except addresses and password

        // Handle profile pic replacement
        if (newProfilePic != null && !newProfilePic.isEmpty()) {
            // Delete old image from Cloudinary if it exists
            if (customer.getProfilePicPublicId() != null) {
                cloudinaryUtil.deleteImage(customer.getProfilePicPublicId());
            }
            // Upload new image
            CloudinaryUtil.UploadResult result = cloudinaryUtil.uploadImage(newProfilePic);
            customer.setProfilePic(result.imageUrl);
            customer.setProfilePicPublicId(result.publicId);
        } 


        if (update.getUsername() != null) customer.setUsername(update.getUsername());
        if (update.getEmail() != null) customer.setEmail(update.getEmail());
        if (update.getPhone() != null) customer.setPhone(update.getPhone());
        if (update.getIsEnabled() != null) customer.setIsEnabled(update.getIsEnabled());
        // Do NOT update addresses or password here

        return customerRepo.save(customer);
    }

    
}
