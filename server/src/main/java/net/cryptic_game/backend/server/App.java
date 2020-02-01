package net.cryptic_game.backend.server;

import net.cryptic_game.backend.base.config.Config;
import net.cryptic_game.backend.base.sql.SQLConnection;
import net.cryptic_game.backend.base.sql.SQLServer;
import net.cryptic_game.backend.base.sql.SQLServerType;
import net.cryptic_game.backend.server.config.ServerConfig;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;

public class App extends AppBootstrap {

    private final Config config;
    private final SQLConnection sqlConnection;

    public App() {
        this.config = new Config(ServerConfig.CONFIG);

        this.setLoglevel(Level.valueOf(this.config.getAsString(ServerConfig.LOG_LEVEL)));

        this.sqlConnection = new SQLConnection();
        this.initSQL();
    }

    private void initSQL() {
        this.sqlConnection.init(new SQLServer(
                this.config.getAsString(ServerConfig.SQL_SERVER_HOSTNAME),
                this.config.getAsInt(ServerConfig.SQL_SERVER_PORT),
                this.config.getAsString(ServerConfig.SQL_SERVER_DATABASE),
                this.config.getAsString(ServerConfig.SQL_SERVER_USERNAME),
                this.config.getAsString(ServerConfig.SQL_SERVER_PASSWORD),
                SQLServerType.getServer(this.config.getAsString(ServerConfig.SQL_SERVER_TYPE))
        ), !this.config.getAsBoolean(ServerConfig.PRODUCTIVE));
    }

    private void setLoglevel(final Level level) {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final LoggerConfig loggerConfig = ctx.getConfiguration().getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        loggerConfig.setLevel(level);
        ctx.updateLoggers();
    }

    public Config getConfig() {
        return this.config;
    }

    public SQLConnection getSqlConnection() {
        return this.sqlConnection;
    }
}
