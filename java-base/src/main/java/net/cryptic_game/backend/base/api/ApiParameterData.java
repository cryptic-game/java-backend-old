package net.cryptic_game.backend.base.api;

public class ApiParameterData {

    private final String key;
    private final Class<?> type;
    private final boolean optional;

    public ApiParameterData(final String key, final Class<?> type, final boolean optional) {
        this.key = key;
        this.type = type;
        this.optional = optional;
    }

    public String getKey() {
        return this.key;
    }

    public Class<?> getType() {
        return this.type;
    }

    public boolean isOptional() {
        return this.optional;
    }
}
