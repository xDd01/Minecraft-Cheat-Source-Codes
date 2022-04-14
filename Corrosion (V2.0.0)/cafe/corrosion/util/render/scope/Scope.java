/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.render.scope;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.lwjgl.opengl.GL11;

public interface Scope {
    public void scope(Runnable var1);

    public static Scope enable(Integer ... capabilities) {
        Set caps = Arrays.stream(capabilities).filter(c2 -> !GL11.glIsEnabled(c2)).collect(Collectors.toSet());
        return f2 -> {
            caps.forEach(GL11::glEnable);
            f2.run();
            caps.forEach(GL11::glDisable);
        };
    }

    public static Scope disable(Integer ... capabilities) {
        Set caps = Arrays.stream(capabilities).filter(GL11::glIsEnabled).collect(Collectors.toSet());
        return f2 -> {
            caps.forEach(GL11::glDisable);
            f2.run();
            caps.forEach(GL11::glEnable);
        };
    }
}

