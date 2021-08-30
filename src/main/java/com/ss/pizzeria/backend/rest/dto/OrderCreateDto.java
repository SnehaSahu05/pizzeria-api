package com.ss.pizzeria.backend.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ss.pizzeria.backend.data.model.Order;
import com.ss.pizzeria.backend.data.model.Pizza;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * DTO for {@link Order}
 * @author Sneha
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateDto {

    @Schema(description = "Crust for the pizza", required = true)
    @JsonProperty("Crust")
    @NonNull
    private Pizza.Crust crust;

    @Schema(description = "Flavor of the pizza", required = true)
    @JsonProperty("Flavor")
    @NonNull
    private Pizza.Flavour flavour;

    @Schema(description = "Size of the pizza", required = true)
    @JsonProperty("Size")
    @NonNull
    private Pizza.Size size;

    @Schema(description = "Customer's table number", required = true)
    @JsonProperty("Table_No")
    @NonNull
    private int tableNo;

    @Schema(description = "ID of the ordering Person ~ customer", required = true)
    @JsonProperty("Customer_ID")
    @NonNull
    private Long customerId;

}
