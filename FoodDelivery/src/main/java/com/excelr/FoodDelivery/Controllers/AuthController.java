package com.excelr.FoodDelivery.Controllers;

import java.io.IOException;

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
                // Check if an active user with the given username, email, or phone already exists.
                if (customerRepo.findEnabled(req.username).isPresent()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("An active user with this username already exists.");
                }
                if (customerRepo.findEnabled(req.email).isPresent()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("An active user with this email already exists.");
                }
                if (customerRepo.findEnabled(req.phone).isPresent()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("An active user with this phone number already exists.");
                }
                Customer c = new Customer();
                c.setUsername(req.username);
                c.setEmail(req.email);
                c.setPassword(passwordEncoder.encode(req.password));
                c.setPhone(req.phone);
                customerRepo.save(c);
            }
            case "DELIVERYPARTNER", "RIDER" -> {
                if (deliveryRepo.findEnabled(req.username).isPresent()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("An active delivery partner with this username already exists.");
                }
                if (deliveryRepo.findEnabled(req.email).isPresent()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("An active delivery partner with this email already exists.");
                }
                if (deliveryRepo.findEnabled(req.phone).isPresent()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("An active delivery partner with this phone number already exists.");
                }
                DeliveryPartner d = new DeliveryPartner();
                d.setUsername(req.username);
                d.setEmail(req.email);
                d.setPassword(passwordEncoder.encode(req.password));
                d.setPhone(req.phone);
                deliveryRepo.save(d);
            }
            case "RESTAURANT" -> {
                if (restaurantRepo.findEnabled(req.username).isPresent()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("An active restaurant with this username already exists.");
                }
                if (restaurantRepo.findEnabled(req.email).isPresent()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("An active restaurant with this email already exists.");
                }
                if (restaurantRepo.findEnabled(req.phone).isPresent()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("An active restaurant with this phone number already exists.");
                }
                Restaurant r = new Restaurant();
                r.setUsername(req.username);
                r.setEmail(req.email);
                r.setPassword(passwordEncoder.encode(req.password));
                r.setPhone(req.phone);
                restaurantRepo.save(r);
            }
            case "ADMIN" -> {
                if (adminRepo.findEnabled(req.username).isPresent()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("An active admin with this username already exists.");
                }
                if (adminRepo.findEnabled(req.email).isPresent()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("An active admin with this email already exists.");
                }
                if (adminRepo.findEnabled(req.phone).isPresent()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("An active admin with this phone number already exists.");
                }
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
        if (req.userType == null) {
            return ResponseEntity.badRequest().body("userType is required");
        }

        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.username, req.password));

        String role = null;
        Object user = null;
        switch (req.userType.toUpperCase()) {
            case "CUSTOMER" -> {
                var opt = customerRepo.findEnabled(req.username);
                if (opt.isEmpty()) return ResponseEntity.status(401).body("Invalid credentials");
                user = opt.get();
                role = "CUSTOMER";
            }
            case "RIDER", "DELIVERYPARTNER" -> {
                var opt = deliveryRepo.findEnabled(req.username);
                if (opt.isEmpty()) return ResponseEntity.status(401).body("Invalid credentials");
                user = opt.get();
                role = "RIDER";
            }
            case "RESTAURANT" -> {
                var opt = restaurantRepo.findEnabled(req.username);
                if (opt.isEmpty()) return ResponseEntity.status(401).body("Invalid credentials");
                user = opt.get();
                role = "RESTAURANT";
            }
            case "ADMIN" -> {
                var opt = adminRepo.findEnabled(req.username);
                if (opt.isEmpty()) return ResponseEntity.status(401).body("Invalid credentials");
                user = opt.get();
                role = "ADMIN";
            }
            default -> {
                return ResponseEntity.badRequest().body("Invalid userType");
            }
        }
        System.out.println(user);
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
        String email = null, name = null, picture = null, googleId = null, firstName = null, lastName = null;
        if (authentication.getPrincipal() instanceof org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser oidcUser) {
            email = oidcUser.getAttribute("email");
            name = oidcUser.getAttribute("name");
            picture = oidcUser.getAttribute("picture");
            googleId = oidcUser.getAttribute("sub");
            firstName = oidcUser.getAttribute("given_name");
            lastName = oidcUser.getAttribute("family_name");
//            System.out.println(oidcUser);
        } else if (authentication.getPrincipal() instanceof org.springframework.security.oauth2.core.user.OAuth2User oauth2User) {
            email = oauth2User.getAttribute("email");
            name = oauth2User.getAttribute("name");
            picture = oauth2User.getAttribute("picture");
            googleId = oauth2User.getAttribute("sub");
            firstName = oauth2User.getAttribute("given_name");
            lastName = oauth2User.getAttribute("family_name");
//            System.out.println(oauth2User);
        }

        // Fallback: If firstName or lastName is null, try to split the name
        if ((firstName == null || lastName == null) && name != null) {
            String[] parts = name.trim().split(" ", 2);
            firstName = (firstName == null && parts.length > 0) ? parts[0] : firstName;
            lastName = (lastName == null && parts.length > 1) ? parts[1] : lastName;
        }

        
        String username = email != null ? email : (name != null ? name : googleId);
//        System.out.println(email+ " ---- "+ name+ " ---- "+ picture+ " ---- "+googleId+ " ---- "+firstName+ " ---- "+lastName);        
        Object user = null;
        String role = null;

        switch (userType != null ? userType.toUpperCase() : "") {
            case "CUSTOMER" -> {
                var opt = customerRepo.findEnabled(username);
                System.out.println(opt);
                if (opt.isPresent() && Boolean.TRUE.equals(opt.get().getEnabled())) {
                    user = opt.get();
                    role = "CUSTOMER";
                } else {
                    Customer c = new Customer();
                    c.setUsername(name != null ? name : username);
                    c.setFirstName(firstName);
                    c.setLastName(lastName);
                    c.setEmail(email);
                    c.setPassword(passwordEncoder.encode(googleId)); // No password for OAuth
                    c.setPhone(googleId); // Not provided by Google
                    c.setProfilePic(picture);
                    c.setGoogleId(googleId);
                    c.setEnabled(true);
                    customerRepo.save(c);
                    user = c;
                    role = "CUSTOMER";
                }
            }
            case "RIDER", "DELIVERYPARTNER" -> {
                var opt = deliveryRepo.findEnabled(username);
                if (opt.isPresent() && Boolean.TRUE.equals(opt.get().getEnabled())) {
                    user = opt.get();
                    role = "RIDER";
                } else {
                    DeliveryPartner d = new DeliveryPartner();
                    d.setUsername(name != null ? name : username);
                    d.setEmail(email);
                    d.setPassword(passwordEncoder.encode(googleId));
                    d.setPhone(googleId);
                    d.setProfilePic(picture);
                    d.setGoogleId(googleId);
                    d.setEnabled(true);
                    deliveryRepo.save(d);
                    user = d;
                    role = "RIDER";
                }
            }
            case "RESTAURANT" -> {
                var opt = restaurantRepo.findEnabled(username);
                if (opt.isPresent() && Boolean.TRUE.equals(opt.get().getEnabled())) {
                    user = opt.get();
                    role = "RESTAURANT";
                } else {
                    Restaurant r = new Restaurant();
                    r.setUsername(name != null ? name : username);
                    r.setEmail(email);
                    r.setPassword(passwordEncoder.encode(googleId));
                    r.setPhone(googleId);
                    r.setResturantPic(picture);
                    r.setGoogleId(googleId);
                    r.setEnabled(true);
                    restaurantRepo.save(r);
                    user = r;
                    role = "RESTAURANT";
                }
            }
            case "ADMIN" -> {
                var opt = adminRepo.findEnabled(username);
                if (opt.isPresent() && Boolean.TRUE.equals(opt.get().getEnabled())) {
                    user = opt.get();
                    role = "ADMIN";
                } else {
                    Admin a = new Admin();
                    a.setUsername(name != null ? name : username);
                    a.setEmail(email);
                    a.setPassword(passwordEncoder.encode(googleId));
                    a.setPhone(googleId);
                    a.setProfilePic(picture);
                    a.setGoogleId(googleId);
                    a.setEnabled(true);
                    adminRepo.save(a);
                    user = a;
                    role = "ADMIN";
                }
            }
            default -> {
                return ResponseEntity.badRequest().body("Invalid userType");
            }
        }

        String jwt = jwtUtil.generateAccessTokken(user, role);
        String html = "<!DOCTYPE html><html><body><script>"
        	    + "window.opener.postMessage({token: '" + jwt + "'}, '*');"
        	    + "window.close();"
        	    + "</script></body></html>";
        	response.setContentType("text/html");
        	try {
				response.getWriter().write(html);
			} catch (IOException e) {
				e.printStackTrace();
			}
        	return null;
    }
    
    
    // getting restaurent details available --------------------
    // getting dishes provide by restaurent at that time by restaurent------------------
}

class RegistrationRequest { public String username, email, password, phone; }
class LoginRequest {
    public String username;
    public String password;
    public String userType; // Add this
}
class JwtResponse { public String token; public JwtResponse(String t) { token = t; } }
