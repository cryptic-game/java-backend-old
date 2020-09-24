package net.cryptic_game.backend.base.utils;

import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public final class SecurityUtils {

    public static final PasswordEncoder PASSWORD_ENCODER = new Argon2PasswordEncoder();

    private SecurityUtils() {
        throw new UnsupportedOperationException();
    }

    public static String hash(final String content) {
        return PASSWORD_ENCODER.encode(content);
    }

    public static boolean verify(final String content, final String hash) {
        return PASSWORD_ENCODER.matches(content, hash);
    }
}
