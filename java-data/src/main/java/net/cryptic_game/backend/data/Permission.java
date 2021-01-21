package net.cryptic_game.backend.data;

public final class Permission {

    // 100 -> Authenticated
    public static final int INTERNAL = 100;

    // 200 -> Manager Website
    public static final int TEAM_MANAGEMENT = 200;
    public static final int FAQ_MANAGEMENT = 201;
    public static final int BLOG_MANAGEMENT = 202;

    // 300 -> Manage Admin Panel Access for other users
    public static final int ACCESS_MANAGEMENT = 300;

    // 400 -> Game content
    public static final int USER_MANAGEMENT = 400;

    // 500 -> Manage backend infrastructure
    public static final int SERVER_MANAGEMENT = 400;

    private Permission() {
        throw new UnsupportedOperationException();
    }
}
