package me.duncanruns.hermes.playlog.creation;

public final class LevelSettingsHolder {
    private LevelSettingsHolder() {
    }

    public static ThreadLocal<Object> lastWorldSettings = new ThreadLocal<>();

    public static Object take(){
        Object out = lastWorldSettings.get();
        lastWorldSettings.remove();
        return out;
    }
}
