package net.cryptic_game.backend.base.timeout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class TimeoutHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(TimeoutHandler.class);
    private final static long tick = 1000; // milliseconds

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
                log.error("Error while waiting for next timer tick.", e);
            }
        }
    }

    public void doTick(final long currentTime) {
        final Iterator<Timeout> iterator = this.timeouts.iterator();
        iterator.forEachRemaining(tick -> {
            if (tick.doTick(currentTime)) {
                iterator.remove();
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
