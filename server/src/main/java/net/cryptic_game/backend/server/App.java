package net.cryptic_game.backend.server;

import net.cryptic_game.backend.base.config.Config;
import net.cryptic_game.backend.base.sql.SQLConnection;
import net.cryptic_game.backend.base.sql.SQLServer;
import net.cryptic_game.backend.base.sql.SQLServerType;
import net.cryptic_game.backend.server.config.ServerConfig;

public class App extends AppBootstrap {

    private final Config config;
    private final SQLConnection sqlConnection;

    public App() {
        this.config = new Config(ServerConfig.CONFIG);
        this.sqlConnection = new SQLConnection();
        this.initSQL();
    }

    private void initSQL() {
        this.sqlConnection.init(new SQLServer(
                this.config.getAsString(ServerConfig.DATABASE_SERVER_HOSTNAME),
                this.config.getAsInt(ServerConfig.DATABASE_SERVER_PORT),
                this.config.getAsString(ServerConfig.DATABASE_SERVER_DATABASE),
                this.config.getAsString(ServerConfig.DATABASE_SERVER_USERNAME),
                this.config.getAsString(ServerConfig.DATABASE_SERVER_PASSWORD),
                SQLServerType.getServer(this.config.getAsString(ServerConfig.DATABASE_SERVER_TYPE))
        ), !this.config.getAsBoolean(ServerConfig.PRODUCTIVE));
    }

    public Config getConfig() {
        return this.config;
    }

    public SQLConnection getSqlConnection() {
        return this.sqlConnection;
    }
}
