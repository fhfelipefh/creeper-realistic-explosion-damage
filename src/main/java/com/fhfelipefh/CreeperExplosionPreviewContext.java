package com.fhfelipefh;

import java.util.function.Supplier;

public final class CreeperExplosionPreviewContext {
    private static final ThreadLocal<Boolean> BYPASS = ThreadLocal.withInitial(() -> Boolean.FALSE);

    private CreeperExplosionPreviewContext() {
    }

    public static boolean isBypassing() {
        return BYPASS.get();
    }

    public static <T> T withoutModifications(Supplier<T> action) {
        boolean previous = BYPASS.get();
        BYPASS.set(true);
        try {
            return action.get();
        } finally {
            BYPASS.set(previous);
        }
    }
}
