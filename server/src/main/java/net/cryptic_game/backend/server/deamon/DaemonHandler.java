package net.cryptic_game.backend.server.deamon;

import java.util.HashMap;
import java.util.Map;

public class DaemonHandler {

    private final Map<String, Daemon> deamons;
    private final Map<String, Function> functions;

    public DaemonHandler() {
        this.deamons = new HashMap<>();
        this.functions = new HashMap<>();
    }

    public Daemon addDeamon(final Daemon daemon) {
        return this.deamons.put(daemon.getName(), daemon);
    }

    public Daemon getDeamon(final String name) {
        return this.deamons.get(name.strip().toLowerCase());
    }

    public Function addFunction(final Function function) {
        return this.functions.put(function.getName(), function);
    }

    public Function getFunction(final String name) {
        return this.functions.get(name.strip().toLowerCase());
    }
}
