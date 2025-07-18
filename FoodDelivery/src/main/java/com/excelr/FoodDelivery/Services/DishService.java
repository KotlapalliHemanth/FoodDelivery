package com.excelr.FoodDelivery.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.excelr.FoodDelivery.Models.Address;
import com.excelr.FoodDelivery.Models.Dish;
import com.excelr.FoodDelivery.Models.Restaurant;
import com.excelr.FoodDelivery.Models.DTO.AddressDTO;
import com.excelr.FoodDelivery.Models.DTO.DishDTO;
import com.excelr.FoodDelivery.Models.DTO.RestaurantDetailsAndAddressDTO;
import com.excelr.FoodDelivery.Models.DTO.RestaurantDetailsDTO;
import com.excelr.FoodDelivery.Repositories.DishRepository;
import com.excelr.FoodDelivery.Services.Utilities.CloudinaryUtil;


@Service
public class DishService {
	@Autowired
	private CloudinaryUtil cloudinaryUtil;
	
	@Autowired
	DishRepository dishRepo;
	@Autowired
    private AddressService addressService;
	
	public DishDTO createDish (Restaurant r, DishDTO d, MultipartFile newProfilePic ) throws Exception {
		Dish dish= new Dish();
	
		dish.setName(d.getName());
		dish.setPrice(d.getPrice());
		dish.setCategory(d.getCategory());
		dish.setCusine(d.getCusine());
		dish.setDescription(d.getDescription());
		dish.setAvailable(d.getAvailable());
		if (newProfilePic != null && !newProfilePic.isEmpty()) {
            // Delete old image from Cloudinary if it exists
            if (dish.getImagePublicId() != null) {
                cloudinaryUtil.deleteImage(dish.getImagePublicId());
            }
            // Upload new image
            CloudinaryUtil.UploadResult result = cloudinaryUtil.uploadImage(newProfilePic);
            dish.setImage(result.imageUrl);
            dish.setImagePublicId(result.publicId);
            
        } 
		dish.setDeleted(false);
		dish.setRestaurant(r);
		return new DishDTO(dishRepo.save(dish));
		
	}
	
	public DishDTO modifyDish (Restaurant r,DishDTO d, MultipartFile newProfilePic)throws Exception {
		Dish dA = dishRepo.findById(d.getId())
				.orElseThrow(() -> new RuntimeException("dish not found"));
		dA.setDeleted(true);
		dishRepo.save(dA);
		Dish dish= new Dish();
		
		dish.setName(d.getName());
		dish.setPrice(d.getPrice());
		dish.setCategory(d.getCategory());
		dish.setCusine(d.getCusine());
		dish.setDescription(d.getDescription());
		dish.setAvailable(d.getAvailable());
		if (newProfilePic != null && !newProfilePic.isEmpty()) {
            // Delete old image from Cloudinary if it exists
            if (dish.getImagePublicId() != null) {
                cloudinaryUtil.deleteImage(dish.getImagePublicId());
            }
            // Upload new image
            CloudinaryUtil.UploadResult result = cloudinaryUtil.uploadImage(newProfilePic);
            dish.setImage(result.imageUrl);
            dish.setImagePublicId(result.publicId);  
        } 
		dish.setDeleted(false);
		dish.setRestaurant(r);
		return new DishDTO(dishRepo.save(dish));
		
	}
	
	public void deleteDish (Restaurant r,DishDTO d)throws Exception{
		Dish dA = dishRepo.findById(d.getId())
				.orElseThrow(() -> new RuntimeException("dish already not found"));
		dA.setDeleted(true);
		dishRepo.save(dA);
		
	}
	
	public RestaurantDetailsAndAddressDTO findRestaurant (Long dId)throws Exception {
		Dish dA = dishRepo.findById(dId)
				.orElseThrow(() -> new RuntimeException("dish already not found"));
		
		Restaurant r= dA.getRestaurant();
		
		List<Address> rAddress= addressService.getAddresses(r.getId());
		
		return new RestaurantDetailsAndAddressDTO(r, rAddress.getFirst());
		
	}
}

