package net.cryptic_game.backend.base.type;

import org.apache.logging.log4j.Level;

public class LevelType implements TypeMapping<Level> {

    @Override
    public Level fromString(final String string) throws Exception {
        return Level.valueOf(string);
    }

    @Override
    public String toString(final Level object) throws Exception {
        return object.name();
    }

    @Override
    public Class<Level> getType() {
        return Level.class;
    }
}
