/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.viamcp.loader;

import cafe.corrosion.viamcp.ViaMCP;
import com.viaversion.viabackwards.api.ViaBackwardsPlatform;
import java.io.File;
import java.util.logging.Logger;

public class MCPBackwardsLoader
implements ViaBackwardsPlatform {
    private final File file;

    public MCPBackwardsLoader(File file) {
        this.file = new File(file, "ViaBackwards");
        this.init(this.file);
    }

    @Override
    public Logger getLogger() {
        return ViaMCP.getInstance().getjLogger();
    }

    @Override
    public void disable() {
    }

    @Override
    public boolean isOutdated() {
        return false;
    }

    @Override
    public File getDataFolder() {
        return new File(this.file, "config.yml");
    }
}

