package com.excelr.FoodDelivery.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.excelr.FoodDelivery.Models.Restaurant;
import com.excelr.FoodDelivery.Models.DTO.RestaurantDetailsDTO;
import com.excelr.FoodDelivery.Repositories.RestaurantRepository;
import com.excelr.FoodDelivery.Services.Utilities.CloudinaryUtil;

@Service
public class RestaurantService {

	
	@Autowired
	RestaurantRepository restaurantRepo;
	
	@Autowired
	private CloudinaryUtil cloudinaryUtil;
	
	public RestaurantDetailsDTO updateRestaurantDetails(Restaurant restaurant, RestaurantDetailsDTO update, MultipartFile newProfilePic) throws Exception{
		
		// Handle profile pic replacement
        if (newProfilePic != null && !newProfilePic.isEmpty()) {
            // Delete old image from Cloudinary if it exists
            if (restaurant.getProfilePicPublicId() != null) {
                cloudinaryUtil.deleteImage(restaurant.getProfilePicPublicId());
            }
            // Upload new image
            CloudinaryUtil.UploadResult result = cloudinaryUtil.uploadImage(newProfilePic);
            restaurant.setProfilePic(result.imageUrl);
            restaurant.setProfilePicPublicId(result.publicId);
        } 
        
        if (update.getUsername() != null) restaurant.setUsername(update.getUsername());
        if (update.getRestaurantName() != null) restaurant.setRestaurantName(update.getRestaurantName());
        
        if (update.getEmail() != null) restaurant.setEmail(update.getEmail());
        if (update.getPhone() != null) restaurant.setPhone(update.getPhone());
        if (update.getEnabled() != null) restaurant.setEnabled(update.getEnabled());
        
        Restaurant r= restaurantRepo.save(restaurant);
        return new RestaurantDetailsDTO(r);
	}
}
