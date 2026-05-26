//? if <=1.15.2 {
/*package me.duncanruns.hermes.playlog.creation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class PlayLogCreationSettings {
    public Boolean allowCommands;
    public Boolean bonusChest;
    public JsonElement levelTypeOptions;

    public static void addSettings(Object levelSettingsObj, JsonObject object) {
        ((Holder) levelSettingsObj).hermes$getCreationSettings().addSettings(object);
    }

    public void addSettings(JsonObject object) {
        if (bonusChest != null) object.addProperty("bonus_chest", bonusChest);
        if (allowCommands != null) object.addProperty("allow_commands", allowCommands);
        if (levelTypeOptions != null) object.add("level_type_options", levelTypeOptions);
    }

    public interface Holder {
        PlayLogCreationSettings hermes$getCreationSettings();
    }
}
*///?}