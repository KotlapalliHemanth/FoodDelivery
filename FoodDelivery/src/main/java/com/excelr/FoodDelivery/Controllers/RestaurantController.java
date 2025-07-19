package com.excelr.FoodDelivery.Controllers;

import java.util.List;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.excelr.FoodDelivery.Models.Dish;
import com.excelr.FoodDelivery.Models.Restaurant;
import com.excelr.FoodDelivery.Models.DTO.AddressDTO;
import com.excelr.FoodDelivery.Models.DTO.DishDTO;
import com.excelr.FoodDelivery.Models.DTO.RestaurantDetailsDTO;
import com.excelr.FoodDelivery.Repositories.RestaurantRepository;
import com.excelr.FoodDelivery.Security.Jwt.JwtUtill;
import com.excelr.FoodDelivery.Services.AddressService;
import com.excelr.FoodDelivery.Services.DishService;
import com.excelr.FoodDelivery.Services.OrderService;
import com.excelr.FoodDelivery.Services.RestaurantService;
import com.fasterxml.jackson.databind.ObjectMapper;


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
	
	@Autowired
    private OrderService orderService;
	
	@Autowired
    private AddressService addressService;
	
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
	
	// menu list-----------------
	@GetMapping("/getDishes")
	public ResponseEntity<?> getDishes (Authentication authentication){
		String email = authentication.getName();
        Restaurant restaurant = restaurantRepo.findEnabled(email)
                .orElseThrow(() -> new RuntimeException("restaurant not found"));
       List<Dish> dishes= restaurant.getDishes().stream().filter(dish-> !dish.getDeleted()).collect(Collectors.toList());
        
        return ResponseEntity.ok(dishes);
	}
	
	
	
	// current orders-------------------
	@GetMapping("/receiveOrders")
	public ResponseEntity<?> receiveOrders(Authentication authentication){
		String email = authentication.getName();
        Restaurant restaurant = restaurantRepo.findEnabled(email)
                .orElseThrow(() -> new RuntimeException("restaurant not found"));
        return ResponseEntity.ok(orderService.getCurrentOrders(restaurant));
	}
	
	// edit accepted orders(declineing with reason)---------------------
	@PutMapping("/acceptOrRejectOrder")
	public ResponseEntity<?> acceptOrRejectOrder (Authentication authentication,@RequestBody AcceptOrRejectOrder a){
		String email = authentication.getName();
        Restaurant restaurant = restaurantRepo.findEnabled(email)
                .orElseThrow(() -> new RuntimeException("restaurant not found"));
        
        return ResponseEntity.ok(orderService.acceptOrRejectOrder(restaurant.getId(), a.oId, a.accept));
	}
	
	
	// accepted orders list---------------------
	@GetMapping("/acceptedOrders")
	public ResponseEntity<?> acceptedOrders(Authentication authentication){
		String email = authentication.getName();
        Restaurant restaurant = restaurantRepo.findEnabled(email)
                .orElseThrow(() -> new RuntimeException("restaurant not found"));
        return ResponseEntity.ok(orderService.acceptedOrdersByRestaurant(restaurant.getId()));
	} 
	
	
	// completed order list with filters---------------------
	
	// todays order stats---------------------
	
	@GetMapping("/deliveredOrders")
	public ResponseEntity<?> getDeliveredOrdersByRestaurant(Authentication authentication){
		String email = authentication.getName();
        Restaurant restaurant = restaurantRepo.findEnabled(email)
                .orElseThrow(() -> new RuntimeException("restaurant not found"));
        return ResponseEntity.ok(orderService.getDeliveredOrdersByRestaurant(restaurant.getId()));
	}
	
	// address curd operations------------------
	@PostMapping("/address")
    public ResponseEntity<?> addAddress(Authentication authentication, @RequestBody AddressDTO a){
    	String email = authentication.getName();
        Restaurant restaurant = restaurantRepo.findEnabled(email)
                .orElseThrow(() -> new RuntimeException("restaurant not found"));
        
        return ResponseEntity.ok(addressService.createRestaurentAddress(restaurant, a));
    }
	
	
	@PutMapping("/address")
    public ResponseEntity<?> modifyAddress(Authentication authentication, @RequestBody AddressDTO a){
    	
    	return ResponseEntity.ok(addressService.modifyAddress( a));
    }
	
	@DeleteMapping("/address")
    public ResponseEntity<?> deleteAddress(Authentication authentication, @RequestBody AddressDTO a){
    	addressService.deleteAddress(a);
    	return ResponseEntity.ok("deleted");	
	}
	
	@GetMapping("/address")
    public ResponseEntity<?> getAddress(Authentication authentication, @RequestBody AddressDTO a){
    	
    	return ResponseEntity.ok(addressService.getAddresses(a.getId()));
    }
	
	
	// ----------------
	class RestaurantResponse { 
		public String token;
		public RestaurantDetailsDTO r;
	public RestaurantResponse(String t, RestaurantDetailsDTO res) { 
		token = t; 
		r=res;
		} 
	}
	
	class AcceptOrRejectOrder {
		public Long oId;
		public Boolean accept;
		
	}
}
