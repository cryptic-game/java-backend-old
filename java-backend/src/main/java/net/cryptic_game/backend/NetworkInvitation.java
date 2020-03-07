package net.cryptic_game.backend;

public interface NetworkInvitation extends TableModelId {

    Network getNetwork();

    void setNetwork(Network network);

    Device getTarget();

    void setTarget(Device target);

    Device getInviter();

    void setInviter(Device inviter);

    boolean isRequest();

    void setRequest(boolean request);
}
