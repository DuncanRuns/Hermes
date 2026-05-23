package me.duncanruns.hermes.instancestate.updaters.client;

import com.google.gson.JsonObject;
import me.duncanruns.hermes.HermesMod;
import net.minecraft.client.Minecraft;

import java.util.function.BiConsumer;


/**
 * Instance state field for client's currently opened screen
 */
public class ScreenStateUpdater implements BiConsumer<JsonObject, Minecraft> {
    @Override
    public void accept(JsonObject json, Minecraft client) {
        json.add("screen", HermesMod.screenToJsonObject(client.screen));
    }
}
