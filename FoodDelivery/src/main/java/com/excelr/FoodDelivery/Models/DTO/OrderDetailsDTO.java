package com.excelr.FoodDelivery.Models.DTO;

import java.util.List;

public class OrderDetailsDTO {
    public Long orderId;
    public String status;
    public String orderTime;
    public Double amount;
    public String instructions;
    public String paymentStatus;
    public String paymentType;
    public String deliveredAt;
    public String pickedUpAt;

    public RestaurantInfo restaurant;
    public CustomerInfo customer;
    public List<DishInfo> dishes;

    public static class RestaurantInfo {
        public String name;
        public String address;
        public String phone;
        public String img;
        public Location location;
    }
    public static class CustomerInfo {
        public String name;
        public String address;
        public String phone;
        public Location location;
    }
    public static class Location {
        public Double lat;
        public Double lng;
    }
    public static class DishInfo {
        public Long dishId;
        public String name;
        public int quantity;
        public Double price;
        public String img;
    }
} 