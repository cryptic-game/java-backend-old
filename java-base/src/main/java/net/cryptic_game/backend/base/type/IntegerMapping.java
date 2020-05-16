package net.cryptic_game.backend.base.type;

class IntegerMapping implements TypeMapping<Integer> {

    @Override
    public Integer fromString(final String string) {
        return Integer.parseInt(string);
    }

    @Override
    public String toString(final Integer object) {
        return String.valueOf(object);
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }
}
