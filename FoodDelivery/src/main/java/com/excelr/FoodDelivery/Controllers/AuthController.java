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
        if (req.userType == null) {
            return ResponseEntity.badRequest().body("userType is required");
        }

        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.username, req.password));

        String role = null;
        Object user = null;
        switch (req.userType.toUpperCase()) {
            case "CUSTOMER" -> {
                var opt = customerRepo.findByUsernameOrEmailOrPhone(req.username);
                if (opt.isEmpty()) return ResponseEntity.status(401).body("Invalid credentials");
                user = opt.get();
                role = "CUSTOMER";
            }
            case "RIDER", "DELIVERYPARTNER" -> {
                var opt = deliveryRepo.findByUsernameOrEmailOrPhone(req.username);
                if (opt.isEmpty()) return ResponseEntity.status(401).body("Invalid credentials");
                user = opt.get();
                role = "RIDER";
            }
            case "RESTAURANT" -> {
                var opt = restaurantRepo.findByUsernameOrEmailOrPhone(req.username);
                if (opt.isEmpty()) return ResponseEntity.status(401).body("Invalid credentials");
                user = opt.get();
                role = "RESTAURANT";
            }
            case "ADMIN" -> {
                var opt = adminRepo.findByUsernameOrEmailOrPhone(req.username);
                if (opt.isEmpty()) return ResponseEntity.status(401).body("Invalid credentials");
                user = opt.get();
                role = "ADMIN";
            }
            default -> {
                return ResponseEntity.badRequest().body("Invalid userType");
            }
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
        String email = null, name = null, picture = null, googleId = null, firstName = null, lastName = null;
        if (authentication.getPrincipal() instanceof org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser oidcUser) {
            email = oidcUser.getAttribute("email");
            name = oidcUser.getAttribute("name");
            picture = oidcUser.getAttribute("picture");
            googleId = oidcUser.getAttribute("sub");
            firstName = oidcUser.getAttribute("given_name");
            lastName = oidcUser.getAttribute("family_name");
        } else if (authentication.getPrincipal() instanceof org.springframework.security.oauth2.core.user.OAuth2User oauth2User) {
            email = oauth2User.getAttribute("email");
            name = oauth2User.getAttribute("name");
            picture = oauth2User.getAttribute("picture");
            googleId = oauth2User.getAttribute("sub");
            firstName = oauth2User.getAttribute("given_name");
            lastName = oauth2User.getAttribute("family_name");
        }

        // Fallback: If firstName or lastName is null, try to split the name
        if ((firstName == null || lastName == null) && name != null) {
            String[] parts = name.trim().split(" ", 2);
            firstName = (firstName == null && parts.length > 0) ? parts[0] : firstName;
            lastName = (lastName == null && parts.length > 1) ? parts[1] : lastName;
        }

        String username = email != null ? email : (name != null ? name : googleId);

        Object user = null;
        String role = null;

        switch (userType != null ? userType.toUpperCase() : "") {
            case "CUSTOMER" -> {
                var opt = customerRepo.findByUsernameOrEmailOrPhone(username);
                if (opt.isPresent() && Boolean.TRUE.equals(opt.get().getIsEnabled())) {
                    user = opt.get();
                    role = "CUSTOMER";
                } else {
                    Customer c = new Customer();
                    c.setUsername(name != null ? name : username);
                    c.setFirstName(firstName);
                    c.setLastName(lastName);
                    c.setEmail(email);
                    c.setPassword(null); // No password for OAuth
                    c.setPhone(null); // Not provided by Google //null because if we give "" it is unquie and we get error
                    c.setProfilePic(picture);
                    c.setGoogleId(googleId);
                    c.setIsEnabled(true);
                    customerRepo.save(c);
                    user = c;
                    role = "CUSTOMER";
                }
            }
            case "RIDER", "DELIVERYPARTNER" -> {
                var opt = deliveryRepo.findByUsernameOrEmailOrPhone(username);
                if (opt.isPresent() && Boolean.TRUE.equals(opt.get().getIsEnabled())) {
                    user = opt.get();
                    role = "RIDER";
                } else {
                    DeliveryPartner d = new DeliveryPartner();
                    d.setUsername(name != null ? name : username);
                    d.setEmail(email);
                    d.setPassword("");
                    d.setPhone("");
                    d.setProfilePic(picture);
                    d.setGoogleId(googleId);
                    d.setIsEnabled(true);
                    deliveryRepo.save(d);
                    user = d;
                    role = "RIDER";
                }
            }
            case "RESTAURANT" -> {
                var opt = restaurantRepo.findByUsernameOrEmailOrPhone(username);
                if (opt.isPresent() && Boolean.TRUE.equals(opt.get().getIsEnabled())) {
                    user = opt.get();
                    role = "RESTAURANT";
                } else {
                    Restaurant r = new Restaurant();
                    r.setUsername(name != null ? name : username);
                    r.setEmail(email);
                    r.setPassword("");
                    r.setPhone("");
                    r.setProfilePic(picture);
                    r.setGoogleId(googleId);
                    r.setIsEnabled(true);
                    restaurantRepo.save(r);
                    user = r;
                    role = "RESTAURANT";
                }
            }
            case "ADMIN" -> {
                var opt = adminRepo.findByUsernameOrEmailOrPhone(username);
                if (opt.isPresent() && Boolean.TRUE.equals(opt.get().getIsEnabled())) {
                    user = opt.get();
                    role = "ADMIN";
                } else {
                    Admin a = new Admin();
                    a.setUsername(name != null ? name : username);
                    a.setEmail(email);
                    a.setPassword("");
                    a.setPhone("");
                    a.setProfilePic(picture);
                    a.setGoogleId(googleId);
                    a.setIsEnabled(true);
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
}

class RegistrationRequest { public String username, email, password, phone; }
class LoginRequest {
    public String username;
    public String password;
    public String userType; // Add this
}
class JwtResponse { public String token; public JwtResponse(String t) { token = t; } }
