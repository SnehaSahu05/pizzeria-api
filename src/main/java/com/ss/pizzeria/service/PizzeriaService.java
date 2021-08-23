package com.ss.pizzeria.service;

import com.ss.pizzeria.dto.AccessTokenDto;
import com.ss.pizzeria.dto.ApiAuthDto;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

/**
 * Service class for the functional logics.
 *
 * @author Sneha
 */
@Service
public class PizzeriaService {

    /**
     * Returns a fixed token for `test' user and an empty token for all other cases
     */
    @NotNull
    public AccessTokenDto getAccessToken(@NotNull final ApiAuthDto credentials) {
        final AccessTokenDto accessTokenDto = new AccessTokenDto();
        if (credentials.getUsername().equals("test") && credentials.getPassword().equals("test")) {
            accessTokenDto.setAccessToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2MjkzMDAxNTgsIm5iZiI6MTYyOTMwMDE1OCwianRpIjoiNDc2M2JjZmUtMzM5Yy00MWFjLTkxY2EtZGE1MDk5Yjg3NmUzIiwiZXhwIjoxNjI5MzAxMDU4LCJpZGVudGl0eSI6InRlc3QiLCJmcmVzaCI6ZmFsc2UsInR5cGUiOiJhY2Nlc3MifQ.E6XL5Ese6yG1VmoVu8cl-sXThjz4TCSYCpi1QmtwdkQ");
        }
        return accessTokenDto;
    }
}
