package net.cryptic_game.backend.base.sql;

public class SQLServer {

    private final String location;
    private final String database;
    private final String username;
    private final String password;

    private final SQLServerType sqlServerType;

    public SQLServer(final String location, final String database, final String username, final String password, final SQLServerType sqlServerType) {
        this.location = location;
        this.database = database;
        this.username = username;
        this.password = password;
        this.sqlServerType = sqlServerType;
    }

    public String getLocation() {
        return this.location;
    }

    public String getDatabase() {
        return this.database;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public SQLServerType getSqlServerType() {
        return this.sqlServerType;
    }
}
