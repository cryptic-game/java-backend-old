package net.cryptic_game.backend.network;

import net.cryptic_game.backend.TableModelId;
import net.cryptic_game.backend.device.Device;

public interface NetworkMember extends TableModelId {

    Network getNetwork();

    void setNetwork(Network network);

    Device getDevice();

    void setDevice();
}
