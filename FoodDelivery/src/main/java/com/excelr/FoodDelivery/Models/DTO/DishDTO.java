package com.excelr.FoodDelivery.Models.DTO;

import com.excelr.FoodDelivery.Models.Dish;
import com.excelr.FoodDelivery.Models.Restaurant;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DishDTO {

	private Long id;

    private String name;
    private String image;
    private Double price;
    private String description;
    private String category;
    private String cusine;
    private Boolean available;
    private Boolean deleted;
    
    public DishDTO(Dish dish) {
    	this.id= dish.getId();
    	this.name= dish.getName();
    	this.image= dish.getImage();
    	this.price= dish.getPrice();
    	this.description= dish.getDescription();
    	this.category= dish.getCategory();
    	this.cusine= dish.getCusine();
    	this.available= dish.getAvailable();
    	this.deleted= dish.getDeleted();
    	
    }
}
