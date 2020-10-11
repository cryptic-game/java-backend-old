package net.cryptic_game.backend.base.daemon;

import lombok.Data;
import reactor.netty.http.client.HttpClient;

@Data
public final class Daemon {

    private final String name;
    private final HttpClient httpClient;
}
