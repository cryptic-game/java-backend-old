package net.cryptic_game.backend.server.server.websocket;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.api.annotations.ApiParameter;
import net.cryptic_game.backend.base.api.data.ApiParameterType;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.daemon.Daemon;
import net.cryptic_game.backend.base.daemon.DaemonEndpointData;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.data.sql.entities.user.User;
import net.cryptic_game.backend.server.api.handler.websocket.WebsocketApiRequest;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * No {@link net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection}!
 */
@Slf4j
@RequiredArgsConstructor
public final class WebSocketDaemonEndpoints {

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    public Mono<ApiResponse> send(@ApiParameter(id = "request", type = ApiParameterType.REQUEST) final WebsocketApiRequest request) {
        final Optional<User> user = request.getContext().get(User.class);

        if (user.isEmpty()) {
            return Mono.just(new ApiResponse(HttpResponseStatus.UNAUTHORIZED, "INVALID_SESSION"));
        }

        if (!(request.getEndpointData() instanceof DaemonEndpointData)) {
            log.warn("Method {}.send(...) was executed with wrong endpoint {}.", WebSocketDaemonEndpoints.class.getName(), request.getEndpoint());
            return Mono.just(new ApiResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR));
        }

        final DaemonEndpointData daemonEndpoint = (DaemonEndpointData) request.getEndpointData();
        final Daemon daemon = daemonEndpoint.getDaemon();

        final JsonElement body = JsonBuilder.create(request.getData())
                .add("user_id", user.get().getId())
                .build();

        final HttpClient.ResponseReceiver<?> daemonResponse = daemon.getHttpClient().post()
                .uri("/" + request.getEndpoint())
                .send(Mono.just(Unpooled.copiedBuffer(body.toString(), CHARSET)));

        return daemonResponse.responseSingle((response, byteBufMono) -> byteBufMono.asString(CHARSET)
                .map(content -> {
                    final Optional<HttpResponseStatus> responseCode = Optional.ofNullable(HttpResponseStatus.valueOf(response.status().code()));
                    if (responseCode.isEmpty()) {
                        log.warn("Unknown status code from daemon {} at endpoint {}: {}.", daemon.getName(), request.getEndpoint(), response.status());
                        return new ApiResponse(HttpResponseStatus.BAD_GATEWAY);
                    }

                    return new ApiResponse(responseCode.get(), JsonParser.parseString(content));
                })
                .onErrorResume(JsonParseException.class, cause -> {
                    log.warn("Wrong json syntax from daemon {} at endpoint {}.", daemon.getName(), request.getEndpoint());
                    return Mono.just(new ApiResponse(HttpResponseStatus.BAD_GATEWAY));
                })
                .retry(2)
                .onErrorResume(cause -> cause.getMessage().contains("refused"), cause -> {
                    log.warn("Unable to connect to daemon {}: {}", daemon.getName(), cause.getMessage());
                    return Mono.just(new ApiResponse(HttpResponseStatus.BAD_GATEWAY));
                })
                .onErrorResume(cause -> {
                    log.warn("Error while receiving daemon {}'s (endpoint {}) response.", daemon.getName(), request.getEndpoint(), cause);
                    return Mono.just(new ApiResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR));
                })
        );
    }
}
