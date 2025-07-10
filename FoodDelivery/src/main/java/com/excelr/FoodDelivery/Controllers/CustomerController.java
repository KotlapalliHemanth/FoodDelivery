package com.excelr.FoodDelivery.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.excelr.FoodDelivery.Models.Customer;
import com.excelr.FoodDelivery.Models.Order;
import com.excelr.FoodDelivery.Models.DTO.CreateOrderDTO;
import com.excelr.FoodDelivery.Models.DTO.CustomerDetailsDTO;
import com.excelr.FoodDelivery.Models.DTO.ModifyOrderDTO;
import com.excelr.FoodDelivery.Repositories.CustomerRepository;
import com.excelr.FoodDelivery.Services.CustomerService;
import com.excelr.FoodDelivery.Services.OrderService;

@RestController
@RequestMapping("/customer")
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private OrderService orderService;

    
//  api for  details
    @PostMapping(value = "/details", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CustomerDetailsDTO> getAndUpdateUserProfile(
            Authentication authentication,
            @RequestPart(required = false) Customer update,
            @RequestPart(required = false) MultipartFile profilePic) throws Exception {

    	String email = authentication.getName();
        Customer customer = customerRepo.findByUsernameOrEmailOrPhone(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        CustomerDetailsDTO d = null;
        if(update==null && profilePic==null) {
        	 d = new CustomerDetailsDTO(customer);
        	System.out.println(d);
        }
        if(update!=null || profilePic!=null) {
        	 d = customerService.updateCustomerDetails(customer, update, profilePic);
            
           
            System.out.println(customer);
            
        }
        return ResponseEntity.ok(d);
    }

    
    // curd operations on orders
    
    // get all users
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getUserOrders(Authentication authentication) {
        String email = authentication.getName();
        Customer customer = customerRepo.findByUsernameOrEmailOrPhone(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        List<Order> orders = customer.getOrders();
        return ResponseEntity.ok(orders);
    }

    
    //create order for the payments
    @PostMapping("/orders")
    public ResponseEntity<Order> createOrder(Authentication authentication, @RequestBody CreateOrderDTO req) {
        String email = authentication.getName();
        Customer customer = customerRepo.findByUsernameOrEmailOrPhone(email)
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
         Customer customer = customerRepo.findByUsernameOrEmailOrPhone(email)
                 .orElseThrow(() -> new RuntimeException("Customer not found"));
         
         Order newOrder = orderService.modifyOrder(customer, req);
         return ResponseEntity.ok(newOrder);
    }
    
    //transactions api (creation, modifications)---------------------------
    
    
    // curd operations on address------------------------
    
    
    //password change------------------------
    
    
    //others-----------------
    
}
