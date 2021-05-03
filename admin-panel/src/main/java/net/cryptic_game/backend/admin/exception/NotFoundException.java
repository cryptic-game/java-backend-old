package net.cryptic_game.backend.admin.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

public class NotFoundException extends ResponseStatusException {

    @Getter
    private final String target;

    public NotFoundException(final String target, final String message) {
        super(HttpStatus.NOT_FOUND, message);
        this.target = target;
    }

    public NotFoundException(final UUID target, final String message) {
        super(HttpStatus.NOT_FOUND, message);
        this.target = target.toString();
    }

    public NotFoundException(final String target, final String message, final Throwable cause) {
        super(HttpStatus.NOT_FOUND, message, cause);
        this.target = target;
    }
}
