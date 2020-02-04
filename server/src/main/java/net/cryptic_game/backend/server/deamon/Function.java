package net.cryptic_game.backend.server.deamon;

import java.util.HashSet;
import java.util.Set;

public class Function {

    private final String name;
    private final Daemon daemon;
    private final Set<String> parameters;

    public Function(final String name, final Daemon daemon) {
        this.name = name;
        this.daemon = daemon;
        this.parameters = new HashSet<>();
    }

    public String getName() {
        return this.name;
    }

    public Daemon getDaemon() {
        return this.daemon;
    }

    public void addParameter(final String parameter) {
        this.parameters.add(parameter);
    }

    public Set<String> getParameters() {
        return this.parameters;
    }
}
