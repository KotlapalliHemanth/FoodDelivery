package com.excelr.FoodDelivery.Security;

import com.excelr.FoodDelivery.Models.*;
import com.excelr.FoodDelivery.Repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UnifiedUserDetailsService implements UserDetailsService {
    @Autowired private CustomerRepository customerRepo;
    @Autowired private DeliveryPartnerRepository deliveryRepo;
    @Autowired private RestaurantRepository restaurantRepo;
    @Autowired private AdminRepository adminRepo;

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        Optional<? extends UserDetails> user =
            customerRepo.findByUsernameOrEmailOrPhone(input).map(c -> buildUserDetails(c, "CUSTOMER"))
            .or(() -> deliveryRepo.findByUsernameOrEmailOrPhone(input).map(d -> buildUserDetails(d, "RIDER")))
            .or(() -> restaurantRepo.findByUsernameOrEmailOrPhone(input).map(r -> buildUserDetails(r, "RESTAURANT")))
            .or(() -> adminRepo.findByUsernameOrEmailOrPhone(input).map(a -> buildUserDetails(a, "ADMIN")));

        return user.orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private UserDetails buildUserDetails(Object entity, String role) {
        String username, password;
        boolean enabled = true;
        if (entity instanceof Customer c) {
            username = c.getEmail();
            password = c.getPassword();
            enabled = c.getIsEnabled();
        } else if (entity instanceof DeliveryPartner d) {
            username = d.getEmail();
            password = d.getPassword();
            enabled = d.getIsEnabled();
        } else if (entity instanceof Restaurant r) {
            username = r.getEmail();
            password = r.getPassword();
            enabled = r.getIsEnabled();
        } else if (entity instanceof Admin a) {
            username = a.getEmail();
            password = a.getPassword();
            enabled = a.getIsEnabled();
        } else {
            throw new IllegalArgumentException("Unknown user type");
        }
        return org.springframework.security.core.userdetails.User
                .withUsername(username)
                .password(password)
                .roles(role)
                .disabled(!enabled)
                .build();
    }
}
