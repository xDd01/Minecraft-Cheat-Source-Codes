package net.minecraft.client.gui.stream;

import net.minecraft.client.resources.*;
import me.satisfactory.base.gui.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.*;
import net.minecraft.util.*;
import tv.twitch.broadcast.*;
import net.minecraft.client.stream.*;

public class GuiIngestServers extends GuiScreen
{
    private final GuiScreen field_152309_a;
    private String field_152310_f;
    private ServerList field_152311_g;
    
    public GuiIngestServers(final GuiScreen p_i46312_1_) {
        this.field_152309_a = p_i46312_1_;
    }
    
    @Override
    public void initGui() {
        this.field_152310_f = I18n.format("options.stream.ingest.title", new Object[0]);
        this.field_152311_g = new ServerList(GuiIngestServers.mc);
        if (!GuiIngestServers.mc.getTwitchStream().func_152908_z()) {
            GuiIngestServers.mc.getTwitchStream().func_152909_x();
        }
        this.buttonList.add(new DarkButton(1, GuiIngestServers.width / 2 - 155, GuiIngestServers.height - 24 - 6, 150, 20, I18n.format("gui.done", new Object[0])));
        this.buttonList.add(new DarkButton(2, GuiIngestServers.width / 2 + 5, GuiIngestServers.height - 24 - 6, 150, 20, I18n.format("options.stream.ingest.reset", new Object[0])));
    }
    
    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        this.field_152311_g.func_178039_p();
    }
    
    @Override
    public void onGuiClosed() {
        if (GuiIngestServers.mc.getTwitchStream().func_152908_z()) {
            GuiIngestServers.mc.getTwitchStream().func_152932_y().func_153039_l();
        }
    }
    
    @Override
    protected void actionPerformed(final GuiButton button) {
        if (button.enabled) {
            if (button.id == 1) {
                GuiIngestServers.mc.displayGuiScreen(this.field_152309_a);
            }
            else {
                GuiIngestServers.mc.gameSettings.streamPreferredServer = "";
                GuiIngestServers.mc.gameSettings.saveOptions();
            }
        }
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        this.drawDefaultBackground();
        this.field_152311_g.drawScreen(mouseX, mouseY, partialTicks);
        Gui.drawCenteredString(this.fontRendererObj, this.field_152310_f, GuiIngestServers.width / 2, 20, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    class ServerList extends GuiSlot
    {
        public ServerList(final Minecraft mcIn) {
            super(mcIn, GuiIngestServers.width, GuiIngestServers.height, 32, GuiIngestServers.height - 35, (int)(mcIn.fontRendererObj.FONT_HEIGHT * 3.5));
            this.setShowSelectionBox(false);
        }
        
        @Override
        protected int getSize() {
            return this.mc.getTwitchStream().func_152925_v().length;
        }
        
        @Override
        protected void elementClicked(final int slotIndex, final boolean isDoubleClick, final int mouseX, final int mouseY) {
            this.mc.gameSettings.streamPreferredServer = this.mc.getTwitchStream().func_152925_v()[slotIndex].serverUrl;
            this.mc.gameSettings.saveOptions();
        }
        
        @Override
        protected boolean isSelected(final int slotIndex) {
            return this.mc.getTwitchStream().func_152925_v()[slotIndex].serverUrl.equals(this.mc.gameSettings.streamPreferredServer);
        }
        
        @Override
        protected void drawBackground() {
        }
        
        @Override
        protected void drawSlot(final int p_180791_1_, int p_180791_2_, final int p_180791_3_, final int p_180791_4_, final int p_180791_5_, final int p_180791_6_) {
            final IngestServer var7 = this.mc.getTwitchStream().func_152925_v()[p_180791_1_];
            String var8 = var7.serverUrl.replaceAll("\\{stream_key\\}", "");
            String var9 = (int)var7.bitrateKbps + " kbps";
            String var10 = null;
            final IngestServerTester var11 = this.mc.getTwitchStream().func_152932_y();
            if (var11 != null) {
                if (var7 == var11.func_153040_c()) {
                    var8 = EnumChatFormatting.GREEN + var8;
                    var9 = (int)(var11.func_153030_h() * 100.0f) + "%";
                }
                else if (p_180791_1_ < var11.func_153028_p()) {
                    if (var7.bitrateKbps == 0.0f) {
                        var9 = EnumChatFormatting.RED + "Down!";
                    }
                }
                else {
                    var9 = EnumChatFormatting.OBFUSCATED + "1234" + EnumChatFormatting.RESET + " kbps";
                }
            }
            else if (var7.bitrateKbps == 0.0f) {
                var9 = EnumChatFormatting.RED + "Down!";
            }
            p_180791_2_ -= 15;
            if (this.isSelected(p_180791_1_)) {
                var10 = EnumChatFormatting.BLUE + "(Preferred)";
            }
            else if (var7.defaultServer) {
                var10 = EnumChatFormatting.GREEN + "(Default)";
            }
            GuiIngestServers.this.drawString(GuiIngestServers.this.fontRendererObj, var7.serverName, p_180791_2_ + 2, p_180791_3_ + 5, 16777215);
            GuiIngestServers.this.drawString(GuiIngestServers.this.fontRendererObj, var8, p_180791_2_ + 2, p_180791_3_ + GuiIngestServers.this.fontRendererObj.FONT_HEIGHT + 5 + 3, 3158064);
            GuiIngestServers.this.drawString(GuiIngestServers.this.fontRendererObj, var9, this.getScrollBarX() - 5 - GuiIngestServers.this.fontRendererObj.getStringWidth(var9), p_180791_3_ + 5, 8421504);
            if (var10 != null) {
                GuiIngestServers.this.drawString(GuiIngestServers.this.fontRendererObj, var10, this.getScrollBarX() - 5 - GuiIngestServers.this.fontRendererObj.getStringWidth(var10), p_180791_3_ + 5 + 3 + GuiIngestServers.this.fontRendererObj.FONT_HEIGHT, 8421504);
            }
        }
        
        @Override
        protected int getScrollBarX() {
            return super.getScrollBarX() + 15;
        }
    }
}