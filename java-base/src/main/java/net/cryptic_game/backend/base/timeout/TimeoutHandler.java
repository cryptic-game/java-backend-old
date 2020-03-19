package net.cryptic_game.backend.base.timeout;

import java.util.HashSet;
import java.util.Set;

public class TimeoutHandler implements Runnable {

    private final static long tick = 10 * 1000;

    private final Set<Timeout> timeouts;
    private boolean running;

    public TimeoutHandler() {
        this.timeouts = new HashSet<>();
        this.running = false;
    }

    private void start() {
        new Thread(this, "timeout").start();
    }

    @Override
    public void run() {
        while (this.running) {
            this.doTick(System.currentTimeMillis());

            try {
                Thread.sleep(tick);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void doTick(final long currentTime) {
        this.timeouts.forEach(tick -> {
            if (tick.toTick(currentTime)) {
                this.timeouts.remove(tick);
            }
        });
    }

    public void addTimeout(final long ms, final Runnable runnable) {
        this.timeouts.add(new Timeout(ms, runnable));
        if (this.timeouts.size() != 0) {
            this.running = true;
            this.start();
        }
    }
}
