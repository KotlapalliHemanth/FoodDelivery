package com.excelr.FoodDelivery.Controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.excelr.FoodDelivery.Models.DTO.RestaurantDetailsAndAddressDTO;
import com.excelr.FoodDelivery.Models.DTO.RiderPositionDTO;
import com.excelr.FoodDelivery.Models.Enum.PaymentStatus;
import com.excelr.FoodDelivery.Repositories.CustomerRepository;
import com.excelr.FoodDelivery.Repositories.RestaurantRepository;
import com.excelr.FoodDelivery.Security.Jwt.JwtUtill;
import com.excelr.FoodDelivery.Services.AddressService;
import com.excelr.FoodDelivery.Services.CustomerService;
import com.excelr.FoodDelivery.Services.DishService;
import com.excelr.FoodDelivery.Services.OrderService;
import com.excelr.FoodDelivery.Services.RestaurantService;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;

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

	@Autowired
	private DishService dishService;

	@Value("${razorpay.key.id}")
	private String razorpayKeyId;

	@Value("${razorpay.key.secret}")
	private String razorpayKeySecret;
    
//  api for  details
    @PostMapping(value = "/details", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<CustomerResponse> getAndUpdateUserProfile(Authentication authentication,
            @RequestPart(required = false) CustomerDetailsDTO update,
            @RequestPart(required = false) MultipartFile profilePic) throws Exception {

    	String email = authentication.getName();
        Customer customer = customerRepo.findEnabled(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Object user = null;
		CustomerDetailsDTO d = null;
		if (update == null && profilePic == null) { // details not provided(used got getting details)
        	 d = new CustomerDetailsDTO(customer);
			user = customerRepo.findEnabled(d.getEmail()).orElseThrow(() -> new RuntimeException("customer not found"));
        	
		} else { // details provided for updating details
        	 d = customerService.updateCustomerDetails(customer, update, profilePic);
        	 System.out.println(d.getEmail());
        }
		user = customerRepo.findEnabled(d.getEmail()).orElseThrow(() -> new RuntimeException("customer not found"));
            System.out.println(d);
          
        System.out.println(d);
        
		String jwt = jwtUtil.generateAccessTokken(user, "CUSTOMER");
        return ResponseEntity.ok(new CustomerResponse(jwt, d));
    }
    
    // curd operations on orders
    
    // get all users
    @GetMapping("/orders")
	public ResponseEntity<List<OrderWithRestaurantDTO>> getUserOrders(Authentication authentication) {
        String email = authentication.getName();
        Customer customer = customerRepo.findEnabled(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        List<Order> orders = customer.getOrders();
		List<OrderWithRestaurantDTO> result = orders.stream().map(order -> {
			RestaurantDetailsAndAddressDTO restaurantDetails = null;
			List<DishWithQuantityDTO> dishWithQuantities = order.getOrderDishes().stream()
				.map(od -> new DishWithQuantityDTO(od.getDish(), od.getQuantity()))
				.collect(Collectors.toList());
			RiderDTO riderDTO = null;
			if (order.getDeliveryPartner() != null) {
				riderDTO = new RiderDTO(order.getDeliveryPartner());
			}
			try {
				if (order.getOrderDishes() != null && !order.getOrderDishes().isEmpty()) {
					Long firstDishId = order.getOrderDishes().get(0).getDish().getId();
					restaurantDetails = dishService.findRestaurant(firstDishId);
					return new OrderWithRestaurantDTO(order, restaurantDetails, dishWithQuantities, riderDTO);
				} else {
					return new OrderWithRestaurantDTO(order, "No dishes found in order", dishWithQuantities, riderDTO);
				}
			} catch (Exception e) {
				// Log the error and return a DTO with the error message
				e.printStackTrace();
				return new OrderWithRestaurantDTO(order, "Failed to fetch restaurant details: " + e.getMessage(), dishWithQuantities, riderDTO);
    }
		}).collect(Collectors.toList());
		return ResponseEntity.ok(result);
	}
    
	// create order for the payments
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
	public ResponseEntity<Order> modifyOrder(Authentication authentication, @RequestBody ModifyOrderDTO req) {
    	 String email = authentication.getName();
         Customer customer = customerRepo.findEnabled(email)
                 .orElseThrow(() -> new RuntimeException("Customer not found"));
         
         Order newOrder = orderService.modifyOrder(customer, req);
         return ResponseEntity.ok(newOrder);
    }
    
	// transactions api (creation, modifications)---------------------------
//    
	@PostMapping("/razorpay/order")
	public ResponseEntity<?> createRazorpayOrder(@RequestBody Map<String, Object> payload) {
		try {
			Long orderId = Long.valueOf(payload.get("orderId").toString());

			// Fetch order from DB
			Order order = orderService.getOrderById(orderId);
			if (order == null) {
				return ResponseEntity.badRequest().body("Order not found");
			}
			if (order.getTransaction().getStatus() != PaymentStatus.PENDING) {
				return ResponseEntity.badRequest().body("Order is not eligible for payment");
			}

			// Use backend-calculated amount
			Double amount = order.getAmount();

			// Use environment variables for keys
			RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);

			JSONObject options = new JSONObject();
			options.put("amount", (int) Math.round(amount * 100));
			options.put("currency", "INR");
			options.put("receipt", "order_rcptid_" + orderId);
			options.put("payment_capture", 1);

			com.razorpay.Order razorpayOrder = razorpay.orders.create(options);

			Map<String, Object> response = new HashMap<>();
			response.put("razorpayOrderId", razorpayOrder.get("id"));
			response.put("amount", razorpayOrder.get("amount"));
			response.put("currency", razorpayOrder.get("currency"));
			response.put("orderId", orderId);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Razorpay order creation failed: " + e.getMessage());
		}
	}

	@PostMapping("/razorpay/verify")
	public ResponseEntity<?> verifyRazorpayPayment(@RequestBody Map<String, String> payload) {
		try {
			String razorpayOrderId = payload.get("razorpay_order_id");
			String razorpayPaymentId = payload.get("razorpay_payment_id");
			String razorpaySignature = payload.get("razorpay_signature");
			String orderId = payload.get("orderId"); // your internal order ID

			String generatedSignature = Utils.getHash(razorpayOrderId + "|" + razorpayPaymentId, razorpayKeySecret);

			if (generatedSignature.equals(razorpaySignature)) {
				// Payment is verified, update order status in DB
				Order order = orderService.getOrderById(Long.valueOf(orderId));
				RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
				com.razorpay.Payment payment = razorpay.payments.fetch(razorpayPaymentId);
				String method = payment.get("method"); // "card", "upi", etc.

				orderService.recordPaymentSuccess(Long.valueOf(orderId), razorpayPaymentId, order.getAmount(), method);
				return ResponseEntity.ok("Payment verified");
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid payment signature");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Payment verification failed");
		}
	}
    
    // curd operations on address------------------------
    
    @PostMapping("/address")
	public ResponseEntity<?> addAddress(Authentication authentication, @RequestBody AddressDTO a) {
    	String email = authentication.getName();
        Customer customer = customerRepo.findEnabled(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        return ResponseEntity.ok(addressService.createCustomerAddress(customer, a));
    }
    
    @PutMapping("/address")
	public ResponseEntity<?> modifyAddress(Authentication authentication, @RequestBody AddressDTO a) {
    	
		return ResponseEntity.ok(addressService.modifyAddress(a));
    }
    
    @PutMapping("/address/default")
	public ResponseEntity<?> setDefaultAddress(Authentication authentication, @RequestParam Long addressId) {
        String email = authentication.getName();
        Customer customer = customerRepo.findEnabled(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        addressService.setDefaultAddress(customer.getId(), addressId);
        return ResponseEntity.ok("Default address updated");
    }
    
    @DeleteMapping("/address")
	public ResponseEntity<?> deleteAddress(Authentication authentication, @RequestBody AddressDTO a) {
        addressService.deleteAddress(a);
        String email = authentication.getName();
        Customer customer = customerRepo.findEnabled(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return ResponseEntity.ok(
				customer.getAddresses() != null ? customer.getAddresses().stream().filter(Address::getIsActive).toList()
						: List.of());
    }

    @GetMapping("/address")
	public ResponseEntity<?> getAddresses(Authentication authentication) {
        String email = authentication.getName();
        Customer customer = customerRepo.findEnabled(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return ResponseEntity.ok(
				customer.getAddresses() != null ? customer.getAddresses().stream().filter(Address::getIsActive).toList()
						: List.of());
    }
    
    // getting rider location details----------------
    
    @GetMapping("/rider")
	public ResponseEntity<?> getRiderPositionDetails(Authentication authentication, @RequestBody Long oId) {
    	Order order = orderService.getOrderById(oId);
		Double lat = order.getDeliveryPartner().getLatitude();
		Double lon = order.getDeliveryPartner().getLongitude();
    	
    	return ResponseEntity.ok(new RiderPositionDTO(lat, lon));
    }
    	 
	// password change------------------------
    @PutMapping("/password")
	public ResponseEntity<?> changePassword(Authentication authentication,
			@RequestBody PasswordReqBody passwordReqBody) {
    	String email = authentication.getName();
        Customer customer = customerRepo.findByUsernameOrEmailOrPhone(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
		if (passwordEncoder.matches(passwordReqBody.oldPassword, customer.getPassword())) {
        	customer.setPassword(passwordEncoder.encode(passwordReqBody.newPassword));
        	customerRepo.save(customer);
		} else {
        	return ResponseEntity.status(HttpStatus.CONFLICT).body("wrong password. Please try again");
        }
        
        String jwt = jwtUtil.generateAccessTokken(customer, "CUSTOMER");
        return ResponseEntity.ok(new JwtResponse(jwt));
    	
    }
    
	// others-----------------
    
	// get restaurant details--------------------
    @GetMapping("/restaurantAtLocation")
	public ResponseEntity<?> getRestaurantDetailsByLocation(@RequestParam Double lat, @RequestParam Double lon,
			@RequestParam Double radius, @RequestParam(required = false, defaultValue = "") String searchName) {

		return ResponseEntity.ok(restaurantService.findAndFilterRestaurantsByLocation(lat, lon, radius, searchName));
    }

    // get restaurant details----------------
    @GetMapping("/restaurantDetails")
	public ResponseEntity<?> getRestaurantDishDetails(@RequestParam Long rId) {
    		
		Restaurant r = restaurantRepo.findById(rId).orElseThrow(() -> new RuntimeException("Customer not found"));
		Map<String, List<Dish>> dishes = r.getDishes().stream().filter(Dish::getAvailable)
				.collect(Collectors.groupingBy(Dish::getCusine));
    	return ResponseEntity.ok(dishes);
    	
    }
    
	// request bodies
	class PasswordReqBody {
		public String oldPassword, newPassword;
	}

	public static class OrderWithRestaurantDTO {
		public Order order;
		public RestaurantDetailsAndAddressDTO restaurantDetails;
		public String errorMessage;
		public List<DishWithQuantityDTO> dishes;
		public RiderDTO rider; // <-- Add this

		public OrderWithRestaurantDTO(Order order, RestaurantDetailsAndAddressDTO restaurantDetails, List<DishWithQuantityDTO> dishes, RiderDTO rider) {
			this.order = order;
			this.restaurantDetails = restaurantDetails;
			this.errorMessage = null;
			this.dishes = dishes;
			this.rider = rider;
		}
		public OrderWithRestaurantDTO(Order order, String errorMessage, List<DishWithQuantityDTO> dishes, RiderDTO rider) {
			this.order = order;
			this.restaurantDetails = null;
			this.errorMessage = errorMessage;
			this.dishes = dishes;
			this.rider = rider;
		}
	}

	public static class DishWithQuantityDTO {
		public Long dishId;
		public String name;
		public Double price;
		public String category;
		public String cuisine;
		public String description;
		public Boolean available;
		public String image;
		public int quantity;

		public DishWithQuantityDTO(Dish dish, int quantity) {
			this.dishId = dish.getId();
			this.name = dish.getName();
			this.price = dish.getPrice();
			this.category = dish.getCategory();
			this.cuisine = dish.getCusine();
			this.description = dish.getDescription();
			this.available = dish.getAvailable();
			this.image = dish.getImage();
			this.quantity = quantity;
		}
	}
    
    // Add RiderDTO for rider details in order response
    public static class RiderDTO {
        public Long id;
        public String username;
        public String email;
        public String phone;
        public String profilePic;
        public Double latitude;
        public Double longitude;

        public RiderDTO(com.excelr.FoodDelivery.Models.DeliveryPartner rider) {
            this.id = rider.getId();
            this.username = rider.getUsername();
            this.email = rider.getEmail();
            this.phone = rider.getPhone();
            this.profilePic = rider.getProfilePic();
            this.latitude = rider.getLatitude();
            this.longitude = rider.getLongitude();
		}
	}
    
    class CustomerResponse { 
		public String token;
		public CustomerDetailsDTO r;

	public CustomerResponse(String t, CustomerDetailsDTO res) { 
		token = t; 
			r = res;
		} 
	}
}
