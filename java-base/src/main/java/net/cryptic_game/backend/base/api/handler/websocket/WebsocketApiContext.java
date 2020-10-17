package net.cryptic_game.backend.base.api.handler.websocket;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import reactor.netty.http.websocket.WebsocketOutbound;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Data
public final class WebsocketApiContext {

    @Getter(AccessLevel.NONE)
    private final Map<Class<?>, Object> values;
    @Getter
    private final WebsocketOutbound outbound;

    public WebsocketApiContext(final WebsocketOutbound outbound) {
        this.values = new HashMap<>();
        this.outbound = outbound;
    }

    public <T> Optional<T> get(@NotNull final Class<T> clazz) {
        return Optional.ofNullable(this.values.get(clazz)).map(clazz::cast);
    }

    public <T> Optional<T> remove(@NotNull final Class<T> clazz) {
        return Optional.ofNullable(this.values.remove(clazz)).map(clazz::cast);
    }

    public void set(@NotNull final Object value) {
        this.values.put(value.getClass(), value);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final WebsocketApiContext that = (WebsocketApiContext) o;
        return Objects.equals(getOutbound(), that.getOutbound());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOutbound());
    }
}
