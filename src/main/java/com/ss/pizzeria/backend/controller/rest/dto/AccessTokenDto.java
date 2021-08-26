package com.ss.pizzeria.backend.controller.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;

/**
 * @author Sneha
 */
@Data
public class AccessTokenDto {

    @JsonProperty("access_token")
    @NotNull
    private String accessToken = StringUtils.EMPTY;
}
