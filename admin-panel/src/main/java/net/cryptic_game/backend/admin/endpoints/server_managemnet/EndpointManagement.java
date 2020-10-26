package net.cryptic_game.backend.admin.endpoints.server_managemnet;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.admin.authentication.AdminPanelAuthenticator;
import net.cryptic_game.backend.admin.authentication.Permission;
import net.cryptic_game.backend.data.sql.repositories.server_management.DisabledEndpointRepository;
import net.cryptic_game.backend.base.BaseConfig;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.annotations.ApiParameter;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiType;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonUtils;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@ApiEndpointCollection(id = "endpoints", description = "manage endpoints", type = ApiType.REST, authenticator = AdminPanelAuthenticator.class)
public final class EndpointManagement {

    private static final int SERVER_PORT = 8086;
    private final BaseConfig baseConfig;
    private final DisabledEndpointRepository disabledEndpointRepository;
    private HttpClient httpClient = null;

    @ApiEndpoint(id = "all", authentication = Permission.INTERNAL)
    public Mono<ApiResponse> getAllEndpoints() {
        return responseFromServer(String.format("http://localhost:%d/api/admin_panel/endpoints", SERVER_PORT), new JsonObject());
    }

    @ApiEndpoint(id = "enable", authentication = Permission.SERVER_MANAGEMENT)
    public Mono<ApiResponse> enableEndpoint(@ApiParameter(id = "name") final String name) {
        return getAllEndpoints().flatMap(apiResponse -> {
            if (!disabledEndpointRepository.existsById(name)) {
                return Mono.just(new ApiResponse(HttpResponseStatus.BAD_REQUEST, "ENDPOINT_NOT_DISABLED"));
            }
            if (((JsonObject) apiResponse.getJson()).get(name) != null) {
                disabledEndpointRepository.deleteById(name);
                return responseFromServer(String.format("http://localhost:%d/api/admin_panel/enable", SERVER_PORT), JsonBuilder.create("name", name).build());
            } else {
                return Mono.just(new ApiResponse(HttpResponseStatus.BAD_REQUEST, "ENDPOINT_DOES_NOT_EXIST"));
            }
        });
    }

    @ApiEndpoint(id = "disable", authentication = Permission.SERVER_MANAGEMENT)
    public Mono<ApiResponse> disableEndpoint(@ApiParameter(id = "name") final String name) {
        return getAllEndpoints().flatMap(apiResponse -> {
            if (disabledEndpointRepository.existsById(name)) {
                return Mono.just(new ApiResponse(HttpResponseStatus.BAD_REQUEST, "ENDPOINT_ALREADY_DISABLED"));
            }
            if (((JsonObject) apiResponse.getJson()).get(name) != null) {
                disabledEndpointRepository.create(name);
                return responseFromServer(String.format("http://localhost:%d/api/admin_panel/disable", SERVER_PORT), JsonBuilder.create("name", name).build());
            } else {
                return Mono.just(new ApiResponse(HttpResponseStatus.BAD_REQUEST, "ENDPOINT_DOES_NOT_EXIST"));
            }
        });
    }

    private Mono<ApiResponse> responseFromServer(final String endpoint, final JsonObject data) {
        if (httpClient == null) httpClient = HttpClient.create().headers(h -> h.set(HttpHeaderNames.AUTHORIZATION, baseConfig.getApiToken()));
        return Mono.from(httpClient.post()
                .uri(endpoint)
                .send(Mono.just(Unpooled.copiedBuffer(data.toString(), StandardCharsets.UTF_8)))
                .responseSingle((client, receiver) -> {
                    return receiver.asString().
                            map(JsonParser::parseString)
                            .map(jsonElement -> JsonUtils.fromJson(jsonElement, JsonObject.class))
                            .flatMap(content ->
                                    Mono.just(new ApiResponse(client.status(), content)));
                }));
    }
}
