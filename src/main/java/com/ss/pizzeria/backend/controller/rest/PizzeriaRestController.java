package com.ss.pizzeria.backend.controller.rest;

import com.ss.pizzeria.backend.Constants;
import com.ss.pizzeria.backend.controller.rest.dto.*;
import com.ss.pizzeria.backend.service.PizzeriaService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Rest Controller to communicate with backend service.
 *
 * @author Sneha
 */
@Slf4j
@RestController
@OpenAPIDefinition(
        info = @Info(
                description = "A REST-ful API for pizza ordering.",
                title = "Order Pizza API",
                version = "1.1"
        ))
@RequestMapping(
        path = "/api",
        //consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class PizzeriaRestController {

    /* Tags */
    private static final String TAG_ORDERS = Constants.Tags.ORDERS;

    /* url for order */
    private static final String ORDERS = Constants.Paths.ORDERS;
    private static final String PARAM_ORDER_ID = Constants.Paths.PARAM_ORDER_ID;

    /* inject service */
    @NonNull
    private final PizzeriaService myService;

    public PizzeriaRestController(@NonNull final PizzeriaService myService) {
        this.myService = myService;
    }

    @PostMapping(path = Constants.Paths.AUTH)
    @Description(value = "Create an access token for user to login")
    @Operation(operationId = "auth.login", summary = "Create an access token", tags = {Constants.Tags.AUTH})
    @RequestBody(required = true, description = "token to create",
            content = @Content(schema = @Schema(implementation = UserAuthDto.class))
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created token",
                    content = @Content(schema = @Schema(implementation = AccessTokenDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid Request Body"),
            @ApiResponse(responseCode = "401", description = "Failed to create token",
                    content = @Content(schema = @Schema(implementation = ResponseMessageDto.class)))
    })
    @ResponseBody
    public ResponseEntity<Object> authenticateApi(
            @org.springframework.web.bind.annotation.RequestBody
            @Valid
            @NonNull final UserAuthDto auth) {
        final AccessTokenDto accessToken = this.myService.getAccessToken(auth);
        if (accessToken.getAccessToken().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessageDto("Bad username or password"));
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body(accessToken);
        }
    }

    // when using default consumes at class level, cannot process -- empty media content '' -- for GET
    @GetMapping(path = ORDERS)
    @Description(value = "Read the entire set of orders, sorted by timestamp.")
    @Operation(operationId = "orders.read_all", summary = "Return list of Pizza orders", tags = {TAG_ORDERS})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched list of Orders",
                    content = @Content(schema = @Schema(implementation = OrderDto.class)))
    })
    @ResponseBody
    public ResponseEntity<List<OrderDto>> getOrders() {
        List<OrderDto> list = this.myService.readAllOrdersSortedByTime();
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    @PostMapping(path = ORDERS)
    @Description(value = "Create a new person")
    @Operation(operationId = "orders.create", summary = "Create an order", tags = {TAG_ORDERS})
    @Parameters(value = {
            @Parameter(name = "token", description = "Token for authentication", required = true, in = ParameterIn.HEADER)
    })
    @RequestBody(required = true, description = "Order to create",
            content = @Content(schema = @Schema(implementation = OrderCeateDto.class))
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created order",
                    content = @Content(schema = @Schema(implementation = OrderDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid Request Body"),
            @ApiResponse(responseCode = "401", description = "Not authorized",
                    content = @Content(schema = @Schema(implementation = ResponseMessageDto.class)))
    })
    @ResponseBody
    public ResponseEntity<Object> createOrders(@RequestHeader(name = "token", defaultValue = Constants.AUTH_TOKEN)
                                               @NonNull final String accessToken,
                                               @org.springframework.web.bind.annotation.RequestBody
                                               @Valid
                                               @NonNull final OrderCeateDto order) {
        // TODO: 1, 2
        if (accessToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessageDto("Missing Authorization Header"));
        }
        if (!accessToken.equals(Constants.AUTH_TOKEN)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessageDto("Incorrect Authorization Header"));
        }
        final OrderDto created = this.myService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping(path = ORDERS + "/{" + PARAM_ORDER_ID + "}")
    @Description(value = "Delete an order")
    @Operation(operationId = "orders.delete", summary = "Delete an order from the orders list", tags = {TAG_ORDERS})
    @Parameters(value = {
            @Parameter(name = PARAM_ORDER_ID, description = "ID of order to delete", required = true, in = ParameterIn.PATH)
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted an order",
                    content = @Content(schema = @Schema(implementation = ResponseMessageDto.class))),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(schema = @Schema(implementation = ResponseMessageDto.class)))
    })
    @ResponseBody
    public ResponseEntity<Object> deleteOrder(@PathVariable(name = PARAM_ORDER_ID)
                                              @Parameter(name = PARAM_ORDER_ID)
                                              @NonNull final String id) {
        try {
            this.myService.removeOrder(Long.parseLong(id, 10));
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseMessageDto("Successfully deleted order #" + id));
        } catch (NoSuchElementException e) {
            // TODO: 1-exception handling
            // throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e.getCause());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessageDto(e.getMessage()));
        }
    }
}
