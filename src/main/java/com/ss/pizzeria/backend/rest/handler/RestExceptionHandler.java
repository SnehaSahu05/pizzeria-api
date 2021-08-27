package com.ss.pizzeria.backend.rest.handler;

import com.ss.pizzeria.backend.rest.dto.ResponseMessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.NoSuchElementException;

/**
 * Rest Exception Handler for all Exceptions
 * @author Sneha
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.info(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessageDto(status.getReasonPhrase()+" >> "+ex.getMessage()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    protected ResponseEntity<ResponseMessageDto> handleNoSuchElementException (NoSuchElementException ex) {
        log.info(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessageDto(ex.getMessage()));
    }
}
