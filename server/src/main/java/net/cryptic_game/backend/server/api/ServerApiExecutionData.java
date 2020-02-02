package net.cryptic_game.backend.server.api;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.api.ApiExecutionData;
import net.cryptic_game.backend.server.client.Client;

public class ServerApiExecutionData extends ApiExecutionData {

    private final String endpoint;
    private final Client client;
    private final JsonObject data;

    public ServerApiExecutionData(String endpoint, Client client, JsonObject data) {
        this.endpoint = endpoint;
        this.client = client;
        this.data = data;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public Client getClient() {
        return client;
    }

    public JsonObject getData() {
        return data;
    }
}
