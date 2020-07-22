package net.cryptic_game.backend.base.api.endpoint;

import com.google.gson.JsonElement;
import com.sun.istack.NotNull;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonSerializable;

@Getter
@ToString
@RequiredArgsConstructor
public enum ApiResponseType implements JsonSerializable {

    // Information responses 1xx

    // Successful responses 2xx
    OK(HttpResponseStatus.OK, false),

    // Redirection messages 3xx

    // Client error responses 4xx
    BAD_REQUEST(HttpResponseStatus.BAD_REQUEST, true),
    UNAUTHORIZED(HttpResponseStatus.UNAUTHORIZED, true),
    FORBIDDEN(HttpResponseStatus.FORBIDDEN, true),
    NOT_FOUND(HttpResponseStatus.NOT_FOUND, true),

    // Server error responses 5xx
    INTERNAL_SERVER_ERROR(HttpResponseStatus.INTERNAL_SERVER_ERROR, true),
    NOT_IMPLEMENTED(HttpResponseStatus.NOT_IMPLEMENTED, true),
    BAD_GATEWAY(HttpResponseStatus.BAD_REQUEST, true),
    GATEWAY_TIMEOUT(HttpResponseStatus.GATEWAY_TIMEOUT, true),

    // Self made
    ALREADY_EXISTS(905, "Already Exists", true);

    private final int code;
    private final String displayName;
    private final boolean error;

    ApiResponseType(final HttpResponseStatus status, final boolean error) {
        this.code = status.code();
        this.displayName = status.reasonPhrase();
        this.error = error;
    }

    @NotNull
    @Override
    public JsonElement serialize() {
        return JsonBuilder.create("code", this.code)
                .add("name", this.name())
                .add("error", this.error)
                .build();
    }
}
