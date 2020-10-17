package net.cryptic_game.backend.base.api.exception;

public class ApiParameterException extends ApiException {

    public ApiParameterException(final String message) {
        super(message);
    }

    public ApiParameterException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
