package net.cryptic_game.backend.base.api.data;

public enum ApiParameterType {

    /**
     * any ({@link Object}).
     */
    NORMAL,

    /**
     * {@link java.util.UUID} / {@link String}.
     */
    USER,

    /**
     * {@link String}.
     */
    TAG,

    /**
     * {@link ApiEndpointData}.
     */
    ENDPOINT,

    /**
     * {@link com.google.gson.JsonObject}.
     */
    DATA
}
