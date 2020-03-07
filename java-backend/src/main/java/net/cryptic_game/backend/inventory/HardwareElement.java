package net.cryptic_game.backend.inventory;

import net.cryptic_game.backend.TableModelId;

public interface HardwareElement extends TableModelId {

    String getName();

    void setName(String name);

    String getManufacturer();

    void setManufacturer(String manufacturer);
}
