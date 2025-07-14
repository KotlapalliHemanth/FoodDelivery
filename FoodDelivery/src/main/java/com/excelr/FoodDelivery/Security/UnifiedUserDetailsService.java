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
            customerRepo.findEnabled(input).map(c -> buildUserDetails(c, "CUSTOMER"))
            .or(() -> deliveryRepo.findEnabled(input).map(d -> buildUserDetails(d, "RIDER")))
            .or(() -> restaurantRepo.findEnabled(input).map(r -> buildUserDetails(r, "RESTAURANT")))
            .or(() -> adminRepo.findEnabled(input).map(a -> buildUserDetails(a, "ADMIN")));

        return user.orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private UserDetails buildUserDetails(Object entity, String role) {
        String username, password;
        boolean enabled = true;
        if (entity instanceof Customer c) {
            username = c.getEmail();
            password = c.getPassword();
            enabled = c.getEnabled();
        } else if (entity instanceof DeliveryPartner d) {
            username = d.getEmail();
            password = d.getPassword();
            enabled = d.getEnabled();
        } else if (entity instanceof Restaurant r) {
            username = r.getEmail();
            password = r.getPassword();
            enabled = r.getEnabled();
        } else if (entity instanceof Admin a) {
            username = a.getEmail();
            password = a.getPassword();
            enabled = a.getEnabled();
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
