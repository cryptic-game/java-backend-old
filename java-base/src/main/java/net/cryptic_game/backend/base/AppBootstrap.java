package net.cryptic_game.backend.base;

import io.sentry.Sentry;
import io.sentry.SentryClient;
import java.sql.SQLException;
import net.cryptic_game.backend.base.api.ApiHandler;
import net.cryptic_game.backend.base.config.BaseConfig;
import net.cryptic_game.backend.base.config.Config;
import net.cryptic_game.backend.base.config.DefaultConfig;
import net.cryptic_game.backend.base.sql.SQLConnection;
import net.cryptic_game.backend.base.sql.SQLServer;
import net.cryptic_game.backend.base.sql.SQLServerType;
import net.cryptic_game.backend.base.sql.TableModel;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AppBootstrap {

    private static final Logger log = LoggerFactory.getLogger(AppBootstrap.class);
    private static AppBootstrap instance;

    protected final Config config;
    protected final SQLConnection sqlConnection;
    private final String dist;
    protected ApiHandler apiHandler;

    public AppBootstrap(final DefaultConfig config, final String dist) {
        AppBootstrap.instance = this;
        this.config = new Config(config);
        this.dist = dist;
        this.setLoglevel(Level.valueOf(this.config.getAsString(BaseConfig.LOG_LEVEL)));

        this.initSentry();

        this.initApi();
        this.sqlConnection = new SQLConnection();

        try {
            this.initSQLTableModels();
        } catch (SQLException e) {
            log.error("Can't register all table models.", e);
        }
        this.setUpSQL();
        this.init();
        this.start();
    }

    public static AppBootstrap getInstance() {
        return instance;
    }

    private void initSQLTableModels() throws SQLException {
        for (Class<? extends TableModel> modelClass : new Reflections("net.cryptic_game.backend.data").getSubTypesOf(TableModel.class)) {
            this.sqlConnection.addEntity(modelClass);
        }
    }

    protected abstract void init();

    protected abstract void start();

    protected abstract void initApi();

    private void initSentry() {
        final String dsn = this.config.getAsString(BaseConfig.SENTRY_DSN);

        if (!dsn.isBlank()) {
            final SentryClient client = Sentry.init(dsn + "?stacktrace.app.packages=net.cryptic_game");

            final String version = AppBootstrap.class.getPackage().getImplementationVersion();
            client.setRelease(version == null ? "debug" : version);
            client.setEnvironment(this.config.getAsBoolean(BaseConfig.PRODUCTIVE) ? "production" : "development");
            client.setDist(this.dist);
//            client.addExtra();
        }
    }

    protected void setUpSQL() {
        this.sqlConnection.init(new SQLServer(
                this.config.getAsString(BaseConfig.SQL_SERVER_HOSTNAME),
                this.config.getAsInt(BaseConfig.SQL_SERVER_PORT),
                this.config.getAsString(BaseConfig.SQL_SERVER_DATABASE),
                this.config.getAsString(BaseConfig.SQL_SERVER_USERNAME),
                this.config.getAsString(BaseConfig.SQL_SERVER_PASSWORD),
                SQLServerType.getServer(this.config.getAsString(BaseConfig.SQL_SERVER_TYPE))
        ), !this.config.getAsBoolean(BaseConfig.PRODUCTIVE));
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

    public ApiHandler getApiHandler() {
        return apiHandler;
    }

    public SQLConnection getSqlConnection() {
        return this.sqlConnection;
    }
}
