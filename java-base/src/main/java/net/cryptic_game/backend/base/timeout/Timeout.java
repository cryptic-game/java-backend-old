package net.cryptic_game.backend.base.timeout;

import java.util.Objects;

class Timeout {

    private final long reached;
    private final Runnable runnable;

    Timeout(final long ms, final Runnable runnable) {
        this.reached = System.currentTimeMillis() + ms;
        this.runnable = runnable;
    }

    boolean toTick(final long currentTime) {
        if (this.isReached(currentTime)) {
            this.runnable.run();
            return true;
        }
        return false;
    }

    private boolean isReached(final long currentTime) {
        return this.reached <= currentTime;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Timeout)) return false;
        final Timeout timeout = (Timeout) o;
        return this.reached == timeout.reached &&
                this.runnable.equals(timeout.runnable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.reached, this.runnable);
    }
}
