package net.cryptic_game.backend.base.sql;

import lombok.Data;

@Data
public class SQLServer {

    private final String location;
    private final String database;
    private final String username;
    private final String password;

    private final SQLServerType sqlServerType;
}
