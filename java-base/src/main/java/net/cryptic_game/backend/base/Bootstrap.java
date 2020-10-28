package net.cryptic_game.backend.base;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.cryptic_game.backend.base.context.ContextHandler;
import net.cryptic_game.backend.base.logging.LogLevel;
import net.cryptic_game.backend.base.logging.LoggingHandler;
import net.cryptic_game.backend.base.logging.logback.LogbackHandler;
import org.springframework.context.annotation.ComponentScan;

import java.io.File;
import java.util.List;

@Slf4j
@Getter
@ComponentScan("net.cryptic_game")
public final class Bootstrap {

    private static final List<String> BANNER = List.of(
            "  _____                  _   _        ____             _                  _",
            " / ____|                | | (_)      |  _ \\           | |                | |",
            "| |     _ __ _   _ _ __ | |_ _  ___  | |_) | __ _  ___| | _____ _ __   __| |",
            "| |    | '__| | | | '_ \\| __| |/ __| |  _ < / _` |/ __| |/ / _ \\ '_ \\ / _` |",
            "| |____| |  | |_| | |_) | |_| | (__  | |_) | (_| | (__|   <  __/ | | | (_| |",
            " \\_____|_|   \\__, | .__/ \\__|_|\\___| |____/ \\__,_|\\___|_|\\_\\___|_| |_|\\__,_|",
            "              __/ | |",
            "             |___/|_| "
    );

    private final long startUpTime;
    private final File workingDir;
    private final LoggingHandler loggingHandler;
    private final ContextHandler contextHandler;

    private boolean debug;
    private boolean shuttingDown;

    public Bootstrap(final long startUpTime, final String[] args) {
        this.startUpTime = startUpTime;
        this.workingDir = new File(System.getProperty("user.dir")).getAbsoluteFile();

        this.loggingHandler = new LogbackHandler(LogLevel.INFO, BANNER, null);
        log.info("Starting in working directory \"{}\"...", this.workingDir.getPath());
        this.loadConfig();

        this.contextHandler = new ContextHandler(Bootstrap.class, this);
        this.shuttingDown = false;
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown0, "shutdown"));

        if (!this.contextHandler.refresh()) this.shutdown();
    }

    public static void main(final String[] args) {
        final long before = System.currentTimeMillis();
        new Bootstrap(before, args);
        final double seconds = (System.currentTimeMillis() - before) / 1000D;
        log.info("The bootstrapping was finished within {} seconds.", Math.round(seconds * 1000) / 1000D);
    }

    private void loadConfig() {
        this.debug = Boolean.parseBoolean(System.getenv("DEBUG"));
        LogLevel level = this.debug ? LogLevel.INFO : null;

        final String levelString = System.getenv("LOG_LEVEL");
        try {
            final LogLevel configLevel = levelString == null ? LogLevel.WARN : LogLevel.valueOf(levelString.toUpperCase());
            if (!this.debug || !(configLevel == LogLevel.OFF || configLevel == LogLevel.ERROR || configLevel == LogLevel.WARN))
                level = configLevel;
        } catch (IllegalArgumentException e) {
            log.error("The log level \"{}\" could not be found.", levelString);
        }

        final String sentryDsn = System.getenv("SENTRY_DSN");
        this.loggingHandler.reinitialize(level, sentryDsn);

        if (this.debug) {
            log.info("Running in debug mode...");
        }
    }

    public void shutdown() {
        System.exit(0);
    }

    private void shutdown0() {
        final double before = System.currentTimeMillis();
        this.contextHandler.close();
        final double seconds = (System.currentTimeMillis() - before) / 1000D;

        log.info("Shutting down took {} seconds...", Math.round(seconds * 1000) / 1000D);
        this.loggingHandler.close();
    }
}
