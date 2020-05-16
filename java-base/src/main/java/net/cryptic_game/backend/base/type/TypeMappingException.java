package net.cryptic_game.backend.base.type;

public class TypeMappingException extends IllegalArgumentException {

    public TypeMappingException(final String message) {
        super(message);
    }

    public TypeMappingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
