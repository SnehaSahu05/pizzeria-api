package com.ss.pizzeria.controller.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Sneha
 */
@Data
public class UserAuthDto {

    @Schema(description = "username of user")
    @JsonProperty("username")
    @NotNull
    private String username;

    @Schema(description = "password of user")
    @JsonProperty("password")
    @NotNull
    private String password;
}
