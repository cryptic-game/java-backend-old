package net.cryptic_game.backend.server.daemon;

import com.google.gson.JsonObject;
import io.netty.channel.Channel;
import net.cryptic_game.backend.base.daemon.Daemon;
import net.cryptic_game.backend.base.daemon.Function;
import net.cryptic_game.backend.base.utils.ApiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class DaemonHandler {

    private final static Logger logger = LoggerFactory.getLogger(DaemonHandler.class);

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
        final Daemon daemon = this.daemons.stream().filter(d -> d.getChannel().equals(channel)).findFirst().orElse(null);
        if (daemon != null) {
            this.daemons.remove(daemon);
            this.functions.values().stream().filter(function -> function.getDaemon().equals(daemon)).collect(Collectors.toSet())
                    .forEach(function -> functions.remove(function.getName()));
            logger.info("Removed daemon \"" + daemon.getName() + "\"");
        }
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

    public UUID executeFunction(final String tag, final Channel client, final Function function, final UUID userId, final JsonObject data) {
        data.addProperty("user_id", userId.toString());
        final UUID requestTag = ApiUtils.request(function.getDaemon().getChannel(), function.getName(), data);
        this.channel.put(requestTag, new ClientRespond(tag, client));
        return requestTag;
    }

    public void respondToClient(final JsonObject json) {
        final ClientRespond respond = this.channel.remove(UUID.fromString(json.get("tag").getAsString()));
        ApiUtils.response(respond.getChannel(), respond.getTag(), json.get("info").getAsJsonObject(), json.get("data"));
    }

    public boolean isRequstOpen(final UUID tag) {
        return this.channel.containsKey(tag);
    }
}
