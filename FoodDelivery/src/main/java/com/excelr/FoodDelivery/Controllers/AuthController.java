package com.excelr.FoodDelivery.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.excelr.FoodDelivery.Models.Admin;
import com.excelr.FoodDelivery.Models.Customer;
import com.excelr.FoodDelivery.Models.DeliveryPartner;
import com.excelr.FoodDelivery.Models.Restaurant;
import com.excelr.FoodDelivery.Repositories.AdminRepository;
import com.excelr.FoodDelivery.Repositories.CustomerRepository;
import com.excelr.FoodDelivery.Repositories.DeliveryPartnerRepository;
import com.excelr.FoodDelivery.Repositories.RestaurantRepository;
import com.excelr.FoodDelivery.Security.Jwt.JwtUtill;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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
    public ResponseEntity<?> oauth2Success(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        HttpSessionOAuth2AuthorizationRequestRepository repo = new HttpSessionOAuth2AuthorizationRequestRepository();
        OAuth2AuthorizationRequest authRequest = repo.removeAuthorizationRequest(request, response);

        String userType = null;
        if (authRequest != null && authRequest.getAdditionalParameters() != null) {
            userType = (String) authRequest.getAdditionalParameters().get("userType");
        }
        if (userType == null) {
            userType = (String) request.getSession().getAttribute("OAUTH2_USER_TYPE");
        }
        System.out.println("User type from OAuth2 flow: " + userType);

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        // Extract Google user info
        String email = null, name = null, picture = null, googleId = null;
        if (authentication.getPrincipal() instanceof org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser oidcUser) {
            email = oidcUser.getAttribute("email");
            name = oidcUser.getAttribute("name");
            picture = oidcUser.getAttribute("picture");
            googleId = oidcUser.getAttribute("sub");
        } else if (authentication.getPrincipal() instanceof org.springframework.security.oauth2.core.user.OAuth2User oauth2User) {
            email = oauth2User.getAttribute("email");
            name = oauth2User.getAttribute("name");
            picture = oauth2User.getAttribute("picture");
            googleId = oauth2User.getAttribute("sub");
        }
        String username = email != null ? email : (name != null ? name : googleId);

        Object user = null;
        String role = null;

        // Try to find user in all repos
        if (customerRepo.findByUsernameOrEmailOrPhone(username).isPresent()) {
            user = customerRepo.findByUsernameOrEmailOrPhone(username).get();
            role = "CUSTOMER";
        } else if (deliveryRepo.findByUsernameOrEmailOrPhone(username).isPresent()) {
            user = deliveryRepo.findByUsernameOrEmailOrPhone(username).get();
            role = "RIDER";
        } else if (restaurantRepo.findByUsernameOrEmailOrPhone(username).isPresent()) {
            user = restaurantRepo.findByUsernameOrEmailOrPhone(username).get();
            role = "RESTAURANT";
        } else if (adminRepo.findByUsernameOrEmailOrPhone(username).isPresent()) {
            user = adminRepo.findByUsernameOrEmailOrPhone(username).get();
            role = "ADMIN";
        } else {
            // Register in the correct table based on userType
            switch (userType != null ? userType.toUpperCase() : "") {
                case "CUSTOMER" -> {
                    Customer c = new Customer();
                    c.setUsername(name != null ? name : username);
                    c.setEmail(email);
                    c.setPassword(""); // No password for OAuth
                    c.setPhone(""); // Not provided by Google
                    c.setProfilePic(picture);
                    c.setGoogleId(googleId);
                    customerRepo.save(c);
                    user = c;
                    role = "CUSTOMER";
                }
                case "RIDER", "DELIVERYPARTNER" -> {
                    DeliveryPartner d = new DeliveryPartner();
                    d.setUsername(name != null ? name : username);
                    d.setEmail(email);
                    d.setPassword("");
                    d.setPhone("");
                    d.setProfilePic(picture);
                    d.setGoogleId(googleId);
                    deliveryRepo.save(d);
                    user = d;
                    role = "RIDER";
                }
                case "RESTAURANT" -> {
                    Restaurant r = new Restaurant();
                    r.setUsername(name != null ? name : username);
                    r.setEmail(email);
                    r.setPassword("");
                    r.setPhone("");
                    r.setProfilePic(picture);
                    r.setGoogleId(googleId);
                    restaurantRepo.save(r);
                    user = r;
                    role = "RESTAURANT";
                }
                case "ADMIN" -> {
                    Admin a = new Admin();
                    a.setUsername(name != null ? name : username);
                    a.setEmail(email);
                    a.setPassword("");
                    a.setPhone("");
                    a.setProfilePic(picture);
                    a.setGoogleId(googleId);
                    adminRepo.save(a);
                    user = a;
                    role = "ADMIN";
                }
                default -> {
                    // Fallback: register as CUSTOMER
                    Customer c = new Customer();
                    c.setUsername(name != null ? name : username);
                    c.setEmail(email);
                    c.setPassword("");
                    c.setPhone("");
                    c.setProfilePic(picture);
                    c.setGoogleId(googleId);
                    customerRepo.save(c);
                    user = c;
                    role = "CUSTOMER";
                }
            }
        }

        String jwt = jwtUtil.generateAccessTokken(user, role);
        return ResponseEntity.ok(new JwtResponse(jwt));
    }
}

class RegistrationRequest { public String username, email, password, phone; }
class LoginRequest { public String username, password; }
class JwtResponse { public String token; public JwtResponse(String t) { token = t; } }
