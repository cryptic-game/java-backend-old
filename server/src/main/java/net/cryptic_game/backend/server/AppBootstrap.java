package net.cryptic_game.backend.server;

public class AppBootstrap {

    private static App instance;

    protected void setInstance(final App app) {
        instance = app;
    }

    public static void main(String[] args) {
        new AppBootstrap();
    }

    public static App getInstance() {
        return instance;
    }
}
