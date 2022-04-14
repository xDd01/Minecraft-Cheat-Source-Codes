package shadersmod.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import optifine.Config;
import optifine.Lang;
import optifine.PropertiesOrdered;
import optifine.Reflector;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.GLU;
import shadersmod.common.SMCLog;

public class Shaders {
   static final int MaxShadowDepthBuffers = 2;
   public static boolean isSleeping;
   static float skyColorG;
   private static final Pattern patternLoadEntityDataMap;
   private static final Pattern gbufferFormatPattern;
   private static int[] programsCompositeMipmapSetting = new int[33];
   public static float aoLevel;
   public static boolean configCloudShadow = true;
   public static boolean isRenderingDfb = false;
   static final FloatBuffer tempMatrixDirectBuffer;
   private static Map<String, String> shaderPackResources;
   public static float shadowAngleInterval;
   public static boolean progUseMidTexCoordAttrib = false;
   public static float configRenderResMul = 1.0F;
   static int systemTimeInt32 = 0;
   public static ShaderUniformInt uniformEntityId = new ShaderUniformInt("entityId");
   static final FloatBuffer previousProjection;
   public static String packNameNone = "OFF";
   public static final int ProgramShadow = 30;
   public static final String[] texMinFilDesc = new String[]{"Nearest", "Nearest-Nearest", "Nearest-Linear"};
   static float rainStrength = 0.0F;
   public static boolean[] shadowColorFilterNearest = new boolean[8];
   static int spShadowMapHeight = 1024;
   public static int configTexMagFilB = 0;
   private static final boolean enableShadersDebug = true;
   public static float blockAoLight;
   public static PropertyDefaultTrueFalse shaderPackDynamicHandLight;
   public static boolean configTweakBlockDamage = true;
   public static int terrainIconSize;
   public static int[] entityData;
   public static float wetnessHalfLife = 600.0F;
   public static final int ProgramShadowSolid = 31;
   static final FloatBuffer tempDirectFloatBuffer;
   public static final int ProgramTerrainCutout = 10;
   public static final int texMagFilRange = 2;
   static final float[] faShadowModelViewInverse;
   public static int configTexMinFilB = 0;
   static final int MaxColorBuffers = 8;
   static final FloatBuffer shadowProjection;
   static final IntBuffer dfbDepthTextures;
   static final IntBuffer dfbDrawBuffers;
   static final boolean[][] programsToggleColorTextures;
   public static final int ProgramBeaconBeam = 14;
   public static final int ProgramComposite2 = 23;
   static double previousCameraPositionY;
   static ShaderProfile[] shaderPackProfiles;
   static int dfb = 0;
   static File currentshader;
   static int frameCounter = 0;
   private static HFNoiseTexture noiseTexture;
   static final FloatBuffer projection;
   static final int[] dfbColorTexturesA;
   static float sunAngle = 0.0F;
   public static boolean[] shadowMipmapEnabled = new boolean[2];
   static boolean usewetness = false;
   private static final int[] formatIds;
   public static float shadowIntervalSize;
   static int usedShadowDepthBuffers = 0;
   public static boolean isHandRendered;
   public static boolean[] shadowFilterNearest = new boolean[2];
   public static boolean[] shadowColorMipmapEnabled = new boolean[8];
   public static boolean isShadowPass = false;
   static String shaderpacksdirname = "shaderpacks";
   private static final boolean saveFinalShaders;
   public static int renderWidth = 0;
   static float centerDepthSmooth = 0.0F;
   static float[] moonPosition = new float[4];
   static double previousCameraPositionZ;
   static IntBuffer[] programsDrawBuffers = new IntBuffer[33];
   public static PropertyDefaultTrueFalse shaderPackOldLighting;
   static final IntBuffer drawBuffersNone;
   static final int[] colorTexturesToggle;
   public static boolean useTangentAttrib = false;
   public static int entityDataIndex;
   public static boolean isShaderPackInitialized = false;
   static final IntBuffer sfbColorTextures;
   static final FloatBuffer modelView;
   static final IntBuffer drawBuffersColorAtt0;
   public static final int[] texMagFilValue = new int[]{9728, 9729};
   static final FloatBuffer modelViewInverse;
   static int usedColorAttachs = 0;
   static Minecraft mc = Minecraft.getMinecraft();
   public static final int ProgramComposite4 = 25;
   static boolean centerDepthSmoothEnabled = false;
   static float[] upPosModelView = new float[]{0.0F, 100.0F, 0.0F, 0.0F};
   static float shadowAngle = 0.0F;
   public static final int ProgramClouds = 6;
   static final float[] faShadowProjectionInverse;
   static float[] shadowLightPosition = new float[4];
   public static final int ProgramSkyBasic = 4;
   public static boolean renderItemPass1DepthMask = false;
   public static final int ProgramEntities = 16;
   public static final boolean enableShadersOption = true;
   static int superSamplingLevel = 1;
   public static float fogColorR;
   static final float[] faShadowProjection;
   static final float[] faShadowModelView;
   public static boolean useEntityAttrib = false;
   public static float blockLightLevel05;
   static final FloatBuffer shadowModelViewInverse;
   public static String glVersionString;
   private static int renderDisplayHeight = 0;
   public static boolean useMidTexCoordAttrib = false;
   private static String[] programsDrawBufSettings = new String[33];
   static float frameTimeCounter = 0.0F;
   public static final int ProgramCount = 33;
   public static int configTexMinFilS = 0;
   static boolean needResetModels = false;
   public static boolean isRenderingWorld = false;
   static boolean fogEnabled = true;
   static float wetness = 0.0F;
   public static float configShadowResMul = 1.0F;
   public static boolean shouldSkipDefaultShadow = false;
   static int shadowPassInterval = 0;
   static final IntBuffer[] drawBuffersBuffer;
   static String packNameDefault = "(internal)";
   public static boolean configNormalMap = true;
   public static int fogMode;
   static final IntBuffer drawBuffersClear0;
   public static float fogColorB;
   static float[] upPosition = new float[4];
   public static final int ProgramComposite1 = 22;
   static String optionsfilename = "optionsshaders.txt";
   public static PropertyDefaultTrueFalse configOldLighting = new PropertyDefaultTrueFalse("oldLighting", "Classic Lighting", 2);
   public static final int ProgramShadowCutout = 32;
   static final IntBuffer sfbDrawBuffers;
   public static float blockLightLevel08;
   public static boolean hasForge = false;
   private static String newColorAtmSetting = null;
   public static boolean shaderPackLoaded = false;
   public static int[] terrainTextureSize;
   private static float[] tempMat = new float[16];
   static float eyePosY = 0.0F;
   private static final int[] programBackups = new int[]{0, 0, 1, 2, 1, 2, 2, 3, 7, 7, 7, 7, 7, 7, 2, 3, 3, 2, 2, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 30, 30};
   private static final ByteBuffer bigBuffer;
   private static int noiseTextureResolution;
   static File configFile;
   static int usedDrawBuffers = 0;
   public static final int ProgramBasic = 1;
   public static final int ProgramFinal = 29;
   static final FloatBuffer previousModelView;
   static File shaderpacksdir;
   static final IntBuffer drawBuffersClear1;
   private static final String[] programNames = new String[]{"", "gbuffers_basic", "gbuffers_textured", "gbuffers_textured_lit", "gbuffers_skybasic", "gbuffers_skytextured", "gbuffers_clouds", "gbuffers_terrain", "gbuffers_terrain_solid", "gbuffers_terrain_cutout_mip", "gbuffers_terrain_cutout", "gbuffers_damagedblock", "gbuffers_water", "gbuffers_block", "gbuffers_beaconbeam", "gbuffers_item", "gbuffers_entities", "gbuffers_armor_glint", "gbuffers_spidereyes", "gbuffers_hand", "gbuffers_weather", "composite", "composite1", "composite2", "composite3", "composite4", "composite5", "composite6", "composite7", "final", "shadow", "shadow_solid", "shadow_cutout"};
   public static final int ProgramItem = 15;
   public static Properties shadersConfig = null;
   public static final int ProgramComposite = 21;
   static float shadowMapFOV = 90.0F;
   public static final int MaxCompositePasses = 8;
   public static float fogColorG;
   public static final int texMinFilRange = 3;
   private static String newDrawBufSetting = null;
   public static boolean useMultiTexCoord3Attrib = false;
   public static final int ProgramComposite7 = 28;
   static final FloatBuffer projectionInverse;
   public static ItemStack itemToRender;
   static final int MaxDepthBuffers = 3;
   static ShaderOption[] shaderPackOptions;
   public static int midTexCoordAttrib = 11;
   public static final int ProgramSpiderEyes = 18;
   public static float sunPathRotation;
   private static int programIDCopyDepth = 0;
   public static PropertyDefaultFastFancyOff shaderPackClouds;
   static final IntBuffer drawBuffersAll;
   static float[] moonPosModelView = new float[]{0.0F, -100.0F, 0.0F, 0.0F};
   public static final int ProgramTextured = 2;
   static float centerDepth = 0.0F;
   static final FloatBuffer shadowProjectionInverse;
   public static boolean normalMapEnabled = false;
   static float eyeBrightnessFadeX = 0.0F;
   public static String glVendorString;
   static float clearColorR;
   static EntityRenderer entityRenderer;
   static long diffWorldTime = 0L;
   static float clearColorB;
   static final FloatBuffer shadowModelView;
   public static final int ProgramHand = 19;
   public static final int ProgramTerrainCutoutMip = 9;
   public static boolean progUseTangentAttrib = false;
   public static int configTexMagFilS = 0;
   static double cameraPositionZ;
   static Map<String, ShaderOption[]> shaderPackGuiScreens;
   static double previousCameraPositionX;
   public static final int ProgramTexturedLit = 3;
   public static boolean isRenderingSky = false;
   static int[] programsID = new int[33];
   static float eyeBrightnessFadeY = 0.0F;
   public static final int ProgramArmorGlint = 17;
   public static final int ProgramDamagedBlock = 11;
   public static final int ProgramBlock = 13;
   static int usedDepthBuffers = 0;
   static int shadowPassCounter = 0;
   static int shadowMapHeight = 1024;
   static int usedShadowColorBuffers = 0;
   static long worldTime = 0L;
   public static int atlasSizeY = 0;
   static final float[] faModelView;
   static int sfb = 0;
   public static final int ProgramSkyTextured = 5;
   public static final int ProgramTerrainSolid = 8;
   static IShaderPack shaderPack = null;
   public static int entityAttrib = 10;
   private static String[] programsColorAtmSettings = new String[33];
   public static boolean configShadowClipFrustrum = true;
   public static int configTexMagFilN = 0;
   static boolean waterShadowEnabled = false;
   public static int activeProgram = 0;
   static int usedColorBuffers = 0;
   static int preShadowPassThirdPersonView;
   private static final String[] formatNames;
   private static World currentWorld;
   public static float configHandDepthMul = 0.125F;
   static float skyColorB;
   static long diffSystemTime = 0L;
   public static final String[] texMagFilDesc = new String[]{"Nearest", "Linear"};
   public static ITextureObject defaultTexture = null;
   static long lastSystemTime = 0L;
   static float celestialAngle = 0.0F;
   private static boolean noiseTextureEnabled;
   public static boolean progUseEntityAttrib = false;
   public static float eyeBrightnessHalflife = 10.0F;
   static int shadowMapWidth = 1024;
   private static int newCompositeMipmapSetting = 0;
   public static ShaderUniformFloat4 uniformEntityColor = new ShaderUniformFloat4("entityColor");
   static final float[] faProjection;
   public static int atlasSizeX = 0;
   static double cameraPositionX;
   public static final int ProgramComposite5 = 26;
   static final IntBuffer drawBuffersClearColor;
   public static boolean configSpecularMap = true;
   static final float[] faProjectionInverse;
   private static int[] gbuffersFormat = new int[8];
   private static int[] programsRef = new int[33];
   static double cameraPositionY;
   static float skyColorR;
   static IntBuffer activeDrawBuffers = null;
   static long systemTime = 0L;
   static float shadowMapHalfPlane = 160.0F;
   static File shadersdir;
   static final int MaxDrawBuffers = 8;
   static float[] shadowLightPositionVector = new float[4];
   static final int[] colorTextureTextureImageUnit;
   static int spShadowMapWidth = 1024;
   public static int configAntialiasingLevel = 0;
   public static final int ProgramComposite3 = 24;
   static float[] sunPosition = new float[4];
   public static ContextCapabilities capabilities;
   public static int renderHeight = 0;
   static boolean shadowMapIsOrtho = true;
   static boolean lightmapEnabled = false;
   public static boolean needResizeShadow = false;
   public static final int ProgramWeather = 20;
   static float centerDepthSmoothHalflife = 1.0F;
   static Map<Block, Integer> mapBlockToEntityData;
   private static final int bigBufferSize = 2196;
   static float[] sunPosModelView = new float[]{0.0F, 100.0F, 0.0F, 0.0F};
   private static final Pattern gbufferMipmapEnabledPattern;
   static int moonPhase = 0;
   private static int renderDisplayWidth = 0;
   public static boolean hasGlGenMipmap = false;
   public static int tangentAttrib = 12;
   private static List<Integer> shaderPackDimensions;
   public static final int ProgramNone = 0;
   static String currentshadername;
   public static int configTexMinFilN = 0;
   public static boolean isInitializedOnce = false;
   static final float[] faModelViewInverse;
   static int isEyeInWater = 0;
   public static boolean[] shadowHardwareFilteringEnabled = new boolean[2];
   public static String glRendererString;
   public static ShaderUniformInt uniformBlockEntityId = new ShaderUniformInt("blockEntityId");
   static boolean updateChunksErrorRecorded = false;
   public static final int[] texMinFilValue = new int[]{9728, 9984, 9986};
   static final int MaxShadowColorBuffers = 8;
   public static float blockLightLevel06;
   private static int activeCompositeMipmapSetting = 0;
   public static int numberResetDisplayList = 0;
   public static float drynessHalfLife = 200.0F;
   static final IntBuffer sfbDepthTextures;
   static float clearColorG;
   public static boolean isCompositeRendered = false;
   static long lastWorldTime = 0L;
   public static final int ProgramWater = 12;
   static final IntBuffer dfbColorTextures;
   public static Properties loadedShaders = null;
   static int eyeBrightness = 0;
   private static String activeColorAtmSettings = null;
   public static final int ProgramTerrain = 7;
   public static final int ProgramComposite6 = 27;

   private static void loadShaderPackProperties() {
      shaderPackClouds.resetValue();
      shaderPackDynamicHandLight.resetValue();
      shaderPackOldLighting.resetValue();
      if (shaderPack != null) {
         String var0 = "/shaders/shaders.properties";

         try {
            InputStream var1 = shaderPack.getResourceAsStream(var0);
            if (var1 == null) {
               return;
            }

            PropertiesOrdered var2 = new PropertiesOrdered();
            var2.load(var1);
            var1.close();
            shaderPackClouds.loadFrom(var2);
            shaderPackDynamicHandLight.loadFrom(var2);
            shaderPackOldLighting.loadFrom(var2);
            shaderPackProfiles = ShaderPackParser.parseProfiles(var2, shaderPackOptions);
            shaderPackGuiScreens = ShaderPackParser.parseGuiScreens(var2, shaderPackProfiles, shaderPackOptions);
         } catch (IOException var3) {
            Config.warn(String.valueOf((new StringBuilder("[Shaders] Error reading: ")).append(var0)));
         }
      }

   }

   public static void disableLightmap() {
      lightmapEnabled = false;
      if (activeProgram == 3) {
         useProgram(2);
      }

   }

   private static boolean printShaderLogInfo(int var0, String var1) {
      IntBuffer var2 = BufferUtils.createIntBuffer(1);
      int var3 = GL20.glGetShaderi(var0, 35716);
      if (var3 > 1) {
         String var4 = GL20.glGetShaderInfoLog(var0, var3);
         SMCLog.info(String.valueOf((new StringBuilder("Shader info log: ")).append(var1).append("\n").append(var4)));
         return false;
      } else {
         return true;
      }
   }

   public static int setFogMode(int var0) {
      fogMode = var0;
      return var0;
   }

   public static void setViewport(int var0, int var1, int var2, int var3) {
      GlStateManager.colorMask(true, true, true, true);
      if (isShadowPass) {
         GL11.glViewport(0, 0, shadowMapWidth, shadowMapHeight);
      } else {
         GL11.glViewport(0, 0, renderWidth, renderHeight);
         EXTFramebufferObject.glBindFramebufferEXT(36160, dfb);
         isRenderingDfb = true;
         GlStateManager.enableCull();
         GlStateManager.enableDepth();
         setDrawBuffers(drawBuffersNone);
         useProgram(2);
         checkGLError("beginRenderPass");
      }

   }

   public static void disableFog() {
      fogEnabled = false;
      setProgramUniform1i("fogMode", 0);
   }

   public static void sglEnableFog(int var0) {
      GL11.glEnable(var0);
      enableFog();
   }

   public static int setEntityData1(int var0) {
      entityData[entityDataIndex * 2] = entityData[entityDataIndex * 2] & '\uffff' | var0 << 16;
      return var0;
   }

   private static void setupNoiseTexture() {
      if (noiseTexture == null) {
         noiseTexture = new HFNoiseTexture(noiseTextureResolution, noiseTextureResolution);
      }

   }

   private static void printChat(String var0) {
      mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(var0));
   }

   private static void printChatAndLogError(String var0) {
      SMCLog.severe(var0);
      mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(var0));
   }

   public static String getShaderPackName() {
      return shaderPack == null ? null : (shaderPack instanceof ShaderPackNone ? null : shaderPack.getName());
   }

   public static void resetDisplayListModels() {
      if (needResetModels) {
         needResetModels = false;
         SMCLog.info("Reset model renderers");
         Iterator var0 = mc.getRenderManager().getEntityRenderMap().values().iterator();

         while(var0.hasNext()) {
            Render var1 = (Render)var0.next();
            if (var1 instanceof RendererLivingEntity) {
               RendererLivingEntity var2 = (RendererLivingEntity)var1;
               resetDisplayListModel(var2.getMainModel());
            }
         }
      }

   }

   public static void sglEnableT2D(int var0) {
      GL11.glEnable(var0);
      enableTexture2D();
   }

   public static void beginRender(Minecraft var0, float var1, long var2) {
      checkGLError("pre beginRender");
      checkWorldChanged(mc.theWorld);
      mc = var0;
      mc.mcProfiler.startSection("init");
      entityRenderer = mc.entityRenderer;
      if (!isShaderPackInitialized) {
         try {
            init();
         } catch (IllegalStateException var10) {
            if (Config.normalize(var10.getMessage()).equals("Function is not supported")) {
               printChatAndLogError(String.valueOf((new StringBuilder("[Shaders] Error: ")).append(var10.getMessage())));
               var10.printStackTrace();
               setShaderPack(packNameNone);
               return;
            }
         }
      }

      if (mc.displayWidth != renderDisplayWidth || mc.displayHeight != renderDisplayHeight) {
         resize();
      }

      if (needResizeShadow) {
         resizeShadow();
      }

      worldTime = mc.theWorld.getWorldTime();
      diffWorldTime = (worldTime - lastWorldTime) % 24000L;
      if (diffWorldTime < 0L) {
         diffWorldTime += 24000L;
      }

      lastWorldTime = worldTime;
      moonPhase = mc.theWorld.getMoonPhase();
      systemTime = System.currentTimeMillis();
      if (lastSystemTime == 0L) {
         lastSystemTime = systemTime;
      }

      diffSystemTime = systemTime - lastSystemTime;
      lastSystemTime = systemTime;
      frameTimeCounter += (float)diffSystemTime * 0.001F;
      frameTimeCounter %= 3600.0F;
      rainStrength = var0.theWorld.getRainStrength(var1);
      float var4 = (float)diffSystemTime * 0.01F;
      float var5 = (float)Math.exp(Math.log(0.5D) * (double)var4 / (double)(wetness < rainStrength ? drynessHalfLife : wetnessHalfLife));
      wetness = wetness * var5 + rainStrength * (1.0F - var5);
      Entity var6 = mc.func_175606_aa();
      isSleeping = var6 instanceof EntityLivingBase && ((EntityLivingBase)var6).isPlayerSleeping();
      eyePosY = (float)var6.posY * var1 + (float)var6.lastTickPosY * (1.0F - var1);
      eyeBrightness = var6.getBrightnessForRender(var1);
      var5 = (float)diffSystemTime * 0.01F;
      float var7 = (float)Math.exp(Math.log(0.5D) * (double)var5 / (double)eyeBrightnessHalflife);
      eyeBrightnessFadeX = eyeBrightnessFadeX * var7 + (float)(eyeBrightness & '\uffff') * (1.0F - var7);
      eyeBrightnessFadeY = eyeBrightnessFadeY * var7 + (float)(eyeBrightness >> 16) * (1.0F - var7);
      isEyeInWater = mc.gameSettings.thirdPersonView == 0 && !isSleeping && mc.thePlayer.isInsideOfMaterial(Material.water) ? 1 : 0;
      Vec3 var8 = mc.theWorld.getSkyColor(mc.func_175606_aa(), var1);
      skyColorR = (float)var8.xCoord;
      skyColorG = (float)var8.yCoord;
      skyColorB = (float)var8.zCoord;
      isRenderingWorld = true;
      isCompositeRendered = false;
      isHandRendered = false;
      if (usedShadowDepthBuffers >= 1) {
         GlStateManager.setActiveTexture(33988);
         GlStateManager.func_179144_i(sfbDepthTextures.get(0));
         if (usedShadowDepthBuffers >= 2) {
            GlStateManager.setActiveTexture(33989);
            GlStateManager.func_179144_i(sfbDepthTextures.get(1));
         }
      }

      GlStateManager.setActiveTexture(33984);

      int var9;
      for(var9 = 0; var9 < usedColorBuffers; ++var9) {
         GlStateManager.func_179144_i(dfbColorTexturesA[var9]);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glTexParameteri(3553, 10241, 9729);
         GlStateManager.func_179144_i(dfbColorTexturesA[8 + var9]);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glTexParameteri(3553, 10241, 9729);
      }

      GlStateManager.func_179144_i(0);

      for(var9 = 0; var9 < 4 && 4 + var9 < usedColorBuffers; ++var9) {
         GlStateManager.setActiveTexture('蓇' + var9);
         GlStateManager.func_179144_i(dfbColorTextures.get(4 + var9));
      }

      GlStateManager.setActiveTexture(33990);
      GlStateManager.func_179144_i(dfbDepthTextures.get(0));
      if (usedDepthBuffers >= 2) {
         GlStateManager.setActiveTexture(33995);
         GlStateManager.func_179144_i(dfbDepthTextures.get(1));
         if (usedDepthBuffers >= 3) {
            GlStateManager.setActiveTexture(33996);
            GlStateManager.func_179144_i(dfbDepthTextures.get(2));
         }
      }

      for(var9 = 0; var9 < usedShadowColorBuffers; ++var9) {
         GlStateManager.setActiveTexture('蓍' + var9);
         GlStateManager.func_179144_i(sfbColorTextures.get(var9));
      }

      if (noiseTextureEnabled) {
         GlStateManager.setActiveTexture('蓀' + noiseTexture.textureUnit);
         GlStateManager.func_179144_i(noiseTexture.getID());
         GL11.glTexParameteri(3553, 10242, 10497);
         GL11.glTexParameteri(3553, 10243, 10497);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glTexParameteri(3553, 10241, 9729);
      }

      GlStateManager.setActiveTexture(33984);
      previousCameraPositionX = cameraPositionX;
      previousCameraPositionY = cameraPositionY;
      previousCameraPositionZ = cameraPositionZ;
      previousProjection.position(0);
      projection.position(0);
      previousProjection.put(projection);
      previousProjection.position(0);
      projection.position(0);
      previousModelView.position(0);
      modelView.position(0);
      previousModelView.put(modelView);
      previousModelView.position(0);
      modelView.position(0);
      checkGLError("beginRender");
      ShadersRender.renderShadowMap(entityRenderer, 0, var1, var2);
      mc.mcProfiler.endSection();
      EXTFramebufferObject.glBindFramebufferEXT(36160, dfb);

      for(var9 = 0; var9 < usedColorBuffers; ++var9) {
         colorTexturesToggle[var9] = 0;
         EXTFramebufferObject.glFramebufferTexture2DEXT(36160, '賠' + var9, 3553, dfbColorTexturesA[var9], 0);
      }

      checkGLError("end beginRender");
   }

   public static void drawComposite() {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glBegin(7);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glVertex3f(0.0F, 0.0F, 0.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex3f(1.0F, 0.0F, 0.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex3f(1.0F, 1.0F, 0.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex3f(0.0F, 1.0F, 0.0F);
      GL11.glEnd();
   }

   public static void resetDisplayListModelRenderer(ModelRenderer var0) {
      var0.resetDisplayList();
      if (var0.childModels != null) {
         int var1 = 0;

         for(int var2 = var0.childModels.size(); var1 < var2; ++var1) {
            resetDisplayListModelRenderer((ModelRenderer)var0.childModels.get(var1));
         }
      }

   }

   public static void readCenterDepth() {
      if (!isShadowPass && centerDepthSmoothEnabled) {
         tempDirectFloatBuffer.clear();
         GL11.glReadPixels(renderWidth / 2, renderHeight / 2, 1, 1, 6402, 5126, tempDirectFloatBuffer);
         centerDepth = tempDirectFloatBuffer.get(0);
         float var0 = (float)diffSystemTime * 0.01F;
         float var1 = (float)Math.exp(Math.log(0.5D) * (double)var0 / (double)centerDepthSmoothHalflife);
         centerDepthSmooth = centerDepthSmooth * var1 + centerDepth * (1.0F - var1);
      }

   }

   public static void endLivingDamage() {
      if (isRenderingWorld && !isShadowPass) {
         setDrawBuffers(programsDrawBuffers[16]);
      }

   }

   public static void beginLivingDamage() {
      if (isRenderingWorld) {
         ShadersTex.bindTexture(defaultTexture);
         if (!isShadowPass) {
            setDrawBuffers(drawBuffersColorAtt0);
         }
      }

   }

   public static void nextAntialiasingLevel() {
      configAntialiasingLevel += 2;
      configAntialiasingLevel = configAntialiasingLevel / 2 * 2;
      if (configAntialiasingLevel > 4) {
         configAntialiasingLevel = 0;
      }

      configAntialiasingLevel = Config.limit(configAntialiasingLevel, 0, 4);
   }

   public static void setFogColor(float var0, float var1, float var2) {
      fogColorR = var0;
      fogColorG = var1;
      fogColorB = var2;
   }

   public static void setFog(int var0) {
      GlStateManager.setFog(var0);
      if (fogEnabled) {
         setProgramUniform1i("fogMode", var0);
      }

   }

   public static void beginHand() {
      GL11.glMatrixMode(5888);
      GL11.glPushMatrix();
      GL11.glMatrixMode(5889);
      GL11.glPushMatrix();
      GL11.glMatrixMode(5888);
      useProgram(19);
      checkGLError("beginHand");
      checkFramebufferStatus("beginHand");
   }

   static String versiontostring(int var0) {
      String var1 = Integer.toString(var0);
      return String.valueOf((new StringBuilder(String.valueOf(Integer.toString(Integer.parseInt(var1.substring(1, 3)))))).append(".").append(Integer.toString(Integer.parseInt(var1.substring(3, 5)))).append(".").append(Integer.toString(Integer.parseInt(var1.substring(5)))));
   }

   public static void glDisableWrapper(int var0) {
      GL11.glDisable(var0);
      if (var0 == 3553) {
         disableTexture2D();
      } else if (var0 == 2912) {
         disableFog();
      }

   }

   public static void endHand() {
      checkGLError("pre endHand");
      checkFramebufferStatus("pre endHand");
      GL11.glMatrixMode(5889);
      GL11.glPopMatrix();
      GL11.glMatrixMode(5888);
      GL11.glPopMatrix();
      GlStateManager.blendFunc(770, 771);
      checkGLError("endHand");
   }

   public static void beginClouds() {
      fogEnabled = true;
      pushEntity(-3, 0);
      useProgram(6);
   }

   private static void saveShader(String var0, String var1) {
      try {
         File var2 = new File(shaderpacksdir, String.valueOf((new StringBuilder("debug/")).append(var0)));
         var2.getParentFile().mkdirs();
         Config.writeFile(var2, var1);
      } catch (IOException var3) {
         Config.warn(String.valueOf((new StringBuilder("Error saving: ")).append(var0)));
         var3.printStackTrace();
      }

   }

   public static ShaderOption[] getShaderPackOptions(String var0) {
      ShaderOption[] var1 = (ShaderOption[])shaderPackOptions.clone();
      if (shaderPackGuiScreens == null) {
         if (shaderPackProfiles != null) {
            ShaderOptionProfile var8 = new ShaderOptionProfile(shaderPackProfiles, var1);
            var1 = (ShaderOption[])Config.addObjectToArray(var1, var8, 0);
         }

         var1 = getVisibleOptions(var1);
         return var1;
      } else {
         String var2 = var0 != null ? String.valueOf((new StringBuilder("screen.")).append(var0)) : "screen";
         ShaderOption[] var3 = (ShaderOption[])shaderPackGuiScreens.get(var2);
         if (var3 == null) {
            return new ShaderOption[0];
         } else {
            ArrayList var4 = new ArrayList();

            for(int var5 = 0; var5 < var3.length; ++var5) {
               ShaderOption var6 = var3[var5];
               if (var6 == null) {
                  var4.add((Object)null);
               } else if (var6 instanceof ShaderOptionRest) {
                  ShaderOption[] var7 = getShaderOptionsRest(shaderPackGuiScreens, var1);
                  var4.addAll(Arrays.asList(var7));
               } else {
                  var4.add(var6);
               }
            }

            ShaderOption[] var9 = (ShaderOption[])var4.toArray(new ShaderOption[var4.size()]);
            return var9;
         }
      }
   }

   public static void beginSky() {
      isRenderingSky = true;
      fogEnabled = true;
      setDrawBuffers(dfbDrawBuffers);
      useProgram(5);
      pushEntity(-2, 0);
   }

   public static void beginProjectRedHalo() {
      if (isRenderingWorld) {
         useProgram(1);
      }

   }

   public static void beginWater() {
      if (isRenderingWorld) {
         if (!isShadowPass) {
            useProgram(12);
            GlStateManager.enableBlend();
            GlStateManager.depthMask(true);
         } else {
            GlStateManager.depthMask(true);
         }
      }

   }

   public static void beginLeash() {
      useProgram(1);
   }

   public static void setUpPosition() {
      FloatBuffer var0 = tempMatrixDirectBuffer;
      var0.clear();
      GL11.glGetFloat(2982, var0);
      var0.get(tempMat, 0, 16);
      SMath.multiplyMat4xVec4(upPosition, tempMat, upPosModelView);
   }

   private static void saveShaderPackOptions(ShaderOption[] var0, IShaderPack var1) {
      Properties var2 = new Properties();
      if (shaderPackOptions != null) {
         for(int var3 = 0; var3 < var0.length; ++var3) {
            ShaderOption var4 = var0[var3];
            if (var4.isChanged() && var4.isEnabled()) {
               var2.setProperty(var4.getName(), var4.getValue());
            }
         }
      }

      try {
         saveOptionProperties(var1, var2);
      } catch (IOException var5) {
         Config.warn(String.valueOf((new StringBuilder("[Shaders] Error saving configuration for ")).append(shaderPack.getName())));
         var5.printStackTrace();
      }

   }

   public static void endWater() {
      if (isRenderingWorld) {
         if (isShadowPass) {
         }

         useProgram(lightmapEnabled ? 3 : 2);
      }

   }

   private static void saveOptionProperties(IShaderPack var0, Properties var1) throws IOException {
      String var2 = String.valueOf((new StringBuilder(String.valueOf(shaderpacksdirname))).append("/").append(var0.getName()).append(".txt"));
      File var3 = new File(Minecraft.getMinecraft().mcDataDir, var2);
      if (var1.isEmpty()) {
         var3.delete();
      } else {
         FileOutputStream var4 = new FileOutputStream(var3);
         var1.store(var4, (String)null);
         var4.flush();
         var4.close();
      }

   }

   public static void postCelestialRotate() {
      FloatBuffer var0 = tempMatrixDirectBuffer;
      var0.clear();
      GL11.glGetFloat(2982, var0);
      var0.get(tempMat, 0, 16);
      SMath.multiplyMat4xVec4(sunPosition, tempMat, sunPosModelView);
      SMath.multiplyMat4xVec4(moonPosition, tempMat, moonPosModelView);
      System.arraycopy(shadowAngle == sunAngle ? sunPosition : moonPosition, 0, shadowLightPosition, 0, 3);
      checkGLError("postCelestialRotate");
   }

   public static void uninit() {
      if (isShaderPackInitialized) {
         checkGLError("Shaders.uninit pre");

         for(int var0 = 0; var0 < 33; ++var0) {
            if (programsRef[var0] != 0) {
               ARBShaderObjects.glDeleteObjectARB(programsRef[var0]);
               checkGLError("del programRef");
            }

            programsRef[var0] = 0;
            programsID[var0] = 0;
            programsDrawBufSettings[var0] = null;
            programsDrawBuffers[var0] = null;
            programsCompositeMipmapSetting[var0] = 0;
         }

         if (dfb != 0) {
            EXTFramebufferObject.glDeleteFramebuffersEXT(dfb);
            dfb = 0;
            checkGLError("del dfb");
         }

         if (sfb != 0) {
            EXTFramebufferObject.glDeleteFramebuffersEXT(sfb);
            sfb = 0;
            checkGLError("del sfb");
         }

         if (dfbDepthTextures != null) {
            GlStateManager.deleteTextures(dfbDepthTextures);
            fillIntBufferZero(dfbDepthTextures);
            checkGLError("del dfbDepthTextures");
         }

         if (dfbColorTextures != null) {
            GlStateManager.deleteTextures(dfbColorTextures);
            fillIntBufferZero(dfbColorTextures);
            checkGLError("del dfbTextures");
         }

         if (sfbDepthTextures != null) {
            GlStateManager.deleteTextures(sfbDepthTextures);
            fillIntBufferZero(sfbDepthTextures);
            checkGLError("del shadow depth");
         }

         if (sfbColorTextures != null) {
            GlStateManager.deleteTextures(sfbColorTextures);
            fillIntBufferZero(sfbColorTextures);
            checkGLError("del shadow color");
         }

         if (dfbDrawBuffers != null) {
            fillIntBufferZero(dfbDrawBuffers);
         }

         if (noiseTexture != null) {
            noiseTexture.destroy();
            noiseTexture = null;
         }

         SMCLog.info("Uninit");
         shadowPassInterval = 0;
         shouldSkipDefaultShadow = false;
         isShaderPackInitialized = false;
         checkGLError("Shaders.uninit");
      }

   }

   public static void endRender() {
      if (isShadowPass) {
         checkGLError("shadow endRender");
      } else {
         if (!isCompositeRendered) {
            renderCompositeFinal();
         }

         isRenderingWorld = false;
         GlStateManager.colorMask(true, true, true, true);
         useProgram(0);
         RenderHelper.disableStandardItemLighting();
         checkGLError("endRender end");
      }

   }

   public static int getEntityData2() {
      return entityData[entityDataIndex * 2 + 1];
   }

   public static void sglDisableFog(int var0) {
      GL11.glDisable(var0);
      disableFog();
   }

   public static void scheduleResize() {
      renderDisplayHeight = 0;
   }

   public static ShaderOption[] getChangedOptions(ShaderOption[] var0) {
      ArrayList var1 = new ArrayList();

      for(int var2 = 0; var2 < var0.length; ++var2) {
         ShaderOption var3 = var0[var2];
         if (var3.isEnabled() && var3.isChanged()) {
            var1.add(var3);
         }
      }

      ShaderOption[] var4 = (ShaderOption[])var1.toArray(new ShaderOption[var1.size()]);
      return var4;
   }

   public static void beginLitParticles() {
      useProgram(3);
   }

   public static void resetDisplayList() {
      ++numberResetDisplayList;
      needResetModels = true;
      SMCLog.info("Reset world renderers");
      mc.renderGlobal.loadRenderers();
   }

   public static String translate(String var0, String var1) {
      String var2 = (String)shaderPackResources.get(var0);
      return var2 == null ? var1 : var2;
   }

   public static int setEntityData2(int var0) {
      entityData[entityDataIndex * 2 + 1] = entityData[entityDataIndex * 2 + 1] & -65536 | var0 & '\uffff';
      return var0;
   }

   private static ShaderOption[] getShaderOptionsRest(Map<String, ShaderOption[]> var0, ShaderOption[] var1) {
      HashSet var2 = new HashSet();
      Set var3 = var0.keySet();
      Iterator var4 = var3.iterator();

      ShaderOption[] var6;
      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         var6 = (ShaderOption[])var0.get(var5);

         for(int var7 = 0; var7 < var6.length; ++var7) {
            ShaderOption var8 = var6[var7];
            if (var8 != null) {
               var2.add(var8.getName());
            }
         }
      }

      ArrayList var9 = new ArrayList();

      for(int var10 = 0; var10 < var1.length; ++var10) {
         ShaderOption var11 = var1[var10];
         if (var11.isVisible()) {
            String var12 = var11.getName();
            if (!var2.contains(var12)) {
               var9.add(var11);
            }
         }
      }

      var6 = (ShaderOption[])var9.toArray(new ShaderOption[var9.size()]);
      return var6;
   }

   public static void printIntBuffer(String var0, IntBuffer var1) {
      StringBuilder var2 = new StringBuilder(128);
      var2.append(var0).append(" [pos ").append(var1.position()).append(" lim ").append(var1.limit()).append(" cap ").append(var1.capacity()).append(" :");
      int var3 = var1.limit();

      for(int var4 = 0; var4 < var3; ++var4) {
         var2.append(" ").append(var1.get(var4));
      }

      var2.append("]");
      SMCLog.info(String.valueOf(var2));
   }

   public static void nextEntity(Entity var0) {
      if (isRenderingWorld) {
         useProgram(16);
         setEntityId(var0);
      }

   }

   public static boolean isOldLighting() {
      return !configOldLighting.isDefault() ? configOldLighting.isTrue() : (!shaderPackOldLighting.isDefault() ? shaderPackOldLighting.isTrue() : true);
   }

   public static void setClearColor(float var0, float var1, float var2, float var3) {
      GlStateManager.clearColor(var0, var1, var2, var3);
      clearColorR = var0;
      clearColorG = var1;
      clearColorB = var2;
   }

   public static void setEntityColor(float var0, float var1, float var2, float var3) {
      if (isRenderingWorld && !isShadowPass) {
         uniformEntityColor.setValue(var0, var1, var2, var3);
      }

   }

   public static void preSkyList() {
      GL11.glColor3f(fogColorR, fogColorG, fogColorB);
      drawHorizon();
      GL11.glColor3f(skyColorR, skyColorG, skyColorB);
   }

   public static void endBlockEntities() {
      if (isRenderingWorld) {
         checkGLError("endBlockEntities");
         useProgram(lightmapEnabled ? 3 : 2);
         ShadersTex.bindNSTextures(defaultTexture.getMultiTexID());
      }

   }

   public static int checkGLError(String var0, String var1) {
      int var2 = GL11.glGetError();
      if (var2 != 0) {
         System.err.format("GL error 0x%04x: %s at %s %s\n", var2, GLU.gluErrorString(var2), var0, var1);
      }

      return var2;
   }

   public static void setProgramUniform1f(String var0, float var1) {
      int var2 = programsID[activeProgram];
      if (var2 != 0) {
         int var3 = ARBShaderObjects.glGetUniformLocationARB(var2, var0);
         ARBShaderObjects.glUniform1fARB(var3, var1);
         checkGLError(programNames[activeProgram], var0);
      }

   }

   private static void checkWorldChanged(World var0) {
      if (currentWorld != var0) {
         World var1 = currentWorld;
         currentWorld = var0;
         if (var1 != null && var0 != null) {
            int var2 = var1.provider.getDimensionId();
            int var3 = var0.provider.getDimensionId();
            boolean var4 = shaderPackDimensions.contains(var2);
            boolean var5 = shaderPackDimensions.contains(var3);
            if (var4 || var5) {
               uninit();
            }
         }
      }

   }

   public static void glEnableWrapper(int var0) {
      GL11.glEnable(var0);
      if (var0 == 3553) {
         enableTexture2D();
      } else if (var0 == 2912) {
         enableFog();
      }

   }

   public static void checkShadersModInstalled() {
      try {
         Class var0 = Class.forName("shadersmod.transform.SMCClassTransformer");
      } catch (Throwable var1) {
         return;
      }

      throw new RuntimeException("Shaders Mod detected. Please remove it, OptiFine has built-in support for shaders.");
   }

   public static void sglDisableT2D(int var0) {
      GL11.glDisable(var0);
      disableTexture2D();
   }

   public static void beginFPOverlay() {
      GlStateManager.disableLighting();
      GlStateManager.disableBlend();
   }

   public static void beginUpdateChunks() {
      checkGLError("beginUpdateChunks1");
      checkFramebufferStatus("beginUpdateChunks1");
      if (!isShadowPass) {
         useProgram(7);
      }

      checkGLError("beginUpdateChunks2");
      checkFramebufferStatus("beginUpdateChunks2");
   }

   public static void preWater() {
      if (usedDepthBuffers >= 2) {
         GlStateManager.setActiveTexture(33995);
         checkGLError("pre copy depth");
         GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, renderWidth, renderHeight);
         checkGLError("copy depth");
         GlStateManager.setActiveTexture(33984);
      }

      ShadersTex.bindNSTextures(defaultTexture.getMultiTexID());
   }

   public static ShaderOption[] getShaderPackOptions() {
      return shaderPackOptions;
   }

   public static void updateBlockLightLevel() {
      if (isOldLighting()) {
         blockLightLevel05 = 0.5F;
         blockLightLevel06 = 0.6F;
         blockLightLevel08 = 0.8F;
      } else {
         blockLightLevel05 = 1.0F;
         blockLightLevel06 = 1.0F;
         blockLightLevel08 = 1.0F;
      }

   }

   public static void setProgramUniform3f(String var0, float var1, float var2, float var3) {
      int var4 = programsID[activeProgram];
      if (var4 != 0) {
         int var5 = ARBShaderObjects.glGetUniformLocationARB(var4, var0);
         ARBShaderObjects.glUniform3fARB(var5, var1, var2, var3);
         checkGLError(programNames[activeProgram], var0);
      }

   }

   public static void disableTexture2D() {
      if (isRenderingSky) {
         useProgram(4);
      } else if (activeProgram == 2 || activeProgram == 3) {
         useProgram(1);
      }

   }

   public static void endEntities() {
      if (isRenderingWorld) {
         useProgram(lightmapEnabled ? 3 : 2);
      }

   }

   static void checkOptifine() {
   }

   public static int getEntityData() {
      return entityData[entityDataIndex * 2];
   }

   public static void loadConfig() {
      SMCLog.info("Load ShadersMod configuration.");

      try {
         if (!shaderpacksdir.exists()) {
            shaderpacksdir.mkdir();
         }
      } catch (Exception var8) {
         SMCLog.severe(String.valueOf((new StringBuilder("Failed to open the shaderpacks directory: ")).append(shaderpacksdir)));
      }

      shadersConfig = new PropertiesOrdered();
      shadersConfig.setProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), "");
      if (configFile.exists()) {
         try {
            FileReader var0 = new FileReader(configFile);
            shadersConfig.load(var0);
            var0.close();
         } catch (Exception var7) {
         }
      }

      if (!configFile.exists()) {
         try {
            storeConfig();
         } catch (Exception var6) {
         }
      }

      EnumShaderOption[] var9 = EnumShaderOption.values();

      for(int var1 = 0; var1 < var9.length; ++var1) {
         EnumShaderOption var2 = var9[var1];
         String var3 = var2.getPropertyKey();
         String var4 = var2.getValueDefault();
         String var5 = shadersConfig.getProperty(var3, var4);
         setEnumShaderOption(var2, var5);
      }

      loadShaderPack();
   }

   private static void clearDirectory(File var0) {
      if (var0.exists() && var0.isDirectory()) {
         File[] var1 = var0.listFiles();
         if (var1 != null) {
            for(int var2 = 0; var2 < var1.length; ++var2) {
               File var3 = var1[var2];
               if (var3.isDirectory()) {
                  clearDirectory(var3);
               }

               var3.delete();
            }
         }
      }

   }

   public static void genCompositeMipmap() {
      if (hasGlGenMipmap) {
         for(int var0 = 0; var0 < usedColorBuffers; ++var0) {
            if ((activeCompositeMipmapSetting & 1 << var0) != 0) {
               GlStateManager.setActiveTexture('蓀' + colorTextureTextureImageUnit[var0]);
               GL11.glTexParameteri(3553, 10241, 9987);
               GL30.glGenerateMipmap(3553);
            }
         }

         GlStateManager.setActiveTexture(33984);
      }

   }

   public static void setProgramUniform1i(String var0, int var1) {
      int var2 = programsID[activeProgram];
      if (var2 != 0) {
         int var3 = ARBShaderObjects.glGetUniformLocationARB(var2, var0);
         ARBShaderObjects.glUniform1iARB(var3, var1);
         checkGLError(programNames[activeProgram], var0);
      }

   }

   public static void popEntity() {
      entityData[entityDataIndex * 2] = 0;
      entityData[entityDataIndex * 2 + 1] = 0;
      --entityDataIndex;
   }

   public static void nextBlockEntity(TileEntity var0) {
      if (isRenderingWorld) {
         checkGLError("nextBlockEntity");
         useProgram(13);
         setBlockEntityId(var0);
      }

   }

   private static void setupFrameBuffer() {
      if (dfb != 0) {
         EXTFramebufferObject.glDeleteFramebuffersEXT(dfb);
         GlStateManager.deleteTextures(dfbDepthTextures);
         GlStateManager.deleteTextures(dfbColorTextures);
      }

      dfb = EXTFramebufferObject.glGenFramebuffersEXT();
      GL11.glGenTextures((IntBuffer)dfbDepthTextures.clear().limit(usedDepthBuffers));
      GL11.glGenTextures((IntBuffer)dfbColorTextures.clear().limit(16));
      dfbDepthTextures.position(0);
      dfbColorTextures.position(0);
      dfbColorTextures.get(dfbColorTexturesA).position(0);
      EXTFramebufferObject.glBindFramebufferEXT(36160, dfb);
      GL20.glDrawBuffers(0);
      GL11.glReadBuffer(0);

      int var0;
      for(var0 = 0; var0 < usedDepthBuffers; ++var0) {
         GlStateManager.func_179144_i(dfbDepthTextures.get(var0));
         GL11.glTexParameteri(3553, 10242, 10496);
         GL11.glTexParameteri(3553, 10243, 10496);
         GL11.glTexParameteri(3553, 10241, 9728);
         GL11.glTexParameteri(3553, 10240, 9728);
         GL11.glTexParameteri(3553, 34891, 6409);
         GL11.glTexImage2D(3553, 0, 6402, renderWidth, renderHeight, 0, 6402, 5126, (FloatBuffer)null);
      }

      EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, dfbDepthTextures.get(0), 0);
      GL20.glDrawBuffers(dfbDrawBuffers);
      GL11.glReadBuffer(0);
      checkGLError("FT d");

      for(var0 = 0; var0 < usedColorBuffers; ++var0) {
         GlStateManager.func_179144_i(dfbColorTexturesA[var0]);
         GL11.glTexParameteri(3553, 10242, 10496);
         GL11.glTexParameteri(3553, 10243, 10496);
         GL11.glTexParameteri(3553, 10241, 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glTexImage2D(3553, 0, gbuffersFormat[var0], renderWidth, renderHeight, 0, 32993, 33639, (ByteBuffer)null);
         EXTFramebufferObject.glFramebufferTexture2DEXT(36160, '賠' + var0, 3553, dfbColorTexturesA[var0], 0);
         checkGLError("FT c");
      }

      for(var0 = 0; var0 < usedColorBuffers; ++var0) {
         GlStateManager.func_179144_i(dfbColorTexturesA[8 + var0]);
         GL11.glTexParameteri(3553, 10242, 10496);
         GL11.glTexParameteri(3553, 10243, 10496);
         GL11.glTexParameteri(3553, 10241, 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glTexImage2D(3553, 0, gbuffersFormat[var0], renderWidth, renderHeight, 0, 32993, 33639, (ByteBuffer)null);
         checkGLError("FT ca");
      }

      var0 = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
      if (var0 == 36058) {
         printChatAndLogError("[Shaders] Error: Failed framebuffer incomplete formats");

         for(int var1 = 0; var1 < usedColorBuffers; ++var1) {
            GlStateManager.func_179144_i(dfbColorTextures.get(var1));
            GL11.glTexImage2D(3553, 0, 6408, renderWidth, renderHeight, 0, 32993, 33639, (ByteBuffer)null);
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, '賠' + var1, 3553, dfbColorTextures.get(var1), 0);
            checkGLError("FT c");
         }

         var0 = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
         if (var0 == 36053) {
            SMCLog.info("complete");
         }
      }

      GlStateManager.func_179144_i(0);
      if (var0 != 36053) {
         printChatAndLogError(String.valueOf((new StringBuilder("[Shaders] Error: Failed creating framebuffer! (Status ")).append(var0).append(")")));
      } else {
         SMCLog.info("Framebuffer created.");
      }

   }

   public static int checkFramebufferStatus(String var0) {
      int var1 = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
      if (var1 != 36053) {
         System.err.format("FramebufferStatus 0x%04X at %s\n", var1, var0);
      }

      return var1;
   }

   public static void setBlockEntityId(TileEntity var0) {
      if (isRenderingWorld && !isShadowPass && uniformBlockEntityId.isDefined()) {
         Block var1 = var0.getBlockType();
         int var2 = Block.getIdFromBlock(var1);
         uniformBlockEntityId.setValue(var2);
      }

   }

   private static void resize() {
      renderDisplayWidth = mc.displayWidth;
      renderDisplayHeight = mc.displayHeight;
      renderWidth = Math.round((float)renderDisplayWidth * configRenderResMul);
      renderHeight = Math.round((float)renderDisplayHeight * configRenderResMul);
      setupFrameBuffer();
   }

   public static void pushEntity(int var0, int var1) {
      ++entityDataIndex;
      entityData[entityDataIndex * 2] = var0 & '\uffff' | var1 << 16;
      entityData[entityDataIndex * 2 + 1] = 0;
   }

   private static int getBufferIndexFromString(String var0) {
      return !var0.equals("colortex0") && !var0.equals("gcolor") ? (!var0.equals("colortex1") && !var0.equals("gdepth") ? (!var0.equals("colortex2") && !var0.equals("gnormal") ? (!var0.equals("colortex3") && !var0.equals("composite") ? (!var0.equals("colortex4") && !var0.equals("gaux1") ? (!var0.equals("colortex5") && !var0.equals("gaux2") ? (!var0.equals("colortex6") && !var0.equals("gaux3") ? (!var0.equals("colortex7") && !var0.equals("gaux4") ? -1 : 7) : 6) : 5) : 4) : 3) : 2) : 1) : 0;
   }

   public static void setProgramUniform2i(String var0, int var1, int var2) {
      int var3 = programsID[activeProgram];
      if (var3 != 0) {
         int var4 = ARBShaderObjects.glGetUniformLocationARB(var3, var0);
         ARBShaderObjects.glUniform2iARB(var4, var1, var2);
         checkGLError(programNames[activeProgram], var0);
      }

   }

   public static void endLeash() {
      useProgram(16);
   }

   public static void storeConfig() {
      SMCLog.info("Save ShadersMod configuration.");
      if (shadersConfig == null) {
         shadersConfig = new PropertiesOrdered();
      }

      EnumShaderOption[] var0 = EnumShaderOption.values();

      for(int var1 = 0; var1 < var0.length; ++var1) {
         EnumShaderOption var2 = var0[var1];
         String var3 = var2.getPropertyKey();
         String var4 = getEnumShaderOption(var2);
         shadersConfig.setProperty(var3, var4);
      }

      try {
         FileWriter var6 = new FileWriter(configFile);
         shadersConfig.store(var6, (String)null);
         var6.close();
      } catch (Exception var5) {
         SMCLog.severe(String.valueOf((new StringBuilder("Error saving configuration: ")).append(var5.getClass().getName()).append(": ").append(var5.getMessage())));
      }

   }

   public static void beginRenderPass(int var0, float var1, long var2) {
      if (!isShadowPass) {
         EXTFramebufferObject.glBindFramebufferEXT(36160, dfb);
         GL11.glViewport(0, 0, renderWidth, renderHeight);
         activeDrawBuffers = null;
         ShadersTex.bindNSTextures(defaultTexture.getMultiTexID());
         useProgram(2);
         checkGLError("end beginRenderPass");
      }

   }

   public static void setCameraShadow(float var0) {
      Entity var1 = mc.func_175606_aa();
      double var2 = var1.lastTickPosX + (var1.posX - var1.lastTickPosX) * (double)var0;
      double var4 = var1.lastTickPosY + (var1.posY - var1.lastTickPosY) * (double)var0;
      double var6 = var1.lastTickPosZ + (var1.posZ - var1.lastTickPosZ) * (double)var0;
      cameraPositionX = var2;
      cameraPositionY = var4;
      cameraPositionZ = var6;
      GL11.glGetFloat(2983, (FloatBuffer)projection.position(0));
      SMath.invertMat4FBFA((FloatBuffer)projectionInverse.position(0), (FloatBuffer)projection.position(0), faProjectionInverse, faProjection);
      projection.position(0);
      projectionInverse.position(0);
      GL11.glGetFloat(2982, (FloatBuffer)modelView.position(0));
      SMath.invertMat4FBFA((FloatBuffer)modelViewInverse.position(0), (FloatBuffer)modelView.position(0), faModelViewInverse, faModelView);
      modelView.position(0);
      modelViewInverse.position(0);
      GL11.glViewport(0, 0, shadowMapWidth, shadowMapHeight);
      GL11.glMatrixMode(5889);
      GL11.glLoadIdentity();
      if (shadowMapIsOrtho) {
         GL11.glOrtho((double)(-shadowMapHalfPlane), (double)shadowMapHalfPlane, (double)(-shadowMapHalfPlane), (double)shadowMapHalfPlane, 0.05000000074505806D, 256.0D);
      } else {
         GLU.gluPerspective(shadowMapFOV, (float)shadowMapWidth / (float)shadowMapHeight, 0.05F, 256.0F);
      }

      GL11.glMatrixMode(5888);
      GL11.glLoadIdentity();
      GL11.glTranslatef(0.0F, 0.0F, -100.0F);
      GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
      celestialAngle = mc.theWorld.getCelestialAngle(var0);
      sunAngle = celestialAngle < 0.75F ? celestialAngle + 0.25F : celestialAngle - 0.75F;
      float var8 = celestialAngle * -360.0F;
      float var9 = shadowAngleInterval > 0.0F ? var8 % shadowAngleInterval - shadowAngleInterval * 0.5F : 0.0F;
      if ((double)sunAngle <= 0.5D) {
         GL11.glRotatef(var8 - var9, 0.0F, 0.0F, 1.0F);
         GL11.glRotatef(sunPathRotation, 1.0F, 0.0F, 0.0F);
         shadowAngle = sunAngle;
      } else {
         GL11.glRotatef(var8 + 180.0F - var9, 0.0F, 0.0F, 1.0F);
         GL11.glRotatef(sunPathRotation, 1.0F, 0.0F, 0.0F);
         shadowAngle = sunAngle - 0.5F;
      }

      float var10;
      float var11;
      if (shadowMapIsOrtho) {
         var10 = shadowIntervalSize;
         var11 = var10 / 2.0F;
         GL11.glTranslatef((float)var2 % var10 - var11, (float)var4 % var10 - var11, (float)var6 % var10 - var11);
      }

      var10 = sunAngle * 6.2831855F;
      var11 = (float)Math.cos((double)var10);
      float var12 = (float)Math.sin((double)var10);
      float var13 = sunPathRotation * 6.2831855F;
      float var14 = var11;
      float var15 = var12 * (float)Math.cos((double)var13);
      float var16 = var12 * (float)Math.sin((double)var13);
      if ((double)sunAngle > 0.5D) {
         var14 = -var11;
         var15 = -var15;
         var16 = -var16;
      }

      shadowLightPositionVector[0] = var14;
      shadowLightPositionVector[1] = var15;
      shadowLightPositionVector[2] = var16;
      shadowLightPositionVector[3] = 0.0F;
      GL11.glGetFloat(2983, (FloatBuffer)shadowProjection.position(0));
      SMath.invertMat4FBFA((FloatBuffer)shadowProjectionInverse.position(0), (FloatBuffer)shadowProjection.position(0), faShadowProjectionInverse, faShadowProjection);
      shadowProjection.position(0);
      shadowProjectionInverse.position(0);
      GL11.glGetFloat(2982, (FloatBuffer)shadowModelView.position(0));
      SMath.invertMat4FBFA((FloatBuffer)shadowModelViewInverse.position(0), (FloatBuffer)shadowModelView.position(0), faShadowModelViewInverse, faShadowModelView);
      shadowModelView.position(0);
      shadowModelViewInverse.position(0);
      setProgramUniformMatrix4ARB("gbufferProjection", false, projection);
      setProgramUniformMatrix4ARB("gbufferProjectionInverse", false, projectionInverse);
      setProgramUniformMatrix4ARB("gbufferPreviousProjection", false, previousProjection);
      setProgramUniformMatrix4ARB("gbufferModelView", false, modelView);
      setProgramUniformMatrix4ARB("gbufferModelViewInverse", false, modelViewInverse);
      setProgramUniformMatrix4ARB("gbufferPreviousModelView", false, previousModelView);
      setProgramUniformMatrix4ARB("shadowProjection", false, shadowProjection);
      setProgramUniformMatrix4ARB("shadowProjectionInverse", false, shadowProjectionInverse);
      setProgramUniformMatrix4ARB("shadowModelView", false, shadowModelView);
      setProgramUniformMatrix4ARB("shadowModelViewInverse", false, shadowModelViewInverse);
      mc.gameSettings.thirdPersonView = 1;
      checkGLError("setCamera");
   }

   public static int checkGLError(String var0, String var1, String var2) {
      int var3 = GL11.glGetError();
      if (var3 != 0) {
         System.err.format("GL error 0x%04x: %s at %s %s %s\n", var3, GLU.gluErrorString(var3), var0, var1, var2);
      }

      return var3;
   }

   public static void setShaderPack(String var0) {
      currentshadername = var0;
      shadersConfig.setProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), var0);
      loadShaderPack();
   }

   public static void beginParticles() {
      useProgram(2);
   }

   public static void saveShaderPackOptions() {
      saveShaderPackOptions(shaderPackOptions, shaderPack);
   }

   public static boolean isDynamicHandLight() {
      return !shaderPackDynamicHandLight.isDefault() ? shaderPackDynamicHandLight.isTrue() : true;
   }

   public static void endFPOverlay() {
   }

   public static void endClouds() {
      disableFog();
      popEntity();
      useProgram(lightmapEnabled ? 3 : 2);
   }

   private static int setupProgram(int var0, String var1, String var2) {
      checkGLError("pre setupProgram");
      int var3 = ARBShaderObjects.glCreateProgramObjectARB();
      checkGLError("create");
      if (var3 != 0) {
         progUseEntityAttrib = false;
         progUseMidTexCoordAttrib = false;
         progUseTangentAttrib = false;
         int var4 = createVertShader(var1);
         int var5 = createFragShader(var2);
         checkGLError("create");
         if (var4 == 0 && var5 == 0) {
            ARBShaderObjects.glDeleteObjectARB(var3);
            var3 = 0;
         } else {
            if (var4 != 0) {
               ARBShaderObjects.glAttachObjectARB(var3, var4);
               checkGLError("attach");
            }

            if (var5 != 0) {
               ARBShaderObjects.glAttachObjectARB(var3, var5);
               checkGLError("attach");
            }

            if (progUseEntityAttrib) {
               ARBVertexShader.glBindAttribLocationARB(var3, entityAttrib, "mc_Entity");
               checkGLError("mc_Entity");
            }

            if (progUseMidTexCoordAttrib) {
               ARBVertexShader.glBindAttribLocationARB(var3, midTexCoordAttrib, "mc_midTexCoord");
               checkGLError("mc_midTexCoord");
            }

            if (progUseTangentAttrib) {
               ARBVertexShader.glBindAttribLocationARB(var3, tangentAttrib, "at_tangent");
               checkGLError("at_tangent");
            }

            ARBShaderObjects.glLinkProgramARB(var3);
            if (GL20.glGetProgrami(var3, 35714) != 1) {
               SMCLog.severe(String.valueOf((new StringBuilder("Error linking program: ")).append(var3)));
            }

            printLogInfo(var3, String.valueOf((new StringBuilder(String.valueOf(var1))).append(", ").append(var2)));
            if (var4 != 0) {
               ARBShaderObjects.glDetachObjectARB(var3, var4);
               ARBShaderObjects.glDeleteObjectARB(var4);
            }

            if (var5 != 0) {
               ARBShaderObjects.glDetachObjectARB(var3, var5);
               ARBShaderObjects.glDeleteObjectARB(var5);
            }

            programsID[var0] = var3;
            useProgram(var0);
            ARBShaderObjects.glValidateProgramARB(var3);
            useProgram(0);
            printLogInfo(var3, String.valueOf((new StringBuilder(String.valueOf(var1))).append(", ").append(var2)));
            int var6 = GL20.glGetProgrami(var3, 35715);
            if (var6 != 1) {
               String var7 = "\"";
               printChatAndLogError(String.valueOf((new StringBuilder("[Shaders] Error: Invalid program ")).append(var7).append(programNames[var0]).append(var7)));
               ARBShaderObjects.glDeleteObjectARB(var3);
               var3 = 0;
            }
         }
      }

      return var3;
   }

   public static void init() {
      boolean var0;
      if (!isInitializedOnce) {
         isInitializedOnce = true;
         var0 = true;
      } else {
         var0 = false;
      }

      if (!isShaderPackInitialized) {
         checkGLError("Shaders.init pre");
         if (getShaderPackName() != null) {
         }

         if (!capabilities.OpenGL20) {
            printChatAndLogError("No OpenGL 2.0");
         }

         if (!capabilities.GL_EXT_framebuffer_object) {
            printChatAndLogError("No EXT_framebuffer_object");
         }

         dfbDrawBuffers.position(0).limit(8);
         dfbColorTextures.position(0).limit(16);
         dfbDepthTextures.position(0).limit(3);
         sfbDrawBuffers.position(0).limit(8);
         sfbDepthTextures.position(0).limit(2);
         sfbColorTextures.position(0).limit(8);
         usedColorBuffers = 4;
         usedDepthBuffers = 1;
         usedShadowColorBuffers = 0;
         usedShadowDepthBuffers = 0;
         usedColorAttachs = 1;
         usedDrawBuffers = 1;
         Arrays.fill(gbuffersFormat, 6408);
         Arrays.fill(shadowHardwareFilteringEnabled, false);
         Arrays.fill(shadowMipmapEnabled, false);
         Arrays.fill(shadowFilterNearest, false);
         Arrays.fill(shadowColorMipmapEnabled, false);
         Arrays.fill(shadowColorFilterNearest, false);
         centerDepthSmoothEnabled = false;
         noiseTextureEnabled = false;
         sunPathRotation = 0.0F;
         shadowIntervalSize = 2.0F;
         aoLevel = 0.8F;
         blockAoLight = 1.0F - aoLevel;
         useEntityAttrib = false;
         useMidTexCoordAttrib = false;
         useMultiTexCoord3Attrib = false;
         useTangentAttrib = false;
         waterShadowEnabled = false;
         updateChunksErrorRecorded = false;
         updateBlockLightLevel();
         ShaderProfile var1 = ShaderUtils.detectProfile(shaderPackProfiles, shaderPackOptions, false);
         String var2 = "";
         int var3;
         if (currentWorld != null) {
            var3 = currentWorld.provider.getDimensionId();
            if (shaderPackDimensions.contains(var3)) {
               var2 = String.valueOf((new StringBuilder("world")).append(var3).append("/"));
            }
         }

         if (saveFinalShaders) {
            clearDirectory(new File(shaderpacksdir, "debug"));
         }

         String var4;
         int var7;
         for(var3 = 0; var3 < 33; ++var3) {
            String var5 = programNames[var3];
            if (var5.equals("")) {
               programsID[var3] = programsRef[var3] = 0;
               programsDrawBufSettings[var3] = null;
               programsColorAtmSettings[var3] = null;
               programsCompositeMipmapSetting[var3] = 0;
            } else {
               newDrawBufSetting = null;
               newColorAtmSetting = null;
               newCompositeMipmapSetting = 0;
               String var6 = String.valueOf((new StringBuilder(String.valueOf(var2))).append(var5));
               if (var1 != null && var1.isProgramDisabled(var6)) {
                  SMCLog.info(String.valueOf((new StringBuilder("Program disabled: ")).append(var6)));
                  var5 = "<disabled>";
                  var6 = String.valueOf((new StringBuilder(String.valueOf(var2))).append(var5));
               }

               var4 = String.valueOf((new StringBuilder("/shaders/")).append(var6));
               var7 = setupProgram(var3, String.valueOf((new StringBuilder(String.valueOf(var4))).append(".vsh")), String.valueOf((new StringBuilder(String.valueOf(var4))).append(".fsh")));
               if (var7 > 0) {
                  SMCLog.info(String.valueOf((new StringBuilder("Program loaded: ")).append(var6)));
               }

               programsID[var3] = programsRef[var3] = var7;
               programsDrawBufSettings[var3] = var7 != 0 ? newDrawBufSetting : null;
               programsColorAtmSettings[var3] = var7 != 0 ? newColorAtmSetting : null;
               programsCompositeMipmapSetting[var3] = var7 != 0 ? newCompositeMipmapSetting : 0;
            }
         }

         var3 = GL11.glGetInteger(34852);
         new HashMap();

         int var11;
         for(var11 = 0; var11 < 33; ++var11) {
            Arrays.fill(programsToggleColorTextures[var11], false);
            if (var11 == 29) {
               programsDrawBuffers[var11] = null;
            } else if (programsID[var11] == 0) {
               if (var11 == 30) {
                  programsDrawBuffers[var11] = drawBuffersNone;
               } else {
                  programsDrawBuffers[var11] = drawBuffersColorAtt0;
               }
            } else {
               var4 = programsDrawBufSettings[var11];
               if (var4 != null) {
                  IntBuffer var12 = drawBuffersBuffer[var11];
                  var7 = var4.length();
                  if (var7 > usedDrawBuffers) {
                     usedDrawBuffers = var7;
                  }

                  if (var7 > var3) {
                     var7 = var3;
                  }

                  programsDrawBuffers[var11] = var12;
                  var12.limit(var7);

                  for(int var8 = 0; var8 < var7; ++var8) {
                     int var9 = 0;
                     if (var4.length() > var8) {
                        int var10 = var4.charAt(var8) - 48;
                        if (var11 != 30) {
                           if (var10 >= 0 && var10 <= 7) {
                              programsToggleColorTextures[var11][var10] = true;
                              var9 = var10 + '賠';
                              if (var10 > usedColorAttachs) {
                                 usedColorAttachs = var10;
                              }

                              if (var10 > usedColorBuffers) {
                                 usedColorBuffers = var10;
                              }
                           }
                        } else if (var10 >= 0 && var10 <= 1) {
                           var9 = var10 + '賠';
                           if (var10 > usedShadowColorBuffers) {
                              usedShadowColorBuffers = var10;
                           }
                        }
                     }

                     var12.put(var8, var9);
                  }
               } else if (var11 != 30 && var11 != 31 && var11 != 32) {
                  programsDrawBuffers[var11] = dfbDrawBuffers;
                  usedDrawBuffers = usedColorBuffers;
                  Arrays.fill(programsToggleColorTextures[var11], 0, usedColorBuffers, true);
               } else {
                  programsDrawBuffers[var11] = sfbDrawBuffers;
               }
            }
         }

         usedColorAttachs = usedColorBuffers;
         shadowPassInterval = usedShadowDepthBuffers > 0 ? 1 : 0;
         shouldSkipDefaultShadow = usedShadowDepthBuffers > 0;
         SMCLog.info(String.valueOf((new StringBuilder("usedColorBuffers: ")).append(usedColorBuffers)));
         SMCLog.info(String.valueOf((new StringBuilder("usedDepthBuffers: ")).append(usedDepthBuffers)));
         SMCLog.info(String.valueOf((new StringBuilder("usedShadowColorBuffers: ")).append(usedShadowColorBuffers)));
         SMCLog.info(String.valueOf((new StringBuilder("usedShadowDepthBuffers: ")).append(usedShadowDepthBuffers)));
         SMCLog.info(String.valueOf((new StringBuilder("usedColorAttachs: ")).append(usedColorAttachs)));
         SMCLog.info(String.valueOf((new StringBuilder("usedDrawBuffers: ")).append(usedDrawBuffers)));
         dfbDrawBuffers.position(0).limit(usedDrawBuffers);
         dfbColorTextures.position(0).limit(usedColorBuffers * 2);

         for(var11 = 0; var11 < usedDrawBuffers; ++var11) {
            dfbDrawBuffers.put(var11, '賠' + var11);
         }

         if (usedDrawBuffers > var3) {
            printChatAndLogError(String.valueOf((new StringBuilder("[Shaders] Error: Not enough draw buffers, needed: ")).append(usedDrawBuffers).append(", available: ").append(var3)));
         }

         sfbDrawBuffers.position(0).limit(usedShadowColorBuffers);

         for(var11 = 0; var11 < usedShadowColorBuffers; ++var11) {
            sfbDrawBuffers.put(var11, '賠' + var11);
         }

         for(var11 = 0; var11 < 33; ++var11) {
            int var13;
            for(var13 = var11; programsID[var13] == 0 && programBackups[var13] != var13; var13 = programBackups[var13]) {
            }

            if (var13 != var11 && var11 != 30) {
               programsID[var11] = programsID[var13];
               programsDrawBufSettings[var11] = programsDrawBufSettings[var13];
               programsDrawBuffers[var11] = programsDrawBuffers[var13];
            }
         }

         resize();
         resizeShadow();
         if (noiseTextureEnabled) {
            setupNoiseTexture();
         }

         if (defaultTexture == null) {
            defaultTexture = ShadersTex.createDefaultTexture();
         }

         GlStateManager.pushMatrix();
         GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
         preCelestialRotate();
         postCelestialRotate();
         GlStateManager.popMatrix();
         isShaderPackInitialized = true;
         loadEntityDataMap();
         resetDisplayList();
         if (!var0) {
         }

         checkGLError("Shaders.init");
      }

   }

   public static void endProjectRedHalo() {
      if (isRenderingWorld) {
         useProgram(3);
      }

   }

   private static void resizeShadow() {
      needResizeShadow = false;
      shadowMapWidth = Math.round((float)spShadowMapWidth * configShadowResMul);
      shadowMapHeight = Math.round((float)spShadowMapHeight * configShadowResMul);
      setupShadowFrameBuffer();
   }

   public static void endWeather() {
      GlStateManager.disableBlend();
      useProgram(3);
   }

   static {
      shadersdir = new File(Minecraft.getMinecraft().mcDataDir, "shaders");
      shaderpacksdir = new File(Minecraft.getMinecraft().mcDataDir, shaderpacksdirname);
      configFile = new File(Minecraft.getMinecraft().mcDataDir, optionsfilename);
      shaderPackOptions = null;
      shaderPackProfiles = null;
      shaderPackGuiScreens = null;
      shaderPackClouds = new PropertyDefaultFastFancyOff("clouds", "Clouds", 0);
      shaderPackOldLighting = new PropertyDefaultTrueFalse("oldLighting", "Classic Lighting", 0);
      shaderPackDynamicHandLight = new PropertyDefaultTrueFalse("dynamicHandLight", "Dynamic Hand Light", 0);
      shaderPackResources = new HashMap();
      currentWorld = null;
      shaderPackDimensions = new ArrayList();
      saveFinalShaders = System.getProperty("shaders.debug.save", "false").equals("true");
      blockLightLevel05 = 0.5F;
      blockLightLevel06 = 0.6F;
      blockLightLevel08 = 0.8F;
      aoLevel = 0.8F;
      blockAoLight = 1.0F - aoLevel;
      sunPathRotation = 0.0F;
      shadowAngleInterval = 0.0F;
      fogMode = 0;
      shadowIntervalSize = 2.0F;
      terrainIconSize = 16;
      terrainTextureSize = new int[2];
      noiseTextureEnabled = false;
      noiseTextureResolution = 256;
      dfbColorTexturesA = new int[16];
      colorTexturesToggle = new int[8];
      colorTextureTextureImageUnit = new int[]{0, 1, 2, 3, 7, 8, 9, 10};
      programsToggleColorTextures = new boolean[33][8];
      bigBuffer = (ByteBuffer)BufferUtils.createByteBuffer(2196).limit(0);
      faProjection = new float[16];
      faProjectionInverse = new float[16];
      faModelView = new float[16];
      faModelViewInverse = new float[16];
      faShadowProjection = new float[16];
      faShadowProjectionInverse = new float[16];
      faShadowModelView = new float[16];
      faShadowModelViewInverse = new float[16];
      projection = nextFloatBuffer(16);
      projectionInverse = nextFloatBuffer(16);
      modelView = nextFloatBuffer(16);
      modelViewInverse = nextFloatBuffer(16);
      shadowProjection = nextFloatBuffer(16);
      shadowProjectionInverse = nextFloatBuffer(16);
      shadowModelView = nextFloatBuffer(16);
      shadowModelViewInverse = nextFloatBuffer(16);
      previousProjection = nextFloatBuffer(16);
      previousModelView = nextFloatBuffer(16);
      tempMatrixDirectBuffer = nextFloatBuffer(16);
      tempDirectFloatBuffer = nextFloatBuffer(16);
      dfbColorTextures = nextIntBuffer(16);
      dfbDepthTextures = nextIntBuffer(3);
      sfbColorTextures = nextIntBuffer(8);
      sfbDepthTextures = nextIntBuffer(2);
      dfbDrawBuffers = nextIntBuffer(8);
      sfbDrawBuffers = nextIntBuffer(8);
      drawBuffersNone = nextIntBuffer(8);
      drawBuffersAll = nextIntBuffer(8);
      drawBuffersClear0 = nextIntBuffer(8);
      drawBuffersClear1 = nextIntBuffer(8);
      drawBuffersClearColor = nextIntBuffer(8);
      drawBuffersColorAtt0 = nextIntBuffer(8);
      drawBuffersBuffer = nextIntBufferArray(33, 8);
      drawBuffersNone.limit(0);
      drawBuffersColorAtt0.put(36064).position(0).limit(1);
      gbufferFormatPattern = Pattern.compile("[ \t]*const[ \t]*int[ \t]*(\\w+)Format[ \t]*=[ \t]*([RGBA81632FUI_SNORM]*)[ \t]*;.*");
      gbufferMipmapEnabledPattern = Pattern.compile("[ \t]*const[ \t]*bool[ \t]*(\\w+)MipmapEnabled[ \t]*=[ \t]*true[ \t]*;.*");
      formatNames = new String[]{"R8", "RG8", "RGB8", "RGBA8", "R8_SNORM", "RG8_SNORM", "RGB8_SNORM", "RGBA8_SNORM", "R16", "RG16", "RGB16", "RGBA16", "R16_SNORM", "RG16_SNORM", "RGB16_SNORM", "RGBA16_SNORM", "R32F", "RG32F", "RGB32F", "RGBA32F", "R32I", "RG32I", "RGB32I", "RGBA32I", "R32UI", "RG32UI", "RGB32UI", "RGBA32UI"};
      formatIds = new int[]{33321, 33323, 32849, 32856, 36756, 36757, 36758, 36759, 33322, 33324, 32852, 32859, 36760, 36761, 36762, 36763, 33326, 33328, 34837, 34836, 33333, 33339, 36227, 36226, 33334, 33340, 36209, 36208};
      patternLoadEntityDataMap = Pattern.compile("\\s*([\\w:]+)\\s*=\\s*([-]?\\d+)\\s*");
      entityData = new int[32];
      entityDataIndex = 0;
   }

   public static void enableFog() {
      fogEnabled = true;
      setProgramUniform1i("fogMode", fogMode);
   }

   public static void beginEntities() {
      if (isRenderingWorld) {
         useProgram(16);
         resetDisplayListModels();
      }

   }

   public static void applyHandDepth() {
      if ((double)configHandDepthMul != 1.0D) {
         GL11.glScaled(1.0D, 1.0D, (double)configHandDepthMul);
      }

   }

   public static boolean isProgramPath(String var0) {
      if (var0 == null) {
         return false;
      } else if (var0.length() <= 0) {
         return false;
      } else {
         int var1 = var0.lastIndexOf("/");
         if (var1 >= 0) {
            var0 = var0.substring(var1 + 1);
         }

         return Arrays.asList(programNames).contains(var0);
      }
   }

   public static void beginWeather() {
      if (!isShadowPass) {
         if (usedDepthBuffers >= 3) {
            GlStateManager.setActiveTexture(33996);
            GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, renderWidth, renderHeight);
            GlStateManager.setActiveTexture(33984);
         }

         GlStateManager.enableDepth();
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
         GlStateManager.enableAlpha();
         useProgram(20);
      }

   }

   public static int checkGLError(String var0) {
      int var1 = GL11.glGetError();
      if (var1 != 0) {
         boolean var2 = false;
         if (!var2) {
            if (var1 == 1286) {
               int var3 = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
               System.err.format("GL error 0x%04X: %s (Fb status 0x%04X) at %s\n", var1, GLU.gluErrorString(var1), var3, var0);
            } else {
               System.err.format("GL error 0x%04X: %s at %s\n", var1, GLU.gluErrorString(var1), var0);
            }
         }
      }

      return var1;
   }

   private static int createVertShader(String var0) {
      int var1 = ARBShaderObjects.glCreateShaderObjectARB(35633);
      if (var1 == 0) {
         return 0;
      } else {
         StringBuilder var2 = new StringBuilder(131072);
         BufferedReader var3 = null;

         try {
            var3 = new BufferedReader(new InputStreamReader(shaderPack.getResourceAsStream(var0)));
         } catch (Exception var7) {
            try {
               var3 = new BufferedReader(new FileReader(new File(var0)));
            } catch (Exception var6) {
               ARBShaderObjects.glDeleteObjectARB(var1);
               return 0;
            }
         }

         ShaderOption[] var4 = getChangedOptions(shaderPackOptions);
         if (var3 != null) {
            try {
               var3 = ShaderPackParser.resolveIncludes(var3, var0, shaderPack, 0);

               String var5;
               while((var5 = var3.readLine()) != null) {
                  var5 = applyOptions(var5, var4);
                  var2.append(var5).append('\n');
                  if (var5.matches("attribute [_a-zA-Z0-9]+ mc_Entity.*")) {
                     useEntityAttrib = true;
                     progUseEntityAttrib = true;
                  } else if (var5.matches("attribute [_a-zA-Z0-9]+ mc_midTexCoord.*")) {
                     useMidTexCoordAttrib = true;
                     progUseMidTexCoordAttrib = true;
                  } else if (var5.matches(".*gl_MultiTexCoord3.*")) {
                     useMultiTexCoord3Attrib = true;
                  } else if (var5.matches("attribute [_a-zA-Z0-9]+ at_tangent.*")) {
                     useTangentAttrib = true;
                     progUseTangentAttrib = true;
                  }
               }

               var3.close();
            } catch (Exception var8) {
               SMCLog.severe(String.valueOf((new StringBuilder("Couldn't read ")).append(var0).append("!")));
               var8.printStackTrace();
               ARBShaderObjects.glDeleteObjectARB(var1);
               return 0;
            }
         }

         if (saveFinalShaders) {
            saveShader(var0, String.valueOf(var2));
         }

         ARBShaderObjects.glShaderSourceARB(var1, var2);
         ARBShaderObjects.glCompileShaderARB(var1);
         if (GL20.glGetShaderi(var1, 35713) != 1) {
            SMCLog.severe(String.valueOf((new StringBuilder("Error compiling vertex shader: ")).append(var0)));
         }

         printShaderLogInfo(var1, var0);
         return var1;
      }
   }

   private static FloatBuffer nextFloatBuffer(int var0) {
      ByteBuffer var1 = bigBuffer;
      int var2 = var1.limit();
      var1.position(var2).limit(var2 + var0 * 4);
      return var1.asFloatBuffer();
   }

   public static void resourcesReloaded() {
      loadShaderPackResources();
   }

   public static void resetDisplayListModel(ModelBase var0) {
      if (var0 != null) {
         Iterator var1 = var0.boxList.iterator();

         while(var1.hasNext()) {
            Object var2 = var1.next();
            if (var2 instanceof ModelRenderer) {
               resetDisplayListModelRenderer((ModelRenderer)var2);
            }
         }
      }

   }

   public static void clearRenderBuffer() {
      if (isShadowPass) {
         checkGLError("shadow clear pre");
         EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, sfbDepthTextures.get(0), 0);
         GL11.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
         GL20.glDrawBuffers(programsDrawBuffers[30]);
         checkFramebufferStatus("shadow clear");
         GL11.glClear(16640);
         checkGLError("shadow clear");
      } else {
         checkGLError("clear pre");
         GL20.glDrawBuffers(36064);
         GL11.glClear(16384);
         GL20.glDrawBuffers(36065);
         GL11.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glClear(16384);

         for(int var0 = 2; var0 < usedColorBuffers; ++var0) {
            GL20.glDrawBuffers('賠' + var0);
            GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            GL11.glClear(16384);
         }

         setDrawBuffers(dfbDrawBuffers);
         checkFramebufferStatus("clear");
         checkGLError("clear");
      }

   }

   private static ByteBuffer nextByteBuffer(int var0) {
      ByteBuffer var1 = bigBuffer;
      int var2 = var1.limit();
      var1.position(var2).limit(var2 + var0);
      return var1.slice();
   }

   public static void pushEntity(int var0) {
      ++entityDataIndex;
      entityData[entityDataIndex * 2] = var0 & '\uffff';
      entityData[entityDataIndex * 2 + 1] = 0;
   }

   private static ShaderOption[] getVisibleOptions(ShaderOption[] var0) {
      ArrayList var1 = new ArrayList();

      for(int var2 = 0; var2 < var0.length; ++var2) {
         ShaderOption var3 = var0[var2];
         if (var3.isVisible()) {
            var1.add(var3);
         }
      }

      ShaderOption[] var4 = (ShaderOption[])var1.toArray(new ShaderOption[var1.size()]);
      return var4;
   }

   static ArrayList listOfShaders() {
      ArrayList var0 = new ArrayList();
      var0.add(packNameNone);
      var0.add(packNameDefault);

      try {
         if (!shaderpacksdir.exists()) {
            shaderpacksdir.mkdir();
         }

         File[] var1 = shaderpacksdir.listFiles();

         for(int var2 = 0; var2 < var1.length; ++var2) {
            File var3 = var1[var2];
            String var4 = var3.getName();
            if (var3.isDirectory()) {
               File var5 = new File(var3, "shaders");
               if (var5.exists() && var5.isDirectory()) {
                  var0.add(var4);
               }
            } else if (var3.isFile() && var4.toLowerCase().endsWith(".zip")) {
               var0.add(var4);
            }
         }
      } catch (Exception var6) {
      }

      return var0;
   }

   public static String getEnumShaderOption(EnumShaderOption var0) {
      switch(var0) {
      case ANTIALIASING:
         return Integer.toString(configAntialiasingLevel);
      case NORMAL_MAP:
         return Boolean.toString(configNormalMap);
      case SPECULAR_MAP:
         return Boolean.toString(configSpecularMap);
      case RENDER_RES_MUL:
         return Float.toString(configRenderResMul);
      case SHADOW_RES_MUL:
         return Float.toString(configShadowResMul);
      case HAND_DEPTH_MUL:
         return Float.toString(configHandDepthMul);
      case CLOUD_SHADOW:
         return Boolean.toString(configCloudShadow);
      case OLD_LIGHTING:
         return configOldLighting.getPropertyValue();
      case SHADER_PACK:
         return currentshadername;
      case TWEAK_BLOCK_DAMAGE:
         return Boolean.toString(configTweakBlockDamage);
      case SHADOW_CLIP_FRUSTRUM:
         return Boolean.toString(configShadowClipFrustrum);
      case TEX_MIN_FIL_B:
         return Integer.toString(configTexMinFilB);
      case TEX_MIN_FIL_N:
         return Integer.toString(configTexMinFilN);
      case TEX_MIN_FIL_S:
         return Integer.toString(configTexMinFilS);
      case TEX_MAG_FIL_B:
         return Integer.toString(configTexMagFilB);
      case TEX_MAG_FIL_N:
         return Integer.toString(configTexMagFilB);
      case TEX_MAG_FIL_S:
         return Integer.toString(configTexMagFilB);
      default:
         throw new IllegalArgumentException(String.valueOf((new StringBuilder("Unknown option: ")).append(var0)));
      }
   }

   private static void loadEntityDataMap() {
      mapBlockToEntityData = new IdentityHashMap(300);
      if (mapBlockToEntityData.isEmpty()) {
         Iterator var0 = Block.blockRegistry.getKeys().iterator();

         while(var0.hasNext()) {
            ResourceLocation var1 = (ResourceLocation)var0.next();
            Block var2 = (Block)Block.blockRegistry.getObject(var1);
            int var3 = Block.blockRegistry.getIDForObject(var2);
            mapBlockToEntityData.put(var2, var3);
         }
      }

      BufferedReader var10 = null;

      try {
         var10 = new BufferedReader(new InputStreamReader(shaderPack.getResourceAsStream("/mc_Entity_x.txt")));
      } catch (Exception var8) {
      }

      if (var10 != null) {
         String var11;
         try {
            while((var11 = var10.readLine()) != null) {
               Matcher var12 = patternLoadEntityDataMap.matcher(var11);
               if (var12.matches()) {
                  String var13 = var12.group(1);
                  String var4 = var12.group(2);
                  int var5 = Integer.parseInt(var4);
                  Block var6 = Block.getBlockFromName(var13);
                  if (var6 != null) {
                     mapBlockToEntityData.put(var6, var5);
                  } else {
                     SMCLog.warning("Unknown block name %s", var13);
                  }
               } else {
                  SMCLog.warning("unmatched %s\n", var11);
               }
            }
         } catch (Exception var9) {
            SMCLog.warning("Error parsing mc_Entity_x.txt");
         }
      }

      if (var10 != null) {
         try {
            var10.close();
         } catch (Exception var7) {
         }
      }

   }

   public static void setProgramUniformMatrix4ARB(String var0, boolean var1, FloatBuffer var2) {
      int var3 = programsID[activeProgram];
      if (var3 != 0 && var2 != null) {
         int var4 = ARBShaderObjects.glGetUniformLocationARB(var3, var0);
         ARBShaderObjects.glUniformMatrix4ARB(var4, var1, var2);
         checkGLError(programNames[activeProgram], var0);
      }

   }

   public static void loadShaderPack() {
      boolean var0 = shaderPackLoaded;
      boolean var1 = isOldLighting();
      shaderPackLoaded = false;
      if (shaderPack != null) {
         shaderPack.close();
         shaderPack = null;
         shaderPackResources.clear();
         shaderPackDimensions.clear();
         shaderPackOptions = null;
         shaderPackProfiles = null;
         shaderPackGuiScreens = null;
         shaderPackClouds.resetValue();
         shaderPackDynamicHandLight.resetValue();
         shaderPackOldLighting.resetValue();
      }

      boolean var2 = false;
      if (Config.isAntialiasing()) {
         SMCLog.info(String.valueOf((new StringBuilder("Shaders can not be loaded, Antialiasing is enabled: ")).append(Config.getAntialiasingLevel()).append("x")));
         var2 = true;
      }

      if (Config.isAnisotropicFiltering()) {
         SMCLog.info(String.valueOf((new StringBuilder("Shaders can not be loaded, Anisotropic Filtering is enabled: ")).append(Config.getAnisotropicFilterLevel()).append("x")));
         var2 = true;
      }

      if (Config.isFastRender()) {
         SMCLog.info("Shaders can not be loaded, Fast Render is enabled.");
         var2 = true;
      }

      String var3 = shadersConfig.getProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), packNameDefault);
      if (!var3.isEmpty() && !var3.equals(packNameNone) && !var2) {
         if (var3.equals(packNameDefault)) {
            shaderPack = new ShaderPackDefault();
            shaderPackLoaded = true;
         } else {
            try {
               File var4 = new File(shaderpacksdir, var3);
               if (var4.isDirectory()) {
                  shaderPack = new ShaderPackFolder(var3, var4);
                  shaderPackLoaded = true;
               } else if (var4.isFile() && var3.toLowerCase().endsWith(".zip")) {
                  shaderPack = new ShaderPackZip(var3, var4);
                  shaderPackLoaded = true;
               }
            } catch (Exception var6) {
            }
         }
      }

      if (shaderPack != null) {
         SMCLog.info(String.valueOf((new StringBuilder("Loaded shaderpack: ")).append(getShaderPackName())));
      } else {
         SMCLog.info("No shaderpack loaded.");
         shaderPack = new ShaderPackNone();
      }

      loadShaderPackResources();
      loadShaderPackDimensions();
      shaderPackOptions = loadShaderPackOptions();
      loadShaderPackProperties();
      boolean var7 = shaderPackLoaded ^ var0;
      boolean var5 = isOldLighting() ^ var1;
      if (var7 || var5) {
         DefaultVertexFormats.updateVertexFormats();
         if (Reflector.LightUtil.exists()) {
            Reflector.LightUtil_itemConsumer.setValue((Object)null);
            Reflector.LightUtil_tessellator.setValue((Object)null);
         }

         updateBlockLightLevel();
         mc.func_175603_A();
      }

   }

   private static IntBuffer fillIntBufferZero(IntBuffer var0) {
      int var1 = var0.limit();

      for(int var2 = var0.position(); var2 < var1; ++var2) {
         var0.put(var2, 0);
      }

      return var0;
   }

   private static String applyOptions(String var0, ShaderOption[] var1) {
      if (var1 != null && var1.length > 0) {
         for(int var2 = 0; var2 < var1.length; ++var2) {
            ShaderOption var3 = var1[var2];
            String var4 = var3.getName();
            if (var3.matchesLine(var0)) {
               var0 = var3.getSourceLine();
               break;
            }
         }

         return var0;
      } else {
         return var0;
      }
   }

   public static void renderCompositeFinal() {
      if (!isShadowPass) {
         checkGLError("pre-renderCompositeFinal");
         GL11.glPushMatrix();
         GL11.glLoadIdentity();
         GL11.glMatrixMode(5889);
         GL11.glPushMatrix();
         GL11.glLoadIdentity();
         GL11.glOrtho(0.0D, 1.0D, 0.0D, 1.0D, 0.0D, 1.0D);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.func_179098_w();
         GlStateManager.disableAlpha();
         GlStateManager.disableBlend();
         GlStateManager.enableDepth();
         GlStateManager.depthFunc(519);
         GlStateManager.depthMask(false);
         GlStateManager.disableLighting();
         if (usedShadowDepthBuffers >= 1) {
            GlStateManager.setActiveTexture(33988);
            GlStateManager.func_179144_i(sfbDepthTextures.get(0));
            if (usedShadowDepthBuffers >= 2) {
               GlStateManager.setActiveTexture(33989);
               GlStateManager.func_179144_i(sfbDepthTextures.get(1));
            }
         }

         int var0;
         for(var0 = 0; var0 < usedColorBuffers; ++var0) {
            GlStateManager.setActiveTexture('蓀' + colorTextureTextureImageUnit[var0]);
            GlStateManager.func_179144_i(dfbColorTexturesA[var0]);
         }

         GlStateManager.setActiveTexture(33990);
         GlStateManager.func_179144_i(dfbDepthTextures.get(0));
         if (usedDepthBuffers >= 2) {
            GlStateManager.setActiveTexture(33995);
            GlStateManager.func_179144_i(dfbDepthTextures.get(1));
            if (usedDepthBuffers >= 3) {
               GlStateManager.setActiveTexture(33996);
               GlStateManager.func_179144_i(dfbDepthTextures.get(2));
            }
         }

         for(var0 = 0; var0 < usedShadowColorBuffers; ++var0) {
            GlStateManager.setActiveTexture('蓍' + var0);
            GlStateManager.func_179144_i(sfbColorTextures.get(var0));
         }

         if (noiseTextureEnabled) {
            GlStateManager.setActiveTexture('蓀' + noiseTexture.textureUnit);
            GlStateManager.func_179144_i(noiseTexture.getID());
            GL11.glTexParameteri(3553, 10242, 10497);
            GL11.glTexParameteri(3553, 10243, 10497);
            GL11.glTexParameteri(3553, 10240, 9729);
            GL11.glTexParameteri(3553, 10241, 9729);
         }

         GlStateManager.setActiveTexture(33984);
         boolean var1 = true;

         int var2;
         for(var2 = 0; var2 < usedColorBuffers; ++var2) {
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, '賠' + var2, 3553, dfbColorTexturesA[8 + var2], 0);
         }

         EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, dfbDepthTextures.get(0), 0);
         GL20.glDrawBuffers(dfbDrawBuffers);
         checkGLError("pre-composite");

         for(var2 = 0; var2 < 8; ++var2) {
            if (programsID[21 + var2] != 0) {
               useProgram(21 + var2);
               checkGLError(programNames[21 + var2]);
               if (activeCompositeMipmapSetting != 0) {
                  genCompositeMipmap();
               }

               drawComposite();

               for(int var3 = 0; var3 < usedColorBuffers; ++var3) {
                  if (programsToggleColorTextures[21 + var2][var3]) {
                     int var4 = colorTexturesToggle[var3];
                     int var5 = colorTexturesToggle[var3] = 8 - var4;
                     GlStateManager.setActiveTexture('蓀' + colorTextureTextureImageUnit[var3]);
                     GlStateManager.func_179144_i(dfbColorTexturesA[var5 + var3]);
                     EXTFramebufferObject.glFramebufferTexture2DEXT(36160, '賠' + var3, 3553, dfbColorTexturesA[var4 + var3], 0);
                  }
               }

               GlStateManager.setActiveTexture(33984);
            }
         }

         checkGLError("composite");
         isRenderingDfb = false;
         mc.getFramebuffer().bindFramebuffer(true);
         OpenGlHelper.func_153188_a(OpenGlHelper.field_153198_e, OpenGlHelper.field_153200_g, 3553, mc.getFramebuffer().framebufferTexture, 0);
         GL11.glViewport(0, 0, mc.displayWidth, mc.displayHeight);
         if (EntityRenderer.anaglyphEnable) {
            boolean var6 = EntityRenderer.anaglyphField != 0;
            GlStateManager.colorMask(var6, !var6, !var6, true);
         }

         GlStateManager.depthMask(true);
         GL11.glClearColor(clearColorR, clearColorG, clearColorB, 1.0F);
         GL11.glClear(16640);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.func_179098_w();
         GlStateManager.disableAlpha();
         GlStateManager.disableBlend();
         GlStateManager.enableDepth();
         GlStateManager.depthFunc(519);
         GlStateManager.depthMask(false);
         checkGLError("pre-final");
         useProgram(29);
         checkGLError("final");
         if (activeCompositeMipmapSetting != 0) {
            genCompositeMipmap();
         }

         drawComposite();
         checkGLError("renderCompositeFinal");
         isCompositeRendered = true;
         GlStateManager.enableLighting();
         GlStateManager.func_179098_w();
         GlStateManager.enableAlpha();
         GlStateManager.enableBlend();
         GlStateManager.depthFunc(515);
         GlStateManager.depthMask(true);
         GL11.glPopMatrix();
         GL11.glMatrixMode(5888);
         GL11.glPopMatrix();
         useProgram(0);
      }

   }

   public static void useProgram(int var0) {
      checkGLError("pre-useProgram");
      if (isShadowPass) {
         var0 = 30;
         if (programsID[30] == 0) {
            normalMapEnabled = false;
            return;
         }
      }

      if (activeProgram != var0) {
         activeProgram = var0;
         ARBShaderObjects.glUseProgramObjectARB(programsID[var0]);
         if (programsID[var0] == 0) {
            normalMapEnabled = false;
         } else {
            if (checkGLError("useProgram ", programNames[var0]) != 0) {
               programsID[var0] = 0;
            }

            IntBuffer var1 = programsDrawBuffers[var0];
            if (isRenderingDfb) {
               setDrawBuffers(var1);
               checkGLError(programNames[var0], " draw buffers = ", programsDrawBufSettings[var0]);
            }

            activeCompositeMipmapSetting = programsCompositeMipmapSetting[var0];
            uniformEntityColor.setProgram(programsID[activeProgram]);
            uniformEntityId.setProgram(programsID[activeProgram]);
            uniformBlockEntityId.setProgram(programsID[activeProgram]);
            switch(var0) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 16:
            case 18:
            case 19:
            case 20:
               normalMapEnabled = true;
               setProgramUniform1i("texture", 0);
               setProgramUniform1i("lightmap", 1);
               setProgramUniform1i("normals", 2);
               setProgramUniform1i("specular", 3);
               setProgramUniform1i("shadow", waterShadowEnabled ? 5 : 4);
               setProgramUniform1i("watershadow", 4);
               setProgramUniform1i("shadowtex0", 4);
               setProgramUniform1i("shadowtex1", 5);
               setProgramUniform1i("depthtex0", 6);
               setProgramUniform1i("depthtex1", 12);
               setProgramUniform1i("shadowcolor", 13);
               setProgramUniform1i("shadowcolor0", 13);
               setProgramUniform1i("shadowcolor1", 14);
               setProgramUniform1i("noisetex", 15);
               break;
            case 14:
            case 15:
            case 17:
            default:
               normalMapEnabled = false;
               break;
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
               normalMapEnabled = false;
               setProgramUniform1i("gcolor", 0);
               setProgramUniform1i("gdepth", 1);
               setProgramUniform1i("gnormal", 2);
               setProgramUniform1i("composite", 3);
               setProgramUniform1i("gaux1", 7);
               setProgramUniform1i("gaux2", 8);
               setProgramUniform1i("gaux3", 9);
               setProgramUniform1i("gaux4", 10);
               setProgramUniform1i("colortex0", 0);
               setProgramUniform1i("colortex1", 1);
               setProgramUniform1i("colortex2", 2);
               setProgramUniform1i("colortex3", 3);
               setProgramUniform1i("colortex4", 7);
               setProgramUniform1i("colortex5", 8);
               setProgramUniform1i("colortex6", 9);
               setProgramUniform1i("colortex7", 10);
               setProgramUniform1i("shadow", waterShadowEnabled ? 5 : 4);
               setProgramUniform1i("watershadow", 4);
               setProgramUniform1i("shadowtex0", 4);
               setProgramUniform1i("shadowtex1", 5);
               setProgramUniform1i("gdepthtex", 6);
               setProgramUniform1i("depthtex0", 6);
               setProgramUniform1i("depthtex1", 11);
               setProgramUniform1i("depthtex2", 12);
               setProgramUniform1i("shadowcolor", 13);
               setProgramUniform1i("shadowcolor0", 13);
               setProgramUniform1i("shadowcolor1", 14);
               setProgramUniform1i("noisetex", 15);
               break;
            case 30:
            case 31:
            case 32:
               setProgramUniform1i("tex", 0);
               setProgramUniform1i("texture", 0);
               setProgramUniform1i("lightmap", 1);
               setProgramUniform1i("normals", 2);
               setProgramUniform1i("specular", 3);
               setProgramUniform1i("shadow", waterShadowEnabled ? 5 : 4);
               setProgramUniform1i("watershadow", 4);
               setProgramUniform1i("shadowtex0", 4);
               setProgramUniform1i("shadowtex1", 5);
               setProgramUniform1i("shadowcolor", 13);
               setProgramUniform1i("shadowcolor0", 13);
               setProgramUniform1i("shadowcolor1", 14);
               setProgramUniform1i("noisetex", 15);
            }

            ItemStack var2 = mc.thePlayer.getCurrentEquippedItem();
            Item var3 = var2 != null ? var2.getItem() : null;
            int var4;
            Block var5;
            if (var3 != null) {
               var4 = Item.itemRegistry.getIDForObject(var3);
               var5 = (Block)Block.blockRegistry.getObjectById(var4);
            } else {
               var4 = -1;
               var5 = null;
            }

            int var6 = var5 != null ? var5.getLightValue() : 0;
            setProgramUniform1i("heldItemId", var4);
            setProgramUniform1i("heldBlockLightValue", var6);
            setProgramUniform1i("fogMode", fogEnabled ? fogMode : 0);
            setProgramUniform3f("fogColor", fogColorR, fogColorG, fogColorB);
            setProgramUniform3f("skyColor", skyColorR, skyColorG, skyColorB);
            setProgramUniform1i("worldTime", (int)worldTime % 24000);
            setProgramUniform1i("moonPhase", moonPhase);
            setProgramUniform1f("frameTimeCounter", frameTimeCounter);
            setProgramUniform1f("sunAngle", sunAngle);
            setProgramUniform1f("shadowAngle", shadowAngle);
            setProgramUniform1f("rainStrength", rainStrength);
            setProgramUniform1f("aspectRatio", (float)renderWidth / (float)renderHeight);
            setProgramUniform1f("viewWidth", (float)renderWidth);
            setProgramUniform1f("viewHeight", (float)renderHeight);
            setProgramUniform1f("near", 0.05F);
            setProgramUniform1f("far", (float)(mc.gameSettings.renderDistanceChunks * 16));
            setProgramUniform3f("sunPosition", sunPosition[0], sunPosition[1], sunPosition[2]);
            setProgramUniform3f("moonPosition", moonPosition[0], moonPosition[1], moonPosition[2]);
            setProgramUniform3f("shadowLightPosition", shadowLightPosition[0], shadowLightPosition[1], shadowLightPosition[2]);
            setProgramUniform3f("upPosition", upPosition[0], upPosition[1], upPosition[2]);
            setProgramUniform3f("previousCameraPosition", (float)previousCameraPositionX, (float)previousCameraPositionY, (float)previousCameraPositionZ);
            setProgramUniform3f("cameraPosition", (float)cameraPositionX, (float)cameraPositionY, (float)cameraPositionZ);
            setProgramUniformMatrix4ARB("gbufferModelView", false, modelView);
            setProgramUniformMatrix4ARB("gbufferModelViewInverse", false, modelViewInverse);
            setProgramUniformMatrix4ARB("gbufferPreviousProjection", false, previousProjection);
            setProgramUniformMatrix4ARB("gbufferProjection", false, projection);
            setProgramUniformMatrix4ARB("gbufferProjectionInverse", false, projectionInverse);
            setProgramUniformMatrix4ARB("gbufferPreviousModelView", false, previousModelView);
            if (usedShadowDepthBuffers > 0) {
               setProgramUniformMatrix4ARB("shadowProjection", false, shadowProjection);
               setProgramUniformMatrix4ARB("shadowProjectionInverse", false, shadowProjectionInverse);
               setProgramUniformMatrix4ARB("shadowModelView", false, shadowModelView);
               setProgramUniformMatrix4ARB("shadowModelViewInverse", false, shadowModelViewInverse);
            }

            setProgramUniform1f("wetness", wetness);
            setProgramUniform1f("eyeAltitude", eyePosY);
            setProgramUniform2i("eyeBrightness", eyeBrightness & '\uffff', eyeBrightness >> 16);
            setProgramUniform2i("eyeBrightnessSmooth", Math.round(eyeBrightnessFadeX), Math.round(eyeBrightnessFadeY));
            setProgramUniform2i("terrainTextureSize", terrainTextureSize[0], terrainTextureSize[1]);
            setProgramUniform1i("terrainIconSize", terrainIconSize);
            setProgramUniform1i("isEyeInWater", isEyeInWater);
            setProgramUniform1i("hideGUI", mc.gameSettings.hideGUI ? 1 : 0);
            setProgramUniform1f("centerDepthSmooth", centerDepthSmooth);
            setProgramUniform2i("atlasSize", atlasSizeX, atlasSizeY);
            checkGLError("useProgram ", programNames[var0]);
         }
      }

   }

   public static void beginBlockEntities() {
      if (isRenderingWorld) {
         checkGLError("beginBlockEntities");
         useProgram(13);
      }

   }

   private static int getTextureFormatFromString(String var0) {
      var0 = var0.trim();

      for(int var1 = 0; var1 < formatNames.length; ++var1) {
         String var2 = formatNames[var1];
         if (var0.equals(var2)) {
            return formatIds[var1];
         }
      }

      return 0;
   }

   private static IntBuffer nextIntBuffer(int var0) {
      ByteBuffer var1 = bigBuffer;
      int var2 = var1.limit();
      var1.position(var2).limit(var2 + var0 * 4);
      return var1.asIntBuffer();
   }

   public static void endSky() {
      isRenderingSky = false;
      setDrawBuffers(dfbDrawBuffers);
      useProgram(lightmapEnabled ? 3 : 2);
      popEntity();
   }

   public static void endParticles() {
      useProgram(3);
   }

   public static void setEntityId(Entity var0) {
      if (isRenderingWorld && !isShadowPass && uniformEntityId.isDefined()) {
         int var1 = EntityList.getEntityID(var0);
         uniformEntityId.setValue(var1);
      }

   }

   private static boolean printLogInfo(int var0, String var1) {
      IntBuffer var2 = BufferUtils.createIntBuffer(1);
      ARBShaderObjects.glGetObjectParameterARB(var0, 35716, var2);
      int var3 = var2.get();
      if (var3 > 1) {
         ByteBuffer var4 = BufferUtils.createByteBuffer(var3);
         var2.flip();
         ARBShaderObjects.glGetInfoLogARB(var0, var2, var4);
         byte[] var5 = new byte[var3];
         var4.get(var5);
         if (var5[var3 - 1] == 0) {
            var5[var3 - 1] = 10;
         }

         String var6 = new String(var5);
         SMCLog.info(String.valueOf((new StringBuilder("Info log: ")).append(var1).append("\n").append(var6)));
         return false;
      } else {
         return true;
      }
   }

   public static void drawHorizon() {
      WorldRenderer var0 = Tessellator.getInstance().getWorldRenderer();
      float var1 = (float)(mc.gameSettings.renderDistanceChunks * 16);
      double var2 = (double)var1 * 0.9238D;
      double var4 = (double)var1 * 0.3826D;
      double var6 = -var4;
      double var8 = -var2;
      double var10 = 16.0D;
      double var12 = -cameraPositionY;
      var0.startDrawingQuads();
      var0.addVertex(var6, var12, var8);
      var0.addVertex(var6, var10, var8);
      var0.addVertex(var8, var10, var6);
      var0.addVertex(var8, var12, var6);
      var0.addVertex(var8, var12, var6);
      var0.addVertex(var8, var10, var6);
      var0.addVertex(var8, var10, var4);
      var0.addVertex(var8, var12, var4);
      var0.addVertex(var8, var12, var4);
      var0.addVertex(var8, var10, var4);
      var0.addVertex(var6, var10, var4);
      var0.addVertex(var6, var12, var4);
      var0.addVertex(var6, var12, var4);
      var0.addVertex(var6, var10, var4);
      var0.addVertex(var4, var10, var2);
      var0.addVertex(var4, var12, var2);
      var0.addVertex(var4, var12, var2);
      var0.addVertex(var4, var10, var2);
      var0.addVertex(var2, var10, var4);
      var0.addVertex(var2, var12, var4);
      var0.addVertex(var2, var12, var4);
      var0.addVertex(var2, var10, var4);
      var0.addVertex(var2, var10, var6);
      var0.addVertex(var2, var12, var6);
      var0.addVertex(var2, var12, var6);
      var0.addVertex(var2, var10, var6);
      var0.addVertex(var4, var10, var8);
      var0.addVertex(var4, var12, var8);
      var0.addVertex(var4, var12, var8);
      var0.addVertex(var4, var10, var8);
      var0.addVertex(var6, var10, var8);
      var0.addVertex(var6, var12, var8);
      Tessellator.getInstance().draw();
   }

   public static void endUpdateChunks() {
      checkGLError("endUpdateChunks1");
      checkFramebufferStatus("endUpdateChunks1");
      if (!isShadowPass) {
         useProgram(7);
      }

      checkGLError("endUpdateChunks2");
      checkFramebufferStatus("endUpdateChunks2");
   }

   public static void enableLightmap() {
      lightmapEnabled = true;
      if (activeProgram == 2) {
         useProgram(3);
      }

   }

   private static String toStringYN(boolean var0) {
      return var0 ? "Y" : "N";
   }

   public static void beginSpiderEyes() {
      if (isRenderingWorld && programsID[18] != programsID[0]) {
         useProgram(18);
         GlStateManager.enableAlpha();
         GlStateManager.alphaFunc(516, 0.0F);
         GlStateManager.blendFunc(770, 771);
      }

   }

   public static void setCamera(float var0) {
      Entity var1 = mc.func_175606_aa();
      double var2 = var1.lastTickPosX + (var1.posX - var1.lastTickPosX) * (double)var0;
      double var4 = var1.lastTickPosY + (var1.posY - var1.lastTickPosY) * (double)var0;
      double var6 = var1.lastTickPosZ + (var1.posZ - var1.lastTickPosZ) * (double)var0;
      cameraPositionX = var2;
      cameraPositionY = var4;
      cameraPositionZ = var6;
      GL11.glGetFloat(2983, (FloatBuffer)projection.position(0));
      SMath.invertMat4FBFA((FloatBuffer)projectionInverse.position(0), (FloatBuffer)projection.position(0), faProjectionInverse, faProjection);
      projection.position(0);
      projectionInverse.position(0);
      GL11.glGetFloat(2982, (FloatBuffer)modelView.position(0));
      SMath.invertMat4FBFA((FloatBuffer)modelViewInverse.position(0), (FloatBuffer)modelView.position(0), faModelViewInverse, faModelView);
      modelView.position(0);
      modelViewInverse.position(0);
      checkGLError("setCamera");
   }

   public static void startup(Minecraft var0) {
      checkShadersModInstalled();
      mc = var0;
      capabilities = GLContext.getCapabilities();
      glVersionString = GL11.glGetString(7938);
      glVendorString = GL11.glGetString(7936);
      glRendererString = GL11.glGetString(7937);
      SMCLog.info("ShadersMod version: 2.4.12");
      SMCLog.info(String.valueOf((new StringBuilder("OpenGL Version: ")).append(glVersionString)));
      SMCLog.info(String.valueOf((new StringBuilder("Vendor:  ")).append(glVendorString)));
      SMCLog.info(String.valueOf((new StringBuilder("Renderer: ")).append(glRendererString)));
      SMCLog.info(String.valueOf((new StringBuilder("Capabilities: ")).append(capabilities.OpenGL20 ? " 2.0 " : " - ").append(capabilities.OpenGL21 ? " 2.1 " : " - ").append(capabilities.OpenGL30 ? " 3.0 " : " - ").append(capabilities.OpenGL32 ? " 3.2 " : " - ").append(capabilities.OpenGL40 ? " 4.0 " : " - ")));
      SMCLog.info(String.valueOf((new StringBuilder("GL_MAX_DRAW_BUFFERS: ")).append(GL11.glGetInteger(34852))));
      SMCLog.info(String.valueOf((new StringBuilder("GL_MAX_COLOR_ATTACHMENTS_EXT: ")).append(GL11.glGetInteger(36063))));
      SMCLog.info(String.valueOf((new StringBuilder("GL_MAX_TEXTURE_IMAGE_UNITS: ")).append(GL11.glGetInteger(34930))));
      hasGlGenMipmap = capabilities.OpenGL30;
      loadConfig();
   }

   private static Properties loadOptionProperties(IShaderPack var0) throws IOException {
      Properties var1 = new Properties();
      String var2 = String.valueOf((new StringBuilder(String.valueOf(shaderpacksdirname))).append("/").append(var0.getName()).append(".txt"));
      File var3 = new File(Minecraft.getMinecraft().mcDataDir, var2);
      if (var3.exists() && var3.isFile() && var3.canRead()) {
         FileInputStream var4 = new FileInputStream(var3);
         var1.load(var4);
         var4.close();
         return var1;
      } else {
         return var1;
      }
   }

   public static boolean shouldRenderClouds(GameSettings var0) {
      if (!shaderPackLoaded) {
         return true;
      } else {
         checkGLError("shouldRenderClouds");
         return isShadowPass ? configCloudShadow : var0.clouds;
      }
   }

   private static IntBuffer[] nextIntBufferArray(int var0, int var1) {
      IntBuffer[] var2 = new IntBuffer[var0];

      for(int var3 = 0; var3 < var0; ++var3) {
         var2[var3] = nextIntBuffer(var1);
      }

      return var2;
   }

   private static void setEnumShaderOption(EnumShaderOption var0, String var1) {
      if (var1 == null) {
         var1 = var0.getValueDefault();
      }

      switch(var0) {
      case ANTIALIASING:
         configAntialiasingLevel = Config.parseInt(var1, 0);
         break;
      case NORMAL_MAP:
         configNormalMap = Config.parseBoolean(var1, true);
         break;
      case SPECULAR_MAP:
         configSpecularMap = Config.parseBoolean(var1, true);
         break;
      case RENDER_RES_MUL:
         configRenderResMul = Config.parseFloat(var1, 1.0F);
         break;
      case SHADOW_RES_MUL:
         configShadowResMul = Config.parseFloat(var1, 1.0F);
         break;
      case HAND_DEPTH_MUL:
         configHandDepthMul = Config.parseFloat(var1, 0.125F);
         break;
      case CLOUD_SHADOW:
         configCloudShadow = Config.parseBoolean(var1, true);
         break;
      case OLD_LIGHTING:
         configOldLighting.setPropertyValue(var1);
         break;
      case SHADER_PACK:
         currentshadername = var1;
         break;
      case TWEAK_BLOCK_DAMAGE:
         configTweakBlockDamage = Config.parseBoolean(var1, true);
         break;
      case SHADOW_CLIP_FRUSTRUM:
         configShadowClipFrustrum = Config.parseBoolean(var1, true);
         break;
      case TEX_MIN_FIL_B:
         configTexMinFilB = Config.parseInt(var1, 0);
         break;
      case TEX_MIN_FIL_N:
         configTexMinFilN = Config.parseInt(var1, 0);
         break;
      case TEX_MIN_FIL_S:
         configTexMinFilS = Config.parseInt(var1, 0);
         break;
      case TEX_MAG_FIL_B:
         configTexMagFilB = Config.parseInt(var1, 0);
         break;
      case TEX_MAG_FIL_N:
         configTexMagFilB = Config.parseInt(var1, 0);
         break;
      case TEX_MAG_FIL_S:
         configTexMagFilB = Config.parseInt(var1, 0);
         break;
      default:
         throw new IllegalArgumentException(String.valueOf((new StringBuilder("Unknown option: ")).append(var0)));
      }

   }

   public static void setDrawBuffers(IntBuffer var0) {
      if (var0 == null) {
         var0 = drawBuffersNone;
      }

      if (activeDrawBuffers != var0) {
         activeDrawBuffers = var0;
         GL20.glDrawBuffers(var0);
      }

   }

   private static void loadShaderPackDimensions() {
      shaderPackDimensions.clear();
      StringBuffer var0 = new StringBuffer();

      for(int var1 = -128; var1 <= 128; ++var1) {
         String var2 = String.valueOf((new StringBuilder("/shaders/world")).append(var1));
         if (shaderPack.hasDirectory(var2)) {
            shaderPackDimensions.add(var1);
            var0.append(String.valueOf((new StringBuilder(" ")).append(var1)));
         }
      }

      if (var0.length() > 0) {
         Config.dbg(String.valueOf((new StringBuilder("[Shaders] Dimensions:")).append(var0)));
      }

   }

   public static void preCelestialRotate() {
      setUpPosition();
      GL11.glRotatef(sunPathRotation * 1.0F, 0.0F, 0.0F, 1.0F);
      checkGLError("preCelestialRotate");
   }

   public static void mcProfilerEndSection() {
      mc.mcProfiler.endSection();
   }

   private static void loadShaderPackResources() {
      shaderPackResources = new HashMap();
      if (shaderPackLoaded) {
         ArrayList var0 = new ArrayList();
         String var1 = "/shaders/lang/";
         String var2 = "en_US";
         String var3 = ".lang";
         var0.add(String.valueOf((new StringBuilder(String.valueOf(var1))).append(var2).append(var3)));
         if (!Config.getGameSettings().language.equals(var2)) {
            var0.add(String.valueOf((new StringBuilder(String.valueOf(var1))).append(Config.getGameSettings().language).append(var3)));
         }

         try {
            Iterator var4 = var0.iterator();

            while(true) {
               InputStream var6;
               do {
                  if (!var4.hasNext()) {
                     return;
                  }

                  String var5 = (String)var4.next();
                  var6 = shaderPack.getResourceAsStream(var5);
               } while(var6 == null);

               Properties var7 = new Properties();
               Lang.loadLocaleData(var6, var7);
               var6.close();
               Set var8 = var7.keySet();
               Iterator var9 = var8.iterator();

               while(var9.hasNext()) {
                  String var10 = (String)var9.next();
                  String var11 = var7.getProperty(var10);
                  shaderPackResources.put(var10, var11);
               }
            }
         } catch (IOException var12) {
            var12.printStackTrace();
         }
      }

   }

   public static ShaderOption getShaderOption(String var0) {
      return ShaderUtils.getShaderOption(var0, shaderPackOptions);
   }

   public static void enableTexture2D() {
      if (isRenderingSky) {
         useProgram(5);
      } else if (activeProgram == 1) {
         useProgram(lightmapEnabled ? 3 : 2);
      }

   }

   public static void setSkyColor(Vec3 var0) {
      skyColorR = (float)var0.xCoord;
      skyColorG = (float)var0.yCoord;
      skyColorB = (float)var0.zCoord;
      setProgramUniform3f("skyColor", skyColorR, skyColorG, skyColorB);
   }

   public static void scheduleResizeShadow() {
      needResizeShadow = true;
   }

   public static void pushEntity(Block var0) {
      ++entityDataIndex;
      entityData[entityDataIndex * 2] = Block.blockRegistry.getIDForObject(var0) & '\uffff' | var0.getRenderType() << 16;
      entityData[entityDataIndex * 2 + 1] = 0;
   }

   public static void sglFogi(int var0, int var1) {
      GL11.glFogi(var0, var1);
      if (var0 == 2917) {
         fogMode = var1;
         if (fogEnabled) {
            setProgramUniform1i("fogMode", fogMode);
         }
      }

   }

   private static void setupShadowFrameBuffer() {
      if (usedShadowDepthBuffers != 0) {
         if (sfb != 0) {
            EXTFramebufferObject.glDeleteFramebuffersEXT(sfb);
            GlStateManager.deleteTextures(sfbDepthTextures);
            GlStateManager.deleteTextures(sfbColorTextures);
         }

         sfb = EXTFramebufferObject.glGenFramebuffersEXT();
         EXTFramebufferObject.glBindFramebufferEXT(36160, sfb);
         GL11.glDrawBuffer(0);
         GL11.glReadBuffer(0);
         GL11.glGenTextures((IntBuffer)sfbDepthTextures.clear().limit(usedShadowDepthBuffers));
         GL11.glGenTextures((IntBuffer)sfbColorTextures.clear().limit(usedShadowColorBuffers));
         sfbDepthTextures.position(0);
         sfbColorTextures.position(0);

         int var0;
         int var1;
         for(var0 = 0; var0 < usedShadowDepthBuffers; ++var0) {
            GlStateManager.func_179144_i(sfbDepthTextures.get(var0));
            GL11.glTexParameterf(3553, 10242, 10496.0F);
            GL11.glTexParameterf(3553, 10243, 10496.0F);
            var1 = shadowFilterNearest[var0] ? 9728 : 9729;
            GL11.glTexParameteri(3553, 10241, var1);
            GL11.glTexParameteri(3553, 10240, var1);
            if (shadowHardwareFilteringEnabled[var0]) {
               GL11.glTexParameteri(3553, 34892, 34894);
            }

            GL11.glTexImage2D(3553, 0, 6402, shadowMapWidth, shadowMapHeight, 0, 6402, 5126, (FloatBuffer)null);
         }

         EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, sfbDepthTextures.get(0), 0);
         checkGLError("FT sd");

         for(var0 = 0; var0 < usedShadowColorBuffers; ++var0) {
            GlStateManager.func_179144_i(sfbColorTextures.get(var0));
            GL11.glTexParameterf(3553, 10242, 10496.0F);
            GL11.glTexParameterf(3553, 10243, 10496.0F);
            var1 = shadowColorFilterNearest[var0] ? 9728 : 9729;
            GL11.glTexParameteri(3553, 10241, var1);
            GL11.glTexParameteri(3553, 10240, var1);
            GL11.glTexImage2D(3553, 0, 6408, shadowMapWidth, shadowMapHeight, 0, 32993, 33639, (ByteBuffer)null);
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, '賠' + var0, 3553, sfbColorTextures.get(var0), 0);
            checkGLError("FT sc");
         }

         GlStateManager.func_179144_i(0);
         if (usedShadowColorBuffers > 0) {
            GL20.glDrawBuffers(sfbDrawBuffers);
         }

         var0 = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
         if (var0 != 36053) {
            printChatAndLogError(String.valueOf((new StringBuilder("[Shaders] Error: Failed creating shadow framebuffer! (Status ")).append(var0).append(")")));
         } else {
            SMCLog.info("Shadow framebuffer created.");
         }
      }

   }

   private static ShaderOption[] loadShaderPackOptions() {
      try {
         ShaderOption[] var0 = ShaderPackParser.parseShaderPackOptions(shaderPack, programNames, shaderPackDimensions);
         Properties var1 = loadOptionProperties(shaderPack);

         for(int var2 = 0; var2 < var0.length; ++var2) {
            ShaderOption var3 = var0[var2];
            String var4 = var1.getProperty(var3.getName());
            if (var4 != null) {
               var3.resetValue();
               if (!var3.setValue(var4)) {
                  Config.warn(String.valueOf((new StringBuilder("[Shaders] Invalid value, option: ")).append(var3.getName()).append(", value: ").append(var4)));
               }
            }
         }

         return var0;
      } catch (IOException var5) {
         Config.warn(String.valueOf((new StringBuilder("[Shaders] Error reading configuration for ")).append(shaderPack.getName())));
         var5.printStackTrace();
         return null;
      }
   }

   private static int createFragShader(String var0) {
      int var1 = ARBShaderObjects.glCreateShaderObjectARB(35632);
      if (var1 == 0) {
         return 0;
      } else {
         StringBuilder var2 = new StringBuilder(131072);
         BufferedReader var3 = null;

         try {
            var3 = new BufferedReader(new InputStreamReader(shaderPack.getResourceAsStream(var0)));
         } catch (Exception var13) {
            try {
               var3 = new BufferedReader(new FileReader(new File(var0)));
            } catch (Exception var12) {
               ARBShaderObjects.glDeleteObjectARB(var1);
               return 0;
            }
         }

         ShaderOption[] var4 = getChangedOptions(shaderPackOptions);
         if (var3 != null) {
            try {
               var3 = ShaderPackParser.resolveIncludes(var3, var0, shaderPack, 0);

               label371:
               while(true) {
                  while(true) {
                     String var5;
                     do {
                        if ((var5 = var3.readLine()) == null) {
                           var3.close();
                           break label371;
                        }

                        var5 = applyOptions(var5, var4);
                        var2.append(var5).append('\n');
                     } while(var5.matches("#version .*"));

                     if (var5.matches("uniform [ _a-zA-Z0-9]+ shadow;.*")) {
                        if (usedShadowDepthBuffers < 1) {
                           usedShadowDepthBuffers = 1;
                        }
                     } else if (var5.matches("uniform [ _a-zA-Z0-9]+ watershadow;.*")) {
                        waterShadowEnabled = true;
                        if (usedShadowDepthBuffers < 2) {
                           usedShadowDepthBuffers = 2;
                        }
                     } else if (var5.matches("uniform [ _a-zA-Z0-9]+ shadowtex0;.*")) {
                        if (usedShadowDepthBuffers < 1) {
                           usedShadowDepthBuffers = 1;
                        }
                     } else if (var5.matches("uniform [ _a-zA-Z0-9]+ shadowtex1;.*")) {
                        if (usedShadowDepthBuffers < 2) {
                           usedShadowDepthBuffers = 2;
                        }
                     } else if (var5.matches("uniform [ _a-zA-Z0-9]+ shadowcolor;.*")) {
                        if (usedShadowColorBuffers < 1) {
                           usedShadowColorBuffers = 1;
                        }
                     } else if (var5.matches("uniform [ _a-zA-Z0-9]+ shadowcolor0;.*")) {
                        if (usedShadowColorBuffers < 1) {
                           usedShadowColorBuffers = 1;
                        }
                     } else if (var5.matches("uniform [ _a-zA-Z0-9]+ shadowcolor1;.*")) {
                        if (usedShadowColorBuffers < 2) {
                           usedShadowColorBuffers = 2;
                        }
                     } else if (var5.matches("uniform [ _a-zA-Z0-9]+ shadowcolor2;.*")) {
                        if (usedShadowColorBuffers < 3) {
                           usedShadowColorBuffers = 3;
                        }
                     } else if (var5.matches("uniform [ _a-zA-Z0-9]+ shadowcolor3;.*")) {
                        if (usedShadowColorBuffers < 4) {
                           usedShadowColorBuffers = 4;
                        }
                     } else if (var5.matches("uniform [ _a-zA-Z0-9]+ depthtex0;.*")) {
                        if (usedDepthBuffers < 1) {
                           usedDepthBuffers = 1;
                        }
                     } else if (var5.matches("uniform [ _a-zA-Z0-9]+ depthtex1;.*")) {
                        if (usedDepthBuffers < 2) {
                           usedDepthBuffers = 2;
                        }
                     } else if (var5.matches("uniform [ _a-zA-Z0-9]+ depthtex2;.*")) {
                        if (usedDepthBuffers < 3) {
                           usedDepthBuffers = 3;
                        }
                     } else if (var5.matches("uniform [ _a-zA-Z0-9]+ gdepth;.*")) {
                        if (gbuffersFormat[1] == 6408) {
                           gbuffersFormat[1] = 34836;
                        }
                     } else if (usedColorBuffers < 5 && var5.matches("uniform [ _a-zA-Z0-9]+ gaux1;.*")) {
                        usedColorBuffers = 5;
                     } else if (usedColorBuffers < 6 && var5.matches("uniform [ _a-zA-Z0-9]+ gaux2;.*")) {
                        usedColorBuffers = 6;
                     } else if (usedColorBuffers < 7 && var5.matches("uniform [ _a-zA-Z0-9]+ gaux3;.*")) {
                        usedColorBuffers = 7;
                     } else if (usedColorBuffers < 8 && var5.matches("uniform [ _a-zA-Z0-9]+ gaux4;.*")) {
                        usedColorBuffers = 8;
                     } else if (usedColorBuffers < 5 && var5.matches("uniform [ _a-zA-Z0-9]+ colortex4;.*")) {
                        usedColorBuffers = 5;
                     } else if (usedColorBuffers < 6 && var5.matches("uniform [ _a-zA-Z0-9]+ colortex5;.*")) {
                        usedColorBuffers = 6;
                     } else if (usedColorBuffers < 7 && var5.matches("uniform [ _a-zA-Z0-9]+ colortex6;.*")) {
                        usedColorBuffers = 7;
                     } else if (usedColorBuffers < 8 && var5.matches("uniform [ _a-zA-Z0-9]+ colortex7;.*")) {
                        usedColorBuffers = 8;
                     } else if (usedColorBuffers < 8 && var5.matches("uniform [ _a-zA-Z0-9]+ centerDepthSmooth;.*")) {
                        centerDepthSmoothEnabled = true;
                     } else {
                        String[] var6;
                        if (var5.matches("/\\* SHADOWRES:[0-9]+ \\*/.*")) {
                           var6 = var5.split("(:| )", 4);
                           SMCLog.info(String.valueOf((new StringBuilder("Shadow map resolution: ")).append(var6[2])));
                           spShadowMapWidth = spShadowMapHeight = Integer.parseInt(var6[2]);
                           shadowMapWidth = shadowMapHeight = Math.round((float)spShadowMapWidth * configShadowResMul);
                        } else if (var5.matches("[ \t]*const[ \t]*int[ \t]*shadowMapResolution[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                           var6 = var5.split("(=[ \t]*|;)");
                           SMCLog.info(String.valueOf((new StringBuilder("Shadow map resolution: ")).append(var6[1])));
                           spShadowMapWidth = spShadowMapHeight = Integer.parseInt(var6[1]);
                           shadowMapWidth = shadowMapHeight = Math.round((float)spShadowMapWidth * configShadowResMul);
                        } else if (var5.matches("/\\* SHADOWFOV:[0-9\\.]+ \\*/.*")) {
                           var6 = var5.split("(:| )", 4);
                           SMCLog.info(String.valueOf((new StringBuilder("Shadow map field of view: ")).append(var6[2])));
                           shadowMapFOV = Float.parseFloat(var6[2]);
                           shadowMapIsOrtho = false;
                        } else if (var5.matches("/\\* SHADOWHPL:[0-9\\.]+ \\*/.*")) {
                           var6 = var5.split("(:| )", 4);
                           SMCLog.info(String.valueOf((new StringBuilder("Shadow map half-plane: ")).append(var6[2])));
                           shadowMapHalfPlane = Float.parseFloat(var6[2]);
                           shadowMapIsOrtho = true;
                        } else if (var5.matches("[ \t]*const[ \t]*float[ \t]*shadowDistance[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                           var6 = var5.split("(=[ \t]*|;)");
                           SMCLog.info(String.valueOf((new StringBuilder("Shadow map distance: ")).append(var6[1])));
                           shadowMapHalfPlane = Float.parseFloat(var6[1]);
                           shadowMapIsOrtho = true;
                        } else if (var5.matches("[ \t]*const[ \t]*float[ \t]*shadowIntervalSize[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                           var6 = var5.split("(=[ \t]*|;)");
                           SMCLog.info(String.valueOf((new StringBuilder("Shadow map interval size: ")).append(var6[1])));
                           shadowIntervalSize = Float.parseFloat(var6[1]);
                        } else if (var5.matches("[ \t]*const[ \t]*bool[ \t]*generateShadowMipmap[ \t]*=[ \t]*true[ \t]*;.*")) {
                           SMCLog.info("Generate shadow mipmap");
                           Arrays.fill(shadowMipmapEnabled, true);
                        } else if (var5.matches("[ \t]*const[ \t]*bool[ \t]*generateShadowColorMipmap[ \t]*=[ \t]*true[ \t]*;.*")) {
                           SMCLog.info("Generate shadow color mipmap");
                           Arrays.fill(shadowColorMipmapEnabled, true);
                        } else if (var5.matches("[ \t]*const[ \t]*bool[ \t]*shadowHardwareFiltering[ \t]*=[ \t]*true[ \t]*;.*")) {
                           SMCLog.info("Hardware shadow filtering enabled.");
                           Arrays.fill(shadowHardwareFilteringEnabled, true);
                        } else if (var5.matches("[ \t]*const[ \t]*bool[ \t]*shadowHardwareFiltering0[ \t]*=[ \t]*true[ \t]*;.*")) {
                           SMCLog.info("shadowHardwareFiltering0");
                           shadowHardwareFilteringEnabled[0] = true;
                        } else if (var5.matches("[ \t]*const[ \t]*bool[ \t]*shadowHardwareFiltering1[ \t]*=[ \t]*true[ \t]*;.*")) {
                           SMCLog.info("shadowHardwareFiltering1");
                           shadowHardwareFilteringEnabled[1] = true;
                        } else if (var5.matches("[ \t]*const[ \t]*bool[ \t]*(shadowtex0Mipmap|shadowtexMipmap)[ \t]*=[ \t]*true[ \t]*;.*")) {
                           SMCLog.info("shadowtex0Mipmap");
                           shadowMipmapEnabled[0] = true;
                        } else if (var5.matches("[ \t]*const[ \t]*bool[ \t]*(shadowtex1Mipmap)[ \t]*=[ \t]*true[ \t]*;.*")) {
                           SMCLog.info("shadowtex1Mipmap");
                           shadowMipmapEnabled[1] = true;
                        } else if (var5.matches("[ \t]*const[ \t]*bool[ \t]*(shadowcolor0Mipmap|shadowColor0Mipmap)[ \t]*=[ \t]*true[ \t]*;.*")) {
                           SMCLog.info("shadowcolor0Mipmap");
                           shadowColorMipmapEnabled[0] = true;
                        } else if (var5.matches("[ \t]*const[ \t]*bool[ \t]*(shadowcolor1Mipmap|shadowColor1Mipmap)[ \t]*=[ \t]*true[ \t]*;.*")) {
                           SMCLog.info("shadowcolor1Mipmap");
                           shadowColorMipmapEnabled[1] = true;
                        } else if (var5.matches("[ \t]*const[ \t]*bool[ \t]*(shadowtex0Nearest|shadowtexNearest|shadow0MinMagNearest)[ \t]*=[ \t]*true[ \t]*;.*")) {
                           SMCLog.info("shadowtex0Nearest");
                           shadowFilterNearest[0] = true;
                        } else if (var5.matches("[ \t]*const[ \t]*bool[ \t]*(shadowtex1Nearest|shadow1MinMagNearest)[ \t]*=[ \t]*true[ \t]*;.*")) {
                           SMCLog.info("shadowtex1Nearest");
                           shadowFilterNearest[1] = true;
                        } else if (var5.matches("[ \t]*const[ \t]*bool[ \t]*(shadowcolor0Nearest|shadowColor0Nearest|shadowColor0MinMagNearest)[ \t]*=[ \t]*true[ \t]*;.*")) {
                           SMCLog.info("shadowcolor0Nearest");
                           shadowColorFilterNearest[0] = true;
                        } else if (var5.matches("[ \t]*const[ \t]*bool[ \t]*(shadowcolor1Nearest|shadowColor1Nearest|shadowColor1MinMagNearest)[ \t]*=[ \t]*true[ \t]*;.*")) {
                           SMCLog.info("shadowcolor1Nearest");
                           shadowColorFilterNearest[1] = true;
                        } else if (var5.matches("/\\* WETNESSHL:[0-9\\.]+ \\*/.*")) {
                           var6 = var5.split("(:| )", 4);
                           SMCLog.info(String.valueOf((new StringBuilder("Wetness halflife: ")).append(var6[2])));
                           wetnessHalfLife = Float.parseFloat(var6[2]);
                        } else if (var5.matches("[ \t]*const[ \t]*float[ \t]*wetnessHalflife[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                           var6 = var5.split("(=[ \t]*|;)");
                           SMCLog.info(String.valueOf((new StringBuilder("Wetness halflife: ")).append(var6[1])));
                           wetnessHalfLife = Float.parseFloat(var6[1]);
                        } else if (var5.matches("/\\* DRYNESSHL:[0-9\\.]+ \\*/.*")) {
                           var6 = var5.split("(:| )", 4);
                           SMCLog.info(String.valueOf((new StringBuilder("Dryness halflife: ")).append(var6[2])));
                           drynessHalfLife = Float.parseFloat(var6[2]);
                        } else if (var5.matches("[ \t]*const[ \t]*float[ \t]*drynessHalflife[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                           var6 = var5.split("(=[ \t]*|;)");
                           SMCLog.info(String.valueOf((new StringBuilder("Dryness halflife: ")).append(var6[1])));
                           drynessHalfLife = Float.parseFloat(var6[1]);
                        } else if (var5.matches("[ \t]*const[ \t]*float[ \t]*eyeBrightnessHalflife[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                           var6 = var5.split("(=[ \t]*|;)");
                           SMCLog.info(String.valueOf((new StringBuilder("Eye brightness halflife: ")).append(var6[1])));
                           eyeBrightnessHalflife = Float.parseFloat(var6[1]);
                        } else if (var5.matches("[ \t]*const[ \t]*float[ \t]*centerDepthHalflife[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                           var6 = var5.split("(=[ \t]*|;)");
                           SMCLog.info(String.valueOf((new StringBuilder("Center depth halflife: ")).append(var6[1])));
                           centerDepthSmoothHalflife = Float.parseFloat(var6[1]);
                        } else if (var5.matches("[ \t]*const[ \t]*float[ \t]*sunPathRotation[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                           var6 = var5.split("(=[ \t]*|;)");
                           SMCLog.info(String.valueOf((new StringBuilder("Sun path rotation: ")).append(var6[1])));
                           sunPathRotation = Float.parseFloat(var6[1]);
                        } else if (var5.matches("[ \t]*const[ \t]*float[ \t]*ambientOcclusionLevel[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                           var6 = var5.split("(=[ \t]*|;)");
                           SMCLog.info(String.valueOf((new StringBuilder("AO Level: ")).append(var6[1])));
                           aoLevel = Float.parseFloat(var6[1]);
                           blockAoLight = 1.0F - aoLevel;
                        } else if (var5.matches("[ \t]*const[ \t]*int[ \t]*superSamplingLevel[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                           var6 = var5.split("(=[ \t]*|;)");
                           int var15 = Integer.parseInt(var6[1]);
                           if (var15 > 1) {
                              SMCLog.info(String.valueOf((new StringBuilder("Super sampling level: ")).append(var15).append("x")));
                              superSamplingLevel = var15;
                           } else {
                              superSamplingLevel = 1;
                           }
                        } else if (var5.matches("[ \t]*const[ \t]*int[ \t]*noiseTextureResolution[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                           var6 = var5.split("(=[ \t]*|;)");
                           SMCLog.info("Noise texture enabled");
                           SMCLog.info(String.valueOf((new StringBuilder("Noise texture resolution: ")).append(var6[1])));
                           noiseTextureResolution = Integer.parseInt(var6[1]);
                           noiseTextureEnabled = true;
                        } else {
                           String var7;
                           Matcher var8;
                           if (var5.matches("[ \t]*const[ \t]*int[ \t]*\\w+Format[ \t]*=[ \t]*[RGBA81632FUI_SNORM]*[ \t]*;.*")) {
                              var8 = gbufferFormatPattern.matcher(var5);
                              var8.matches();
                              var7 = var8.group(1);
                              String var16 = var8.group(2);
                              int var10 = getBufferIndexFromString(var7);
                              int var11 = getTextureFormatFromString(var16);
                              if (var10 >= 0 && var11 != 0) {
                                 gbuffersFormat[var10] = var11;
                                 SMCLog.info("%s format: %s", var7, var16);
                              }
                           } else if (var5.matches("/\\* GAUX4FORMAT:RGBA32F \\*/.*")) {
                              SMCLog.info("gaux4 format : RGB32AF");
                              gbuffersFormat[7] = 34836;
                           } else if (var5.matches("/\\* GAUX4FORMAT:RGB32F \\*/.*")) {
                              SMCLog.info("gaux4 format : RGB32F");
                              gbuffersFormat[7] = 34837;
                           } else if (var5.matches("/\\* GAUX4FORMAT:RGB16 \\*/.*")) {
                              SMCLog.info("gaux4 format : RGB16");
                              gbuffersFormat[7] = 32852;
                           } else if (var5.matches("[ \t]*const[ \t]*bool[ \t]*\\w+MipmapEnabled[ \t]*=[ \t]*true[ \t]*;.*")) {
                              if (var0.matches(".*composite[0-9]?.fsh") || var0.matches(".*final.fsh")) {
                                 var8 = gbufferMipmapEnabledPattern.matcher(var5);
                                 var8.matches();
                                 var7 = var8.group(1);
                                 int var9 = getBufferIndexFromString(var7);
                                 if (var9 >= 0) {
                                    newCompositeMipmapSetting |= 1 << var9;
                                    SMCLog.info("%s mipmap enabled for %s", var7, var0);
                                 }
                              }
                           } else if (var5.matches("/\\* DRAWBUFFERS:[0-7N]* \\*/.*")) {
                              var6 = var5.split("(:| )", 4);
                              newDrawBufSetting = var6[2];
                           }
                        }
                     }
                  }
               }
            } catch (Exception var14) {
               SMCLog.severe(String.valueOf((new StringBuilder("Couldn't read ")).append(var0).append("!")));
               var14.printStackTrace();
               ARBShaderObjects.glDeleteObjectARB(var1);
               return 0;
            }
         }

         if (saveFinalShaders) {
            saveShader(var0, String.valueOf(var2));
         }

         ARBShaderObjects.glShaderSourceARB(var1, var2);
         ARBShaderObjects.glCompileShaderARB(var1);
         if (GL20.glGetShaderi(var1, 35713) != 1) {
            SMCLog.severe(String.valueOf((new StringBuilder("Error compiling fragment shader: ")).append(var0)));
         }

         printShaderLogInfo(var1, var0);
         return var1;
      }
   }

   static class NamelessClass341846571 {
      static final int[] $SwitchMap$shadersmod$client$EnumShaderOption = new int[EnumShaderOption.values().length];

      static {
         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.ANTIALIASING.ordinal()] = 1;
         } catch (NoSuchFieldError var17) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.NORMAL_MAP.ordinal()] = 2;
         } catch (NoSuchFieldError var16) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.SPECULAR_MAP.ordinal()] = 3;
         } catch (NoSuchFieldError var15) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.RENDER_RES_MUL.ordinal()] = 4;
         } catch (NoSuchFieldError var14) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.SHADOW_RES_MUL.ordinal()] = 5;
         } catch (NoSuchFieldError var13) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.HAND_DEPTH_MUL.ordinal()] = 6;
         } catch (NoSuchFieldError var12) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.CLOUD_SHADOW.ordinal()] = 7;
         } catch (NoSuchFieldError var11) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.OLD_LIGHTING.ordinal()] = 8;
         } catch (NoSuchFieldError var10) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.SHADER_PACK.ordinal()] = 9;
         } catch (NoSuchFieldError var9) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TWEAK_BLOCK_DAMAGE.ordinal()] = 10;
         } catch (NoSuchFieldError var8) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.SHADOW_CLIP_FRUSTRUM.ordinal()] = 11;
         } catch (NoSuchFieldError var7) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TEX_MIN_FIL_B.ordinal()] = 12;
         } catch (NoSuchFieldError var6) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TEX_MIN_FIL_N.ordinal()] = 13;
         } catch (NoSuchFieldError var5) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TEX_MIN_FIL_S.ordinal()] = 14;
         } catch (NoSuchFieldError var4) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TEX_MAG_FIL_B.ordinal()] = 15;
         } catch (NoSuchFieldError var3) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TEX_MAG_FIL_N.ordinal()] = 16;
         } catch (NoSuchFieldError var2) {
         }

         try {
            $SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TEX_MAG_FIL_S.ordinal()] = 17;
         } catch (NoSuchFieldError var1) {
         }

      }
   }
}
