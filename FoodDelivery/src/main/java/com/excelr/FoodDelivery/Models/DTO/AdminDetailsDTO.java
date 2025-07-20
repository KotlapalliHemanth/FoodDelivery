package com.excelr.FoodDelivery.Models.DTO;

import com.excelr.FoodDelivery.Models.Admin;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AdminDetailsDTO {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String profilePic;
    private String profilePicPublicId;
    private String googleId;
    private Boolean enabled;
    private java.time.LocalDateTime createdAt;

    public AdminDetailsDTO(Admin admin) {
        this.id = admin.getId();
        this.username = admin.getUsername();
        this.email = admin.getEmail();
        if (admin.getPhone() != null && admin.getPhone().equals(admin.getGoogleId())) {
            this.phone = "";
        } else {
            this.phone = admin.getPhone();
        }
        this.profilePic = admin.getProfilePic();
        this.profilePicPublicId = admin.getProfilePicPublicId();
        this.googleId = admin.getGoogleId();
        this.enabled = admin.getEnabled();
        this.createdAt = admin.getCreatedAt();
    }
} 