package com.excelr.FoodDelivery.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.excelr.FoodDelivery.Models.Customer;
import com.excelr.FoodDelivery.Models.DTO.CustomerDetailsDTO;
import com.excelr.FoodDelivery.Repositories.CustomerRepository;
import com.excelr.FoodDelivery.Services.Utilities.CloudinaryUtil;

@Service
public class CustomerService {

	@Autowired
	CustomerRepository customerRepo;
	
	
	
	@Autowired
	private CloudinaryUtil cloudinaryUtil;

	public CustomerDetailsDTO updateCustomerDetails(Customer customer, CustomerDetailsDTO update, MultipartFile newProfilePic) throws Exception {
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


       if(update!=null) {
    	   if (update.getUsername() != null) customer.setUsername(update.getUsername());
           if (update.getFirstName() != null) customer.setFirstName(update.getFirstName());
           if (update.getLastName() != null) customer.setLastName(update.getLastName());
           if (update.getEmail() != null) customer.setEmail(update.getEmail());
           if (update.getPhone() != null) customer.setPhone(update.getPhone());
           if (update.getIsEnabled() != null)customer.setEnabled(update.getIsEnabled());
       }
       
        // Do NOT update addresses or password here

        Customer c = customerRepo.save(customer);
        
        return new CustomerDetailsDTO(c);
        
    }

    
}
