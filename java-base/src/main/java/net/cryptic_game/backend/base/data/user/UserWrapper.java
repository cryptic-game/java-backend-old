package net.cryptic_game.backend.base.data.user;

import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.base.sql.SQLConnection;
import net.cryptic_game.backend.base.utils.SecurityUtils;

public class UserWrapper {

    private static final SQLConnection sqlConnection;

    static {
        final AppBootstrap app = AppBootstrap.getInstance();
        sqlConnection = app.getSqlConnection();
    }

    public static void hasPassword(final User user, final String newPassword) {
        user.setPasswordHash(SecurityUtils.hash(newPassword));
    }

    public static boolean verifyPassword(final User user, final String input) {
        return SecurityUtils.verify(input, user.getPasswordHash());
    }
}
