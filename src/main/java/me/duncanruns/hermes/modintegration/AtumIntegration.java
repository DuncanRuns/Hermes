package me.duncanruns.hermes.modintegration;

import me.voidxwalker.autoreset.Atum;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BooleanSupplier;

public final class AtumIntegration {
    private static final BooleanSupplier atumRunningGetter;

    static {
        BooleanSupplier getter;
        try {
            Method isRunningMethod = Atum.class.getDeclaredMethod("isRunning");
            getter = () -> {
                try {
                    return (boolean) isRunningMethod.invoke(null);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            };
        } catch (NoSuchMethodException e) {
            try {
                Field isRunningField = Atum.class.getDeclaredField("isRunning");
                getter = () -> {
                    try {
                        return isRunningField.getBoolean(null);
                    } catch (IllegalAccessException ex) {
                        throw new RuntimeException(ex);
                    }
                };
            } catch (NoSuchFieldException ex) {
                throw new RuntimeException("Failed to find isRunning method or field from Atum", ex);
            }
        }
        atumRunningGetter = getter;
    }

    private AtumIntegration() {
    }

    public static boolean isRunning() {
        return atumRunningGetter.getAsBoolean();
    }
}
