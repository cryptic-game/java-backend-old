package net.cryptic_game.backend.base.type;

class LongMapping implements TypeMapping<Long> {

    @Override
    public Long fromString(final String string) {
        return Long.parseLong(string);
    }

    @Override
    public String toString(final Long object) {
        return String.valueOf(object);
    }

    @Override
    public Class<Long> getType() {
        return Long.class;
    }
}
