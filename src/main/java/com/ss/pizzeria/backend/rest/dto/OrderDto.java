package com.ss.pizzeria.backend.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ss.pizzeria.backend.data.model.Order;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

/**
 * DTO for {@link Order}
 * @author Sneha
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OrderDto extends OrderCreateDto {

    @Schema(description = "Id of the order")
    @JsonProperty("Order_ID")
    @NonNull
    private Long orderId;

    @Schema(description = "Creation/Update timestamp of the order")
    @JsonProperty("Timestamp")
    @NonNull
    private Long timestamp;

}
