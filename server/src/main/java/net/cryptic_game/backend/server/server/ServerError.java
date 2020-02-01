package net.cryptic_game.backend.server.server;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.interfaces.Error;

import static net.cryptic_game.backend.base.utils.JsonBuilder.error;

public enum ServerError implements Error {

    PERMISSION_DENIED("permissions_denied"),
    UNSUPPORTED_FORMAT("unsupported_format"),
    INVALID_AUTHORIZATION("invalid_authorization"),
    MISSING_ACTION("missing_action"),
    UNKNOWN_ACTION("unknown_action"),
    USERNAME_ALREADY_EXISTS("username_already_exists"),
    INVALID_EMAIL("invalid_email"),
    INVALID_PASSWORD("invalid_password"),
    INVALID_TOKEN("invalid_token"),
    MISSING_PARAMETERS("missing_parameters"),
    UNEXPECTED_ERROR("unexpected_error"),
    UNKNOWN_MICROSERVICE("unknown_microservice"),
    NOT_FOUND("not_found"),
    UNKNOWN_SETTING("unknown_setting"),
    UNSUPPORTED_PARAMETER_SIZE("unsupported_parameter_size"),
    JSON_SYNTAX_ERROR("json_syntax_error");

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
