package com.ss.pizzeria.backend.controller.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.lang.Nullable;

/**
 * @author Sneha
 */
@Data
@AllArgsConstructor
public class ResponseMessageDto {

    @Nullable
    private String msg;
}
