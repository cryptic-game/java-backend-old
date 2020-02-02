package net.cryptic_game.backend.base.data.user;

import net.cryptic_game.backend.base.utils.Security;

public class UserWrapper {

    public static void hasPassword(final User user, final String newPassword) {
        user.setPasswordHash(Security.has(newPassword));
    }

    public static boolean verifyPassword(final User user, final String input) {
        return Security.verify(input, user.getPasswordHash());
    }
}
