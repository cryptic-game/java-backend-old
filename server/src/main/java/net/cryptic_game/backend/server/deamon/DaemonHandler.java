package net.cryptic_game.backend.server.deamon;

import java.util.HashMap;
import java.util.Map;

public class DaemonHandler {

    private final Map<String, Daemon> daemons;
    private final Map<String, Function> functions;

    public DaemonHandler() {
        this.daemons = new HashMap<>();
        this.functions = new HashMap<>();
    }

    public Daemon addDaemon(final Daemon daemon) {
        return this.daemons.put(daemon.getName(), daemon);
    }

    public Daemon getDaemon(final String name) {
        return this.daemons.get(name.strip().toLowerCase());
    }

    public Function addFunction(final Function function) {
        return this.functions.put(function.getName(), function);
    }

    public Function getFunction(final String name) {
        return this.functions.get(name.strip().toLowerCase());
    }
}
