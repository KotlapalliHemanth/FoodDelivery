package com.excelr.FoodDelivery.Security.Jwt;

import java.security.Key;
import java.util.Date;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.excelr.FoodDelivery.Models.Customer;
import com.excelr.FoodDelivery.Models.Admin;
import com.excelr.FoodDelivery.Models.Restaurant;
import com.excelr.FoodDelivery.Models.DeliveryPartner;
import com.excelr.FoodDelivery.Repositories.CustomerRepository;
import com.excelr.FoodDelivery.Repositories.AdminRepository;
import com.excelr.FoodDelivery.Repositories.RestaurantRepository;
import com.excelr.FoodDelivery.Repositories.DeliveryPartnerRepository;


@Component
public class JwtUtill {

    @Autowired
    private CustomerRepository customerRepo;
    @Autowired
    private AdminRepository adminRepo;
    @Autowired
    private RestaurantRepository restaurantRepo;
    @Autowired
    private DeliveryPartnerRepository deliveryPartnerRepo;

    private final Key key;
    private final int accessTokenExpirationMs;
    private final int refreshTokenExpirationMs;
    
    public JwtUtill( @Value("${jwt.secret}") String screctKey,@Value("${jwt.expirationMs}") int accessTokenExpirationMs,@Value("${jwt.refreshExpirationMs}") int refreshTokenExpirationMs) {
        this.key = Keys.hmacShaKeyFor(screctKey.getBytes());
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs= refreshTokenExpirationMs;
    }
    
    public String generateAccessTokken(Object user, String role) {
        String username = "";
        if (user instanceof Customer c) username = c.getUsername();
        else if (user instanceof Admin a) username = a.getEmail();
        else if (user instanceof Restaurant r) username = r.getEmail();
        else if (user instanceof DeliveryPartner d) username = d.getEmail();
        else throw new IllegalArgumentException("Unknown user type");

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationMs))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
    
    public boolean validateToken(String Token) {
        Claims claim = getAllClaimsFromToken(Token);
        String subject = claim.getSubject();
        String role = claim.get("role", String.class);

        boolean userExists = false;
        switch (role) {
            case "CUSTOMER" -> userExists = customerRepo.findByUsernameOrEmailOrPhone(subject).isPresent();
            case "ADMIN" -> userExists = adminRepo.findByUsernameOrEmailOrPhone(subject).isPresent();
            case "RESTAURANT" -> userExists = restaurantRepo.findByUsernameOrEmailOrPhone(subject).isPresent();
            case "RIDER", "DELIVERYPARTNER" -> userExists = deliveryPartnerRepo.findByUsernameOrEmailOrPhone(subject).isPresent();
        }

        if (claim.getExpiration().before(new Date())) {
            System.out.println("Token Expired");
            return false;
        } else if (!userExists) {
            System.out.println("user does not exist");
            return false;
        } else {
            return true;
        }
    }
    
    public Claims getAllClaimsFromToken(String Token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(Token)
                .getBody();
    }
}
