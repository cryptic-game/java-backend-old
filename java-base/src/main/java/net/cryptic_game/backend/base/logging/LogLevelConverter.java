package net.cryptic_game.backend.base.logging;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class LogLevelConverter<T> {

    private final Map<LogLevel, T> systemToNative;
    private final Map<T, LogLevel> nativeToSystem;

    public LogLevelConverter() {
        this.systemToNative = new EnumMap<>(LogLevel.class);
        this.nativeToSystem = new HashMap<>();
    }

    public void map(final LogLevel system, final T nativeLevel) {
        this.systemToNative.putIfAbsent(system, nativeLevel);
        this.nativeToSystem.putIfAbsent(nativeLevel, system);
    }

    public LogLevel convertNativeToSystem(final T level) {
        return this.nativeToSystem.get(level);
    }

    public T convertSystemToNative(final LogLevel level) {
        return this.systemToNative.get(level);
    }

    public Set<LogLevel> getSupported() {
        return new LinkedHashSet<>(this.nativeToSystem.values());
    }
}
