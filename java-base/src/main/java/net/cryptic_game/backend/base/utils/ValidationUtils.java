package net.cryptic_game.backend.base.utils;

import java.util.regex.Pattern;

public final class ValidationUtils {

    private ValidationUtils() {
        throw new UnsupportedOperationException();
    }

    public static boolean checkPassword(final String password) {
        return Pattern.compile("(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}").matcher(password).find();
    }
}
