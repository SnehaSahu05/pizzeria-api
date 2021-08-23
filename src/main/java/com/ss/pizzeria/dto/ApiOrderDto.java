package com.ss.pizzeria.dto;

import lombok.Data;

/**
 * @author Sneha
 */
@Data
public class ApiOrderDto {

    private int orderId;

    private String crust;

    private String size;

    private int tableNo;

    private String flavour;

    private String timestamp;

}
