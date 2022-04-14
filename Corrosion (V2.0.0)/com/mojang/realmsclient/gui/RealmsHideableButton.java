/*
 * Decompiled with CFR 0.152.
 */
package com.mojang.realmsclient.gui;

import net.minecraft.realms.RealmsButton;

public class RealmsHideableButton
extends RealmsButton {
    boolean visible = true;

    public RealmsHideableButton(int id2, int x2, int y2, int width, int height, String msg) {
        super(id2, x2, y2, width, height, msg);
    }

    @Override
    public void render(int xm2, int ym2) {
        if (!this.visible) {
            return;
        }
        super.render(xm2, ym2);
    }

    @Override
    public void clicked(int mx2, int my2) {
        if (!this.visible) {
            return;
        }
        super.clicked(mx2, my2);
    }

    @Override
    public void released(int mx2, int my2) {
        if (!this.visible) {
            return;
        }
        super.released(mx2, my2);
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean getVisible() {
        return this.visible;
    }
}

