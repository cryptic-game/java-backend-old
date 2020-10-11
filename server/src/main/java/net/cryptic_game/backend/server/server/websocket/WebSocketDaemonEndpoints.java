package net.cryptic_game.backend.server.server.websocket;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaderNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.api.annotations.ApiParameter;
import net.cryptic_game.backend.base.api.data.ApiEndpointData;
import net.cryptic_game.backend.base.api.data.ApiParameterType;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiResponseStatus;
import net.cryptic_game.backend.base.daemon.Daemon;
import net.cryptic_game.backend.base.daemon.DaemonEndpointData;
import net.cryptic_game.backend.base.json.JsonBuilder;
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
    private static final String AUTHORIZATION_HEADER = HttpHeaderNames.AUTHORIZATION.toString();
    private final String apiToken;

    public Mono<ApiResponse> send(/*@ApiParameter(id = "client", type = ApiParameterType.CLIENT) final ApiClient client,*/
            @ApiParameter(id = "tag", type = ApiParameterType.TAG) final String tag,
            @ApiParameter(id = "endpoint", type = ApiParameterType.ENDPOINT) final ApiEndpointData endpoint,
            @ApiParameter(id = "data", type = ApiParameterType.DATA) final JsonObject data) {

//        TODO
//        final Session session = client.get(Session.class);
//
//        if (session == null) {
//            return new ApiResponse(ApiResponseStatus.UNAUTHORIZED, "INVALID_SESSION");
//        }

        if (!(endpoint instanceof DaemonEndpointData)) {
            log.warn("Method {}.send(...) was executed with wrong endpoint {}.", WebSocketDaemonEndpoints.class.getName(), endpoint.getId());
            return Mono.just(new ApiResponse(ApiResponseStatus.INTERNAL_SERVER_ERROR));
        }

        final DaemonEndpointData daemonEndpoint = (DaemonEndpointData) endpoint;
        final Daemon daemon = daemonEndpoint.getDaemon();

        final JsonElement body = JsonBuilder.create(data)
//                    .add("user_id", session.getUserId())
                .build();

        System.out.println(endpoint.getId());

        final HttpClient.ResponseReceiver<?> daemonResponse = daemon.getHttpClient().post()
                .uri("/" + endpoint.getId())
                .send(Mono.just(Unpooled.copiedBuffer(body.toString(), CHARSET)));

        return daemonResponse.responseSingle((response, byteBufMono) -> byteBufMono.asString(CHARSET)
                .map(content -> {
                    final Optional<ApiResponseStatus> responseCode = Optional.ofNullable(ApiResponseStatus.getByCode(response.status().code()));
                    if (responseCode.isEmpty()) {
                        log.warn("Unknown status code from daemon {} at endpoint {}: {}.", daemon.getName(), endpoint.getId(), response.status().toString());
                        return new ApiResponse(ApiResponseStatus.BAD_GATEWAY);
                    }

                    return new ApiResponse(responseCode.get(), JsonParser.parseString(content));
                })
                .onErrorResume(JsonParseException.class, cause -> {
                    log.warn("Wrong json syntax from daemon {} at endpoint {}.", daemon.getName(), endpoint.getId());
                    return Mono.just(new ApiResponse(ApiResponseStatus.BAD_GATEWAY));
                })
                .onErrorResume(cause -> cause.getMessage().contains("refused"), cause -> {
                    log.warn("Unable to connect to daemon {}: {}", daemon.getName(), cause.getMessage());
                    return Mono.just(new ApiResponse(ApiResponseStatus.BAD_GATEWAY));
                })
                .onErrorResume(cause -> {
                    log.warn("Error while receiving daemon {}'s (endpoint {}) response.", daemon.getName(), endpoint.getId(), cause);
                    return Mono.just(new ApiResponse(ApiResponseStatus.INTERNAL_SERVER_ERROR));
                })
        );
    }
}
