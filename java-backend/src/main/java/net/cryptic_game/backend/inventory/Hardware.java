package net.cryptic_game.backend.inventory;

import net.cryptic_game.backend.TableModelId;
import net.cryptic_game.backend.device.Device;

public interface Hardware extends TableModelId {

    Device getDevice();

    void setDevice(Device device);

    HardwareElement getHardwareElement();

    void setHardwareElement(HardwareElement hardwareElement);

    HardwareType getHardwareType();

    void setHardwareType(HardwareType hardwareType);
}
