package me.duncanruns.hermes.playlog;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.duncanruns.hermes.core.HermesCore;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModOrigin;

public final class PlayLogInstanceInfo {
    private static final JsonObject INFO = new JsonObject();

    private PlayLogInstanceInfo() {
    }

    static {
        JsonArray mods = new JsonArray();
        FabricLoader.getInstance().getAllMods().forEach(modContainer -> {
            JsonObject modObject = new JsonObject();
            modObject.addProperty("name", modContainer.getMetadata().getName());
            modObject.addProperty("id", modContainer.getMetadata().getId());
            modObject.addProperty("version", modContainer.getMetadata().getVersion().getFriendlyString());
            modObject.addProperty("nested", modContainer.getOrigin().getKind() == ModOrigin.Kind.NESTED);
            mods.add(modObject);
        });
        INFO.add("mods", mods);
        INFO.addProperty("server", !HermesCore.IS_CLIENT);
    }

    public static JsonObject get() {
        return INFO;
    }
}
