package net.cryptic_game.backend.server.daemon;

import io.netty.channel.ChannelHandlerContext;
import net.cryptic_game.backend.base.daemon.Daemon;
import net.cryptic_game.backend.base.daemon.Function;

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

    public void removeDaemon(final ChannelHandlerContext ctx) {
        this.daemons = this.daemons.stream().filter(daemon -> !daemon.getChannelHandlerContext().equals(ctx)).collect(Collectors.toSet());
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
}
