package net.cryptic_game.backend.base.logging.logback;

import ch.qos.logback.classic.pattern.RootCauseFirstThrowableProxyConverter;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.CoreConstants;

public final class RootCauseFirstWhitespaceThrowableProxyConverter extends RootCauseFirstThrowableProxyConverter {

    @Override
    protected String throwableProxyToString(final IThrowableProxy throwableProxy) {
        return CoreConstants.LINE_SEPARATOR + super.throwableProxyToString(throwableProxy) + CoreConstants.LINE_SEPARATOR;
    }
}
