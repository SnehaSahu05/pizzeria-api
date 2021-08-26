package com.ss.pizzeria.backend.controller.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * @author Sneha
 */
@Data
@NoArgsConstructor
public class OrderCeateDto {

    @Schema(description = "Crust for the pizza", required = true)
    @JsonProperty("Crust")
    @NonNull
    private String crust;

    @Schema(description = "Flavor of the pizza", required = true)
    @JsonProperty("Flavor")
    @NonNull
    private String flavour;

    @Schema(description = "Size of the pizza", required = true)
    @JsonProperty("Size")
    @NonNull
    private String size;

    @Schema(description = "Customer's table number", required = true)
    @JsonProperty("Table_No")
    @NonNull
    private int tableNo;

}
