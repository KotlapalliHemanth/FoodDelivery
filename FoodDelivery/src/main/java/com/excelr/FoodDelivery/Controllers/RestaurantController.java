package com.excelr.FoodDelivery.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.excelr.FoodDelivery.Models.Customer;
import com.excelr.FoodDelivery.Models.Restaurant;
import com.excelr.FoodDelivery.Models.DTO.CustomerDetailsDTO;
import com.excelr.FoodDelivery.Models.DTO.RestaurantDetailsDTO;
import com.excelr.FoodDelivery.Repositories.RestaurantRepository;
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
        }
        if(update!=null || profilePic!=null) { //details provided for updating details
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
	
	// delete items -------------------------
	
	// availability of items------------------
	
	// menu list-----------------
	
	// current orders--------------------
	
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
