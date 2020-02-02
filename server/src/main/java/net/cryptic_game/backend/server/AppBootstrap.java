package net.cryptic_game.backend.server;

public class AppBootstrap {

    private static App instance;

    public static void main(String[] args) {
        instance = new App();
    }

    public static App getInstance() {
        return instance;
    }
}
