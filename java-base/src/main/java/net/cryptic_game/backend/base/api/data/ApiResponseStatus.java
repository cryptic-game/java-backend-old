package net.cryptic_game.backend.base.api.data;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public enum ApiResponseStatus {

    // Information responses 1xx

    // Successful responses 2xx
    OK(HttpResponseStatus.OK, false),

    // Redirection messages 3xx

    // Client error responses 4xx
    BAD_REQUEST(HttpResponseStatus.BAD_REQUEST, true),
    UNAUTHORIZED(HttpResponseStatus.UNAUTHORIZED, true),
    FORBIDDEN(HttpResponseStatus.FORBIDDEN, true),
    NOT_FOUND(HttpResponseStatus.NOT_FOUND, true),
    CONFLICT(HttpResponseStatus.CONFLICT, true),

    // Server error responses 5xx
    INTERNAL_SERVER_ERROR(HttpResponseStatus.INTERNAL_SERVER_ERROR, true),
    NOT_IMPLEMENTED(HttpResponseStatus.NOT_IMPLEMENTED, true),
    BAD_GATEWAY(HttpResponseStatus.BAD_GATEWAY, true),
    SERVICE_UNAVAILABLE(HttpResponseStatus.SERVICE_UNAVAILABLE, true);

    // Self made 9xx

    private final int code;
    @NotNull
    private final String name;
    private final boolean error;

    ApiResponseStatus(@NotNull final HttpResponseStatus status, final boolean error) {
        this(status.code(), status.reasonPhrase(), error);
    }

    public static ApiResponseStatus getByCode(final int code) {
        for (ApiResponseStatus value : ApiResponseStatus.values()) {
            if (value.getCode() == code) return value;
        }
        return null;
    }

    public String toString() {
        return code + " " + name;
    }
}
