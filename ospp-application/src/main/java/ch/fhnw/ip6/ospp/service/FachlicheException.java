package ch.fhnw.ip6.ospp.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.stream.Collectors;


@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class FachlicheException extends RuntimeException {
    public FachlicheException(String message) {
        super(message);
    }

    public FachlicheException(String message, Object... params) {
    }
}
