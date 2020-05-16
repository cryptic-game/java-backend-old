package net.cryptic_game.backend.base.type;

class BooleanMapping implements TypeMapping<Boolean> {

    @Override
    public Boolean fromString(final String string) {
        return Boolean.parseBoolean(string);
    }

    @Override
    public String toString(final Boolean object) {
        return String.valueOf(object);
    }

    @Override
    public Class<Boolean> getType() {
        return Boolean.class;
    }
}
