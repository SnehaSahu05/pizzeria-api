package com.ss.pizzeria.controller.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Sneha
 */
@Data
public class OrderDto {

    @Schema(description = "Crust for the pizza")
    @JsonProperty("Crust")
    private String crust;

    @Schema(description = "Flavor of the pizza")
    @JsonProperty("Flavor")
    private String flavour;

    @Schema(description = "Id of the order")
    @JsonProperty("Order_ID")
    private int orderId;

    @Schema(description = "Size of the pizza")
    @JsonProperty("Size")
    private String size;

    @Schema(description = "Customer's table number")
    @JsonProperty("Table_No")
    private int tableNo;

    @Schema(description = "Creation/Update timestamp of the order")
    @JsonProperty("Timestamp")
    private String timestamp;

}
