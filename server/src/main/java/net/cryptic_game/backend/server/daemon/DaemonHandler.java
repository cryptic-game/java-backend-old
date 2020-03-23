package net.cryptic_game.backend.server.daemon;

import com.google.gson.JsonObject;
import io.netty.channel.Channel;
import net.cryptic_game.backend.base.daemon.Daemon;
import net.cryptic_game.backend.base.daemon.Function;
import net.cryptic_game.backend.base.utils.ApiUtils;

import java.util.*;
import java.util.stream.Collectors;

public class DaemonHandler {

    private final Map<String, Function> functions;
    private final HashMap<UUID, ClientRespond> channel;
    private Set<Daemon> daemons;

    public DaemonHandler() {
        this.daemons = new HashSet<>();
        this.functions = new HashMap<>();
        this.channel = new HashMap<>();
    }

    public void addDaemon(final Daemon daemon) {
        this.daemons.add(daemon);
    }

    public void removeDaemon(final Channel channel) {
        this.daemons = this.daemons.stream().filter(daemon -> !daemon.getChannel().equals(channel)).collect(Collectors.toSet());
    }

    public Set<Daemon> getDaemons() {
        return this.daemons;
    }

    public void addFunctions(final Set<Function> functions) {
        functions.forEach(func -> this.functions.put(func.getName(), func));
    }

    public Function getFunction(final String name) {
        return this.functions.get(name.strip().toLowerCase());
    }

    public void executeFunction(final String tag, final Channel client, final Function function, final UUID userId, final JsonObject data) {
        data.addProperty("user_id", userId.toString());
        this.channel.put(ApiUtils.request(function.getDaemon().getChannel(), function.getName(), data), new ClientRespond(tag, client));
    }

    public void respondToClient(final JsonObject json) {
        final ClientRespond respond = this.channel.get(UUID.fromString(json.get("tag").getAsString()));
        ApiUtils.response(respond.getChannel(), respond.getTag(), json.get("info").getAsJsonObject(), json.get("data"));
    }
}
