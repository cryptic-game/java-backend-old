package net.cryptic_game.backend;

import java.util.UUID;

public interface TableModelId extends TableModel {

    UUID getId();

    void setId(UUID id);
}
