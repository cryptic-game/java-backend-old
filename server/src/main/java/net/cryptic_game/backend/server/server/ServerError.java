package net.cryptic_game.backend.server.server;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.interfaces.Error;

import static net.cryptic_game.backend.base.utils.JsonBuilder.error;

public enum ServerError implements Error {

    PERMISSION_DENIED("permissions denied"),
    UNSUPPORTED_FORMAT("unsupported format"),
    INVALID_AUTHORIZATION("invalid authorization"),
    MISSING_ACTION("missing action"),
    UNKNOWN_ACTION("unknown action"),
    USERNAME_ALREADY_EXISTS("username already exists"),
    INVALID_EMAIL("invalid email"),
    INVALID_PASSWORD("invalid password"),
    INVALID_TOKEN("invalid token"),
    MISSING_PARAMETERS("missing parameters"),
    UNEXPECTED_ERROR("unexpected error"),
    UNKNOWN_MICROSERVICE("unknown microservice"),
    NOT_FOUND("not found"),
    UNKNOWN_SETTING("unknown setting"),
    UNSUPPORTED_PARAMETER_SIZE("unsupported parameter size"),
    JSON_SYNTAX_ERROR("json syntax error");

    private String message;
    private JsonObject response;

    ServerError(String message) {
        this.response = error(message);
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public String toString() {
        return message;
    }

    @Override
    public JsonObject getResponse() {
        return null;
    }
}
