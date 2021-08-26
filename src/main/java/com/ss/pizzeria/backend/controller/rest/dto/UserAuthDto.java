package com.ss.pizzeria.backend.controller.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NonNull;

/**
 * @author Sneha
 */
@Data
public class UserAuthDto {

    @Schema(description = "username of user", required = true, example = "name")
    @JsonProperty("username")
    @NonNull
    private String username;

    @Schema(description = "password of user", required = true, example = "password")
    @JsonProperty("password")
    @NonNull
    private String password;
}
