package com.excelr.FoodDelivery.Controllers;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.excelr.FoodDelivery.Models.Customer;
import com.excelr.FoodDelivery.Models.Dish;
import com.excelr.FoodDelivery.Models.Restaurant;
import com.excelr.FoodDelivery.Models.DTO.CustomerDetailsDTO;
import com.excelr.FoodDelivery.Models.DTO.DishDTO;
import com.excelr.FoodDelivery.Models.DTO.RestaurantDetailsDTO;
import com.excelr.FoodDelivery.Repositories.RestaurantRepository;
import com.excelr.FoodDelivery.Services.DishService;
import com.excelr.FoodDelivery.Services.RestaurantService;
import com.excelr.FoodDelivery.Security.Jwt.JwtUtill;


@RestController
@RequestMapping("/restaurant")
@PreAuthorize("hasRole('RESTAURANT')")
public class RestaurantController {

	@Autowired
	RestaurantRepository restaurantRepo;
	
	@Autowired
	RestaurantService restaurantService;
	
	@Autowired JwtUtill jwtUtil;
	
	@Autowired
	private DishService dishService;
	
	// restaurent details (curd operations)-------------------
	
	@PostMapping(value = "/details", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RestaurantResponse> getAndUpdateUserProfile(
            Authentication authentication,
            @RequestPart(required = false) RestaurantDetailsDTO update,
            @RequestPart(required = false) MultipartFile profilePic) throws Exception {

    	String email = authentication.getName();
        Restaurant restaurant = restaurantRepo.findEnabled(email)
                .orElseThrow(() -> new RuntimeException("restaurant not found"));

        RestaurantDetailsDTO d = null;
        if(update==null && profilePic==null) { //details not provided(used got getting details)
        	 d = new RestaurantDetailsDTO(restaurant);
        	System.out.println(d);
        }else{ //details provided for updating details
        	 d = restaurantService.updateRestaurantDetails(restaurant, update, profilePic);
            System.out.println(restaurant);
            
        }
        
        Object user = restaurantRepo.findEnabled(d.getEmail())
                .orElseThrow(() -> new RuntimeException("restaurant not found"));
        
		String jwt = jwtUtil.generateAccessTokken(user, "RESTAURANT");
//        return ResponseEntity.ok(new JwtResponse(jwt));
        return ResponseEntity.ok(new RestaurantResponse(jwt, d));
    }
	
	// add & edit items-----------------------
	@PostMapping(value="/dishadd", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<DishDTO> addDish(Authentication authentication,
            @RequestPart(required = false) DishDTO update,
            @RequestPart(required = false) MultipartFile profilePic) throws Exception{
		String email = authentication.getName();
        Restaurant restaurant = restaurantRepo.findEnabled(email)
                .orElseThrow(() -> new RuntimeException("restaurant not found"));
        DishDTO d= null;
        if(update != null || profilePic!= null) {
        	 d= dishService.createDish(restaurant, update, profilePic);
        }
        
        return ResponseEntity.ok(d);
	}
	
	// update dish
	
	@PutMapping(value="/dishmodify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<DishDTO> modifyDish(Authentication authentication,
            @RequestPart DishDTO update,
            @RequestPart(required = false) MultipartFile profilePic) throws Exception{
		String email = authentication.getName();
        Restaurant restaurant = restaurantRepo.findEnabled(email)
                .orElseThrow(() -> new RuntimeException("restaurant not found"));
        
		DishDTO d= dishService.modifyDish(restaurant,update, profilePic);
		return ResponseEntity.ok(d);
	}
	
	
	// delete items -------------------------
	
	@DeleteMapping("/deletedish")
	public ResponseEntity<String> deleteDish(Authentication authentication,
            @RequestPart DishDTO update) throws Exception{
		String email = authentication.getName();
        Restaurant restaurant = restaurantRepo.findEnabled(email)
                .orElseThrow(() -> new RuntimeException("restaurant not found"));
		dishService.deleteDish(restaurant, update);
		
		return ResponseEntity.ok("deleted sucessfully");
		
		
		
	}
	
	// availability of items------------------
	@GetMapping("/getDishes")
	public ResponseEntity<?> getDishes (Authentication authentication){
		String email = authentication.getName();
        Restaurant restaurant = restaurantRepo.findEnabled(email)
                .orElseThrow(() -> new RuntimeException("restaurant not found"));
       List<Dish> dishes= restaurant.getDishes().stream().filter(dish-> !dish.getDeleted()).collect(Collectors.toList());
        
        return ResponseEntity.ok(dishes);
	}
	
	// menu list-----------------
	
	// current orders-------------------
	
	// edit accepted orders(declineing with reason)---------------------
	
	// completed order list with filters---------------------
	
	// todays order stats---------------------
	
	
	
	
	// ----------------
	class RestaurantResponse { 
		public String token;
		public RestaurantDetailsDTO r;
	public RestaurantResponse(String t, RestaurantDetailsDTO res) { 
		token = t; 
		r=res;
		} 
	}
}
