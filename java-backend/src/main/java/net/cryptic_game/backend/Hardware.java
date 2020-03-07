package net.cryptic_game.backend;

public interface Hardware extends TableModelId {

    Device getDevice();

    void setDevice(Device device);

    HardwareElement getHardwareElement();

    void setHardwareElement(HardwareElement hardwareElement);

    HardwareType getHardwareType();

    void setHardwareType(HardwareType hardwareType);
}
