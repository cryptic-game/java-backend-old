package net.cryptic_game.backend;

import java.time.LocalDateTime;

public interface User extends TableModelId {

    String getName();

    void setName(String name);

    String getMail();

    void setMail(String mail);

    String getPasswordHash();

    void setPasswordHash(String mail);

    LocalDateTime getCreated();

    void setCreated(LocalDateTime created);

    LocalDateTime getLast();

    void setLast(LocalDateTime last);
}
