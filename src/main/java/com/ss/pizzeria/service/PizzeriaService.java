package com.ss.pizzeria.service;

import com.ss.pizzeria.controller.rest.dto.OrderDto;
import com.ss.pizzeria.data.dao.OrderRepository;
import com.ss.pizzeria.data.model.Order;
import com.ss.pizzeria.controller.rest.dto.AccessTokenDto;
import com.ss.pizzeria.controller.rest.dto.UserAuthDto;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for the functional logics.
 *
 * @author Sneha
 */
@Service
@Slf4j
public class PizzeriaService {

    /* injectrepository to access orders Table */
    @NonNull
    private final OrderRepository orderRepos;

    /* injectmapper to convert from jpa entity to dto */
    @NonNull
    private final ModelMapper mapper;

    public PizzeriaService(@NonNull final OrderRepository orderRepos, @NonNull final ModelMapper mapper) {
        this.orderRepos = orderRepos;
        this.mapper = mapper;
    }

    /**
     * Returns a fixed token for `test' user and an empty token for all other cases
     */
    @NotNull
    public AccessTokenDto getAccessToken(@NotNull final UserAuthDto credentials) {
        final AccessTokenDto accessTokenDto = new AccessTokenDto();
        if (credentials.getUsername().equals("test") && credentials.getPassword().equals("test")) {
            accessTokenDto.setAccessToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2MjkzMDAxNTgsIm5iZiI6MTYyOTMwMDE1OCwianRpIjoiNDc2M2JjZmUtMzM5Yy00MWFjLTkxY2EtZGE1MDk5Yjg3NmUzIiwiZXhwIjoxNjI5MzAxMDU4LCJpZGVudGl0eSI6InRlc3QiLCJmcmVzaCI6ZmFsc2UsInR5cGUiOiJhY2Nlc3MifQ.E6XL5Ese6yG1VmoVu8cl-sXThjz4TCSYCpi1QmtwdkQ");
        }
        return accessTokenDto;
    }

    @NotNull
    public List<OrderDto> readAllOrdersSortedByTime() {
        List<OrderDto> dtoList = new ArrayList<>();
        List<Order> orderList = this.orderRepos.findAll(Sort.by("timestamp"));
        log.info("...db data set: {}", orderList);
        orderList.forEach(a -> dtoList.add(mapper.map(a, OrderDto.class)));
        log.info("...dto list: {}", dtoList);
        return dtoList;
    }
}
