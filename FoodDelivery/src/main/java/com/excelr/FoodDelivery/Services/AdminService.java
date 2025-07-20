package com.excelr.FoodDelivery.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.excelr.FoodDelivery.Models.Admin;
import com.excelr.FoodDelivery.Models.DTO.AdminDetailsDTO;
import com.excelr.FoodDelivery.Repositories.AdminRepository;
import com.excelr.FoodDelivery.Services.Utilities.CloudinaryUtil;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepo;

    @Autowired
    private CloudinaryUtil cloudinaryUtil;

    public AdminDetailsDTO updateAdminDetails(Admin admin, AdminDetailsDTO update, MultipartFile newProfilePic) throws Exception {
        // Handle profile pic replacement
        if (newProfilePic != null && !newProfilePic.isEmpty()) {
            // Delete old image from Cloudinary if it exists
            if (admin.getProfilePicPublicId() != null) {
                cloudinaryUtil.deleteImage(admin.getProfilePicPublicId());
            }
            // Upload new image
            CloudinaryUtil.UploadResult result = cloudinaryUtil.uploadImage(newProfilePic);
            admin.setProfilePic(result.imageUrl);
            admin.setProfilePicPublicId(result.publicId);
        }
        if (update != null) {
            if (update.getUsername() != null) admin.setUsername(update.getUsername());
            if (update.getEmail() != null) admin.setEmail(update.getEmail());
            if (update.getPhone() != null) admin.setPhone(update.getPhone());
            if (update.getEnabled() != null) admin.setEnabled(update.getEnabled());
            // Add more fields as needed
        }
        Admin saved = adminRepo.save(admin);
        return new AdminDetailsDTO(saved);
    }
}
