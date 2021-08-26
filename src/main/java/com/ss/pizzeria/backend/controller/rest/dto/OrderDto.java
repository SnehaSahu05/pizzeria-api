package com.ss.pizzeria.backend.controller.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Sneha
 */
@Data
@NoArgsConstructor
public class OrderDto extends OrderCeateDto{

    @Schema(description = "Id of the order")
    @JsonProperty("Order_ID")
    private int orderId;

   @Schema(description = "Creation/Update timestamp of the order")
    @JsonProperty("Timestamp")
    private String timestamp;

}
