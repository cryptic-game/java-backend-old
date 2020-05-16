package net.cryptic_game.backend.base.type;

class FloatMapping implements TypeMapping<Float> {

    @Override
    public Float fromString(final String string) {
        return Float.parseFloat(string);
    }

    @Override
    public String toString(final Float object) {
        return String.valueOf(object);
    }

    @Override
    public Class<Float> getType() {
        return Float.class;
    }
}
