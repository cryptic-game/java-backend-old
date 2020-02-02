package net.cryptic_game.backend.base.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class SecurityUtils {

    private static final BCrypt.Hasher hasher = BCrypt.withDefaults();
    private static final BCrypt.Verifyer verifyer = BCrypt.verifyer();

    public static String hash(final String content) {
        return hasher.hashToString(12, content.toCharArray());
    }

    public static boolean verify(final String content, final String hash) {
        return verifyer.verify(content.toCharArray(), hash.toCharArray()).verified;
    }
}
