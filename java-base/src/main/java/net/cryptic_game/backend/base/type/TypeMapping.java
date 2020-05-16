package net.cryptic_game.backend.base.type;

public interface TypeMapping<T> {

    T fromString(final String string) throws Exception;

    String toString(final T object) throws Exception;

    Class<T> getType();
}
