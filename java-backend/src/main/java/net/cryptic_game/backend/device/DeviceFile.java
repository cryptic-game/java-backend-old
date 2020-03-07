package net.cryptic_game.backend.device;

import net.cryptic_game.backend.TableModelId;

public interface DeviceFile extends TableModelId {

    Device getDevice();

    void setDevice(Device device);

    String getName();

    void setName(String name);

    String getContents();

    void setContents();

    boolean isDirectory();

    void setIsDirectory(boolean isDirectory);

    DeviceFile getParentDirectory();

    void setParentDirectory(DeviceFile parentDirectory);
}
