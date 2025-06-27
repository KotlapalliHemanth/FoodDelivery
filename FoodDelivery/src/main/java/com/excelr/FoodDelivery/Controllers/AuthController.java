package com.excelr.FoodDelivery.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.excelr.FoodDelivery.Repositories.AdminRepository;
import com.excelr.FoodDelivery.Repositories.CustomerRepository;
import com.excelr.FoodDelivery.Repositories.DeliveryPartnerRepository;
import com.excelr.FoodDelivery.Repositories.RestaurantRepository;
import com.excelr.FoodDelivery.Security.Jwt.JwtUtill;
import com.excelr.FoodDelivery.Models.Customer;
import com.excelr.FoodDelivery.Models.Admin;
import com.excelr.FoodDelivery.Models.Restaurant;
import com.excelr.FoodDelivery.Models.DeliveryPartner;
import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired AuthenticationManager authenticationManager;
    @Autowired JwtUtill jwtUtil;
    @Autowired CustomerRepository customerRepo;
    @Autowired DeliveryPartnerRepository deliveryRepo;
    @Autowired RestaurantRepository restaurantRepo;
    @Autowired AdminRepository adminRepo;
    @Autowired PasswordEncoder passwordEncoder;

    @PostMapping("/register/{role}")
    public ResponseEntity<?> register(@PathVariable String role, @RequestBody RegistrationRequest req) {
        switch (role.toUpperCase()) {
            case "CUSTOMER" -> {
                Customer c = new Customer();
                c.setUsername(req.username);
                c.setEmail(req.email);
                c.setPassword(passwordEncoder.encode(req.password));
                c.setPhone(req.phone);
                customerRepo.save(c);
            }
            case "DELIVERYPARTNER", "RIDER" -> {
                DeliveryPartner d = new DeliveryPartner();
                d.setUsername(req.username);
                d.setEmail(req.email);
                d.setPassword(passwordEncoder.encode(req.password));
                d.setPhone(req.phone);
                deliveryRepo.save(d);
            }
            case "RESTAURANT" -> {
                Restaurant r = new Restaurant();
                r.setUsername(req.username);
                r.setEmail(req.email);
                r.setPassword(passwordEncoder.encode(req.password));
                r.setPhone(req.phone);
                restaurantRepo.save(r);
            }
            case "ADMIN" -> {
                Admin a = new Admin();
                a.setUsername(req.username);
                a.setEmail(req.email);
                a.setPassword(passwordEncoder.encode(req.password));
                a.setPhone(req.phone);
                adminRepo.save(a);
            }
            default -> {
                return ResponseEntity.badRequest().body("Invalid role");
            }
        }
        return ResponseEntity.ok("Registered");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.username, req.password));

        // Try to find user in each repo using the unified method
        String role = null;
        Object user = null;
        if (customerRepo.findByUsernameOrEmailOrPhone(req.username).isPresent()) {
            user = customerRepo.findByUsernameOrEmailOrPhone(req.username).get();
            role = "CUSTOMER";
        } else if (deliveryRepo.findByUsernameOrEmailOrPhone(req.username).isPresent()) {
            user = deliveryRepo.findByUsernameOrEmailOrPhone(req.username).get();
            role = "RIDER";
        } else if (restaurantRepo.findByUsernameOrEmailOrPhone(req.username).isPresent()) {
            user = restaurantRepo.findByUsernameOrEmailOrPhone(req.username).get();
            role = "RESTAURANT";
        } else if (adminRepo.findByUsernameOrEmailOrPhone(req.username).isPresent()) {
            user = adminRepo.findByUsernameOrEmailOrPhone(req.username).get();
            role = "ADMIN";
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        String jwt = jwtUtil.generateAccessTokken(user, role);
        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    @GetMapping("/oauth2/success")
    public ResponseEntity<?> oauth2Success(Authentication authentication) {
        String email = authentication.getName();
        Object user = null;
        String role = null;

        if (customerRepo.findByUsernameOrEmailOrPhone(email).isPresent()) {
            user = customerRepo.findByUsernameOrEmailOrPhone(email).get();
            role = "CUSTOMER";
        } else if (deliveryRepo.findByUsernameOrEmailOrPhone(email).isPresent()) {
            user = deliveryRepo.findByUsernameOrEmailOrPhone(email).get();
            role = "RIDER";
        } else if (restaurantRepo.findByUsernameOrEmailOrPhone(email).isPresent()) {
            user = restaurantRepo.findByUsernameOrEmailOrPhone(email).get();
            role = "RESTAURANT";
        } else if (adminRepo.findByUsernameOrEmailOrPhone(email).isPresent()) {
            user = adminRepo.findByUsernameOrEmailOrPhone(email).get();
            role = "ADMIN";
        } else {
            // Default: register as CUSTOMER if not found
            Customer c = new Customer();
            c.setUsername(email);
            c.setEmail(email);
            c.setPassword(""); // No password for OAuth
            c.setPhone("");
            customerRepo.save(c);
            user = c;
            role = "CUSTOMER";
        }

        String jwt = jwtUtil.generateAccessTokken(user, role);
        return ResponseEntity.ok(new JwtResponse(jwt));
    }
}

class RegistrationRequest { public String username, email, password, phone; }
class LoginRequest { public String username, password; }
class JwtResponse { public String token; public JwtResponse(String t) { token = t; } }
