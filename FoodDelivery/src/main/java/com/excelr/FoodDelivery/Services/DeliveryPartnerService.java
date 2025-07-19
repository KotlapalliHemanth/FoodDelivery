package com.excelr.FoodDelivery.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.excelr.FoodDelivery.Models.Customer;
import com.excelr.FoodDelivery.Models.DeliveryPartner;
import com.excelr.FoodDelivery.Models.DTO.CustomerDetailsDTO;
import com.excelr.FoodDelivery.Models.DTO.DeliveryPartnerDetailsDTO;
import com.excelr.FoodDelivery.Repositories.DeliveryPartnerRepository;
import com.excelr.FoodDelivery.Services.Utilities.CloudinaryUtil;

@Service
public class DeliveryPartnerService {

	@Autowired
	DeliveryPartnerRepository riderRepo;
	
	@Autowired
	private CloudinaryUtil cloudinaryUtil;
	
	public DeliveryPartnerDetailsDTO updateRiderDetails(DeliveryPartner rider, DeliveryPartnerDetailsDTO update, MultipartFile newProfilePic) throws Exception {
        // Only update fields if provided (not null), except addresses and password

        // Handle profile pic replacement
        if (newProfilePic != null && !newProfilePic.isEmpty()) {
            // Delete old image from Cloudinary if it exists
            if (rider.getProfilePicPublicId() != null) {
                cloudinaryUtil.deleteImage(rider.getProfilePicPublicId());
            }
            // Upload new image
            CloudinaryUtil.UploadResult result = cloudinaryUtil.uploadImage(newProfilePic);
            rider.setProfilePic(result.imageUrl);
            rider.setProfilePicPublicId(result.publicId);
        } 


        if(update!=null) {
        	if (update.getUsername() != null) rider.setUsername(update.getUsername());
            if (update.getEmail() != null) rider.setEmail(update.getEmail());
            if (update.getPhone() != null) rider.setPhone(update.getPhone());
            if (update.getEnabled() != null) rider.setEnabled(update.getEnabled());
        }
       
        // Do NOT update addresses or password here

        DeliveryPartner c= riderRepo.save(rider);
        
        return new DeliveryPartnerDetailsDTO(c);
        
    }
}
