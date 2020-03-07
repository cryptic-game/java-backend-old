package net.cryptic_game.backend;

public interface NetworkMember extends TableModelId {

    Network getNetwork();

    void setNetwork(Network network);

    Device getDevice();

    void setDevice();
}
