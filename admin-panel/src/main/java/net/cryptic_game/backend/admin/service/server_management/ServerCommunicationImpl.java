package net.cryptic_game.backend.admin.service.server_management;

import com.nimbusds.jose.shaded.json.JSONObject;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaderNames;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.admin.Config;
import net.cryptic_game.backend.admin.exception.InternalServerErrorException;
import net.cryptic_game.backend.admin.exception.NotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class ServerCommunicationImpl implements ServerCommunication {

    private final Config config;
    private HttpClient httpClient;

    public Mono<String> responseFromServer(final String endpoint, final JSONObject data) {
        if (this.httpClient == null) {
            this.httpClient = HttpClient.create()
                    .baseUrl(this.config.getServerUrl())
                    .headers(h -> h.set(HttpHeaderNames.AUTHORIZATION, this.config.getApiToken()));
        }
        return this.httpClient.post()
                .uri(endpoint)
                .send(Mono.just(Unpooled.copiedBuffer(data.toJSONString(), StandardCharsets.ISO_8859_1)))
                .responseSingle((response, body) -> {
                    if (!Integer.toString(response.status().code()).startsWith("2")) {
                        if (response.status().code() == 404) {
                            throw new NotFoundException("", "");
                        }
                        throw new InternalServerErrorException("Got status code " + response.status().codeAsText() + " from the server");
                    }
                    return body.asString();
                })
                .doOnError(err -> {
                    if (err instanceof ResponseStatusException) {
                        throw (ResponseStatusException) err;
                    } else {
                        throw new InternalServerErrorException(err.getMessage());
                    }
                });

    }
}
