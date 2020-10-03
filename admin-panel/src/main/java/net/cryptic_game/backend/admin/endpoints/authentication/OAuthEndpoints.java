package net.cryptic_game.backend.admin.endpoints.authentication;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.admin.authentication.AuthenticationErrorException;
import net.cryptic_game.backend.admin.authentication.OAuthConfig;
import net.cryptic_game.backend.base.api.annotations.ApiEndpoint;
import net.cryptic_game.backend.base.api.annotations.ApiEndpointCollection;
import net.cryptic_game.backend.base.api.annotations.ApiParameter;
import net.cryptic_game.backend.base.api.data.ApiResponse;
import net.cryptic_game.backend.base.api.data.ApiResponseStatus;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonUtils;
import net.cryptic_game.backend.base.utils.SecurityUtils;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.security.Key;
import java.time.OffsetDateTime;

@RequiredArgsConstructor
@ApiEndpointCollection(id = "authentication/oauth")
public final class OAuthEndpoints {

    private final OAuthConfig config;
    private final Key key;

    @ApiEndpoint(id = "callback")
    public Mono<ApiResponse> callback(@ApiParameter(id = "code") final String code) {
        return HttpClient.create()
                .headers(h -> h.set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                        .set(HttpHeaderNames.ACCEPT, HttpHeaderValues.APPLICATION_JSON))
                .post()
                .uri("https://github.com/login/oauth/access_token")
                .send(Mono.just(Unpooled.wrappedBuffer(JsonBuilder.create("client_id", config.getClientId())
                        .add("client_secret", config.getClientSecret())
                        .add("code", code).build().toString().getBytes())))
                .responseContent()
                .aggregate()
                .asString()
                .map(JsonParser::parseString)
                .map(jsonElement -> JsonUtils.fromJson(jsonElement, JsonObject.class))
                .flatMap(response -> {
                    if (!response.has("access_token")) return Mono.error(new AuthenticationErrorException());
                    return HttpClient.create()
                            .headers(h -> h.set(HttpHeaderNames.AUTHORIZATION, "token " + JsonUtils.fromJson(response.get("access_token"), String.class)))
                            .get()
                            .uri("https://api.github.com/user")
                            .responseContent()
                            .aggregate()
                            .asString();
                })
                .map(JsonParser::parseString)
                .map(jsonElement -> JsonUtils.fromJson(jsonElement, JsonObject.class))
                .flatMap(response -> {
                    if (!response.has("id")) return Mono.error(new AuthenticationErrorException());
                    return Mono.just(new ApiResponse(ApiResponseStatus.OK, JsonBuilder.create("jwt", SecurityUtils.jwt(
                            key,
                            JsonBuilder.create("id", JsonUtils.fromJson(response.get("id"), Long.class))
                                    .add("name", JsonUtils.fromJson(response.get("name"), String.class))
                                    .add("avatar_url", JsonUtils.fromJson(response.get("avatar_url"), String.class))
                                    .add("groups", new String[]{"admin"})
                                    .add("exp", OffsetDateTime.now().plusDays(1))
                                    .build()))));
                })
                .onErrorReturn(AuthenticationErrorException.class, new ApiResponse(ApiResponseStatus.INTERNAL_SERVER_ERROR, "BAD_GITHUB_RESPONSE"));
    }
}
