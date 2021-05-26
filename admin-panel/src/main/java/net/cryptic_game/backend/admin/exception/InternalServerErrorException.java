package net.cryptic_game.backend.admin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InternalServerErrorException extends ResponseStatusException {

    public InternalServerErrorException(final String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

}
