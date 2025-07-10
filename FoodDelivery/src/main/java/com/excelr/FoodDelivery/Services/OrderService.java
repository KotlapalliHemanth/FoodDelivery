package com.excelr.FoodDelivery.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.excelr.FoodDelivery.Models.Customer;
import com.excelr.FoodDelivery.Models.Dish;
import com.excelr.FoodDelivery.Models.Order;
import com.excelr.FoodDelivery.Models.DTO.CreateOrderDTO;
import com.excelr.FoodDelivery.Models.DTO.ModifyOrderDTO;
import com.excelr.FoodDelivery.Repositories.DishRepository;
import com.excelr.FoodDelivery.Repositories.OrderRepository;

@Service
public class OrderService {

	
	@Autowired
	DishRepository dishRepo;
	
	@Autowired
	OrderRepository orderRepo;
	
	
//	order manipulations
	
	//create order
	public Order createOrder(Customer customer, CreateOrderDTO req) {
        List<Dish> dishes = dishRepo.findAllById(req.getDishIds());

        Order order = new Order();
        order.setCustomer(customer);
        order.setDishes(dishes);
        order.setAmount(req.getAmount());
        // Set other fields as needed

        return orderRepo.save(order);
    }
	
	public Order modifyOrder(Customer customer, ModifyOrderDTO req) {
		Order order= orderRepo.findById(req.getId())
				.orElseThrow(() -> new RuntimeException("order not found"));
		
		if(req.getType().equalsIgnoreCase("Transaction")) {
			// user transaction object  to create with order linked ------------------------------
		}
		if(req.getType().equalsIgnoreCase("status")){
			order.setStatus(req.getS());
		}
		return order;
	}

}
