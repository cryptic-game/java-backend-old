package net.cryptic_game.backend.base.type;

class ShortMapping implements TypeMapping<Short> {

    @Override
    public Short fromString(final String string) {
        return Short.parseShort(string);
    }

    @Override
    public String toString(final Short object) {
        return String.valueOf(object);
    }

    @Override
    public Class<Short> getType() {
        return Short.class;
    }
}
