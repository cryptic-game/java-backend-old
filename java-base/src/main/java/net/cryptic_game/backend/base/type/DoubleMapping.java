package net.cryptic_game.backend.base.type;

class DoubleMapping implements TypeMapping<Double> {

    @Override
    public Double fromString(final String string) {
        return Double.parseDouble(string);
    }

    @Override
    public String toString(final Double object) {
        return String.valueOf(object);
    }

    @Override
    public Class<Double> getType() {
        return Double.class;
    }
}
