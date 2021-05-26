package net.cryptic_game.backend.admin.service.server_management;

import com.nimbusds.jose.shaded.json.JSONObject;
import reactor.core.publisher.Mono;

public interface ServerCommunication {

    Mono<String> responseFromServer(String endpoint, JSONObject data);
}
