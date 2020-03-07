package net.cryptic_game.backend;

public interface Device extends TableModelId {

    String getName();

    void setName(String name);

    User getOwner();

    void setOwner(User owner);

    boolean isPoweredOn();

    void setPoweredOn(boolean poweredOn);
}
