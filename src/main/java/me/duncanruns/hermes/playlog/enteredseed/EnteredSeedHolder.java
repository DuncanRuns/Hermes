package me.duncanruns.hermes.playlog.enteredseed;

public final class EnteredSeedHolder {
    private EnteredSeedHolder() {
    }

    public static final ThreadLocal<String> enteredSeed = new ThreadLocal<>();
}
