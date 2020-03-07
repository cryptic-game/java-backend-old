package net.cryptic_game.backend;

import java.time.LocalDateTime;

public interface DeviceAccess extends TableModelId {

    Device getDevice();

    void setDevice(Device device);

    User getUser();

    void setUser(User user);

    LocalDateTime getAccessGranted();

    void setAccessGranted(LocalDateTime accessGranted);

    LocalDateTime getExpire();

    void setExpire(LocalDateTime expire);

    boolean isValid();

    void setValid(boolean valid);
}
