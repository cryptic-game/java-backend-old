package net.cryptic_game.backend.base.logging;

public interface LoggingHandler {

    void cleanUp();

    /**
     * Initializes the logging engine and configures
     * the root logger with the specified {@link LogLevel}.
     *
     * @param level the {@link LogLevel} for the root logger
     * @param dsn   the sentry dsn to log errors
     * @param json  if the output should be formatted as json
     */
    void initialize(LogLevel level, String dsn, boolean json);

    default void reinitialize(LogLevel level, String dsn, boolean json) {
        this.cleanUp();
        this.initialize(level, dsn, json);
    }

    LogLevel getLogLevel();

    void setLogLevel(LogLevel level);

    LogLevel getLogLevel(String loggerName);

    void setLogLevel(String loggerName, LogLevel level);

    void close();
}
