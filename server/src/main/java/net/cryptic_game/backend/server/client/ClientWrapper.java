package net.cryptic_game.backend.server.client;

import io.netty.channel.ChannelHandlerContext;
import net.cryptic_game.backend.data.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientWrapper {

    private static final Map<ChannelHandlerContext, Client> clients = new HashMap<>();

    public static void addClient(final ChannelHandlerContext ctx) {
        clients.put(ctx, new Client(ctx));
    }

    public static void removeClient(Client client) {
        clients.remove(client.getChannelHandlerContext());
    }

    public static Client getClient(final ChannelHandlerContext ctx) {
        return clients.get(ctx);
    }

    public static List<Client> getClients(final User user) {
        final List<Client> clients = new ArrayList<>();
        for (Client c : ClientWrapper.clients.values()) {
            if (c.getUser() != null && c.getUser().getId().equals(user.getId())) clients.add(c);
        }
        return clients;
    }

    public static int getOnlineCount() {
        return clients.size();
    }
}
