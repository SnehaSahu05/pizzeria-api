package com.ss.pizzeria.backend.service;

import com.ss.pizzeria.backend.Constants;
import com.ss.pizzeria.backend.data.dao.OrderRepository;
import com.ss.pizzeria.backend.data.dao.PersonRepository;
import com.ss.pizzeria.backend.data.model.Order;
import com.ss.pizzeria.backend.data.model.Person;
import com.ss.pizzeria.backend.data.model.Pizza;
import com.ss.pizzeria.backend.rest.dto.OrderCreateDto;
import com.ss.pizzeria.backend.rest.dto.OrderDto;
import com.ss.pizzeria.backend.rest.dto.UserAuthDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests for Service methods
 *
 * @author Sneha
 */
@Slf4j
@SpringBootTest
class PizzeriaServiceTest {

    @InjectMocks
    private PizzeriaService pizzeriaService;

    @MockBean
    private PersonRepository personRepository;
    @MockBean
    private OrderRepository orderRepository;
    @MockBean
    private ModelMapper mapper;

    @BeforeEach
    void setUp() {
        checkInitializations();
    }

    @Test
    void testGetAccessToken() {

        final var resultOne = this.pizzeriaService.fetchTokenForUser(new UserAuthDto("", ""));
        assertEquals(StringUtils.EMPTY, resultOne.getAccessToken(), "The access token should be empty");

        final var resultTwo = this.pizzeriaService.fetchTokenForUser(new UserAuthDto("test", "xyz"));
        assertEquals(StringUtils.EMPTY, resultTwo.getAccessToken(), "The access token should be empty");

        final var resultThree = this.pizzeriaService.fetchTokenForUser(new UserAuthDto("test", "test"));
        assertEquals(Constants.AUTH_TOKEN, resultThree.getAccessToken(), "The access token should match with the right one");
    }

    @Test
    void testReadAllOrdersSortedByTime() {
        final List<Order> list = buildDataSet();
        final List<OrderDto> mappedList = buildDtoData(list);
        Mockito.when(this.orderRepository.findAll(Sort.by("timestamp"))).thenReturn(list);
        list.forEach(o ->
                Mockito.when(this.mapper.map(o, OrderDto.class))
                        .thenReturn(mappedList.get(list.indexOf(o)))
        );

        //log.info("\n method[{}]", this.orderRepository.findAll(Sort.by("timestamp")));

        final var result = this.pizzeriaService.readAllOrdersSortedByTime();
        final var length = result.size();
        assertEquals(list.size(), length, "Result size should match to that of 'list'.");
        for (int i = 0; i < length; i++) {
            final var r = result.get(i);
            assertEquals(OrderDto.class, r.getClass(),
                    "result at index=" + i + " should be of type OrderDto, but instead is of type: " + r.getClass().getSimpleName());
            if (i != 0)
                assertTrue(result.get(i - 1).getTimestamp() <= r.getTimestamp(),
                        "The resulting Orders should be in ascending order of timestamp");
        }
//        Mockito.verify(this.orderRepository, Mockito.times(length))
//                .findAll(Mockito.any(Sort.class)); //description("Method should be called exact number of times.")
    }

    @Test
    void testCreateOrder() {
        final List<Order> list = buildDataSet();
        final List<OrderDto> mappedList = buildDtoData(list);

        final Person newPerson = buildPerson(8, "TestUser");
        OrderCreateDto newOrdercreateDto = new OrderCreateDto(
                Pizza.Crust.THIN,Pizza.Flavour.HAWAII,
                Pizza.Size.M,134,newPerson.getId());
        final Order newOrder = buildOrder(13,
                newOrdercreateDto.getSize(),
                newOrdercreateDto.getFlavour(),
                newOrdercreateDto.getTableNo(),
                newPerson
                );
        final OrderDto newOrderDto = buildOrderDto(
                newOrder.getId(),
                newOrder.getCrust(),
                newOrder.getSize(),
                newOrder.getFlavour(),
                newOrder.getTableNo(),
                newOrder.getCustomer().getId(),
                newOrder.getTimestamp()
        );

        Mockito.when(this.personRepository.findById(newPerson.getId()))
                .thenReturn(Optional.of(newPerson));
        Mockito.when(this.mapper.map(newOrdercreateDto, Order.class))
                        .thenReturn(newOrder);
        Mockito.when(this.orderRepository.saveAndFlush(newOrder))
                .thenReturn(newOrder);
        Mockito.when(this.mapper.map(newOrder, OrderDto.class))
                .thenReturn(newOrderDto);

        final var result = this.pizzeriaService.createOrder(newOrdercreateDto);
        assertEquals(OrderDto.class, result.getClass(),
                    "result should be of type OrderDto, but instead is of type: "
                            + result.getClass().getSimpleName());
        assertEquals(newPerson.getId(), result.getCustomerId(),
                "The input customer ID and that of the ordering person in result should be same");
    }

    @AfterEach
    void tearDown() {
        Mockito.clearAllCaches();
    }

    /**
     * non-null checks for auto-injected objects
     */
    private void checkInitializations() {

        // initialization of the mocks is taken care by the annotations
        /*log.info("\n injected objects: \n {} \n\t{} \n\t{}, \n\t{}",
                this.pizzeriaService, this.personRepository, this.orderRepository, this.mapper);*/
        assertNotNull(this.pizzeriaService, "Corresponding class instance should not be null.");
        assertNotNull(this.orderRepository, "Mocked dao instance for Order should not be null.");
        assertNotNull(this.personRepository, "Mocked dao instance for Person should not be null.");
        assertNotNull(this.mapper, "Mocked mapper instance should not be null.");

        // ensure that the service in test is actually injected with the desired objects
        this.pizzeriaService = new PizzeriaService(this.personRepository, this.orderRepository, this.mapper);
    }

    /**
     * generates a dummy DTO data set
     */
    private List<OrderDto> buildDtoData(List<Order> list) {
        List<OrderDto> dtos = new ArrayList<>();
        list.forEach(o -> {
            OrderDto d = new OrderDto(o.getId(),o.getTimestamp());
            d.setCrust(o.getCrust());
            d.setFlavour(o.getFlavour());
            d.setSize(o.getSize());
            d.setTableNo(o.getTableNo());
            d.setCustomerId(o.getCustomer().getId());
            dtos.add(d);
        });
        return dtos;
    }

    /**
     * generates a dummy data set
     */
    private List<Order> buildDataSet() {
        final Person a = buildPerson(1, "Alen");
        final Person b = buildPerson(2, "Berry");
        final Order o1 = buildOrder(1, Pizza.Size.L, Pizza.Flavour.REGINA, 12, a);
        final Order o2 = buildOrder(2, Pizza.Size.M, Pizza.Flavour.HAWAII, 7, b);
        final Order o3 = buildOrder(3, Pizza.Size.M, Pizza.Flavour.HAWAII, 7, a);
        a.getOrderList().addAll(List.of(o1, o3));
        b.getOrderList().add(o2);
        return List.of(o1, o2, o3);
    }

    /**
     * adds a new Person
     */
    private Person buildPerson(final int id, @NonNull final String name) {
        Person p = new Person(name);
        p.setId((long) id);
        return p;
    }

    /**
     * adds a new Order
     */
    private Order buildOrder(final int id, @NonNull final Pizza.Size size,
                             @NonNull final Pizza.Flavour flavour,
                             final int table, @NonNull final Person p) {
        Order o = new Order();
        o.setId((long) id);
        o.setCrust(Pizza.Crust.THIN);
        o.setSize(size);
        o.setFlavour(flavour);
        o.setTableNo(table);
        o.setCustomer(p);
        return o;
    }

    /**
     * creates a new OrderDto
     */
    private OrderDto buildOrderDto(@NonNull final Long oId, @NonNull final Pizza.Crust crust,
                                   @NonNull final Pizza.Size size,
                                   @NonNull final Pizza.Flavour flavour,
                                   final int tableNo, @NonNull final Long customerId,
                                   @NonNull final Long time) {
    OrderDto order = new OrderDto(oId, time);
    order.setCustomerId(customerId);
    order.setTableNo(tableNo);
    order.setSize(size);
    order.setFlavour(flavour);
    order.setCrust(crust);
    return order;
    }

}