package me.duncanruns.hermes.playlog;

import java.util.Optional;

public interface PlayLogServer {
    Optional<PlayLog> hermes$getPlayLog();

    String hermes$takeEnteredSeed();
}
