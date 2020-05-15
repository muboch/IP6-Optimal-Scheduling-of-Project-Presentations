package ch.fhnw.ip6.ospp.controller;

import ch.fhnw.ip6.ospp.service.FachlicheException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(FachlicheException.class)
    protected ResponseEntity<Object> handleFachlicheException(FachlicheException ex) {
        return new ResponseEntity<>(ex, HttpStatus.BAD_REQUEST);
    }


}