package net.cryptic_game.backend.server;

import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.base.config.Config;
import net.cryptic_game.backend.base.sql.SQLServer;
import net.cryptic_game.backend.base.sql.SQLServerType;
import net.cryptic_game.backend.server.config.ServerConfig;
import net.cryptic_game.backend.server.server.NettyServerHandler;
import net.cryptic_game.backend.server.server.http.HttpCodec;
import net.cryptic_game.backend.server.server.websocket.WebSocketCodec;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;

public class App extends AppBootstrap {

    private final Config config;
    private NettyServerHandler serverHandler;

    public App() {
        super();
        this.config = new Config(ServerConfig.CONFIG);

        this.setLoglevel(Level.valueOf(this.config.getAsString(ServerConfig.LOG_LEVEL)));

        serverHandler = new NettyServerHandler();
        serverHandler.addServer("websocket", config.getAsString(ServerConfig.WEBSOCKET_HOST),
                config.getAsInt(ServerConfig.WEBSOCKET_PORT), new WebSocketCodec());
        serverHandler.addServer("http", config.getAsString(ServerConfig.HTTP_HOST),
                config.getAsInt(ServerConfig.HTTP_PORT), new HttpCodec());
        serverHandler.start();
    }

    public static void main(String[] args) {
        new App();
    }

    @Override
    protected void init() {
        this.setUpSQL(
                !this.config.getAsBoolean(ServerConfig.PRODUCTIVE),
                new SQLServer(
                        this.config.getAsString(ServerConfig.SQL_SERVER_HOSTNAME),
                        this.config.getAsInt(ServerConfig.SQL_SERVER_PORT),
                        this.config.getAsString(ServerConfig.SQL_SERVER_DATABASE),
                        this.config.getAsString(ServerConfig.SQL_SERVER_USERNAME),
                        this.config.getAsString(ServerConfig.SQL_SERVER_PASSWORD),
                        SQLServerType.getServer(this.config.getAsString(ServerConfig.SQL_SERVER_TYPE))
                )
        );
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
}
