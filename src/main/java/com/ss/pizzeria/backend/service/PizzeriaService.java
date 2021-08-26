package com.ss.pizzeria.backend.service;

import com.ss.pizzeria.backend.Constants;
import com.ss.pizzeria.backend.controller.rest.dto.AccessTokenDto;
import com.ss.pizzeria.backend.controller.rest.dto.OrderCeateDto;
import com.ss.pizzeria.backend.controller.rest.dto.OrderDto;
import com.ss.pizzeria.backend.controller.rest.dto.UserAuthDto;
import com.ss.pizzeria.backend.data.dao.OrderRepository;
import com.ss.pizzeria.backend.data.model.Order;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Service class for the functional logics.
 *
 * @author Sneha
 */
@Service
@Slf4j
public class PizzeriaService {

    /* inject repository to access orders Table */
    @NonNull
    private final OrderRepository orderRepos;

    /* inject mapper to convert from jpa entity to dto */
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
            accessTokenDto.setAccessToken(Constants.AUTH_TOKEN);
        }
        return accessTokenDto;
    }

    @NotNull
    public List<OrderDto> readAllOrdersSortedByTime() {
        List<OrderDto> dtoList = new ArrayList<>();
        List<Order> orderList = this.orderRepos.findAll(Sort.by("timestamp"));
        orderList.forEach(order -> dtoList.add(mapper.map(order, OrderDto.class)));
        return dtoList;
    }

    @NotNull
    public OrderDto createOrder(@NotNull final OrderCeateDto orderCreateDto) {
        // retrieve information
        final Order request = this.mapper.map(orderCreateDto, Order.class);
        // check person exist
        // create when not
        // create order
        final Order created = this.orderRepos.saveAndFlush(request);
        return this.mapper.map(created, OrderDto.class);

    }

    public void removeOrder(final Long orderId) {
        log.info("recieve order id {} of type {}", orderId, orderId.getClass().getSimpleName());
        final Optional<Order> findOrder = this.orderRepos.findById(orderId);
        this.orderRepos.delete(findOrder.orElseThrow(() -> new NoSuchElementException("Order #" + orderId + " not found.")));
    }
}
