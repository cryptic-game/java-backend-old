package net.cryptic_game.backend.admin.service.server_management;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nimbusds.jose.shaded.json.JSONObject;
import reactor.core.publisher.Mono;

public interface ServerCommunication {

    Mono<ObjectNode> responseFromServer(String endpoint, JSONObject data);
}
