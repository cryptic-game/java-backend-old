package net.cryptic_game.backend.base.interfaces;

import net.cryptic_game.backend.base.json.JsonSerializable;

public interface ResponseType extends JsonSerializable {

    String toString();

    int getCode();

    String getName();

    boolean isError();
}
