package net.cryptic_game.backend.server.api;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.api.ApiExecutionData;
import net.cryptic_game.backend.server.client.Client;

public class ServerApiExecutionData extends ApiExecutionData {

    private final String endpoint;
    private final Client client;
    private final JsonObject data;

    public ServerApiExecutionData(final String endpoint, final Client client, final JsonObject data) {
        this.endpoint = endpoint;
        this.client = client;
        this.data = data;
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    public Client getClient() {
        return this.client;
    }

    public JsonObject getData() {
        return this.data;
    }
}
