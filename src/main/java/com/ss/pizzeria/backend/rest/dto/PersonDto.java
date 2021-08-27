package com.ss.pizzeria.backend.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ss.pizzeria.backend.data.model.Person;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for {@link Person}
 * @author Sneha
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class PersonDto extends PersonCreateDto{

    @Schema(description = "Id of the registered person")
    @JsonProperty("Customer_ID")
    private Long personId;

    @Schema(description = "List of orders made by person")
    @JsonProperty("Order_List")
    private List<OrderDto> orderList;
}
