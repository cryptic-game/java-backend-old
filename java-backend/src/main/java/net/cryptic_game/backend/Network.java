package net.cryptic_game.backend;

import java.time.LocalDateTime;

public interface Network extends TableModelId {

    String getName();

    void setName(String name);

    Device getOwner();

    void setOwner(Device device);

    boolean isHidden();

    void setHidden(boolean hidden);

    LocalDateTime getCreated();

    void setCreated(LocalDateTime created);
}
