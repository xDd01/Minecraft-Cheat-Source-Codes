/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.command;

import net.minecraft.client.Minecraft;

public interface ICommand {
    public static final Minecraft mc = Minecraft.getMinecraft();

    public void handle(String[] var1);
}

