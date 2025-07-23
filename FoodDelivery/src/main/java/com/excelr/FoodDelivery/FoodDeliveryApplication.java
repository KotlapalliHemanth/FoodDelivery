package com.excelr.FoodDelivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import com.excelr.FoodDelivery.Models.Admin;
import com.excelr.FoodDelivery.Repositories.AdminRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class FoodDeliveryApplication {

	@Bean
	public CommandLineRunner createDefaultAdmin(AdminRepository adminRepo, PasswordEncoder passwordEncoder) {
		return args -> {
			String defaultUsername = "admin";
			String defaultEmail = "admin@example.com";
			String defaultPhone = "1234567890";
			String defaultPassword = "admin@123";

			if (adminRepo.findEnabled(defaultUsername).isEmpty() && adminRepo.findEnabled(defaultEmail).isEmpty()) {
				Admin admin = new Admin();
				admin.setUsername(defaultUsername);
				admin.setEmail(defaultEmail);
				admin.setPhone(defaultPhone);
				admin.setPassword(passwordEncoder.encode(defaultPassword));
				admin.setEnabled(true);
				adminRepo.save(admin);
				System.out.println("Default admin user created.");
			}
		};
	}

	public static void main(String[] args) {
		SpringApplication.run(FoodDeliveryApplication.class, args);
	}

}
