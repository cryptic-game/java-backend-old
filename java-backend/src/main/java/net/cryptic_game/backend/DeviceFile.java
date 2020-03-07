package net.cryptic_game.backend;

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
