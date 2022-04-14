/*
 * Decompiled with CFR 0.152.
 */
package com.mojang.realmsclient.gui;

import net.minecraft.realms.RealmsButton;

public interface GuiCallback {
    public void tick();

    public void buttonClicked(RealmsButton var1);
}

