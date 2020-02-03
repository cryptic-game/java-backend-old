package net.cryptic_game.backend.base.interfaces;

public interface ResponseType extends JsonSerializable {

    String toString();

    int getCode();

    String getName();

    boolean isError();
}
