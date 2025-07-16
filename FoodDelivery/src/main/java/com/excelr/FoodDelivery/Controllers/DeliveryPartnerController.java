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

import com.excelr.FoodDelivery.Controllers.RestaurantController.RestaurantResponse;
import com.excelr.FoodDelivery.Models.DeliveryPartner;
import com.excelr.FoodDelivery.Models.Restaurant;
import com.excelr.FoodDelivery.Models.DTO.DeliveryPartnerDetailsDTO;
import com.excelr.FoodDelivery.Models.DTO.RestaurantDetailsDTO;
import com.excelr.FoodDelivery.Repositories.DeliveryPartnerRepository;
import com.excelr.FoodDelivery.Security.Jwt.JwtUtill;
import com.excelr.FoodDelivery.Services.DeliveryPartnerService;

@RestController
@RequestMapping("/rider")
@PreAuthorize("hasRole('RIDER')")
public class DeliveryPartnerController {
	
	@Autowired
	DeliveryPartnerRepository riderRepo;
	
	@Autowired
	DeliveryPartnerService riderService;
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
                .orElseThrow(() -> new RuntimeException("restaurant not found"));
        
		String jwt = jwtUtil.generateAccessTokken(user, "RIDER");
//        return ResponseEntity.ok(new JwtResponse(jwt));
        return ResponseEntity.ok(new RiderResponse(jwt, d));
    }
	
	// get available orders-----------------
	
	// order selection and get details --------------------
	
	// order delivery status change-----------------
	
	//order completion list--------------------
	
	// ratings
	
	// amount withdrawal----------------***
	
	
	class RiderResponse { 
		public String token;
		public DeliveryPartnerDetailsDTO r;
	public RiderResponse(String t, DeliveryPartnerDetailsDTO res) { 
		token = t; 
		r=res;
		} 
	}
	
}
