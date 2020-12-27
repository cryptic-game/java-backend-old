package net.cryptic_game.backend.base.api.handler.websocket;

import lombok.AccessLevel;
import lombok.Getter;
import reactor.netty.http.websocket.WebsocketInbound;
import reactor.netty.http.websocket.WebsocketOutbound;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class WebsocketApiContext {

    @Getter(AccessLevel.NONE)
    private final Map<Class<?>, Object> values;
    @Getter
    private final WebsocketInbound inbound;
    @Getter
    private final WebsocketOutbound outbound;

    public WebsocketApiContext(final WebsocketInbound inbound, final WebsocketOutbound outbound) {
        this.values = new HashMap<>();
        this.inbound = inbound;
        this.outbound = outbound;
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
        final WebsocketApiContext that = (WebsocketApiContext) o;
        return Objects.equals(this.inbound, that.inbound)
                && Objects.equals(this.outbound, that.outbound);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.inbound, this.outbound);
    }
}
