package com.excelr.FoodDelivery.Controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.excelr.FoodDelivery.Models.DeliveryPartner;
import com.excelr.FoodDelivery.Models.Order;
import com.excelr.FoodDelivery.Models.DTO.DeliveryPartnerDetailsDTO;
import com.excelr.FoodDelivery.Models.DTO.RiderOrderDTO;
import com.excelr.FoodDelivery.Models.DTO.RiderPositionDTO;
import com.excelr.FoodDelivery.Models.Enum.OrderStatus;
import com.excelr.FoodDelivery.Repositories.DeliveryPartnerRepository;
import com.excelr.FoodDelivery.Repositories.OrderRepository;
import com.excelr.FoodDelivery.Security.Jwt.JwtUtill;
import com.excelr.FoodDelivery.Services.DeliveryPartnerService;
import com.excelr.FoodDelivery.Services.OrderService;

@RestController
@RequestMapping("/rider")
@PreAuthorize("hasRole('RIDER')")
public class DeliveryPartnerController {
	
	@Autowired
	DeliveryPartnerRepository riderRepo;
	
	@Autowired
	DeliveryPartnerService riderService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	OrderRepository orderRepo;
	
	@Autowired JwtUtill jwtUtil;



	//partner details(curd operations)---------------------
	@PostMapping(value = "/details", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RiderResponse> getAndUpdateUserProfile(
            Authentication authentication,
            @RequestPart(required = false) DeliveryPartnerDetailsDTO update,
            @RequestPart(required = false) MultipartFile profilePic) throws Exception {

    	String email = authentication.getName();
        DeliveryPartner rider = riderRepo.findEnabled(email)
                .orElseThrow(() -> new RuntimeException("restaurant not found"));

        DeliveryPartnerDetailsDTO d = null;
        if(update==null && profilePic==null) { //details not provided(used got getting details)
        	 d = new DeliveryPartnerDetailsDTO(rider);
        	System.out.println(d);
        }else{ //details provided for updating details
        	 d = riderService.updateRiderDetails(rider, update, profilePic);
            System.out.println(rider);
            
        }
        Object user = riderRepo.findEnabled(d.getEmail())
                .orElseThrow(() -> new RuntimeException("rider not found"));
        
		String jwt = jwtUtil.generateAccessTokken(user, "RIDER");
//        return ResponseEntity.ok(new JwtResponse(jwt));
        return ResponseEntity.ok(new RiderResponse(jwt, d));
    }
	
	// get available orders (near by 5km radius)-----------------
	@PostMapping("/getAvailableOrders")
public ResponseEntity<List<RiderOrderDTO>> getPreparingOrders(Authentication authentication, @RequestBody RiderPositionDTO position ){
    List<Order> orders = orderService.getPreparingOrders(position.getLat(), position.getLon());
    return ResponseEntity.ok(orderService.toRiderOrderDTOs(orders));
}
	
	//update rider location(lat, lon)-----------------
	@PutMapping("/updateRiderPosition")
	public ResponseEntity<DeliveryPartner> updateRiderposition(Authentication authentication, @RequestBody RiderPositionDTO position){
		String email = authentication.getName();
        DeliveryPartner rider = riderRepo.findEnabled(email)
                .orElseThrow(() -> new RuntimeException("restaurant not found"));
        
        rider.setLatitude(position.getLat());
        rider.setLongitude(position.getLon());
        
        return ResponseEntity.ok(riderRepo.save(rider));
	}
	
	// order selection and get details(set rider to order) --------------------
	@PutMapping("/acceptOrder")
	public ResponseEntity<?> acceptOrderForDelivery(Authentication authentication, @RequestBody Long oId ){
		String email = authentication.getName();
        DeliveryPartner rider = riderRepo.findEnabled(email)
                .orElseThrow(() -> new RuntimeException("restaurant not found"));
		
		Order order = orderRepo.findById(oId)
				.orElseThrow(() -> new RuntimeException("Order not found with id: " + oId));
		
		if(order.getRiderAssigned()== false) {
			order.setDeliveryPartner(rider);
			order.setRiderAssigned(true);	
			orderRepo.save(order);
			return ResponseEntity.ok(Map.of("success", true, "message", "Order assigned successfully"));
		}
		
	     return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("success", false, "message", "Order already assigned! Try another."));
	}
	
	
	// my assigned order--------------------
	@GetMapping()
	public ResponseEntity<List<Order>> assignedOrder (Authentication authentication){
		String email = authentication.getName();
        DeliveryPartner rider = riderRepo.findEnabled(email)
                .orElseThrow(() -> new RuntimeException("restaurant not found"));
        
        return ResponseEntity.ok(orderRepo.findPreparingOrdersByDeliveryPartnerId(rider.getId()));
	}
	
	// order delivery status change(picked the order)-----------------
	@PutMapping("/orderPickup")
	public ResponseEntity<Order> pickOrderFromRestaurant( Authentication authentication, @RequestBody Long oId){
		Order order = orderRepo.findById(oId)
				.orElseThrow(() -> new RuntimeException("Order not found with id: " + oId));
		
		order.setStatus(OrderStatus.ON_THE_WAY);
		orderRepo.save(order);
		return ResponseEntity.ok(orderRepo.save(order));
	}
	
	//order completion list(delivered the order)--------------------
	@PutMapping("/orderDelivered")
	public ResponseEntity<Order> deliverOrderToCustomer( Authentication authentication, @RequestBody Long oId){
		Order order = orderRepo.findById(oId)
				.orElseThrow(() -> new RuntimeException("Order not found with id: " + oId));
		
		order.setStatus(OrderStatus.DELIVERED);
		orderRepo.save(order);
		// if possible add the mailing to user about order with details----------------
		return ResponseEntity.ok(orderRepo.save(order));
	}
	
	// getting all orders delivered by rider for that day------------------------
	@GetMapping("/deliveredOrdersByRider")
	public ResponseEntity<?> getOrdersDelivered(Authentication authentication, 
			@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){
		String email = authentication.getName();
        DeliveryPartner rider = riderRepo.findEnabled(email)
                .orElseThrow(() -> new RuntimeException("restaurant not found"));
        
        LocalDateTime startOfDay = startDate.atStartOfDay();
        LocalDateTime endOfDay = endDate.atStartOfDay();
        
        List<Order> deliveredOrders= orderRepo.findDeliveredOrdersByRiderForDateRange(rider.getId(),startOfDay, endOfDay );
        
        return ResponseEntity.ok(deliveredOrders);
		
	}
	
	
	// delivery Partner cancelling order due to unknown reasons like accidents etc.,-------------------------------
	
	// ratings
	
	// amount withdrawal----------------***
	
	//------------
	class RiderResponse { 
		public String token;
		public DeliveryPartnerDetailsDTO r;
	public RiderResponse(String t, DeliveryPartnerDetailsDTO res) { 
		token = t; 
		r=res;
		} 
	}
		
}


