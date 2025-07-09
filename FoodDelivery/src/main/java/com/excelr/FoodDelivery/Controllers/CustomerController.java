package com.excelr.FoodDelivery.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import com.excelr.FoodDelivery.Models.Customer;
import com.excelr.FoodDelivery.Repositories.CustomerRepository;
import com.excelr.FoodDelivery.Services.CustomerService;

@RestController
@RequestMapping("/customer")
@PreAuthorize("hasRole('CUSTOMER')")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepo;

    @Autowired
    private CustomerService customerService;

    @PostMapping(value = "/details", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Customer> getAndUpdateUserProfile(
            Authentication authentication,
            @RequestPart(required = false) Customer update,
            @RequestPart(required = false) MultipartFile profilePic) throws Exception {

        String email = authentication.getName();
        Customer customer = customerRepo.findByUsernameOrEmailOrPhone(email)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customer = customerService.updateCustomerDetails(customer, update, profilePic);

        customer.setOrders(List.of());
        return ResponseEntity.ok(customer);
    }
}
