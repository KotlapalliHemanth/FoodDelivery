package com.excelr.FoodDelivery.Models.DTO;

import java.util.List;

public class RiderOrderDTO {
    public Long id;
    public Double amount;
    public String status;
    public Boolean riderAssigned;
    public String createdAt;
    public RestaurantInfo restaurant;
    public List<DishWithQuantityDTO> dishes;

    public static class RestaurantInfo {
        public String name;
        public String address;
        public Double lat;
        public Double lon;
        public String phone;
    }
    public static class DishWithQuantityDTO {
        public Long dishId;
        public String name;
        public int quantity;
    }
} 