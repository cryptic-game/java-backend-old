package net.cryptic_game.backend.admin.service.server_management;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nimbusds.jose.shaded.json.JSONObject;
import lombok.RequiredArgsConstructor;
import net.cryptic_game.backend.admin.Config;
import net.cryptic_game.backend.admin.exception.NotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ServerCommunicationImpl implements ServerCommunication {

    private final Config config;
    private WebClient webClient;

    public Mono<ObjectNode> responseFromServer(final String endpoint, final JSONObject data) {
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
                        return response.bodyToMono(ObjectNode.class);
                    }
                    if (response.rawStatusCode() == 404) {
                        throw new NotFoundException("", "element not found");
                    }
                    return response.createException().flatMap(Mono::error);

                });
    }
}
