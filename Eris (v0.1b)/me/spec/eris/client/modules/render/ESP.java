package me.spec.eris.client.modules.render;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import me.spec.eris.Eris;
import me.spec.eris.api.module.ModuleCategory;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import me.spec.eris.api.event.Event;
import me.spec.eris.client.events.render.EventRender3D;
import me.spec.eris.client.events.render.EventRenderSR;
import me.spec.eris.api.module.Module;
import me.spec.eris.client.modules.combat.AntiBot;
import me.spec.eris.api.value.types.BooleanValue;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class ESP extends Module {

    private final Map<EntityPlayer, float[]> entityPosMap = new HashMap<>();
    private static Map<EntityPlayer, float[][]> entities = new HashMap<>();

    private final FloatBuffer windowPosition = BufferUtils.createFloatBuffer(4);
    private final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    private final FloatBuffer modelMatrix = GLAllocation.createDirectFloatBuffer(16);
    private final FloatBuffer projectionMatrix = GLAllocation.createDirectFloatBuffer(16);

    public ESP(String racism) {
        super("ESP", ModuleCategory.RENDER, racism);
    }

    public BooleanValue<Boolean> box = new BooleanValue<>("2D Box", false, this, "");
    public BooleanValue<Boolean> healthbar = new BooleanValue<>("Health Bar", false, this, "");
    public BooleanValue<Boolean> nametags = new BooleanValue<>("Nametags", false, this, "");
    public BooleanValue<Boolean> skeletal = new BooleanValue<>("Skeletal", false, this, "");
    public BooleanValue<Boolean> chests = new BooleanValue<>("Chests", false, this, "");


    @Override
    public void onEvent(Event e) {
        if (e instanceof EventRenderSR) {
            EventRenderSR event = (EventRenderSR) e;
            for (EntityPlayer player : entityPosMap.keySet()) {
                GL11.glPushMatrix();
                mc.entityRenderer.setupOverlayRendering(event.sr);
                float[] positions = entityPosMap.get(player);
                float x = positions[0];
                float x2 = positions[1];
                float y = positions[2];
                float y2 = positions[3];

                if (healthbar.getValue()) {
                    Gui.drawRect(x - 2.5, y - 0.5F, x - 0.5F, y2 + 0.5F, 0x96000000);

                    float health = player.getHealth();
                    float maxHealth = player.getMaxHealth();
                    float healthPercentage = health / maxHealth;

                    boolean needScissor = health < maxHealth;
                    float heightDif = y - y2;
                    float healthBarHeight = heightDif * healthPercentage;

                    if (needScissor)
                        startScissorBox(event.sr, (int) x - 2, (int) (y2 + healthBarHeight), 2, (int) -healthBarHeight + 1);
                    int col = getColorFromPercentage(health, maxHealth);
                    Gui.drawGradientRect(x - 2, y, x - 1, y2, col, col);

                    if (needScissor)
                        endScissorBox();
                }
                if (nametags.getValue()) {
                    String text = player.getDisplayName().getUnformattedText();
                    float xDif = x2 - x;
                    float minScale = 0.65F;
                    float scale = Math.max(minScale,
                            Math.min(1.0F, 1.0F - (mc.thePlayer.getDistanceToEntity(player) / 100.0F)));
                    float yOff = Math.max(0.0F,
                            Math.min(1.0F, mc.thePlayer.getDistanceToEntity(player) / 12.0F));
                    float upscale = 1.0F / scale;
                    GL11.glPushMatrix();
                    GL11.glScalef(scale, scale, scale);
                    Eris.getInstance().getFontRenderer().drawStringWithShadow(text, (x + xDif / 2.0F) * upscale - Eris.getInstance().getFontRenderer().getStringWidth(text) / 2.0F, (y - 9 + yOff) * upscale, -1);


                    GL11.glScalef(1.0F, 1.0F, 1.0F);
                    GL11.glPopMatrix();
                }
                if (box.getValue()) {
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    enableAlpha();
                    GL11.glColor4f(255F, 0F, 0F, 1.0F);
                    GL11.glBegin(GL11.GL_LINE_LOOP);
                    GL11.glVertex2f(x, y);
                    GL11.glVertex2f(x, y2);
                    GL11.glVertex2f(x2, y2);
                    GL11.glVertex2f(x2, y);
                    GL11.glEnd();
                    disableAlpha();
                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                }
                GL11.glPopMatrix();
            }
        }

        if (e instanceof EventRender3D) {
            EventRender3D event = (EventRender3D) e;
            entities.keySet().removeIf(player -> !mc.theWorld.playerEntities.contains(player));

            if (!entityPosMap.isEmpty())
                entityPosMap.clear();

            if (box.getValue() || healthbar.getValue() || nametags.getValue()) {
                int scaleFactor = event.scaledResolution.getScaleFactor();
                for (EntityPlayer player : mc.theWorld.playerEntities) {
                    if (player.getDistanceToEntity(mc.thePlayer) < 1.0F || AntiBot.bots.contains(player))
                        continue;

                    GL11.glPushMatrix();
                    Vec3 vec3 = getVec3(player);
                    float posX = (float) (vec3.xCoord - RenderManager.viewerPosX);
                    float posY = (float) (vec3.yCoord - RenderManager.viewerPosY);
                    float posZ = (float) (vec3.zCoord - RenderManager.viewerPosZ);

                    double halfWidth = player.width / 2.0D + 0.18F;
                    AxisAlignedBB bb = new AxisAlignedBB(posX - halfWidth, posY, posZ - halfWidth, posX + halfWidth,
                            posY + player.height + 0.18D, posZ + halfWidth);

                    double[][] vectors = {{bb.minX, bb.minY, bb.minZ}, {bb.minX, bb.maxY, bb.minZ},
                            {bb.minX, bb.maxY, bb.maxZ}, {bb.minX, bb.minY, bb.maxZ}, {bb.maxX, bb.minY, bb.minZ},
                            {bb.maxX, bb.maxY, bb.minZ}, {bb.maxX, bb.maxY, bb.maxZ}, {bb.maxX, bb.minY, bb.maxZ}};

                    Vector3f projection;
                    Vector4f position = new Vector4f(Float.MAX_VALUE, Float.MAX_VALUE, -1.0F, -1.0F);

                    for (double[] vec : vectors) {
                        projection = project2D((float) vec[0], (float) vec[1], (float) vec[2], scaleFactor);
                        if (projection != null && projection.z >= 0.0F && projection.z < 1.0F) {
                            position.x = Math.min(position.x, projection.x);
                            position.y = Math.min(position.y, projection.y);
                            position.z = Math.max(position.z, projection.x);
                            position.w = Math.max(position.w, projection.y);
                        }
                    }

                    entityPosMap.put(player, new float[]{position.x, position.z, position.y, position.w});

                    GL11.glPopMatrix();
                }
            }

            if (skeletal.getValue()) {
                this.startEnd(true);
                GL11.glEnable(2903);
                GL11.glDisable(2848);
                for (EntityPlayer ent : entities.keySet()) {
                    drawSkeleton((EventRender3D) e, ent);
                }
                this.startEnd(false);
            }
        }
        if (e instanceof EventRender3D) {
            for (final Object o : mc.theWorld.loadedTileEntityList) {
                if (o instanceof TileEntityChest) {
                    final TileEntityLockable storage = (TileEntityLockable) o;
                    this.drawESPOnStorage(storage, storage.getPos().getX(), storage.getPos().getY(),
                            storage.getPos().getZ());
                }
            }
        }
    }

    public static void startScissorBox(ScaledResolution sr, int x, int y, int width, int height) {
        int sf = sr.getScaleFactor();

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(x * sf, (sr.getScaledHeight() - (y + height)) * sf, width * sf, height * sf);
    }

    public static void endScissorBox() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public static void enableAlpha() {
        GL11.glEnable(GL11.GL_BLEND);
        GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
    }

    public static void disableAlpha() {
        GL11.glDisable(GL11.GL_BLEND);
    }

    private Vector3f project2D(float x, float y, float z, int scaleFactor) {
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelMatrix);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projectionMatrix);
        GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);

        if (GLU.gluProject(x, y, z, modelMatrix, projectionMatrix, viewport, windowPosition)) {
            return new Vector3f(windowPosition.get(0) / scaleFactor,
                    (mc.displayHeight - windowPosition.get(1)) / scaleFactor, windowPosition.get(2));
        }

        return null;
    }

    public static void addEntity(final EntityPlayer e, final ModelPlayer model) {
        entities.put(e, new float[][]{
                {model.bipedHead.rotateAngleX, model.bipedHead.rotateAngleY, model.bipedHead.rotateAngleZ},
                {model.bipedRightArm.rotateAngleX, model.bipedRightArm.rotateAngleY,
                        model.bipedRightArm.rotateAngleZ},
                {model.bipedLeftArm.rotateAngleX, model.bipedLeftArm.rotateAngleY, model.bipedLeftArm.rotateAngleZ},
                {model.bipedRightLeg.rotateAngleX, model.bipedRightLeg.rotateAngleY,
                        model.bipedRightLeg.rotateAngleZ},
                {model.bipedLeftLeg.rotateAngleX, model.bipedLeftLeg.rotateAngleY,
                        model.bipedLeftLeg.rotateAngleZ}});
    }

    private void drawSkeleton(final EventRender3D event, final EntityPlayer e) {
        final Color color = new Color(
                e.getName().equalsIgnoreCase(mc.thePlayer.getName()) ? -6684775 : new Color(16775672).getRGB());
        if (!e.isInvisible()) {
            final float[][] entPos = entities.get(e);
            if (entPos != null && e.isEntityAlive() && !e.isDead && e != mc.thePlayer && !e.isPlayerSleeping()) {
                GL11.glPushMatrix();
                GL11.glLineWidth(1.3F);
                if (e.hurtTime > 0) {
                    GL11.glColor3f(150f, 0f, 0f);
                } else
                    GlStateManager.color((float) (color.getRed() / 255), (float) (color.getGreen() / 255),
                            (float) (color.getBlue() / 255), 1.0f);

                final Vec3 vec = this.getVec3(e);
                final double x = vec.xCoord - RenderManager.renderPosX;
                final double y = vec.yCoord - RenderManager.renderPosY;
                final double z = vec.zCoord - RenderManager.renderPosZ;
                GL11.glTranslated(x, y, z);
                final float xOff = e.prevRenderYawOffset
                        + (e.renderYawOffset - e.prevRenderYawOffset) * event.getPartialTicks();
                GL11.glRotatef(-xOff, 0.0f, 1.0f, 0.0f);
                GL11.glTranslated(0.0, 0.0, e.isSneaking() ? -0.235 : 0.0);
                final float yOff = e.isSneaking() ? 0.6f : 0.75f;
                GL11.glPushMatrix();
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glTranslated(-0.125, yOff, 0.0);
                if (entPos[3][0] != 0.0f) {
                    GL11.glRotatef(entPos[3][0] * 57.295776f, 1.0f, 0.0f, 0.0f);
                }
                if (entPos[3][1] != 0.0f) {
                    GL11.glRotatef(entPos[3][1] * 57.295776f, 0.0f, 1.0f, 0.0f);
                }
                if (entPos[3][2] != 0.0f) {
                    GL11.glRotatef(entPos[3][2] * 57.295776f, 0.0f, 0.0f, 1.0f);
                }
                GL11.glBegin(3);
                GL11.glVertex3d(0.0, 0.0, 0.0);
                GL11.glVertex3d(0.0, -yOff, 0.0);
                GL11.glEnd();
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glTranslated(0.125, yOff, 0.0);
                if (entPos[4][0] != 0.0f) {
                    GL11.glRotatef(entPos[4][0] * 57.295776f, 1.0f, 0.0f, 0.0f);
                }
                if (entPos[4][1] != 0.0f) {
                    GL11.glRotatef(entPos[4][1] * 57.295776f, 0.0f, 1.0f, 0.0f);
                }
                if (entPos[4][2] != 0.0f) {
                    GL11.glRotatef(entPos[4][2] * 57.295776f, 0.0f, 0.0f, 1.0f);
                }
                GL11.glBegin(3);
                GL11.glVertex3d(0.0, 0.0, 0.0);
                GL11.glVertex3d(0.0, -yOff, 0.0);
                GL11.glEnd();
                GL11.glPopMatrix();
                GL11.glTranslated(0.0, 0.0, e.isSneaking() ? 0.25 : 0.0);
                GL11.glPushMatrix();
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glTranslated(0.0, e.isSneaking() ? -0.05 : 0.0, e.isSneaking() ? -0.01725 : 0.0);
                GL11.glPushMatrix();
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glTranslated(-0.375, yOff + 0.55, 0.0);
                if (entPos[1][0] != 0.0f) {
                    GL11.glRotatef(entPos[1][0] * 57.295776f, 1.0f, 0.0f, 0.0f);
                }
                if (entPos[1][1] != 0.0f) {
                    GL11.glRotatef(entPos[1][1] * 57.295776f, 0.0f, 1.0f, 0.0f);
                }
                if (entPos[1][2] != 0.0f) {
                    GL11.glRotatef(-entPos[1][2] * 57.295776f, 0.0f, 0.0f, 1.0f);
                }
                GL11.glBegin(3);
                GL11.glVertex3d(0.0, 0.0, 0.0);
                GL11.glVertex3d(0.0, -0.5, 0.0);
                GL11.glEnd();
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glTranslated(0.375, yOff + 0.55, 0.0);
                if (entPos[2][0] != 0.0f) {
                    GL11.glRotatef(entPos[2][0] * 57.295776f, 1.0f, 0.0f, 0.0f);
                }
                if (entPos[2][1] != 0.0f) {
                    GL11.glRotatef(entPos[2][1] * 57.295776f, 0.0f, 1.0f, 0.0f);
                }
                if (entPos[2][2] != 0.0f) {
                    GL11.glRotatef(-entPos[2][2] * 57.295776f, 0.0f, 0.0f, 1.0f);
                }
                GL11.glBegin(3);
                GL11.glVertex3d(0.0, 0.0, 0.0);
                GL11.glVertex3d(0.0, -0.5, 0.0);
                GL11.glEnd();
                GL11.glPopMatrix();
                GL11.glRotatef(xOff - e.rotationYawHead, 0.0f, 1.0f, 0.0f);
                GL11.glPushMatrix();
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glTranslated(0.0, yOff + 0.55, 0.0);
                if (entPos[0][0] != 0.0f) {
                    GL11.glRotatef(entPos[0][0] * 57.295776f, 1.0f, 0.0f, 0.0f);
                }
                GL11.glBegin(3);
                GL11.glVertex3d(0.0, 0.0, 0.0);
                GL11.glVertex3d(0.0, 0.3, 0.0);
                GL11.glEnd();
                GL11.glPopMatrix();
                GL11.glPopMatrix();
                GL11.glRotatef(e.isSneaking() ? 25.0f : 0.0f, 1.0f, 0.0f, 0.0f);
                GL11.glTranslated(0.0, e.isSneaking() ? -0.16175 : 0.0, e.isSneaking() ? -0.48025 : 0.0);
                GL11.glPushMatrix();
                GL11.glTranslated(0.0, yOff, 0.0);
                GL11.glBegin(3);
                GL11.glVertex3d(-0.125, 0.0, 0.0);
                GL11.glVertex3d(0.125, 0.0, 0.0);
                GL11.glEnd();
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glTranslated(0.0, yOff, 0.0);
                GL11.glBegin(3);
                GL11.glVertex3d(0.0, 0.0, 0.0);
                GL11.glVertex3d(0.0, 0.55, 0.0);
                GL11.glEnd();
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glTranslated(0.0, yOff + 0.55, 0.0);
                GL11.glBegin(3);
                GL11.glVertex3d(-0.375, 0.0, 0.0);
                GL11.glVertex3d(0.375, 0.0, 0.0);
                GL11.glEnd();
                GL11.glPopMatrix();
                GL11.glPopMatrix();
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
    }

    private Vec3 getVec3(final EntityPlayer var0) {
        final float timer = mc.timer.renderPartialTicks;
        final double x = var0.lastTickPosX + (var0.posX - var0.lastTickPosX) * timer;
        final double y = var0.lastTickPosY + (var0.posY - var0.lastTickPosY) * timer;
        final double z = var0.lastTickPosZ + (var0.posZ - var0.lastTickPosZ) * timer;
        return new Vec3(x, y, z);
    }

    private void startEnd(final boolean revert) {
        if (revert) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GL11.glEnable(2848);
            GlStateManager.disableDepth();
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GlStateManager.blendFunc(770, 771);
            GL11.glHint(3154, 4354);
        } else {
            GlStateManager.disableBlend();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(2848);
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
        }
        GlStateManager.depthMask(!revert);
    }

    public static int getColorFromPercentage(float current, float max) {
        float percentage = (current / max) / 3;
        return Color.HSBtoRGB(percentage, 1.0F, 1.0F);
    }

    public void drawESPOnStorage(final TileEntityLockable storage, final double x, final double y, final double z) {
        if (chests.getValue()) {
            assert !storage.isLocked();
            final TileEntityChest chest = (TileEntityChest) storage;
            Vec3 vec = new Vec3(0.0, 0.0, 0.0);
            Vec3 vec2 = new Vec3(0.0, 0.0, 0.0);
            if (chest.adjacentChestZNeg != null) {
                vec = new Vec3(x + 0.0625, y, z - 0.9375);
                vec2 = new Vec3(x + 0.9375, y + 0.875, z + 0.9375);
            } else if (chest.adjacentChestXNeg != null) {
                vec = new Vec3(x + 0.9375, y, z + 0.0625);
                vec2 = new Vec3(x - 0.9375, y + 0.875, z + 0.9375);
            } else {
                if (chest.adjacentChestXPos != null || chest.adjacentChestZPos != null) {
                    return;
                }
                vec = new Vec3(x + 0.0625, y, z + 0.0625);
                vec2 = new Vec3(x + 0.9375, y + 0.875, z + 0.9375);
            }
            GL11.glPushMatrix();
            pre3D();
            GlStateManager.disableDepth();
            mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
            GL11.glColor4f(255F,
                    255F,
                    255F, 0.3F);
            drawFilledBoundingBox(
                    new AxisAlignedBB(vec.xCoord - RenderManager.renderPosX, vec.yCoord - RenderManager.renderPosY,
                            vec.zCoord - RenderManager.renderPosZ, vec2.xCoord - RenderManager.renderPosX,
                            vec2.yCoord - RenderManager.renderPosY, vec2.zCoord - RenderManager.renderPosZ));
            GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
            GlStateManager.enableDepth();
            post3D();
            GL11.glPopMatrix();
        }
    }

    public static void pre3D() {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDisable(2896);
        GL11.glDepthMask(false);
        GL11.glHint(3154, 4354);
    }

    public static void post3D() {
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void drawFilledBoundingBox(final AxisAlignedBB box) {
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181703_c);
        worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
        tessellator.draw();
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181703_c);
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
        tessellator.draw();
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181703_c);
        worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
        tessellator.draw();
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181703_c);
        worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
        tessellator.draw();
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181703_c);
        worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
        tessellator.draw();
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181703_c);
        worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
        tessellator.draw();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}