package net.cryptic_game.backend.base.api;

import com.google.gson.JsonObject;

public abstract class ApiExecutor {

    protected final ApiHandler apiHandler;

    public ApiExecutor(final ApiHandler apiHandler) {
        this.apiHandler = apiHandler;
    }

    public abstract JsonObject execute(final ApiExecutionData data) throws ApiException;
}
