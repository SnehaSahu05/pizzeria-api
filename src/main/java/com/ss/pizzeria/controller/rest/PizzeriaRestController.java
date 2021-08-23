package com.ss.pizzeria.controller.rest;

import com.ss.pizzeria.controller.rest.dto.AccessTokenDto;
import com.ss.pizzeria.controller.rest.dto.UserAuthDto;
import com.ss.pizzeria.controller.rest.dto.OrderDto;
import com.ss.pizzeria.controller.rest.dto.ErrorResponseMessageDto;
import com.ss.pizzeria.service.PizzeriaService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
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
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class PizzeriaRestController {

    /* Tags */
    private static final String TAG_AUTH = "Auth";
    private static final String TAG_ORDERS = "Orders";

    /* url for order */
    private static final String ORDERS = "/orders";

    /* inject service */
    @NonNull
    private final PizzeriaService myService;

    public PizzeriaRestController(@NonNull final PizzeriaService myService) {
        this.myService = myService;
    }

    @PostMapping(path = "/auth")
    @Description(value = "Create an access token for user to login")
    @Operation(operationId = "auth.login", summary = "Create an access token", tags = {TAG_AUTH})
    @RequestBody(required = true, description = "token to create",
            content = @Content(schema = @Schema(implementation = UserAuthDto.class))
    ) // alternative to using in='body' with @Parameter
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created token",
                    content = @Content(schema = @Schema(implementation = AccessTokenDto.class))),
            @ApiResponse(responseCode = "401", description = "Failed to create token",
                    content = @Content(schema = @Schema(implementation = ErrorResponseMessageDto.class)))
    })
    @ResponseBody
    public ResponseEntity<Object> authenticateApi(
            @org.springframework.web.bind.annotation.RequestBody
            @Valid final UserAuthDto auth) {
        final AccessTokenDto accessToken = this.myService.getAccessToken(auth);
        if (accessToken.getAccessToken().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseMessageDto("Bad username or password"));
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body(accessToken);
        }
    }

    @GetMapping(path = ORDERS)
    @Description(value = "Read the entire set of orders, sorted by timestamp.")
    @Operation(operationId = "orders.read_all", summary = "Return list of Pizza orders", tags = {TAG_ORDERS})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully read orders set operation")
    })
    @ResponseBody
    public ResponseEntity<List<OrderDto>> getOrders() {
        log.info("...REST request received");
        List<OrderDto> list = this.myService.readAllOrdersSortedByTime();
        log.info("...REST dto list: {}", list);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

}
