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
            if (restaurant.getResturantPic() != null) {
                cloudinaryUtil.deleteImage(restaurant.getResturantPicPublicId());
            }
            // Upload new image
            CloudinaryUtil.UploadResult result = cloudinaryUtil.uploadImage(newProfilePic);
            restaurant.setResturantPic(result.imageUrl);
            restaurant.setResturantPicPublicId(result.publicId);
        } 
        
        if(update!=null) {
        	if (update.getUsername() != null) restaurant.setUsername(update.getUsername());
            if (update.getRestaurantName() != null) restaurant.setRestaurantName(update.getRestaurantName());
            if (update.getDescription()!=null) restaurant.setDescription(update.getDescription());
            if (update.getEmail() != null) restaurant.setEmail(update.getEmail());
            if (update.getPhone() != null) restaurant.setPhone(update.getPhone());
            if (update.getEnabled() != null) restaurant.setEnabled(update.getEnabled());
            if (update.getOpen() != null) restaurant.setOpen(update.getOpen());
            if(update.getRating()!=null) restaurant.setRating(update.getRating());
        }
        
        Restaurant r= restaurantRepo.save(restaurant);
        return new RestaurantDetailsDTO(r);
	}
	
	public List<RestaurantDetailsforCustomersDTO> findAndFilterRestaurantsByLocation(
	        Double latitude, Double longitude, Double radius, String searchName) {
	    List<Restaurant> restaurants = restaurantRepo.findRestaurantsWithinRadius(latitude, longitude, radius);

	    if (searchName != null && !searchName.isEmpty()) {
	        String searchLower = searchName.toLowerCase();
	        return restaurants.stream()
	            .filter(restaurant -> {
	                // Match by restaurant name
	                boolean matchesRestaurant = restaurant.getRestaurantName().toLowerCase().contains(searchLower);

	                // Match by cuisine
	                boolean matchesCuisine = restaurant.getDishes().stream()
	                        .anyMatch(dish -> dish.getCusine().equalsIgnoreCase(searchName));

	                // Match by dish name
	                boolean matchesDish = restaurant.getDishes().stream()
	                        .anyMatch(dish -> dish.getName().equalsIgnoreCase(searchName));

	                // Include if any match
	                return matchesRestaurant || matchesCuisine || matchesDish;
	            })
	            .map(RestaurantDetailsforCustomersDTO::new)
	            .collect(Collectors.toList());
	    } else {
	        // No searchName: return all in radius
	        return restaurants.stream()
	            .map(RestaurantDetailsforCustomersDTO::new)
	            .collect(Collectors.toList());
	    }
	}
}
