package net.cryptic_game.backend.base.api.endpoint;

import java.util.List;

public class ApiEndpointParameterData {

    private final String key;
    private final Class<?> type;
    private final List<ApiEndpointParameterData> parameters;
    private final boolean optional;

    ApiEndpointParameterData(final String key, final Class<?> type, final List<ApiEndpointParameterData> parameters, final boolean optional) {
        this.key = key;
        this.type = type;
        this.parameters = parameters;
        this.optional = optional;
    }

    public String getKey() {
        return this.key;
    }

    public Class<?> getType() {
        return this.type;
    }

    List<ApiEndpointParameterData> getParameters() {
        return this.parameters;
    }

    public boolean isOptional() {
        return this.optional;
    }
}
