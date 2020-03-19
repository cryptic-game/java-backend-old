package net.cryptic_game.backend.base.netty;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.utils.JsonBuilder;

public enum ResponseType implements net.cryptic_game.backend.base.interfaces.ResponseType {

    // Information responses

    // Successful responses
    OK(200, "OK", false),

    // Redirection messages

    // Client error responses
    BAD_REQUEST(400, "BAD_REQUEST", true),
    UNAUTHORIZED(401, "UNAUTHORIZED", true),
    FORBIDDEN(403, "FORBIDDEN", true),
    NOT_FOUND(404, "NOT_FOUND", true),
    IM_A_TEAPOT(418, "IM_A_TEAPOT", true),

    // Server error responses
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", true),
    NOT_IMPLEMENTED(501, "NOT_IMPLEMENTED", true),

    // Custom
    PUSH(900, "PUSH", false);

    private final int code;
    private final String name;
    private final boolean error;

    ResponseType(final int code, final String name, final boolean error) {
        this.code = code;
        this.name = name;
        this.error = error;
    }

    @Override
    public String toString() {
        return this.code + " " + this.name;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isError() {
        return this.error;
    }

    @Override
    public JsonObject serialize() {
        return JsonBuilder.anJSON()
                .add("code", this.code)
                .add("name", this.name)
                .add("error", this.error)
                .build();
    }
}
