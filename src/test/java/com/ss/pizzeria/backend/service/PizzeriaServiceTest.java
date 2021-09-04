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
import java.util.NoSuchElementException;
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
        // dataset
        final Person p = buildPerson(8, "TestUser");
        final List<Order> list = buildOrdersForPerson(p);
        p.getOrderList().addAll(list);
        final List<OrderDto> mappedList = buildDtoData(list);
        // input not needed
        // mock
        Mockito.when(this.orderRepository.findAll(Sort.by("timestamp"))).thenReturn(list);
        list.forEach(o ->
                Mockito.when(this.mapper.map(o, OrderDto.class))
                        .thenReturn(mappedList.get(list.indexOf(o)))
        );

        //log.info("\n method[{}]", this.orderRepository.findAll(Sort.by("timestamp")));

        // test
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

        Mockito.verify(this.orderRepository,
                        Mockito.times(1)
                                .description("Method should be called exact one time."))
                .findAll(Mockito.any(Sort.class));
    }

    @Test
    void testCreateOrder() {
        // dataset
        final Person p = buildPerson(8, "TestUser");
        final List<Order> list = buildOrdersForPerson(p);
        p.getOrderList().addAll(list);
        final List<OrderDto> mappedList = buildDtoData(list);
        // input
        OrderCreateDto newOrdercreateDto = new OrderCreateDto(
                Pizza.Crust.THIN,Pizza.Flavour.HAWAII,
                Pizza.Size.M,134,p.getId());
        // mocks
        final Order newOrder = buildOrder(13L,
                newOrdercreateDto.getSize(),
                newOrdercreateDto.getFlavour(),
                newOrdercreateDto.getTableNo(),
                p
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
        Mockito.when(this.mapper.map(newOrdercreateDto, Order.class))
                        .thenReturn(newOrder);
        Mockito.when(this.orderRepository.saveAndFlush(newOrder))
                .thenReturn(newOrder);
        Mockito.when(this.mapper.map(newOrder, OrderDto.class))
                .thenReturn(newOrderDto);

        // when person does not exist
        Mockito.when(this.personRepository.findById(p.getId()))
                .thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class,
                () -> this.pizzeriaService.createOrder(newOrdercreateDto),
                "Exception should be thrown when person could not be found");

        // when person exists
        Mockito.when(this.personRepository.findById(p.getId()))
                .thenReturn(Optional.of(p));
        final var result = this.pizzeriaService.createOrder(newOrdercreateDto);
        assertEquals(OrderDto.class, result.getClass(),
                    "result should be of type OrderDto, but instead is of type: "
                            + result.getClass().getSimpleName());
        assertEquals(p.getId(), result.getCustomerId(),
                "The input customer ID and that of the ordering person in result should be same");
    }

    @Test
    void removeOrder() {
        // input
        final String id = "6";
        // mock
        final Long idAsLong = Long.parseLong(id, 10);
        final Person person = buildPerson(4, "Annie");
        final Order order = buildOrder(idAsLong, Pizza.Size.L,
                Pizza.Flavour.QUARTTRO_FORMAGGI, 6, person);
        Mockito.doNothing().when(this.orderRepository).delete(order);

        // when order does not exist
        Mockito.when(this.orderRepository.findById(idAsLong))
                .thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class,
                () -> this.pizzeriaService.removeOrder(id),
                "Should throw appropriate exception when order not found.");

        // when order exists
        Mockito.when(this.orderRepository.findById(idAsLong))
                        .thenReturn(Optional.of(order));
        assertDoesNotThrow(
                () -> this.pizzeriaService.removeOrder(id),
                "Should not throw any exception when order is found.");
    }

//    @Disabled("To be added later")
//    @Test
//    void registerPerson() {
//    }

    @Test
    void readAllOrdersForPersonSortedByTime()  {
        // dataset
        final Person p = buildPerson(1, "Alen");
        final List<Order> list = buildOrdersForPerson(p);
        p.getOrderList().addAll(list);
        final List<OrderDto> mappedList = buildDtoData(list);
        // input
        final String id = "5";
        // mock
        Mockito.when(this.personRepository.findById(p.getId()))
                .thenReturn(Optional.of(p));
        Mockito.when(this.personRepository.findById(Long.parseLong(id,10)))
                .thenReturn(Optional.empty());
        Mockito.when(this.orderRepository
                .findAllByCustomer(p,Sort.by("timestamp")))
                .thenReturn(list);
        list.forEach(o ->
                Mockito.when(this.mapper.map(o, OrderDto.class))
                        .thenReturn(mappedList.get(list.indexOf(o)))
        );

        // check when person does not exist
        assertThrows(NoSuchElementException.class,
                () -> this.pizzeriaService.readAllOrdersForPersonSortedByTime(id),
                "Should throw exception when person not found");

        // check when person does exist
        final var result = this.pizzeriaService
                .readAllOrdersForPersonSortedByTime(p.getId().toString());
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

        Mockito.verify(this.personRepository,
                        Mockito.times(2)
                                .description("Method should be called exactly two times."))
                .findById(Mockito.any(Long.class));
        Mockito.verify(this.orderRepository,
                        Mockito.times(1)
                                .description("Method should be called exact one time."))
                .findAllByCustomer(Mockito.any(Person.class), Mockito.any(Sort.class));
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
    private List<Order> buildOrdersForPerson(@NonNull final Person person) {
        final Order o1 = buildOrder(1L, Pizza.Size.L, Pizza.Flavour.REGINA, 12, person);
        final Order o2 = buildOrder(2L, Pizza.Size.M, Pizza.Flavour.HAWAII, 7, person);
        final Order o3 = buildOrder(3L, Pizza.Size.M, Pizza.Flavour.HAWAII, 7, person);
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
    private Order buildOrder(@NonNull final Long id,
                             @NonNull final Pizza.Size size,
                             @NonNull final Pizza.Flavour flavour,
                             final int table, @NonNull final Person p) {
        Order o = new Order();
        o.setId(id);
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