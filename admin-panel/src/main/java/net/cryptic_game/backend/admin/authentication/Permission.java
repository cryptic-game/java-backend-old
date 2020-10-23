package net.cryptic_game.backend.admin.authentication;

public final class Permission {

    public static final int INTERNAL = 100;
    public static final int TEAM_MANAGEMENT = 200;
    public static final int ACCESS_MANAGEMENT = 300;

    private Permission() {
        throw new UnsupportedOperationException();
    }
}
