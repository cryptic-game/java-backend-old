package net.cryptic_game.backend.base.logging.logback;

import ch.qos.logback.contrib.json.JsonFormatter;

import java.util.Map;

import net.cryptic_game.backend.base.json.JsonUtils;

public class GsonJsonFormatter implements JsonFormatter {

    private static final String LINE_SEPARATOR = System.lineSeparator();

    @Override
    public String toJsonString(final Map m) throws Exception {
        try {
            return JsonUtils.toJson(m).toString() + LINE_SEPARATOR;
        } catch (Throwable cause) {
            throw new Exception(cause);
        }
    }
}
