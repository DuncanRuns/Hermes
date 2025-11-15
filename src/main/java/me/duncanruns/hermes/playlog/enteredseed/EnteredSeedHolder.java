package me.duncanruns.hermes.playlog.enteredseed;

public final class EnteredSeedHolder {
    public static final ThreadLocal<String> enteredSeed = new ThreadLocal<>();

    private EnteredSeedHolder() {
    }
}
