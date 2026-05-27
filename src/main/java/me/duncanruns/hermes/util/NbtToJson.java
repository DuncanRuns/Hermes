//? if <=1.12.2 {
/*package me.duncanruns.hermes.util;

import com.google.gson.*;
import net.minecraft.nbt.*;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/^*
 * Since 1.12.2 doesn't ship with fancy codecs or dynamic or ops, we gotta do it the old-fashioned way...
 ^/
public final class NbtToJson {
    private NbtToJson() {
    }

    public static JsonElement convert(NbtElement element) {
        switch (element.getType()) {
            case 1:
                return new JsonPrimitive(((NbtByte) element).getByte());
            case 2:
                return new JsonPrimitive(((NbtShort) element).getShort());
            case 3:
                return new JsonPrimitive(((NbtInt) element).getInt());
            case 4:
                return new JsonPrimitive(((NbtLong) element).getLong());
            case 5:
                return new JsonPrimitive(((NbtFloat) element).getFloat());
            case 6:
                return new JsonPrimitive(((NbtDouble) element).getDouble());
            case 7:
                return convertByteArray((NbtByteArray) element);
            case 8:
                return new JsonPrimitive(((NbtString) element).asString());
            case 9:
                return convertList((NbtList) element);
            case 10:
                return convertCompound((NbtCompound) element);
            case 11:
                return convertIntArray((NbtIntArray) element);
            // case 12 is a long array, which seems to go unused and doesn't have a public method exposing the values
            default:
                return JsonNull.INSTANCE;
        }
    }

    public static JsonObject convertCompound(NbtCompound compound) {
        JsonObject out = new JsonObject();
        Set<String> keys = compound.getKeys();
        for (String key : keys) {
            out.add(key, convert(compound.get(key)));
        }
        return out;
    }

    private static @NotNull JsonArray convertList(NbtList nbtList) {
        JsonArray out = new JsonArray(nbtList.size());
        for (int i = 0; i < nbtList.size(); i++) {
            out.add(convert(nbtList.getElement(i)));
        }
        return out;
    }

    private static @NotNull JsonArray convertIntArray(NbtIntArray element) {
        int[] ints = element.getIntArray();
        JsonArray jsonArray = new JsonArray(ints.length);
        for (int i : ints) jsonArray.add(i);
        return jsonArray;
    }

    private static @NotNull JsonArray convertByteArray(NbtByteArray element) {
        byte[] bytes = element.getByteArray();
        JsonArray jsonArray = new JsonArray(bytes.length);
        for (byte b : bytes) jsonArray.add(b);
        return jsonArray;
    }
}
*///?}