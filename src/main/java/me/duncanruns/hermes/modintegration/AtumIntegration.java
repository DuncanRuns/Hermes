package me.duncanruns.hermes.modintegration;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.BooleanSupplier;

public final class AtumIntegration {
    private static final BooleanSupplier atumRunningGetter;
    private static final boolean atumRunningExists;

    static {
        Optional<BooleanSupplier> isRunningSupplier = getIsRunningSupplier();
        atumRunningExists = isRunningSupplier.isPresent();
        atumRunningGetter = isRunningSupplier.orElse(null);
    }

    private static @NotNull Optional<BooleanSupplier> getIsRunningSupplier() {
        Class<?> atumClass;
        try {
            atumClass = Class.forName("me.voidxwalker.autoreset.Atum");
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
        try {
            Method isRunningMethod = atumClass.getDeclaredMethod("isRunning");
            return Optional.of(() -> {
                try {
                    return (boolean) isRunningMethod.invoke(null);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (NoSuchMethodException e) {
            try {
                Field isRunningField = atumClass.getDeclaredField("isRunning");
                return Optional.of(() -> {
                    try {
                        return isRunningField.getBoolean(null);
                    } catch (IllegalAccessException ex) {
                        throw new RuntimeException(ex);
                    }
                });
            } catch (NoSuchFieldException ex) {
                return Optional.empty();
            }
        }
    }

    private AtumIntegration() {
    }

    public static boolean isRunning() {
        return atumRunningExists && atumRunningGetter.getAsBoolean();
    }
}
