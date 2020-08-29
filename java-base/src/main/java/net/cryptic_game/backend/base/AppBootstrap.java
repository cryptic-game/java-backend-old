package net.cryptic_game.backend.base;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.influx.InfluxConfig;
import io.micrometer.influx.InfluxMeterRegistry;
import io.sentry.Sentry;
import io.sentry.SentryClient;
import lombok.Getter;
import net.cryptic_game.backend.base.config.ConfigHandler;
import net.cryptic_game.backend.base.sql.SQLConnection;
import net.cryptic_game.backend.base.sql.SQLServer;
import net.cryptic_game.backend.base.sql.SQLServerType;
import net.cryptic_game.backend.base.sql.models.TableModel;
import net.cryptic_game.backend.base.timeout.TimeoutHandler;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.sql.SQLException;
import java.time.Duration;

public abstract class AppBootstrap {

    private static final Logger LOG = LoggerFactory.getLogger(AppBootstrap.class);
    private static AppBootstrap instance;
    private static TimeoutHandler timeoutHandler;
    @Getter
    private final BaseConfig config;
    @Getter
    private final SQLConnection sqlConnection;
    @Getter
    private final String name;

    public AppBootstrap(final String[] args, final Object config, final String name) {
        System.setOut(new PrintStream(new LoggerOutputStream("SysOut", Level.TRACE, System.out)));
        System.setErr(new PrintStream(new LoggerOutputStream("SysErr", Level.ERROR, System.err)));

        AppBootstrap.instance = this;

        ConfigHandler configHandler = null;
        for (final String arg : args) {
            final String currentArg = arg.toLowerCase();

            if (currentArg.equals("--env") || currentArg.equals("-e")) {
                configHandler = new ConfigHandler(true);
                break;
            }
        }
        if (configHandler == null) configHandler = new ConfigHandler(false);

        this.config = configHandler.addConfig(new BaseConfig());
        configHandler.addConfig(config);
        configHandler.loadConfig();

        timeoutHandler = new TimeoutHandler();
        this.name = name;
        this.setLoglevel(Level.valueOf(this.config.getLogLevel().toUpperCase()));

        LOG.info("Starting {}...", name);

        this.initSentry();
        if (!this.config.getInfluxUri().isBlank()) this.setUpMicrometer();

        this.preInit();
        this.initApi();
        this.sqlConnection = new SQLConnection();

        try {
            this.initSQLTableModels();
        } catch (SQLException e) {
            LOG.error("Can't register all table models.", e);
        }
        this.setUpSQL();
        this.init();
        this.start();
    }

    public static AppBootstrap getInstance() {
        return instance;
    }

    public static void addTimeout(final long ms, final Runnable runnable) {
        timeoutHandler.addTimeout(ms, runnable);
    }

    private void initSQLTableModels() throws SQLException {
        for (Class<? extends TableModel> modelClass : new Reflections("net.cryptic_game.backend.data").getSubTypesOf(TableModel.class)) {
            this.sqlConnection.addEntity(modelClass);
        }
    }

    protected abstract void preInit();

    protected abstract void initApi();

    protected abstract void init();

    protected abstract void start();

    private void initSentry() {
        final String dsn = this.config.getSentryDsn();

        if (!dsn.isBlank()) {
            final SentryClient client = Sentry.init(dsn + "?stacktrace.app.packages=net.cryptic_game");

            client.addTag("OS", System.getProperty("OS"));

            final String version = AppBootstrap.class.getPackage().getImplementationVersion();
            client.setRelease(version == null ? "debug" : version);
            client.setEnvironment(this.config.isProductive() ? "production" : "development");
            client.setDist(this.name);
        } else {
            Sentry.init("");
        }
    }

    protected final void setUpSQL() {
        this.sqlConnection.init(new SQLServer(
                this.config.getSqlServerLocation(),
                this.config.getSqlServerDatabase(),
                this.config.getSqlServerUsername(),
                this.config.getSqlServerUsername(),
                SQLServerType.valueOf(this.config.getSqlServerType().toUpperCase())
        ), !this.config.isProductive());
    }

    private void setLoglevel(final Level level) {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final LoggerConfig loggerConfig = ctx.getConfiguration().getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        loggerConfig.setLevel(level);
        ctx.updateLoggers();
    }

    private void setUpMicrometer() {
        final InfluxConfig influxConfig = new InfluxConfig() {

            @Override
            public @NotNull String db() {
                return name.toLowerCase();
            }

            @Override
            public @NotNull String uri() {
                return config.getInfluxUri();
            }

            @Override
            public @NotNull Duration step() {
                return Duration.ofSeconds(config.getInfluxStep());
            }

            @Override
            public String get(final @NotNull String key) {
                return null;
            }
        };
        Metrics.addRegistry(InfluxMeterRegistry.builder(influxConfig).clock(Clock.SYSTEM).build());
    }
}
