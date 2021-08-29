package com.ss.pizzeria.backend.service;

import com.ss.pizzeria.backend.Constants;
import com.ss.pizzeria.backend.data.dao.OrderRepository;
import com.ss.pizzeria.backend.data.dao.PersonRepository;
import com.ss.pizzeria.backend.data.model.Order;
import com.ss.pizzeria.backend.data.model.Person;
import com.ss.pizzeria.backend.rest.dto.*;
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
    private final PersonRepository peopleRepos;

    /* inject repository to access orders Table */
    @NonNull
    private final OrderRepository orderRepos;

    /* inject mapper to convert from jpa entity to dto */
    @NonNull
    private final ModelMapper mapper;

    public PizzeriaService(@NonNull PersonRepository peopleRepos, @NonNull final OrderRepository orderRepos, @NonNull final ModelMapper mapper) {
        this.peopleRepos = peopleRepos;
        this.orderRepos = orderRepos;
        this.mapper = mapper;
    }

    /**
     * Returns a fixed token for `test' user and an empty token otherwise
     */
    @NotNull
    public AccessTokenDto fetchTokenForUser(@NotNull final UserAuthDto credentials) {
        final AccessTokenDto accessTokenDto = new AccessTokenDto();
        if (credentials.getUsername().equals("test") && credentials.getPassword().equals("test")) {
            accessTokenDto.setAccessToken(Constants.AUTH_TOKEN);
        }
        return accessTokenDto;
    }

    /**
     * fetches a list of orders
     */
    @NotNull
    public List<OrderDto> readAllOrdersSortedByTime() {
        List<OrderDto> dtoList = new ArrayList<>();
        List<Order> orderList = this.orderRepos.findAll(Sort.by("timestamp"));
        orderList.forEach(order -> dtoList.add(mapper.map(order, OrderDto.class)));
        return dtoList;
    }

    /**
     * creates a new Order
     */
    @NotNull
    public OrderDto createOrder(@NotNull final OrderCreateDto orderRequest) {
        // retrieve valid customer
        final Long personId = orderRequest.getCustomerId();
        final Optional<Person> findRegisteredCustomer = peopleRepos.findById(personId);
        final Person customer = findRegisteredCustomer.orElseThrow(
                () -> new NoSuchElementException(Constants.Messages.NO_PERSON_EXISTS_WITH_ID + personId)
        );
        // continue to create order
        final Order order = this.mapper.map(orderRequest, Order.class);
        order.setCustomer(customer);
        customer.getOrderList().add(order);
        /* save to any one repository is enough because of the oneToMany/ManyToOne mappings */
        final Order created = this.orderRepos.saveAndFlush(order);
        return this.mapper.map(created, OrderDto.class);
    }

    /**
     * removes the Order with given ID
     */
    public void removeOrder(@NotNull final String orderId) {
        final Optional<Order> findOrder = this.orderRepos.findById(Long.parseLong(orderId, 10));
        this.orderRepos.delete(findOrder.orElseThrow(() -> new NoSuchElementException("Order #" + orderId + " not found.")));
    }

    /**
     * registers a new Person with empty order list
     */
    @NotNull
    public PersonDto registerPerson(@NotNull final PersonCreateDto request) {
        // no need to check existing name, as it is possible to have 2 people with same name
        final Person registered = this.peopleRepos.saveAndFlush(new Person(request.getName()));
        // not considered mapping back from List<Order> to List<OrderDto>, as this is always empty list for new Person()
        return this.mapper.map(registered, PersonDto.class);
    }

    @NotNull
    public List<OrderDto> readAllOrdersForPersonSortedByTime(@NotNull final String customerId) {
        List<OrderDto> dtoList = new ArrayList<>();
        // find requesting person from id
        Optional<Person> findPerson = peopleRepos.findById(Long.parseLong(customerId));
        Person requestingCustomer = findPerson.orElseThrow(
                ()-> new NoSuchElementException(Constants.Messages.NO_PERSON_EXISTS_WITH_ID + customerId)
        );
        List<Order> orderList = this.orderRepos.findAllByCustomer(requestingCustomer, Sort.by("timestamp"));
        orderList.forEach(order -> dtoList.add(mapper.map(order, OrderDto.class)));
        return dtoList;
    }
}
