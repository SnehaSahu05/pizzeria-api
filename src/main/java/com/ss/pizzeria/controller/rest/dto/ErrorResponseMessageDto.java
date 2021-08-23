package com.ss.pizzeria.controller.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.Nullable;

/**
 * @author Sneha
 */
@Data
@AllArgsConstructor
public class ErrorResponseMessageDto {

    @Nullable
    private String msg;
}
