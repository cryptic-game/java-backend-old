package net.cryptic_game.backend.device;

import net.cryptic_game.backend.TableModelId;
import net.cryptic_game.backend.base.interfaces.JsonSerializable;
import net.cryptic_game.backend.user.User;

public interface Device extends TableModelId, JsonSerializable {

    String getName();

    void setName(String name);

    User getOwner();

    void setOwner(User owner);

    boolean isPoweredOn();

    void setPoweredOn(boolean poweredOn);
}
