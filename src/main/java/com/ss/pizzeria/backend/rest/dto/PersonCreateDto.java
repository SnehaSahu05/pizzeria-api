package com.ss.pizzeria.backend.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ss.pizzeria.backend.data.model.Person;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * DTO for {@link Person}
 * @author Sneha
 */
@Data
@NoArgsConstructor
public class PersonCreateDto {

    @Schema(description = "Name of Person for registration", required = true, example = "Muster")
    @JsonProperty("name")
    @NonNull
    private String name;

}
