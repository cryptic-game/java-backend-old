package net.cryptic_game.backend.daemon;

import net.cryptic_game.backend.base.AppBootstrap;
import net.cryptic_game.backend.daemon.config.DeamonConfig;

public class App extends AppBootstrap {

    public App() {
        super(DeamonConfig.CONFIG);
    }

    public static void main(String[] args) {
        new App();
    }

    @Override
    protected void init() {

    }

    @Override
    protected void start() {

    }

    @Override
    protected void initApi() {

    }
}
