package net.cryptic_game.backend.data;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public final class Constants {

    public static final long SESSION_EXPIRE = 7 * 24 * 60 * 60; //in Seconds
    public static final int USERNAME_LENGTH = 24;
    public static final Pattern USERNAME = Pattern.compile("^[a-zA-Z0-9\\-_.]{2," + USERNAME_LENGTH + "}$");

    private Constants() {
        throw new UnsupportedOperationException();
    }
}
