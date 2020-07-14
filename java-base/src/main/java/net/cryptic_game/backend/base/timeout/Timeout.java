package net.cryptic_game.backend.base.timeout;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
class Timeout {

    private final long reached;
    private final Runnable runnable;

    Timeout(final long ms, final Runnable runnable) {
        this.reached = System.currentTimeMillis() + ms;
        this.runnable = runnable;
    }

    boolean doTick(final long currentTime) {
        if (this.reached <= currentTime) {
            this.runnable.run();
            return true;
        }
        return false;
    }
}
