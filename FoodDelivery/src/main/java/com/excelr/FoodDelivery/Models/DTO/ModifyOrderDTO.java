package com.excelr.FoodDelivery.Models.DTO;

import com.excelr.FoodDelivery.Models.Transaction;
import com.excelr.FoodDelivery.Models.Enum.OrderStatus;

import lombok.Data;

@Data
public class ModifyOrderDTO {
	private Long id;
	private Transaction t;
	private OrderStatus s;
	private String type;
}
