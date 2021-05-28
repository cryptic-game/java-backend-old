package net.cryptic_game.backend.admin.service.server_management;

import com.nimbusds.jose.shaded.json.JSONObject;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.admin.Config;
import net.cryptic_game.backend.admin.exception.InternalServerErrorException;
import net.cryptic_game.backend.admin.exception.NotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ServerCommunicationImpl implements ServerCommunication {

    private final Config config;
    private WebClient webClient;

    public Mono<JSONObject> responseFromServer(final String endpoint, final JSONObject data) {
        if (this.webClient == null) {
            this.webClient = WebClient.builder()
                    .baseUrl(this.config.getServerUrl())
                    .defaultHeader(HttpHeaders.AUTHORIZATION, this.config.getApiToken())
                    .build();
        }
        return this.webClient.post()
                .uri(endpoint)
                .body(Mono.just(data), JSONObject.class)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(JSONObject.class);
                    }
                    if (response.rawStatusCode() == 404) {
                        throw new NotFoundException("", "element not found");
                    }
                    return response.createException().flatMap(Mono::error);

                })
                .onErrorMap(err -> !(err instanceof ResponseStatusException), err -> new InternalServerErrorException(err.getMessage()));
    }
}
