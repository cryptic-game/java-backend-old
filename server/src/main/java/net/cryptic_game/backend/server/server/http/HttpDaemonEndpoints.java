package net.cryptic_game.backend.server.server.http;

import com.google.gson.JsonElement;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.base.Bootstrap;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.annotations.ApiParameter;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.base.api.handler.websocket.WebsocketApiContext;
import net.cryptic_game.backend.base.api.handler.websocket.WebsocketApiService;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.data.redis.entities.Session;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@ApiEndpointCollection(id = "daemon", description = "Endpoints for the daemon", type = ApiType.REST, authenticator = HttpServerAuthenticator.class)
public final class HttpDaemonEndpoints {

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private final Bootstrap bootstrap;
    private WebsocketApiService websocketApiService = null;

    //TODO Redis implementation
    @ApiEndpoint(id = "notify", description = "Send a notification to all sessions of a user.")
    public Mono<ApiResponse> notify(@ApiParameter(id = "user_id") final UUID userId,
                                    @ApiParameter(id = "topic") final String topic,
                                    @ApiParameter(id = "data") final JsonElement data) {
        if (this.websocketApiService == null) this.websocketApiService = this.bootstrap.getContextHandler().getBean(WebsocketApiService.class);
        final Set<WebsocketApiContext> userContexts = this.websocketApiService.getContexts().stream().filter(context -> {
            final Optional<Session> session = context.get(Session.class);
            return session.isPresent() && session.get().getUserId().equals(userId);
        }).collect(Collectors.toUnmodifiableSet());

        if (userContexts.isEmpty()) return Mono.just(new ApiResponse(HttpResponseStatus.NOT_FOUND, "USER_CLIENT"));

        final String notification = JsonBuilder.create("status",
                JsonBuilder.create("code", 900)
                        .add("name", "Notification"))
                .add("topic", topic)
                .add("data", data).toString();

        return Flux.fromIterable(userContexts)
                .flatMap(context -> context.getOutbound().sendString(Mono.just(notification), CHARSET))
                .then(Mono.just(new ApiResponse(HttpResponseStatus.OK)));
    }
}
