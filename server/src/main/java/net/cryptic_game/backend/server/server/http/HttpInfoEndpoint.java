package net.cryptic_game.backend.server.server.http;

import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.server.api.handler.websocket.WebsocketApiInitializer;
import net.cryptic_game.backend.server.redis.OnlineUsersCounter;

@RequiredArgsConstructor
@ApiEndpointCollection(id = "info", type = ApiType.REST)
public final class HttpInfoEndpoint {

    private final WebsocketApiInitializer websocketApiInitializer;
    private final OnlineUsersCounter onlineUsersCounter;

    @ApiEndpoint(id = "online")
    public ApiResponse online() {
        return new ApiResponse(HttpResponseStatus.OK, JsonBuilder.create("online", onlineUsersCounter.getOnlineCount())
                .add("online_instance", websocketApiInitializer.getContexts().size()));
    }

    @ApiEndpoint(id = "leaderboard")
    public ApiResponse leaderboard() {
        return new ApiResponse(HttpResponseStatus.NOT_IMPLEMENTED);
    }
}
