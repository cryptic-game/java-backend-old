package net.cryptic_game.backend.base.config;

import net.cryptic_game.backend.base.interfaces.DefaultConfig;

import java.util.Map;

public class Config {

    private static Map<String, String> env = System.getenv();
    private static Map<String, String> defaults;

    public Config(DefaultConfig defaultConfig) {
        defaults = defaultConfig.getDefaults();
    }

    public static String get(String key) {
        if (env.containsKey(key)) {
            return env.get(key);
        } else if (defaults.containsKey(key)) {
            return defaults.get(key);
        }
        return null;
    }

    public static int getInteger(String key) {
        //noinspection ConstantConditions
        return Integer.parseInt(get(key));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public static String get(DefaultConfig d) {
        return get(d.toString());
    }

    public static int getInteger(DefaultConfig d) {
        return getInteger(d.toString());
    }

    public static boolean getBoolean(DefaultConfig d) {
        return getBoolean(d.toString());
    }

}
