package net.cryptic_game.backend.base.api.handler.rest;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import reactor.netty.http.server.HttpServerRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Data
public final class RestApiContext {

    @Getter(AccessLevel.NONE)
    private final Map<Class<?>, Object> values;
    @Getter
    private final HttpServerRequest httpRequest;

    public RestApiContext(final HttpServerRequest httpRequest) {
        this.values = new HashMap<>();
        this.httpRequest = httpRequest;
    }

    public <T> Optional<T> get(final Class<T> clazz) {
        return Optional.ofNullable(this.values.get(clazz)).map(clazz::cast);
    }

    public <T> Optional<T> remove(final Class<T> clazz) {
        return Optional.ofNullable(this.values.remove(clazz)).map(clazz::cast);
    }

    public void set(final Object value) {
        this.values.put(value.getClass(), value);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RestApiContext that = (RestApiContext) o;
        return Objects.equals(this.httpRequest, that.httpRequest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.httpRequest);
    }
}
