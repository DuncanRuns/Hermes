//? if <=1.12.2 {
/*package me.duncanruns.hermes.playlog;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class StructureHelper {
    private static final Set<String> STRUCTURE_NAMES = new HashSet<>();

    public static synchronized Collection<String> getStructureNames() {
        return new HashSet<>(STRUCTURE_NAMES);
    }

    public static synchronized void addStructureName(String structureName) {
        STRUCTURE_NAMES.add(structureName);
    }
}
*///?}