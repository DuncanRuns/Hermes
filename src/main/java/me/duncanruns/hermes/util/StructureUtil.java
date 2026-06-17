//? if <=1.12.2 {
/*package me.duncanruns.hermes.util;

import java.util.*;

public final class StructureUtil {
    private static final Set<String> STRUCTURE_NAMES = new HashSet<>();

    public static synchronized Collection<String> getStructureNames() {
        return new HashSet<>(STRUCTURE_NAMES);
    }

    public static synchronized void addStructureName(String structureName) {
        STRUCTURE_NAMES.add(structureName);
    }
}
*///?}