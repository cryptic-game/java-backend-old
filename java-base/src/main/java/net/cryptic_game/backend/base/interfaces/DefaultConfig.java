package net.cryptic_game.backend.base.interfaces;

import java.util.Map;

public interface DefaultConfig {

    Map<String, String> getDefaults();
    String toString();
}
