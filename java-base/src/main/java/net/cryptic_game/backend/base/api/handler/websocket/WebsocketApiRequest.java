package net.cryptic_game.backend.base.api.handler.websocket;

import com.google.gson.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.cryptic_game.backend.base.api.data.ApiRequest;
import reactor.netty.http.websocket.WebsocketInbound;

@Getter
@ToString
@EqualsAndHashCode
public class WebsocketApiRequest extends ApiRequest {

    private final WebsocketInbound websocketInbound;

    public WebsocketApiRequest(final String endpoint, final JsonObject data, final String tag, final WebsocketInbound websocketInbound) {
        super(endpoint, data, tag);
        this.websocketInbound = websocketInbound;
    }
}
