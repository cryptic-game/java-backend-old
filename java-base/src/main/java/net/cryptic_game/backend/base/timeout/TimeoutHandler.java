package net.cryptic_game.backend.base.timeout;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
public final class TimeoutHandler extends TimerTask {

    private static final long TICK = 5000;

    private final Timer timer;
    private final Set<Timeout> timeouts;
    private boolean running;

    public TimeoutHandler() {
        this.timer = new Timer();
        this.timeouts = new LinkedHashSet<>();
        this.running = false;
    }

    private void start() {
        this.timer.scheduleAtFixedRate(this, TICK, TICK);
        this.running = true;
    }

    private void stop() {
        this.timer.cancel();
        this.running = false;
    }

    @Override
    public void run() {
        final long currentTime = System.currentTimeMillis();
        synchronized (this.timeouts) {
            this.timeouts.removeIf(timeout -> timeout.doTick(currentTime));
        }
        if (this.timeouts.isEmpty()) this.stop();
    }

    public void addTimeout(final long ms, final Runnable runnable) {
        this.timeouts.add(new Timeout(ms, runnable));
        if (!this.running) this.start();
    }
}
