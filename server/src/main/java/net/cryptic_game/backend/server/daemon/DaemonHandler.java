package net.cryptic_game.backend.server.daemon;

import com.google.gson.JsonObject;
import io.netty.channel.Channel;
import net.cryptic_game.backend.base.daemon.Daemon;
import net.cryptic_game.backend.base.daemon.Function;
import net.cryptic_game.backend.base.utils.ApiUtils;
import net.cryptic_game.backend.data.user.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DaemonHandler {

    private final Map<String, Function> functions;
    private Set<Daemon> daemons;

    public DaemonHandler() {
        this.daemons = new HashSet<>();
        this.functions = new HashMap<>();
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

    public Function addFunction(final Function function) {
        return this.functions.put(function.getName(), function);
    }

    public Function getFunction(final String name) {
        return this.functions.get(name.strip().toLowerCase());
    }

    public void executeFunction(final Function function, final User user, final JsonObject data) {
        data.add("user", user.serialize());
        ApiUtils.request(function.getDaemon().getChannel(), function.getName(), data);
    }
}
