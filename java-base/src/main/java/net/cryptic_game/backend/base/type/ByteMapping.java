package net.cryptic_game.backend.base.type;

class ByteMapping implements TypeMapping<Byte> {

    @Override
    public Byte fromString(final String string) {
        return Byte.parseByte(string);
    }

    @Override
    public String toString(final Byte object) {
        return String.valueOf(object);
    }

    @Override
    public Class<Byte> getType() {
        return Byte.class;
    }
}
