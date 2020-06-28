package net.cryptic_game.backend.base.api.client;

import com.google.gson.JsonObject;
import io.netty.channel.Channel;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode
public final class ApiClient {

    @Getter
    private final Channel channel;
    private final Set<Object> objects;

    public ApiClient(final Channel channel) {
        this.channel = channel;
        this.objects = new HashSet<>();
    }

    public void write(final JsonObject json) {
        this.channel.writeAndFlush(json);
    }

    public void add(final Object object) {
        this.objects.add(object);
    }

    public <T> T get(final Class<? extends T> type) {
        for (final Object object : this.objects) {
            if (object.getClass().equals(type)) return type.cast(object);
        }
        return null;
    }

    public <T> void remove(final Class<? extends T> type) {
        this.objects.removeIf(object -> object.getClass().equals(type));
    }
}
