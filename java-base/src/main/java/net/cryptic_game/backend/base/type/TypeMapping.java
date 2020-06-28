package net.cryptic_game.backend.base.type;

public interface TypeMapping<T> {

    T fromString(String string) throws Exception;

    String toString(T object) throws Exception;

    Class<T> getType();
}
