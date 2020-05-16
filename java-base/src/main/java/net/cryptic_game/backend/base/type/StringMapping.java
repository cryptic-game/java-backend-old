package net.cryptic_game.backend.base.type;

class StringMapping implements TypeMapping<String> {

    @Override
    public String fromString(final String string) {
        return string;
    }

    @Override
    public String toString(final String object) {
        return object;
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }
}
