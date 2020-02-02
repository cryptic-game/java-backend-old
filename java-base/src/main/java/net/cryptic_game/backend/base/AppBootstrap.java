package net.cryptic_game.backend.base;

import net.cryptic_game.backend.base.sql.SQLConnection;
import net.cryptic_game.backend.base.sql.SQLServer;

public abstract class AppBootstrap {

    private static AppBootstrap instance;

    private final SQLConnection sqlConnection;

    public AppBootstrap() {
        this.sqlConnection = new SQLConnection();
    }

    protected abstract void init();

    protected void setUpSQL(final boolean debug, final SQLServer server) {

    }

    public SQLConnection getSqlConnection() {
        return this.sqlConnection;
    }

    public static AppBootstrap getInstance() {
        return instance;
    }
}
