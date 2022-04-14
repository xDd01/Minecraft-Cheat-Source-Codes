package me.spec.eris.client.modules.render;

import java.awt.Color;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.api.value.types.BooleanValue;
import me.spec.eris.api.value.types.ModeValue;
import me.spec.eris.api.value.types.NumberValue;
import me.spec.eris.client.events.player.EventUpdate;
import me.spec.eris.client.modules.combat.Killaura;
import me.spec.eris.client.ui.hud.CustomHUD;
import me.spec.eris.utils.math.MathUtils;
import me.spec.eris.utils.player.PlayerUtils;
import me.spec.eris.utils.visual.ColorUtilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import com.mojang.realmsclient.gui.ChatFormatting;

import me.spec.eris.Eris;
import me.spec.eris.api.event.Event;
import me.spec.eris.client.events.render.EventRender2D;
import me.spec.eris.api.module.Module;
import me.spec.eris.client.ui.fonts.TTFFontRenderer;
import me.spec.eris.utils.visual.RenderUtilities;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class HUD extends Module {

    private BooleanValue<Boolean> blocksPerSecond = new BooleanValue<>("BPS", true, this, "Shows BPS");
    private BooleanValue<Boolean> ping = new BooleanValue<>("Ping", true, this, "Shows Ping");
    private BooleanValue<Boolean> coordinates = new BooleanValue<>("Coordinates", true, this, "Shows Coords");
    private BooleanValue<Boolean> label = new BooleanValue<>("Watermark", true, this, true, "Shows Watermark");
    public BooleanValue<Boolean> labelTime = new BooleanValue<>("Watermark Time", true, this, () -> label.getValue(), "Shows Time In Watermark");
    private BooleanValue<Boolean> buildInfo = new BooleanValue<>("Build Info", true, this, "Shows UID And Build");
    private BooleanValue<Boolean> potions = new BooleanValue<>("Potions", true, this, "Shows Potion Effects");
    private BooleanValue<Boolean> killLeaderboard = new BooleanValue<>("Kill Leaderboard", true, this, "Shows Kill Leaderboard");
    public ModeValue<BPSMode> bpsType = new ModeValue<>("BPS Formatting", BPSMode.BPS, this, true, () -> blocksPerSecond.getValue(), "BPS Formatting");
    public NumberValue<Integer> bpsPlaces = new NumberValue<>("BPS Rounding", 3, 1, 10, this, () -> blocksPerSecond.getValue(), "Rounding For BPS");

    public BooleanValue<Boolean> customFontChat = new BooleanValue<>("Chat Font", true, this, true, "Ingame Chat Custom Font");
    public NumberValue<Integer> customChatOpacity = new NumberValue<>("Chat Opacity", 145, 1, 200, this, () -> customFontChat.getValue(), "Chat Background Opacity");

    private BooleanValue<Boolean> customClientColor = new BooleanValue<>("Custom Client Color", true, this, true, "Change client color");
    private NumberValue<Integer> customClientColorRed = new NumberValue<>("Custom Color Red", 255, 0, 255, this, () -> customClientColor.getValue(), "RED For Client Color");
    private NumberValue<Integer> customClientColorGreen = new NumberValue<>("Custom Color Green", 0, 0, 255, this, () -> customClientColor.getValue(), "GREEN For Client Color");
    private NumberValue<Integer> customClientColorBlue = new NumberValue<>("Custom Color Blue", 0, 0, 255, this, () -> customClientColor.getValue(), "BLUE For Client Color");

   private BooleanValue<Boolean> arraylist = new BooleanValue<>("Arraylist", true, this, true, "Shows Arraylist");
    private BooleanValue<Boolean> arraylistBackground = new BooleanValue<>("Arraylist Background", true, this, () -> arraylist.getValue(), "Backdrop On Arraylist");
    private NumberValue<Integer> arraylistBackgroundOpacity = new NumberValue<>("Background Opacity", 145, 1, 200, this, () -> arraylistBackground.getValue() && arraylist.getValue(), "Background Opacity");
    private ModeValue<ColorMode> colorMode = new ModeValue<>("Arraylist Color", ColorMode.STATIC, this, true, () -> arraylist.getValue(), "Arraylist Color");

    private NumberValue<Double> rainSpeed = new NumberValue<>("Speed", 3d, 1d, 6d, this, () -> colorMode.getValue().equals(ColorMode.RAINBOW) && arraylist.getValue(), "Rainbow Speed");
    private NumberValue<Double> rainOffset = new NumberValue<>("Offset", 2d, 1d, 6d, this, () -> colorMode.getValue().equals(ColorMode.RAINBOW) && arraylist.getValue(), "Rainbow Offset");
    private NumberValue<Double> saturation = new NumberValue<>("Saturation", 1d, 0d, 1d, this, () -> colorMode.getValue().equals(ColorMode.RAINBOW) && arraylist.getValue(), "Rainbow Saturation");
    private NumberValue<Double> brightness = new NumberValue<>("Brightness", 1d, 0d, 1d, this, () -> colorMode.getValue().equals(ColorMode.RAINBOW) && arraylist.getValue(), "Rainbow Brightness");

    private NumberValue<Integer> red = new NumberValue<>("Red", 255, 0, 255, this, () -> colorMode.getValue().equals(ColorMode.STATIC) && arraylist.getValue(), "RED for Static ArrayList Color");
    private NumberValue<Integer> green = new NumberValue<>("Green", 0, 0, 255, this, () -> colorMode.getValue().equals(ColorMode.STATIC) && arraylist.getValue(), "GREEN for Static ArrayList Color");
    private NumberValue<Integer> blue = new NumberValue<>("Blue", 0, 0, 255, this, () -> colorMode.getValue().equals(ColorMode.STATIC) && arraylist.getValue(), "BLUE for Static ArrayList Color");

    public enum ColorMode {
        STATIC, RAINBOW, RANDOMIZED;
    }

    public enum BPSMode {
        BPS, BPERS;
    }
    private float animated = 20f;
    private int targetX = 400, targetY = 300, bpsX = 0, bpsY = 440, playerListX = 30, playerListY = 30, pingX = 0, pingY = 425, coordX = 0, coordY= 425, labelX = 2, labelY = 2, buildInfoX = 0, buildInfoY = 400, size = 16, potionsX = 37, potionsY = (new ScaledResolution(Minecraft.getMinecraft()).getScaledHeight() - (230) - size * 2) - 5, moduleListX = new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth(), moduleListY = 0;
    private double lastPosX;
    private double lastPosZ;
    public ArrayList<Double> distances;
    private int y;
    private static TTFFontRenderer fontRender;

    public HUD(String racism) {
        super("HUD", ModuleCategory.RENDER, racism);
        this.lastPosX = Double.NaN;
        this.lastPosZ = Double.NaN;
        this.distances = new ArrayList<Double>();
    }

    public static TTFFontRenderer getFont() {
        if (fontRender == null) {
            fontRender = Eris.INSTANCE.fontManager.getFont("SFUI 18");
        }

        return fontRender;
    }

    int yText = 3;
    int yPos = 3;

    @Override
    public void onEvent(Event e) {
        if(customClientColor.getValue()) {
            Eris.getInstance().setClientColor(new Color(customClientColorRed.getValue(), customClientColorGreen.getValue(), customClientColorBlue.getValue()));
        }
        if (e instanceof EventUpdate) {
            if (!Double.isNaN(this.lastPosX) && !Double.isNaN(this.lastPosZ)) {
                final double differenceX = Math.abs(this.lastPosX - Minecraft.getMinecraft().thePlayer.posX);
                final double differenceZ = Math.abs(this.lastPosZ - Minecraft.getMinecraft().thePlayer.posZ);
                final double distance = Math.sqrt(differenceX * differenceX + differenceZ * differenceZ) * 2.0;
                this.distances.add(distance);
                if (this.distances.size() > 20) {
                    this.distances.remove(0);
                }
            }
            this.lastPosX = Minecraft.getMinecraft().thePlayer.posX;
            this.lastPosZ = Minecraft.getMinecraft().thePlayer.posZ;
        }
        if (e instanceof EventRender2D) {
            if(label.getValue()) {
                renderLabel(labelX, labelY);
            }
            if(arraylist.getValue()) {
                renderModuleList(moduleListX, moduleListY);
            }
            if(coordinates.getValue()) {
                renderCoords(coordX, coordY);
            }
            if(buildInfo.getValue()) {
                renderBuildInfo(buildInfoX, buildInfoY);
            }
            if(potions.getValue()) {
                renderPotions(potionsX, potionsY);
            }
            if (blocksPerSecond.getValue()) {
                renderBPS(bpsX, bpsY);
            }
            if(ping.getValue()) {
                renderPing(pingX, pingY);
            }
            if(killLeaderboard.getValue()) {
                renderPlayerlist(playerListX, playerListY);
            }
            renderTarget(targetX, targetY);
        }
    }

    public int[] renderModuleList(int xPos, int yPos) {
        moduleListX = xPos;
        moduleListY = yPos;
        yText = moduleListY;
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        List<Module> modulesForRender = Eris.getInstance().moduleManager.getModulesForRender();
        int width = 0;
        int height = 0;
        modulesForRender.sort((b, a) -> Double.compare(getFont().getStringWidth(a.getFullModuleDisplayName()), getFont().getStringWidth(b.getFullModuleDisplayName())));

        if (!modulesForRender.isEmpty()) {
            width = (int) getFont().getStringWidth(modulesForRender.get(0).getFullModuleDisplayName());
            GlStateManager.pushMatrix();
            GlStateManager.scale(1, 1f, 1);

            y = moduleListY;

            modulesForRender.forEach(mod -> {
                String name = mod.getFullModuleDisplayName();

                double x = moduleListX - getFont().getStringWidth(name);

                if(arraylistBackground.getValue()) {
                    RenderUtilities.drawRectangle(x - 2, y, (double) getFont().getStringWidth(name) + 2, getFont().getHeight(name) + 2, new Color(0, 0, 0, arraylistBackgroundOpacity.getValue().intValue()).getRGB());
                }
                switch (colorMode.getValue()) {
                    case RAINBOW: {
                        getFont().drawStringWithShadow(name, (float) x, y, ColorUtilities.getRainbow(6000, -15 * yText, rainSpeed.getValue(), rainOffset.getValue(), saturation.getValue().floatValue(), brightness.getValue().floatValue()));
                        break;
                    }

                    case STATIC: {
                        getFont().drawStringWithShadow(name, (float) x, y, new Color(red.getValue(), green.getValue(), blue.getValue()).getRGB());
                        break;
                    }
                    case RANDOMIZED: {
                        getFont().drawStringWithShadow(name, (float) x, y, mod.getColor());
                        break;
                    }
                }

                y += getFont().getHeight(name) + 2;
                yText += 12;
            });
            height = y;
            GlStateManager.popMatrix();
        }
        return new int[]{width, height};
    }

    public void renderTarget(int x, int y) {
        EntityLivingBase currentEntity = Killaura.target;
        targetX = x;
        targetY = y;
        int width = 250;
        Killaura aura = (Killaura) Eris.getInstance().getModuleManager().getModuleByClass(Killaura.class);
        if (mc.currentScreen instanceof CustomHUD) {
            RenderUtilities.drawRoundedRect(targetX, targetY, targetX + 250, targetY - 75, new Color(255,90,90,100).getRGB(), new Color(0,0,0, 127).getRGB());
        } else {

            if (currentEntity != null && Killaura.getTarget() != null) {
                String name = "Name: " + (currentEntity instanceof EntityPlayer ? currentEntity.getDisplayName().getFormattedText() : currentEntity.getName());
                String reach = "Reach: " + String.valueOf(MathUtils.round((double) mc.thePlayer.getDistanceToEntity(currentEntity), 2));
                String armor = "Armor: " + Math.round(currentEntity.getTotalArmorValue());
                String hasBetterArmor = "";
                if (currentEntity.getTotalArmorValue() > Minecraft.getMinecraft().thePlayer.getTotalArmorValue()) {
                    hasBetterArmor = "Them";
                } else if (currentEntity.getTotalArmorValue() == Minecraft.getMinecraft().thePlayer.getTotalArmorValue()) {
                    hasBetterArmor = "None";
                } else {
                    hasBetterArmor = "You";
                }

                String moreHealth = "Health Advantage: ";
                if (currentEntity.getHealth() > mc.thePlayer.getHealth()) {
                    moreHealth += "Them: " + EnumChatFormatting.GRAY + MathUtils.round((double) (Math.abs(currentEntity.getHealth() - mc.thePlayer.getHealth()) / 2), 2);
                } else if (currentEntity.getTotalArmorValue() == mc.thePlayer.getHealth() || Math.abs(currentEntity.getHealth() - mc.thePlayer.getHealth()) / 2 == 0.0) {
                    moreHealth += "None";
                } else {
                    moreHealth += "You: " + EnumChatFormatting.GRAY + MathUtils.round((double) (Math.abs(currentEntity.getHealth() - mc.thePlayer.getHealth()) / 2), 2);
                }

                width = 170;
                RenderUtilities.drawRoundedRect(targetX, targetY, targetX + width, targetY - 75, new Color(255,90,90,100).getRGB(), new Color(0,0,0, 127).getRGB());

                getFont().drawStringWithShadow(name, targetX + 45, targetY - 70, new Color(255,255,255).getRGB());
                getFont().drawStringWithShadow(reach, targetX + 45, targetY - 60, new Color(255,255,255).getRGB());
                getFont().drawStringWithShadow(armor, targetX + 45, targetY - 50, new Color(255,255,255).getRGB());

                getFont().drawStringWithShadow("Armor Advantage: " + hasBetterArmor, targetX + 45, targetY - 30, new Color(255,255,255).getRGB());
                if (!Float.isNaN(currentEntity.getHealth())) {
                    float xSpeed = (150 - 45) / (mc.debugFPS * 1.2f);
                    float desiredWidth = (150 - 45) / currentEntity.getMaxHealth()
                            * Math.min(currentEntity.getHealth(), currentEntity.getMaxHealth());
                    if (desiredWidth < animated || desiredWidth > animated) {
                        if (Math.abs(desiredWidth - animated) <= xSpeed) {
                            animated = desiredWidth;
                        } else {
                            animated += (animated < desiredWidth ? xSpeed * 4 : -xSpeed);
                        }
                    }
                    double hpPercentage = currentEntity.isDead ? 0 : currentEntity.getHealth() / 20;
                    if (hpPercentage > 1)
                        hpPercentage = 1;
                    else if (hpPercentage < 0)
                        hpPercentage = 0;


                    int r = (int) (230 + (50 - 230) * hpPercentage);
                    int g = (int) (50 + (230 - 50) * hpPercentage);
                    int b = 50;

                    getFont().drawStringWithShadow(moreHealth, targetX + 45, targetY - 20, new Color(255,255,255).getRGB());
                    RenderUtilities.drawRoundedRect(targetX + 44, targetY - 3, targetX + 151, targetY - 8, new Color(255,90,90,100).getRGB(), new Color(0,0,0,90).getRGB());
                    RenderUtilities.drawRoundedRect(targetX + 45, targetY - 3, targetX + 45 + animated, targetY - 8, new Color(255,90,90,100).getRGB(), new Color(r,g,b).getRGB());
                } else {
                    getFont().drawStringWithShadow("Health is a NaN! (Bot?)", targetX + 35, targetY - 16, new Color(255,255,255).getRGB());
                    RenderUtilities.drawRoundedRect(targetX + 30, targetY - 5, targetX + 150, targetY - 20, new Color(255,90,90,100).getRGB(), new Color(0,0,0, 127).getRGB());
                }
                RenderUtilities.drawRoundedRect(targetX + 5, targetY - 7, targetX + 45, targetY - 70, new Color(255,90,90,100).getRGB(), new Color(0,0,0, 127).getRGB());
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glColor4f(1, 1, 1, 1);
                drawEntityOnScreen(targetX + 25, targetY - 10, 30, currentEntity.rotationYaw, -currentEntity.rotationPitch, currentEntity);
                GL11.glDisable(GL11.GL_BLEND);
            }
        }
    }

    public static void drawEntityOnScreen(double d, double e, int scale, float mouseX, float mouseY, EntityLivingBase ent) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) d, (float) e, 50.0F);
        GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        ent.renderYawOffset = (float) Math.atan((double) (mouseX / 40.0F)) * 20.0F;
        ent.rotationYaw = (float) Math.atan((double) (mouseX / 40.0F)) * 40.0F;
        ent.rotationPitch = -((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntityWithPosYaw(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
        rendermanager.setRenderShadow(true);
        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    public void renderCoords(int x, int y) {
        coordX = x;
        coordY = y;
        String coords = "XYZ" + EnumChatFormatting.GRAY + ": " + Math.round(mc.thePlayer.posX) + EnumChatFormatting.WHITE + ", " + EnumChatFormatting.GRAY + Math.round(mc.thePlayer.posY) + EnumChatFormatting.WHITE + ", " + EnumChatFormatting.GRAY + Math.round(mc.thePlayer.posZ);
        getFont().drawStringWithShadow(coords, coordX, (mc.ingameGUI.getChatGUI().getChatOpen() && MathUtils.isInRange(coordY, 400, 445) ? coordY - 10 : coordY), Eris.getInstance().getClientColor());
    }

    public void renderBPS(int x, int y) {
        bpsX = x;
        bpsY = y;
        String bps = bpsType.getValue().equals(BPSMode.BPS) ? "BPS" + EnumChatFormatting.GRAY + ": " + EnumChatFormatting.RESET + MathUtils.round(PlayerUtils.getDistTraveled(distances), bpsPlaces.getValue() != null ? bpsPlaces.getValue() : 3) : "Blocks per/s" + EnumChatFormatting.GRAY + ": " + EnumChatFormatting.RESET + MathUtils.round(PlayerUtils.getDistTraveled(distances), bpsPlaces.getValue() != null ? bpsPlaces.getValue() : 3);
        getFont().drawStringWithShadow(bps, x, y, Eris.getInstance().getClientColor());
    }

    public void renderPing(int x, int y) {
        pingX = x;
        pingY = y;
        String ping = "Ping" + EnumChatFormatting.GRAY + ": " + EnumChatFormatting.RESET + PlayerUtils.getPlayerPing();
        getFont().drawStringWithShadow(ping, x, y, Eris.getInstance().getClientColor());
    }

    public void renderBuildInfo(int x, int y) {
        buildInfoX = x;
        buildInfoY = y;
        getFont().drawStringWithShadow("Build" + EnumChatFormatting.GRAY + ": " + EnumChatFormatting.RESET + "dev" + EnumChatFormatting.GRAY + "#0001" + EnumChatFormatting.GRAY + " | " + EnumChatFormatting.WHITE + " " + Eris.getInstance().getClientBuildExperimental(), buildInfoX, (mc.ingameGUI.getChatGUI().getChatOpen() && MathUtils.isInRange(coordY, 400, 445) ? buildInfoX - 10 : buildInfoY), Eris.getInstance().getClientColor());
    }

    public void renderLabel(int x, int y) {
        labelX = x;
        labelY = y;
        getFont().drawStringWithShadow((labelTime.getValue() ? Eris.getInstance().getClientName().substring(0, 1) + EnumChatFormatting.WHITE + Eris.getInstance().getClientName().replace(Eris.getInstance().getClientName().substring(0, 1), "") + EnumChatFormatting.GRAY + " " + getTime() : Eris.getInstance().getClientName().substring(0, 1) + EnumChatFormatting.WHITE + Eris.getInstance().getClientName().replace(Eris.getInstance().getClientName().substring(0, 1), "")), labelX, labelY, Eris.getInstance().getClientColor());
    }

    public int[] renderPlayerlist(int x, int y) {
        playerListX = x;
        playerListY = y;
        int yTextLevel = playerListY;
        int yRectLevel = playerListY;
        HashMap<EntityPlayer, Integer> leaderboard = Eris.getInstance().killTracker.getPlayerKills();
        ArrayList<EntityPlayer> playerArrayList = PlayerUtils.getPlayersInDistanceForPlayerList(50, 30, 30);
        int[] dimensions = new int[]{50, 50};
        if(!playerArrayList.isEmpty()) {
            playerArrayList.sort((b, a) -> Double.compare(getFont().getStringWidth(a.getName()), getFont().getStringWidth(b.getName())));
            for(EntityPlayer player : playerArrayList) {
                yRectLevel += 10;
            }
            int width = (int) (playerListX + getFont().getStringWidth(playerArrayList.get(0).getName()) + 24);
            int height = yRectLevel;
            dimensions = new int[]{width, height};
            Gui.drawRect(playerListX, playerListY, width, height, new Color(0,0,0, 120).getRGB());
            for(EntityPlayer player : playerArrayList) {
                fontRender.drawStringWithShadow(player.getName() + EnumChatFormatting.GRAY + " (" + Math.round(mc.thePlayer.getDistanceToEntity(player)) + "m" + ")", playerListX, yTextLevel, -1);
                yTextLevel += 10;
            }
            return dimensions;
        }

        return new int[]{50, 50};
    }

    public int[] renderPotions(int x, int y) {
        potionsX = x;
        potionsY = y;
        GL11.glPushMatrix();
        Collection<?> var4 = Module.mc.thePlayer.getActivePotionEffects();
        int i = 0;
        if (!var4.isEmpty()) {
            for (PotionEffect var7 : Module.mc.thePlayer.getActivePotionEffects()) {
                Potion var8 = Potion.potionTypes[var7.getPotionID()];
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
                if (var8.hasStatusIcon()) {
                    int var9 = var8.getStatusIconIndex();
                    Gui theGui = new Gui();
                    theGui.drawTexturedModalRect((int) x, (int) y - (18 * i), var9 % 8 * 18, 198 + var9 / 8 * 18, 18, 18);
                    getFont().drawStringWithShadow("" + (var7.getDuration() <= 300 ? ChatFormatting.RED : ChatFormatting.WHITE) + Potion.getDurationString(var7), (int) x - Eris.getInstance().getFontRenderer().getStringWidth("" + Potion.getDurationString(var7)) - 5, (int) y - (18 * i) + 6, -1);
                    i++;
                    GL11.glPopMatrix();
                    return new int[]{(int) getFont().getStringWidth("" + Potion.getDurationString(var7)), (int) getFont().getHeight("" + Potion.getDurationString(var7))};

                }
            }
        }
        GL11.glPopMatrix();
        return new int[]{50,50};
    }

    public String getTime() {
        LocalTime localTime = LocalTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        return localTime.format(dtf);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }


}