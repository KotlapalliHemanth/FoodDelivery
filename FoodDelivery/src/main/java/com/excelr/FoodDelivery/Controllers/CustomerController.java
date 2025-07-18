package com.excelr.FoodDelivery.Controllers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.excelr.FoodDelivery.Models.Address;
import com.excelr.FoodDelivery.Models.Customer;
import com.excelr.FoodDelivery.Models.Dish;
import com.excelr.FoodDelivery.Models.Order;
import com.excelr.FoodDelivery.Models.Restaurant;
import com.excelr.FoodDelivery.Models.DTO.AddressDTO;
import com.excelr.FoodDelivery.Models.DTO.CreateOrderDTO;
import com.excelr.FoodDelivery.Models.DTO.CustomerDetailsDTO;
import com.excelr.FoodDelivery.Models.DTO.ModifyOrderDTO;
import com.excelr.FoodDelivery.Models.DTO.RestaurantDetailsDTO;
import com.excelr.FoodDelivery.Models.DTO.RiderPositionDTO;
import com.excelr.FoodDelivery.Repositories.AddressRepository;
import com.excelr.FoodDelivery.Repositories.CustomerRepository;
import com.excelr.FoodDelivery.Repositories.RestaurantRepository;
import com.excelr.FoodDelivery.Security.Jwt.JwtUtill;
import com.excelr.FoodDelivery.Services.AddressService;
import com.excelr.FoodDelivery.Services.CustomerService;
import com.excelr.FoodDelivery.Services.OrderService;
import com.excelr.FoodDelivery.Services.RestaurantService;

@RestController
@RequestMapping("/customer")
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepo;
    
    @Autowired
    private RestaurantService restaurantService;
    
    @Autowired
    private RestaurantRepository restaurantRepo;
    
    @Autowired
    private AddressService addressService;

    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired 
    PasswordEncoder passwordEncoder;
    
    @Autowired 
    JwtUtill jwtUtil;

    
//  api for  details
    @PostMapping(value = "/details", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomerResponse> getAndUpdateUserProfile(
            Authentication authentication,
            @RequestPart(required = false) CustomerDetailsDTO update,
            @RequestPart(required = false) MultipartFile profilePic) throws Exception {

    	String email = authentication.getName();
        Customer customer = customerRepo.findEnabled(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        
		CustomerDetailsDTO d = null;
        if(update==null && profilePic==null) { //details not provided(used got getting details)
        	 d = new CustomerDetailsDTO(customer);
        	System.out.println(d);
        }else { //details provided for updating details
        	 d = customerService.updateCustomerDetails(customer, update, profilePic);
            System.out.println(d);
            
        }
        
        Object user = customerRepo.findEnabled(d.getEmail())
                .orElseThrow(() -> new RuntimeException("customer not found"));
        
		String jwt = jwtUtil.generateAccessTokken(user, "CUSTOMER");
        return ResponseEntity.ok(new CustomerResponse(jwt, d));
    }

    
    // curd operations on orders
    
    // get all users
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getUserOrders(Authentication authentication) {
        String email = authentication.getName();
        Customer customer = customerRepo.findEnabled(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        List<Order> orders = customer.getOrders();
        return ResponseEntity.ok(orders);
    }

    
    //create order for the payments
    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(Authentication authentication, @RequestBody CreateOrderDTO req) {
        String email = authentication.getName();
        Customer customer = customerRepo.findEnabled(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Assuming you have a method to create an order
        Order newOrder = orderService.createOrder(customer, req);
        if (newOrder == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(newOrder);
    }
    
    // update the order
    @PutMapping("/orders")
    public ResponseEntity<Order> modifyOrder(Authentication authentication, @RequestBody ModifyOrderDTO req){
    	 String email = authentication.getName();
         Customer customer = customerRepo.findEnabled(email)
                 .orElseThrow(() -> new RuntimeException("Customer not found"));
         
         Order newOrder = orderService.modifyOrder(customer, req);
         return ResponseEntity.ok(newOrder);
    }
    
    //transactions api (creation, modifications)---------------------------
    
    
    // curd operations on address------------------------
    
    @PostMapping("/address")
    public ResponseEntity<?> addAddress(Authentication authentication, @RequestBody AddressDTO a){
    	String email = authentication.getName();
        Customer customer = customerRepo.findEnabled(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        return ResponseEntity.ok(addressService.createCustomerAddress(customer, a));
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
    public ResponseEntity<List<Address>> getAddress(Authentication authentication, @RequestBody AddressDTO a){
    	
    	return ResponseEntity.ok(addressService.getAddresses(a.getId()));
    }
    
    // getting rider location details----------------
    
    @GetMapping("/rider")
    public ResponseEntity<?> getRiderPositionDetails(Authentication authentication, @RequestBody Long oId){
    	Order order = orderService.getOrderById(oId);
    	Double lat=order.getDeliveryPartner().getLatitude();
    	Double lon=order.getDeliveryPartner().getLongitude();
    	
    	return ResponseEntity.ok(new RiderPositionDTO(lat, lon));
    }
    	 
    
    
    //password change------------------------
    @PutMapping("/password")
    public ResponseEntity<?> changePassword(Authentication authentication, @RequestBody PasswordReqBody passwordReqBody){
    	String email = authentication.getName();
        Customer customer = customerRepo.findEnabled(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        if(passwordEncoder.matches(passwordReqBody.oldPassword, customer.getPassword())) {
        	customer.setPassword(passwordEncoder.encode(passwordReqBody.newPassword));
        	customerRepo.save(customer);
        }else {
        	return ResponseEntity.status(HttpStatus.CONFLICT).body("wrong password. Please try again");
        }
        
        String jwt = jwtUtil.generateAccessTokken(customer, "CUSTOMER");
        return ResponseEntity.ok(new JwtResponse(jwt));
    	
    }
    
    //others-----------------
    
    	//get restaurant details--------------------
    @GetMapping("/restaurantAtLocation")
    public ResponseEntity<?> getRestaurantDetailsByLocation(@RequestParam Double lat, @RequestParam Double lon,@RequestParam Double radius,@RequestParam String searchName){
    	
    	return ResponseEntity.ok(restaurantService.findAndFilterRestaurantsByLocation(lat, lon, radius, searchName));
    }
    
    // get reataurant details----------------
    @GetMapping("/restaurantDetails")
    public ResponseEntity<?> getRestaurantDishDetails(@RequestParam Long rId){
    		
    	Restaurant r= restaurantRepo.findById(rId).orElseThrow(() -> new RuntimeException("Customer not found"));
    	Map<String, List<Dish>> dishes= r.getDishes().stream().filter(Dish::getAvailable).collect(Collectors.groupingBy(Dish::getCusine));
    	return ResponseEntity.ok(dishes);
    	
    }
    
    
    
    //request bodies 
    class PasswordReqBody { public String oldPassword, newPassword; }
    
    
    
    class CustomerResponse { 
		public String token;
		public CustomerDetailsDTO r;
	public CustomerResponse(String t, CustomerDetailsDTO res) { 
		token = t; 
		r=res;
		} 
	}
}
