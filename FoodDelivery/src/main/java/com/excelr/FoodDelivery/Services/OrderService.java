package com.excelr.FoodDelivery.Services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.excelr.FoodDelivery.Models.Enum.OrderStatus;
import com.excelr.FoodDelivery.Models.Customer;
import com.excelr.FoodDelivery.Models.Dish;
import com.excelr.FoodDelivery.Models.Order;
import com.excelr.FoodDelivery.Models.Restaurant;
import com.excelr.FoodDelivery.Models.DTO.CreateOrderDTO;
import com.excelr.FoodDelivery.Models.DTO.ModifyOrderDTO;
import com.excelr.FoodDelivery.Repositories.DishRepository;
import com.excelr.FoodDelivery.Repositories.OrderRepository;
import com.excelr.FoodDelivery.Models.Address;
import com.excelr.FoodDelivery.Models.Enum.PaymentStatus;
import com.excelr.FoodDelivery.Repositories.AddressRepository;
import com.excelr.FoodDelivery.Repositories.RestaurantRepository;
import com.excelr.FoodDelivery.Models.Transaction;
import com.excelr.FoodDelivery.Repositories.TransactionRepository;

@Service
public class OrderService {

	
	@Autowired
	DishRepository dishRepo;
	
	@Autowired
	OrderRepository orderRepo;

	@Autowired
	AddressRepository addressRepo;

	@Autowired
	RestaurantRepository restaurantRepo;
	
	@Autowired
	TransactionRepository transactionRepo;
	
	
//	order manipulations
	
	//create order
	public Order createOrder(Customer customer, CreateOrderDTO req) {
        // Validate dishes
        List<Dish> dishes = dishRepo.findAllById(req.getDishIds());
        if (dishes.size() != req.getDishIds().size()) {
            throw new IllegalArgumentException("One or more dishes not found");
        }

        // // Validate restaurant (optional)
        // if (req.getRestaurantId() != null) {
        //     Restaurant restaurant = restaurantRepo.findById(req.getRestaurantId())
        //             .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
        //     boolean allDishesBelong = dishes.stream()
        //         .allMatch(d -> d.getRestaurant().getId().equals(restaurant.getId()));
        //     if (!allDishesBelong) {
        //         throw new IllegalArgumentException("All dishes must belong to the selected restaurant");
        //     }
        // }

        // // Validate address (optional)
        // Address address = null;
        // if (req.getDeliveryAddressId() != null) {
        //     address = addressRepo.findById(req.getDeliveryAddressId())
        //             .orElseThrow(() -> new IllegalArgumentException("Delivery address not found"));
        //     if (!customer.getAddresses().contains(address)) {
        //         throw new IllegalArgumentException("Address does not belong to customer");
        //     }
        // }

        // Calculate amount from dishes and quantities
        double calculatedAmount = 0.0;
        if (req.getDishQuantities() != null) {
            for (Dish dish : dishes) {
                int qty = req.getDishQuantities().getOrDefault(dish.getId(), 1);
                calculatedAmount += dish.getPrice() * qty;
            }
        } else {
            for (Dish dish : dishes) {
                calculatedAmount += dish.getPrice();
            }
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setDishes(dishes);
        order.setAmount(calculatedAmount);
        order.setStatus(req.getStatus() != null ? req.getStatus() : com.excelr.FoodDelivery.Models.Enum.OrderStatus.CREATED);
        order.setRiderAssigned(false);
        order.setCreatedAt(java.time.LocalDateTime.now());
        order.setUpdatedAt(java.time.LocalDateTime.now());
        order.setDeliveredAt(null);
        
		
		// Create a pending transaction
    Transaction txn = new Transaction();
    txn.setAmount(calculatedAmount);
    txn.setStatus(PaymentStatus.PENDING);
    txn.setOrder(order);

    order.setTransaction(txn);

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
	
	
	
	//get orders for the new restaurent.......................
	public List<Order> getCurrentOrders(Restaurant restaurant){
		return orderRepo.findCreatedOrdersByRestaurantId(restaurant.getId());
	}
	
	//accept or reject order by restaurant-----------------
	public Order acceptOrRejectOrder(Long rId, Long oId, boolean accept) {
		Order order = orderRepo.findById(oId)
				.orElseThrow(() -> new RuntimeException("Order not found with id: " + oId));

		
		boolean isAuthorized = order.getDishes().stream()
			.anyMatch(dish -> dish.getRestaurant() != null && dish.getRestaurant().getId().equals(rId));

		if (!isAuthorized) {
			throw new SecurityException("Restaurant with ID " + rId + " is not authorized to modify order with ID " + oId);
		}

		
		if (order.getStatus() != OrderStatus.CREATED) {
			throw new IllegalStateException("Order cannot be modified. Current status is: " + order.getStatus());
		}

		// 3. Update Status and Save
		order.setStatus(accept ? OrderStatus.PREPARING : OrderStatus.REJECTED);
		return orderRepo.save(order);
	}
	
	//accepted order by restaurant---------------
	public List<Order> acceptedOrdersByRestaurant(Long rID){
		return orderRepo.findAcceptedOrdersByRestaurantId(rID);
	}
	
	//get restaurant finshed orders---------------------
	public List<Order> getDeliveredOrdersByRestaurant (Long rId){
		return orderRepo.findDeliveredOrdersByRestaurantId(rId);
	}
	
	// get order by Id--------------
	public Order getOrderById(Long oId) {
		return orderRepo.findById(oId)
				.orElseThrow(() -> new RuntimeException("Order not found with id: " + oId));
	}
	
	
	//for rider---------
	//get available orders( preparing)----------------
	public List<Order> getPreparingOrders(Double lat, Double lon){
		return orderRepo.findPreparingOrders(lat, lon).stream().filter(order->!order.getRiderAssigned()).collect(Collectors.toList());
	}

    public void recordPaymentSuccess(Long orderId, String paymentId, Double amount, String typeOfPay) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        Transaction txn = order.getTransaction();
        if (txn == null) {
            txn = new Transaction();
            txn.setOrder(order);
        }
        txn.setTransactionId(paymentId);
        txn.setAmount(amount);
        txn.setTypeOfPay(typeOfPay); // e.g., "UPI", "CREDIT_CARD", "RAZORPAY"
        txn.setStatus(PaymentStatus.PAID);
        txn.setPaidAt(java.time.LocalDateTime.now());
        transactionRepo.save(txn);
        order.setTransaction(txn);
        orderRepo.save(order);
    }
}
