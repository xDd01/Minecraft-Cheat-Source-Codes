package org.neverhook.client.ui.components.draggable.impl;

import com.mojang.realmsclient.gui.ChatFormatting;
import org.neverhook.client.NeverHook;
import org.neverhook.client.feature.impl.hud.HUD;
import org.neverhook.client.helpers.misc.ClientHelper;
import org.neverhook.client.ui.components.draggable.DraggableModule;
import org.neverhook.security.utils.LicenseUtil;

public class ClientInfoComponent extends DraggableModule {

    public ClientInfoComponent() {
        super("ClientInfoComponent", sr.getScaledWidth() - mc.robotoRegularFontRender.getStringWidth(NeverHook.instance.type + " - " + ChatFormatting.WHITE + NeverHook.instance.version + ChatFormatting.RESET + " - " + LicenseUtil.userName), sr.getScaledHeight() - 20);
    }

    @Override
    public int getWidth() {
        return 120;
    }

    @Override
    public int getHeight() {
        return 15;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        if (HUD.clientInfo.getBoolValue()) {

            String buildStr = NeverHook.instance.type + " - " + ChatFormatting.WHITE + NeverHook.instance.version + ChatFormatting.RESET + " - " + LicenseUtil.userName;
            if (mc.player != null && mc.world != null) {
                mc.robotoRegularFontRender.drawStringWithShadow(buildStr, getX(), getY(), ClientHelper.getClientColor().getRGB());

            }
        }
        super.render(mouseX, mouseY);
    }

    @Override
    public void draw() {
        if (HUD.clientInfo.getBoolValue()) {

            String buildStr = NeverHook.instance.type + " - " + ChatFormatting.WHITE + NeverHook.instance.version + ChatFormatting.RESET + " - " + LicenseUtil.userName;
            if (mc.player != null && mc.world != null) {
                mc.robotoRegularFontRender.drawStringWithShadow(buildStr, getX(), getY(), ClientHelper.getClientColor().getRGB());
            }
        }
        super.draw();
    }
}