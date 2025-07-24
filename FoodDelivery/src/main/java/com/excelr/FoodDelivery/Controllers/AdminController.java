package com.excelr.FoodDelivery.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.excelr.FoodDelivery.Models.Admin;
import com.excelr.FoodDelivery.Models.Customer;
import com.excelr.FoodDelivery.Models.Restaurant;
import com.excelr.FoodDelivery.Models.DeliveryPartner;
import com.excelr.FoodDelivery.Models.Order;
import com.excelr.FoodDelivery.Models.Transaction;
import com.excelr.FoodDelivery.Models.DTO.AdminDetailsDTO;
import com.excelr.FoodDelivery.Models.DTO.CustomerDetailsDTO;
import com.excelr.FoodDelivery.Models.DTO.RestaurantDetailsDTO;
import com.excelr.FoodDelivery.Models.DTO.DeliveryPartnerDetailsDTO;
import com.excelr.FoodDelivery.Models.DTO.OrderDTO;
import com.excelr.FoodDelivery.Models.DTO.RestaurantDetailsAndAddressDTO;
import com.excelr.FoodDelivery.Repositories.AdminRepository;
import com.excelr.FoodDelivery.Repositories.CustomerRepository;
import com.excelr.FoodDelivery.Repositories.RestaurantRepository;
import com.excelr.FoodDelivery.Repositories.DeliveryPartnerRepository;
import com.excelr.FoodDelivery.Repositories.OrderRepository;
import com.excelr.FoodDelivery.Repositories.TransactionRepository;
import com.excelr.FoodDelivery.Security.Jwt.JwtUtill;
import com.excelr.FoodDelivery.Services.AdminService;

import java.util.*;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired private AdminRepository adminRepo;
    @Autowired private CustomerRepository customerRepo;
    @Autowired private RestaurantRepository restaurantRepo;
    @Autowired private DeliveryPartnerRepository riderRepo;
    @Autowired private OrderRepository orderRepo;
    @Autowired private TransactionRepository transactionRepo;
    @Autowired private AdminService adminService;
    @Autowired private JwtUtill jwtUtil;

    // 1. Get platform counts
    @GetMapping("/counts")
    public ResponseEntity<?> getCounts() {
        Map<String, Object> counts = new HashMap<>();
        counts.put("admins", adminRepo.count());
        counts.put("customers", customerRepo.count());
        counts.put("restaurants", restaurantRepo.count());
        counts.put("riders", riderRepo.count());
        counts.put("orders", orderRepo.count());
        counts.put("transactions", transactionRepo.count());
        return ResponseEntity.ok(counts);
    }

    // 2. List all customers
    @GetMapping("/customers")
    public ResponseEntity<?> getAllCustomers() {
        List<Customer> customers = customerRepo.findAll();
        List<CustomerDetailsDTO> dtos = customers.stream().map(CustomerDetailsDTO::new).toList();
        return ResponseEntity.ok(dtos);
    }

    // 3. List all restaurants
    @GetMapping("/restaurants")
    public ResponseEntity<?> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantRepo.findAll();
        List<RestaurantDetailsAndAddressDTO> dtos = restaurants.stream().map(RestaurantDetailsAndAddressDTO::new).toList();
        return ResponseEntity.ok(dtos);
    }

    // 4. List all delivery partners
    @GetMapping("/riders")
    public ResponseEntity<?> getAllRiders() {
        List<DeliveryPartner> riders = riderRepo.findAll();
        List<DeliveryPartnerDetailsDTO> dtos = riders.stream().map(DeliveryPartnerDetailsDTO::new).toList();
        return ResponseEntity.ok(dtos);
    }

    // 5. Get user details by ID (admin, customer, rider, restaurant)
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id, @RequestParam String type) {
        switch (type.toLowerCase()) {
            case "admin" -> {
                Admin admin = adminRepo.findById(id).orElseThrow(() -> new RuntimeException("Admin not found"));
                return ResponseEntity.ok(new AdminDetailsDTO(admin));
            }
            case "customer" -> {
                Customer customer = customerRepo.findById(id).orElseThrow(() -> new RuntimeException("Customer not found"));
                return ResponseEntity.ok(new CustomerDetailsDTO(customer));
            }
            case "restaurant" -> {
                Restaurant restaurant = restaurantRepo.findById(id).orElseThrow(() -> new RuntimeException("Restaurant not found"));
                return ResponseEntity.ok(new RestaurantDetailsDTO(restaurant));
            }
            case "rider" -> {
                DeliveryPartner rider = riderRepo.findById(id).orElseThrow(() -> new RuntimeException("Rider not found"));
                return ResponseEntity.ok(new DeliveryPartnerDetailsDTO(rider));
            }
            default -> throw new RuntimeException("Invalid user type");
        }
    }

    // 6. Update/enable/disable user
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestParam String type, @RequestBody Map<String, Object> updates) {
        switch (type.toLowerCase()) {
            case "admin" -> {
                Admin admin = adminRepo.findById(id).orElseThrow(() -> new RuntimeException("Admin not found"));
                if (updates.containsKey("enabled")) admin.setEnabled((Boolean) updates.get("enabled"));
                // Add more fields as needed
                adminRepo.save(admin);
                return ResponseEntity.ok(new AdminDetailsDTO(admin));
            }
            case "customer" -> {
                Customer customer = customerRepo.findById(id).orElseThrow(() -> new RuntimeException("Customer not found"));
                if (updates.containsKey("enabled")) customer.setEnabled((Boolean) updates.get("enabled"));
                // Add more fields as needed
                customerRepo.save(customer);
                return ResponseEntity.ok(new CustomerDetailsDTO(customer));
            }
            case "restaurant" -> {
                Restaurant restaurant = restaurantRepo.findById(id).orElseThrow(() -> new RuntimeException("Restaurant not found"));
                if (updates.containsKey("enabled")) restaurant.setEnabled((Boolean) updates.get("enabled"));
                // Add more fields as needed
                restaurantRepo.save(restaurant);
                return ResponseEntity.ok(new RestaurantDetailsDTO(restaurant));
            }
            case "rider" -> {
                DeliveryPartner rider = riderRepo.findById(id).orElseThrow(() -> new RuntimeException("Rider not found"));
                if (updates.containsKey("enabled")) rider.setEnabled((Boolean) updates.get("enabled"));
                // Add more fields as needed
                riderRepo.save(rider);
                return ResponseEntity.ok(new DeliveryPartnerDetailsDTO(rider));
            }
            default -> throw new RuntimeException("Invalid user type");
        }
    }

    // 7. Disable user
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> disableUser(@PathVariable Long id, @RequestParam String type) {
        switch (type.toLowerCase()) {
            case "admin" -> {
                Admin admin = adminRepo.findById(id).orElseThrow(() -> new RuntimeException("Admin not found"));
                admin.setEnabled(false);
                adminRepo.save(admin);
                return ResponseEntity.ok("Admin disabled");
            }
            case "customer" -> {
                Customer customer = customerRepo.findById(id).orElseThrow(() -> new RuntimeException("Customer not found"));
                customer.setEnabled(false);
                customerRepo.save(customer);
                return ResponseEntity.ok("Customer disabled");
            }
            case "restaurant" -> {
                Restaurant restaurant = restaurantRepo.findById(id).orElseThrow(() -> new RuntimeException("Restaurant not found"));
                restaurant.setEnabled(false);
                restaurantRepo.save(restaurant);
                return ResponseEntity.ok("Restaurant disabled");
            }
            case "rider" -> {
                DeliveryPartner rider = riderRepo.findById(id).orElseThrow(() -> new RuntimeException("Rider not found"));
                rider.setEnabled(false);
                riderRepo.save(rider);
                return ResponseEntity.ok("Rider disabled");
            }
            default -> throw new RuntimeException("Invalid user type");
        }
    }

    // 8. List all orders
    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders() {
        List<Order> orders = orderRepo.findAll();
        List<OrderDTO> orderDTOs = orders.stream().map(OrderDTO::new).toList();
        return ResponseEntity.ok(orderDTOs);
    }

    // 9. Get order details
    @GetMapping("/orders/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        Order order = orderRepo.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        return ResponseEntity.ok(order);
    }

    // 10. Update order status/assignment
    @PutMapping("/orders/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Order order = orderRepo.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        if (updates.containsKey("status")) order.setStatus(com.excelr.FoodDelivery.Models.Enum.OrderStatus.valueOf((String) updates.get("status")));
        // Add more fields as needed (e.g., assign delivery partner)
        orderRepo.save(order);
        return ResponseEntity.ok(order);
    }

    // 11. List all transactions
    @GetMapping("/transactions")
    public ResponseEntity<?> getAllTransactions() {
        List<Transaction> txns = transactionRepo.findAll();
        return ResponseEntity.ok(txns);
    }

    // 12. Get transaction details
    @GetMapping("/transactions/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable Long id) {
        Transaction txn = transactionRepo.findById(id).orElseThrow(() -> new RuntimeException("Transaction not found"));
        return ResponseEntity.ok(txn);
    }

    // Existing admin details endpoint remains
    @PostMapping(value = "/details", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdminResponse> getAndUpdateAdminProfile(
            Authentication authentication,
            @RequestPart(required = false) AdminDetailsDTO update,
            @RequestPart(required = false) MultipartFile profilePic) throws Exception {
        String email = authentication.getName();
        Admin admin = adminRepo.findEnabled(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        AdminDetailsDTO d;
        if (update == null && profilePic == null) {
            d = new AdminDetailsDTO(admin);
        } else {
            d = adminService.updateAdminDetails(admin, update, profilePic);
        }
        Object user = adminRepo.findEnabled(d.getEmail())
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        String jwt = jwtUtil.generateAccessTokken(user, "ADMIN");
        return ResponseEntity.ok(new AdminResponse(jwt, d));
    }

    static class AdminResponse {
        public String token;
        public AdminDetailsDTO r;
        public AdminResponse(String t, AdminDetailsDTO res) {
            token = t;
            r = res;
        }
    }
}
