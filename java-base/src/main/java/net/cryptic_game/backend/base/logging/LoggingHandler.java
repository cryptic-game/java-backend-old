package net.cryptic_game.backend.base.logging;

public interface LoggingHandler {

    void cleanUp();

    /**
     * Initializes the logging engine and configures
     * the root logger with the specified {@link LogLevel}.
     *
     * @param level the {@link LogLevel} for the root logger
     * @param dsn   the sentry dsn to log errors
     */
    void initialize(LogLevel level, String dsn);

    default void reinitialize(final LogLevel level, String dsn) {
        this.cleanUp();
        this.initialize(level, dsn);
    }

    LogLevel getLogLevel();

    void setLogLevel(LogLevel level);

    LogLevel getLogLevel(String loggerName);

    void setLogLevel(String loggerName, LogLevel level);

    void close();
}
