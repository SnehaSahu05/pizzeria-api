package com.ss.pizzeria.backend.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ss.pizzeria.backend.Constants;
import com.ss.pizzeria.backend.data.model.Person;
import com.ss.pizzeria.backend.data.model.Pizza;
import com.ss.pizzeria.backend.rest.dto.*;
import com.ss.pizzeria.backend.service.PizzeriaService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit Tests for Rest API requests to Controller
 *
 * @author Sneha
 */
@Slf4j
@WebMvcTest({RestController.class})
class RestControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @InjectMocks
    private RestController restController;

    @MockBean
    private PizzeriaService pizzeriaService;

    @BeforeEach
    void setUp() {
        checkInitializations();

        // initialize Mvc for building http requests
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        assertNotNull(this.mockMvc, "MockMvc instance should not be null.");
    }

    @Test
    void testAuthenticateApi_responseCreated() throws Exception {
        // input
        final UserAuthDto testAuth = new UserAuthDto("test", "test");
        // mock
        final AccessTokenDto access = new AccessTokenDto();
        access.setAccessToken(Constants.AUTH_TOKEN);
        Mockito.when(this.pizzeriaService.fetchTokenForUser(testAuth)).thenReturn(access);
        // request
        final MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post(Constants.Paths.API + Constants.Paths.AUTH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectAsJson(testAuth));
        // response
        mockMvc.perform(mockRequest)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON_VALUE));

        Mockito.verify(this.pizzeriaService, Mockito.times(1))
                .fetchTokenForUser(Mockito.any(UserAuthDto.class));
    }

    @Test
    void testAuthenticateApi_responseBadRequest() throws Exception {
        // input
        // mock
        // request
        final MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post(Constants.Paths.API + Constants.Paths.AUTH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{ username: 5000.0 }");
        // response
        mockMvc.perform(mockRequest)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON_VALUE));

        Mockito.verify(this.pizzeriaService, Mockito.times(0))
                .fetchTokenForUser(Mockito.any(UserAuthDto.class));
    }

    @Test
    void testAuthenticateApi_responseUnAuthorised() throws Exception {
        // input
        final UserAuthDto testAuth = new UserAuthDto("test", "abc");
        // mock
        Mockito.when(this.pizzeriaService.fetchTokenForUser(testAuth))
                .thenReturn(new AccessTokenDto());
        // request
        final MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post(Constants.Paths.API + Constants.Paths.AUTH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectAsJson(testAuth));
        // response
        mockMvc.perform(mockRequest)
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON_VALUE));

        Mockito.verify(this.pizzeriaService, Mockito.times(1))
                .fetchTokenForUser(Mockito.any(UserAuthDto.class));
    }

    @Test
    void getAllOrders_responseOk() throws Exception {
        // no input
        // mock
        final AccessTokenDto access = new AccessTokenDto();
        access.setAccessToken(Constants.AUTH_TOKEN);
        Mockito.when(this.pizzeriaService.readAllOrdersSortedByTime())
                .thenReturn(List.of());
        // request
        final MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(Constants.Paths.API + Constants.Paths.ORDERS);
        // response
        mockMvc.perform(mockRequest)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON_VALUE));
        // verify count of service calls
        Mockito.verify(this.pizzeriaService, Mockito.times(1))
                .readAllOrdersSortedByTime();
    }

    @Test
    void addPerson_responseCreated() throws Exception {
        // input
        PersonCreateDto personCreateDto = new PersonCreateDto();
        personCreateDto.setName("John");
        // mock
        final PersonDto personDto = new PersonDto();
        personDto.setPersonId(4L);
        personDto.setName(personCreateDto.getName());
        personDto.setOrderList(List.of());
        Mockito.when(this.pizzeriaService.registerPerson(personCreateDto))
                .thenReturn(personDto);
        // request
        final MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post(Constants.Paths.API + Constants.Paths.REG)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("token", Constants.AUTH_TOKEN)
                .content(objectAsJson(personCreateDto));
        // response
        mockMvc.perform(mockRequest)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON_VALUE));
        // check that exactly one service call is made with respect to one successful request
        Mockito.verify(this.pizzeriaService, Mockito.times(1))
                .registerPerson(Mockito.any(PersonCreateDto.class));
    }

    @Test
    void addPerson_responseBadRequest() throws Exception {
        // input
        PersonCreateDto personCreateDto = new PersonCreateDto();
        personCreateDto.setName("John");
        // mock not needed
        // request
        final MockHttpServletRequestBuilder mockRequestBadToken = MockMvcRequestBuilders
                .post(Constants.Paths.API + Constants.Paths.REG)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("bad_token_name", Constants.AUTH_TOKEN)
                .content(objectAsJson(personCreateDto));
        // response
        mockMvc.perform(mockRequestBadToken)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());        // request
        // request
        final MockHttpServletRequestBuilder mockRequestBadContent = MockMvcRequestBuilders
                .post(Constants.Paths.API + Constants.Paths.REG)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("token", Constants.AUTH_TOKEN)
                .content("{ bad content }");
        // response
        mockMvc.perform(mockRequestBadContent)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        // check that no service call is ever made
        Mockito.verify(this.pizzeriaService, Mockito.times(0))
                .registerPerson(Mockito.any(PersonCreateDto.class));
    }

    @Test
    void addPerson_responseUnAuthorized() throws Exception {
        // input
        PersonCreateDto personCreateDto = new PersonCreateDto();
        personCreateDto.setName("John");
        // mock not needed
        // request
        final MockHttpServletRequestBuilder mockRequestEmptyToken = MockMvcRequestBuilders
                .post(Constants.Paths.API + Constants.Paths.REG)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("token", "")
                .content(objectAsJson(personCreateDto));
        // response
        mockMvc.perform(mockRequestEmptyToken)
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());        // request
        // request
        final MockHttpServletRequestBuilder mockRequestWrongToken = MockMvcRequestBuilders
                .post(Constants.Paths.API + Constants.Paths.REG)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("token", "wrong_token")
                .content(objectAsJson(personCreateDto));
        // response
        mockMvc.perform(mockRequestWrongToken)
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
        // check that no service call is ever made
        Mockito.verify(this.pizzeriaService, Mockito.times(0))
                .registerPerson(Mockito.any(PersonCreateDto.class));
    }

    @Test
    void getAllOrdersForPerson_responseOk() throws Exception {
        // input
        final String id = "id";
        // mock
        Mockito.when(this.pizzeriaService.readAllOrdersForPersonSortedByTime(id))
                .thenReturn(List.of());
        // request
        final MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(Constants.Paths.API + Constants.Paths.ORDERS
                        + "/" + id);
        // response
        mockMvc.perform(mockRequest)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON_VALUE));
        // verify count of service calls
        Mockito.verify(this.pizzeriaService, Mockito.times(1))
                .readAllOrdersForPersonSortedByTime(id);
    }

    @Test
    void testCreateOrder_responseCreated() throws Exception {
        // input
        Person p = buildPerson(10L, "Axel");
        final OrderCreateDto requestOrderDto = new OrderCreateDto(
                Pizza.Crust.THIN, Pizza.Flavour.QUARTTRO_FORMAGGI,
                Pizza.Size.M, 4444, p.getId());
        // mock
        OrderDto createdOrderDto = buildOrderDto(15L, requestOrderDto);
        Mockito.when(this.pizzeriaService.createOrder(requestOrderDto))
                .thenReturn(createdOrderDto);
        // request
        final MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post(Constants.Paths.API + Constants.Paths.ORDERS)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("token", Constants.AUTH_TOKEN)
                .content(objectAsJson(requestOrderDto));
        // response
        mockMvc.perform(mockRequest)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON_VALUE));
        // check that exactly one service call is made with respect to one successful request
        Mockito.verify(this.pizzeriaService, Mockito.times(1))
                .createOrder(Mockito.any(OrderCreateDto.class));
    }

    @Test
    void testCreateOrder_responseUnAuthorized() throws Exception {
        // input
        Person p = new Person("Axel");
        p.setId(10L);
        final OrderCreateDto requestOrderDto = new OrderCreateDto(
                Pizza.Crust.THIN, Pizza.Flavour.QUARTTRO_FORMAGGI,
                Pizza.Size.M, 4444, p.getId());
        // mock not needed
        // request
        final MockHttpServletRequestBuilder mockRequestEmptyToken = MockMvcRequestBuilders
                .post(Constants.Paths.API + Constants.Paths.ORDERS)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("token", "")
                .content(objectAsJson(requestOrderDto));
        // response
        mockMvc.perform(mockRequestEmptyToken)
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON_VALUE));
        // request
        final MockHttpServletRequestBuilder mockRequestWrongToken = MockMvcRequestBuilders
                .post(Constants.Paths.API + Constants.Paths.ORDERS)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("token", "wrong_token")
                .content(objectAsJson(requestOrderDto));
        // response
        mockMvc.perform(mockRequestWrongToken)
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON_VALUE));
        // check that no service call is ever made
        Mockito.verify(this.pizzeriaService, Mockito.times(0))
                .createOrder(Mockito.any(OrderCreateDto.class));
    }

    @Test
    void testCreateOrder_responseBadRequest() throws Exception {
        // input
        Person p = new Person("Axel");
        p.setId(10L);
        final OrderCreateDto requestOrderDto = new OrderCreateDto(
                Pizza.Crust.THIN, Pizza.Flavour.QUARTTRO_FORMAGGI,
                Pizza.Size.M, 4444, p.getId());
        // mock not needed
        // request
        final MockHttpServletRequestBuilder mockRequestBadToken = MockMvcRequestBuilders
                .post(Constants.Paths.API + Constants.Paths.ORDERS)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("bad_token_name", Constants.AUTH_TOKEN)
                .content(objectAsJson(requestOrderDto));
        // response
        mockMvc.perform(mockRequestBadToken)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        // request
        final MockHttpServletRequestBuilder mockRequestBadBody = MockMvcRequestBuilders
                .post(Constants.Paths.API + Constants.Paths.ORDERS)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("token", Constants.AUTH_TOKEN)
                .content("{ bad body : incorrect}");
        // response
        mockMvc.perform(mockRequestBadBody)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON_VALUE));
        // check that no service call is ever made
        Mockito.verify(this.pizzeriaService, Mockito.times(0))
                .createOrder(Mockito.any(OrderCreateDto.class));
    }

    @Test
    void deleteOrder_responseOk() throws Exception {
        // input
        final String id = "id";
        // mock
        Mockito.doNothing().when(this.pizzeriaService).removeOrder(id);
        // request
        final MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .delete(Constants.Paths.API + Constants.Paths.ORDERS
                        + "/" + id);
        // response
        mockMvc.perform(mockRequest)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON_VALUE));
        // verify count of service calls
        Mockito.verify(this.pizzeriaService, Mockito.times(1))
                .removeOrder(id);
    }

    @Test
    void deleteOrder_responseNotFound() throws Exception {
        // input
        final String id = "id";
        // mock
        Mockito.doThrow(new NoSuchElementException())
                .when(this.pizzeriaService).removeOrder(id);
        // request
        final MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .delete(Constants.Paths.API + Constants.Paths.ORDERS
                        + "/" + id);
        // response
        mockMvc.perform(mockRequest)
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON_VALUE));
        // verify count of service calls
        Mockito.verify(this.pizzeriaService, Mockito.times(1))
                .removeOrder(id);
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
        /*log.info("\n injected objects: \n {} \n\t{} \n\t",
                this.restController, this.pizzeriaService, this.context);*/
        assertNotNull(this.context, "Web context should not be null.");
        assertNotNull(this.restController, "Corresponding class instance should not be null.");
        assertNotNull(this.pizzeriaService, "Mocked service instance should not be null.");

        // ensure that the controller in test is actually injected with the desired service
        this.restController = new RestController(this.pizzeriaService);

    }

    /**
     * returns JSON String from given object, otherwise throws parsing error
     */
    private String objectAsJson(Object obj) throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }

    /**
     * Create a new Person with given name and id.
     */
    private Person buildPerson(@NonNull final long id, @NonNull final String name) {
        Person p = new Person(name);
        p.setId(id);
        return p;
    }

    /**
     * Create a new OrderDto with given id and CreateOrderDto.
     */
    private OrderDto buildOrderDto(@NonNull final Long id,
                                   @NonNull final OrderCreateDto in) {
        OrderDto out = new OrderDto(id,
                Instant.now().truncatedTo(ChronoUnit.MILLIS).toEpochMilli());
        out.setCrust(in.getCrust());
        out.setFlavour(in.getFlavour());
        out.setSize(in.getSize());
        out.setTableNo(in.getTableNo());
        out.setCustomerId(in.getCustomerId());
        return out;
    }

}
