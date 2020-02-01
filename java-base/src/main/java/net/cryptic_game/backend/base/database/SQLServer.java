package net.cryptic_game.backend.base.database;

public class SQLServer {

    private final String host;
    private final int port;
    private final String database;
    private final String username;
    private final String password;

    private final SQLServerType sqlServerType;

    public SQLServer(final String host, final int port, final String database, final String username, final String password, final SQLServerType sqlServerType) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        this.sqlServerType = sqlServerType;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
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
