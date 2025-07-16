package com.excelr.FoodDelivery.Services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.excelr.FoodDelivery.Models.Restaurant;
import com.excelr.FoodDelivery.Models.DTO.RestaurantDetailsDTO;
import com.excelr.FoodDelivery.Models.DTO.RestaurantDetailsforCustomersDTO;
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
        if (update.getOpen() != null) restaurant.setOpen(update.getOpen());
        
        Restaurant r= restaurantRepo.save(restaurant);
        return new RestaurantDetailsDTO(r);
	}
	
	public List<RestaurantDetailsforCustomersDTO> findAndFilterRestaurantsByLocation(Double latitude, Double longitude, Double radius,String searchName){
		List<Restaurant> restaurants = restaurantRepo.findRestaurantsWithinRadius(latitude, longitude, radius);

        // Apply filters using Java Streams
        return restaurants.stream()
            .filter(restaurant -> {
                // Cuisine filter
                if (searchName != null && !searchName.isEmpty()) {
                    return restaurant.getDishes().stream()
                        .anyMatch(dish -> dish.getCusine().equalsIgnoreCase(searchName));
                }
                return true; // No filter applied
            })
            .filter(restaurant -> {
                // Dish name filter
                if (searchName != null && !searchName.isEmpty()) {
                    return restaurant.getDishes().stream()
                        .anyMatch(dish -> dish.getName().equalsIgnoreCase(searchName));
                }
                return true; // No filter applied
            })
            .filter(restaurant -> {
                // Restaurant name filter (substring, case-insensitive)
                if (searchName != null && !searchName.isEmpty()) {
                    return restaurant.getRestaurantName().toLowerCase().contains(searchName.toLowerCase());
                }
                return true; // No filter applied
            })
            .map(RestaurantDetailsforCustomersDTO::new).collect(Collectors.toList());
	}
}
