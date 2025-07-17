package com.excelr.FoodDelivery.Models.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RiderPositionDTO {
 private Double lat;
 private Double lon;
 
 public RiderPositionDTO(Double lattitude, Double longitude) {
	 this.lat= lattitude;
	 this.lon= longitude;
	 
 }
}
