package com.ss.pizzeria.backend.rest.controller;

import ch.qos.logback.core.util.TimeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ss.pizzeria.backend.Constants;
import com.ss.pizzeria.backend.data.model.Person;
import com.ss.pizzeria.backend.data.model.Pizza;
import com.ss.pizzeria.backend.rest.dto.AccessTokenDto;
import com.ss.pizzeria.backend.rest.dto.OrderCreateDto;
import com.ss.pizzeria.backend.rest.dto.OrderDto;
import com.ss.pizzeria.backend.rest.dto.UserAuthDto;
import com.ss.pizzeria.backend.service.PizzeriaService;
import io.swagger.v3.core.util.Json;
import lombok.extern.slf4j.Slf4j;
import org.h2.util.DateTimeUtils;
import org.junit.Ignore;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit Tests for Rest API requests to Controller
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
    void testCreateOrder() throws Exception {
        // input
        Person p = new Person("Axel");
        p.setId(10L);
        final OrderCreateDto requestOrderDto = new OrderCreateDto(
                Pizza.Crust.THIN,Pizza.Flavour.QUARTTRO_FORMAGGI,
                Pizza.Size.M,4444,p.getId());
        // mock
        OrderDto createdOrderDto = new OrderDto(15L,
                Instant.now().truncatedTo(ChronoUnit.MILLIS).toEpochMilli());
        createdOrderDto.setCrust(requestOrderDto.getCrust());
        createdOrderDto.setFlavour(requestOrderDto.getFlavour());
        createdOrderDto.setSize(requestOrderDto.getSize());
        createdOrderDto.setTableNo(requestOrderDto.getTableNo());
        createdOrderDto.setCustomerId(requestOrderDto.getCustomerId());

        Mockito.when(this.pizzeriaService.createOrder(requestOrderDto))
                .thenReturn(createdOrderDto);

        /* CASE: CREATED */
        // request 1
        final MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .post(Constants.Paths.API + Constants.Paths.ORDERS)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("token", Constants.AUTH_TOKEN)
                .content(objectAsJson(requestOrderDto));
        // response 1
        mockMvc.perform(mockRequest)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON_VALUE));

        /* CASE: UNAUTHORIZED */
        // request 2
        final MockHttpServletRequestBuilder mockRequestTwo = MockMvcRequestBuilders
                .post(Constants.Paths.API + Constants.Paths.ORDERS)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("token", "")
                .content(objectAsJson(requestOrderDto));
        // response 2
        mockMvc.perform(mockRequestTwo)
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON_VALUE));

        /* CASE: BAD REQUEST */
        // request 3
        final MockHttpServletRequestBuilder mockRequestThree = MockMvcRequestBuilders
                .post(Constants.Paths.API + Constants.Paths.ORDERS)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("wrong_token_name", Constants.AUTH_TOKEN)
                .content(objectAsJson(requestOrderDto));
        // response 3
        mockMvc.perform(mockRequestThree)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        // request 4
        final MockHttpServletRequestBuilder mockRequestFour = MockMvcRequestBuilders
                .post(Constants.Paths.API + Constants.Paths.ORDERS)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("token", Constants.AUTH_TOKEN)
                .content("{ wrong body : wrong}");
        // response 4
        mockMvc.perform(mockRequestFour)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON_VALUE));

        // check that even though there are four http requests only one actually proceeds to service call
        Mockito.verify(this.pizzeriaService, Mockito.times(1))
                .createOrder(Mockito.any(OrderCreateDto.class));
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

}
