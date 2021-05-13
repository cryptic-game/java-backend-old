package net.cryptic_game.backend.server.authentication;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.base.json.JsonBuilder;
import net.cryptic_game.backend.base.json.JsonUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Component
@RequiredArgsConstructor
public class OAuth2Provider {

    private final OAuth2Config config;

    public Mono<String> discordUserId(final String code) {
        return HttpClient.create()
                .headers(h -> h.set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED)
                        .set(HttpHeaderNames.ACCEPT, HttpHeaderValues.APPLICATION_JSON))
                .post()
                .uri("https://discord.com/api/oauth2/token")
                .sendForm((httpClientRequest, httpClientForm) -> httpClientForm
                        .encoding(HttpPostRequestEncoder.EncoderMode.HTML5)
                        .attr("client_id", this.config.getDiscordClientId())
                        .attr("client_secret", this.config.getDiscordClientSecret())
                        .attr("grant_type", "authorization_code")
                        .attr("redirect_uri", this.config.getDiscordRedirectUri())
                        .attr("code", code)
                )
                .responseContent()
                .aggregate()
                .asString()
                .map(JsonParser::parseString)
                .map(jsonElement -> JsonUtils.fromJson(jsonElement, JsonObject.class))
                .flatMap(response -> {
                    if (!response.has("access_token")) return Mono.error(new AuthenticationErrorException("No Access Token provided by Discord"));
                    return HttpClient.create()
                            .headers(h -> h.set(HttpHeaderNames.AUTHORIZATION, "Bearer " + JsonUtils.fromJson(response.get("access_token"), String.class)))
                            .get()
                            .uri("https://discord.com/api/users/@me")
                            .responseContent()
                            .aggregate()
                            .asString();
                })
                .map(JsonParser::parseString)
                .map(jsonElement -> JsonUtils.fromJson(jsonElement, JsonObject.class))
                .flatMap(response -> {
                    if (!response.has("id")) return Mono.error(new AuthenticationErrorException("No User Id Provided by Discord"));
                    return Mono.just(JsonUtils.fromJson(response.get("id"), String.class));
                });
    }

    public Mono<String> githubUserId(final String code) {
        return HttpClient.create()
                .headers(h -> h.set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                        .set(HttpHeaderNames.ACCEPT, HttpHeaderValues.APPLICATION_JSON))
                .post()
                .uri("https://github.com/login/oauth/access_token")
                .send(Mono.just(Unpooled.wrappedBuffer(JsonBuilder.create("client_id", this.config.getGithubClientId())
                        .add("client_secret", this.config.getGithubClientSecret())
                        .add("code", code).build().toString().getBytes())))
                .responseContent()
                .aggregate()
                .asString()
                .map(JsonParser::parseString)
                .map(jsonElement -> JsonUtils.fromJson(jsonElement, JsonObject.class))
                .flatMap(response -> {
                    if (!response.has("access_token")) return Mono.error(new AuthenticationErrorException("No Access Token provided by GitHub"));
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
                    if (!response.has("id")) return Mono.error(new AuthenticationErrorException("No User Id Provided by GitHub"));
                    return Mono.just(JsonUtils.fromJson(response.get("id"), String.class));
                });
    }
}
