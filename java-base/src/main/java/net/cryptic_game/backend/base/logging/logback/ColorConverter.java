package net.cryptic_game.backend.base.logging.logback;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.CompositeConverter;
import ch.qos.logback.core.pattern.color.ANSIConstants;
import net.cryptic_game.backend.base.ansi.AnsiColor;
import net.cryptic_game.backend.base.ansi.AnsiElement;
import net.cryptic_game.backend.base.ansi.AnsiStyle;

public final class ColorConverter extends CompositeConverter<ILoggingEvent> {

    private static final String START = ANSIConstants.ESC_START + AnsiStyle.BOLD + ";";
    private static final String RESET = ANSIConstants.ESC_START + AnsiStyle.RESET + ANSIConstants.ESC_END;

    @Override
    protected String transform(final ILoggingEvent event, final String in) {
        AnsiElement color;
        switch (event.getLevel().toInt()) {
            case Level.ERROR_INT:
                color = AnsiColor.RED;
                break;
            case Level.WARN_INT:
                color = AnsiColor.YELLOW;
                break;
            case Level.INFO_INT:
                color = AnsiColor.BLUE;
                break;
            default:
                color = AnsiColor.GREEN;
                break;
        }

        return START + color + ANSIConstants.ESC_END + in + RESET;
    }
}
