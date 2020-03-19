package net.cryptic_game.backend.server.server.daemon.endpoints;

import com.google.gson.JsonObject;
import net.cryptic_game.backend.base.netty.ResponseType;
import net.cryptic_game.backend.data.user.User;
import net.cryptic_game.backend.data.user.UserWrapper;
import net.cryptic_game.backend.server.client.Client;
import net.cryptic_game.backend.server.client.ClientWrapper;
import net.cryptic_game.backend.server.server.daemon.DaemonEndpoint;

import java.util.List;
import java.util.UUID;

import static net.cryptic_game.backend.base.utils.JsonSocketUtils.build;
import static net.cryptic_game.backend.base.utils.JsonUtils.getJsonObject;
import static net.cryptic_game.backend.base.utils.JsonUtils.getUUID;

public class SendEndpoint extends DaemonEndpoint {

    public SendEndpoint() {
        super("send");
    }

    @Override
    public JsonObject handleRequest(final UUID tag, final JsonObject data) throws Exception {
        final UUID userId = getUUID(data, "user");
        final JsonObject userData = getJsonObject(data, "data");

        if (userId == null) {
            return build(ResponseType.BAD_REQUEST, "MISSING_USER");
        }

        if (userData == null) {
            return build(ResponseType.BAD_REQUEST, "MISSING_USER_DATA");
        }

        final User user = UserWrapper.getById(userId);
        final List<Client> clients = ClientWrapper.getClients(user);

        clients.forEach(client -> client.getChannelHandlerContext().writeAndFlush(build(ResponseType.PUSH, userData)));

        return build(ResponseType.OK, tag);
    }
}
