package net.cryptic_game.backend;

import java.time.LocalDateTime;

public interface Session extends TableModelId {

    User getUser();

    void setUser(User user);

    String getTokenHash();

    void setTokenHash(String tokenHash);

    String getDeviceName();

    void setDeviceName(String deviceName);

    LocalDateTime getExpire();

    void setExpire(LocalDateTime expire);

    boolean isValid();

    void setValid(boolean valid);

    LocalDateTime getLastActive();

    void setLastActive(LocalDateTime lastActive);

}
