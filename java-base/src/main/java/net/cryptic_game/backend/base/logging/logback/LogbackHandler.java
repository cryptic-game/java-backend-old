package net.cryptic_game.backend.base.logging.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.LevelFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.contrib.json.classic.JsonLayout;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.pattern.Converter;
import ch.qos.logback.core.pattern.color.ANSIConstants;
import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.util.OptionHelper;
import io.sentry.SentryOptions;
import io.sentry.logback.SentryAppender;
import net.cryptic_game.backend.base.ansi.AnsiColor;
import net.cryptic_game.backend.base.ansi.AnsiStyle;
import net.cryptic_game.backend.base.logging.LogLevel;
import net.cryptic_game.backend.base.logging.LogLevelConverter;
import net.cryptic_game.backend.base.logging.LoggingHandler;
import org.slf4j.impl.StaticLoggerBinder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.slf4j.Logger.ROOT_LOGGER_NAME;

public final class LogbackHandler implements LoggingHandler {

    private static final String BANNER_SPACING = " ".repeat(2);
    private static final String PATTERN = "[%cyan(%d{yyyy-MM-dd HH:mm:ss})] [%clr(%5p)] [%cyan(%15.15t)] "
            + "%cyan(%-40.40logger{39}) : %clr(%m%n%rExW){}%nopex";
    private static final String COLOR_LESS_PATTERN = "[%d{yyyy-MM-dd HH:mm:ss}] [%5p] [%15.15t] "
            + "%-40.40logger{39} : %m%n%rExW";
    private static final LogLevelConverter<Level> LEVELS = new LogLevelConverter<>();

    static {
        LEVELS.map(LogLevel.ALL, Level.ALL);
        LEVELS.map(LogLevel.TRACE, Level.TRACE);
        LEVELS.map(LogLevel.DEBUG, Level.DEBUG);
        LEVELS.map(LogLevel.INFO, Level.INFO);
        LEVELS.map(LogLevel.WARN, Level.WARN);
        LEVELS.map(LogLevel.ERROR, Level.ERROR);
        LEVELS.map(LogLevel.FATAL, Level.ERROR);
        LEVELS.map(LogLevel.OFF, Level.OFF);
    }

    private final LoggerContext context;

    public LogbackHandler(final LogLevel level, final List<String> banner, final String dsn, final boolean json) {
        this.context = (LoggerContext) StaticLoggerBinder.getSingleton().getLoggerFactory();
        this.cleanUp();
        this.initialize(level, dsn, json);
        if (!banner.isEmpty()) this.printBanner(banner);
    }

    private void printBanner(final List<String> banner) {
        final String version = LogbackHandler.class.getPackage().getImplementationVersion();
        System.out.println(ANSIConstants.ESC_START + AnsiStyle.BOLD + ";" + AnsiColor.BRIGHT_BLUE + ANSIConstants.ESC_END
                + BANNER_SPACING + String.join(CoreConstants.LINE_SEPARATOR + BANNER_SPACING
                + ANSIConstants.ESC_START + AnsiStyle.BOLD + ";" + AnsiColor.BRIGHT_BLUE + ANSIConstants.ESC_END, banner)
                + CoreConstants.LINE_SEPARATOR
                + BANNER_SPACING + ANSIConstants.ESC_START + AnsiStyle.BOLD + ";" + AnsiColor.BRIGHT_BLUE + ANSIConstants.ESC_END
                + ":: Cryptic Backend (" + (version == null ? "development" : version) + ") :: Java Runtime: " + System.getProperty("java.version")
                + " (" + System.getProperty("java.vendor") + ") ::"
                + ANSIConstants.ESC_START + AnsiStyle.RESET + ANSIConstants.ESC_END
                + CoreConstants.LINE_SEPARATOR
        );
    }

    @Override
    public void cleanUp() {
        this.context.reset();
        this.context.getStatusManager().clear();
    }

    @Override
    public void initialize(final LogLevel level, final String dsn, final boolean json) {
        this.addConversionRule("clr", ColorConverter.class);
        this.addConversionRule("rExW", RootCauseFirstWhitespaceThrowableProxyConverter.class);

        final Encoder<ILoggingEvent> encoder = json
                ? this.jsonEncoder()
                : this.patternEncoder(PATTERN);

        this.setLogLevel(level);
        this.getRootLogger().addAppender(this.consoleAppender(encoder));

        if (dsn != null) this.getRootLogger().addAppender(this.sentryAppender(dsn));
    }

    @Override
    public LogLevel getLogLevel() {
        return this.getLogLevel(null);
    }

    @Override
    public void setLogLevel(final LogLevel level) {
        this.setLogLevel(null, level);
    }

    @Override
    public LogLevel getLogLevel(final String loggerName) {
        final Logger logger = loggerName == null ? this.getRootLogger() : this.context.getLogger(loggerName);
        return logger == null ? null : LEVELS.convertNativeToSystem(logger.getLevel());
    }

    @Override
    public void setLogLevel(final String loggerName, final LogLevel level) {
        final Logger logger = loggerName == null ? this.getRootLogger() : this.context.getLogger(loggerName);
        if (logger != null) logger.setLevel(LEVELS.convertSystemToNative(level));
    }

    @Override
    public void close() {
        this.context.stop();
    }

    private PatternLayoutEncoder patternEncoder(final String pattern) {
        final PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setPattern(OptionHelper.substVars(pattern, this.context));
        return encoder;
    }

    private LayoutWrappingEncoder<ILoggingEvent> jsonEncoder() {
        final JsonLayout layout = new JsonLayout();
        layout.setJsonFormatter(new GsonJsonFormatter());

        final LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
        encoder.setLayout(layout);

        return encoder;
    }

    private Appender<ILoggingEvent> consoleAppender(final Encoder<ILoggingEvent> encoder) {
        this.start(encoder);

        final ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
        appender.setName("console");
        appender.setEncoder(encoder);
        this.start(appender);

        return appender;
    }

    private Appender<ILoggingEvent> sentryAppender(final String dsn) {
        final LevelFilter levelFilter = new LevelFilter();
        levelFilter.setLevel(Level.WARN);

        final SentryOptions options = new SentryOptions();
        options.setDsn(dsn);

        final SentryAppender appender = new SentryAppender();
        appender.setName("sentry");
        appender.setOptions(options);
        appender.addFilter(levelFilter);
        this.start(appender);

        return appender;
    }

    @SuppressWarnings({"unchecked"})
    private void addConversionRule(final String name, final Class<? extends Converter<?>> converterClass) {
        Map<String, String> registry = (Map<String, String>) this.context.getObject(CoreConstants.PATTERN_RULE_REGISTRY);
        if (registry == null) {
            registry = new HashMap<>();
            this.context.putObject(CoreConstants.PATTERN_RULE_REGISTRY, registry);
        }
        registry.put(name, converterClass.getName());
    }

    private Logger getRootLogger() {
        return this.context.getLogger(ROOT_LOGGER_NAME);
    }

    private void start(final LifeCycle lifeCycle) {
        if (lifeCycle instanceof ContextAware) ((ContextAware) lifeCycle).setContext(this.context);
        lifeCycle.start();
    }
}
