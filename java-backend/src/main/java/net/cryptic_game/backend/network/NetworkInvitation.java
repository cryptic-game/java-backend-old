package net.cryptic_game.backend.network;

import net.cryptic_game.backend.TableModelId;
import net.cryptic_game.backend.device.Device;

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
