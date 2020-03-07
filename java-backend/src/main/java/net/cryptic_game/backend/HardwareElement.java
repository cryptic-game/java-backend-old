package net.cryptic_game.backend;

public interface HardwareElement extends TableModelId {

    String getName();

    void setName(String name);

    String getManufacturer();

    void setManufacturer(String manufacturer);
}
