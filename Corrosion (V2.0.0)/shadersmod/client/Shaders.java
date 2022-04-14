/*
 * Decompiled with CFR 0.152.
 */
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
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import optifine.Config;
import optifine.CustomColors;
import optifine.Lang;
import optifine.PropertiesOrdered;
import optifine.Reflector;
import optifine.StrUtils;
import org.apache.commons.io.IOUtils;
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
import shadersmod.client.BlockAliases;
import shadersmod.client.CustomTexture;
import shadersmod.client.EnumShaderOption;
import shadersmod.client.HFNoiseTexture;
import shadersmod.client.IShaderPack;
import shadersmod.client.PropertyDefaultFastFancyOff;
import shadersmod.client.PropertyDefaultTrueFalse;
import shadersmod.client.SMath;
import shadersmod.client.ShaderOption;
import shadersmod.client.ShaderOptionProfile;
import shadersmod.client.ShaderOptionRest;
import shadersmod.client.ShaderPackDefault;
import shadersmod.client.ShaderPackFolder;
import shadersmod.client.ShaderPackNone;
import shadersmod.client.ShaderPackParser;
import shadersmod.client.ShaderPackZip;
import shadersmod.client.ShaderProfile;
import shadersmod.client.ShaderUniformFloat4;
import shadersmod.client.ShaderUniformInt;
import shadersmod.client.ShaderUtils;
import shadersmod.client.ShadersRender;
import shadersmod.client.ShadersTex;
import shadersmod.client.SimpleShaderTexture;
import shadersmod.common.SMCLog;

public class Shaders {
    static Minecraft mc;
    static EntityRenderer entityRenderer;
    public static boolean isInitializedOnce;
    public static boolean isShaderPackInitialized;
    public static ContextCapabilities capabilities;
    public static String glVersionString;
    public static String glVendorString;
    public static String glRendererString;
    public static boolean hasGlGenMipmap;
    public static boolean hasForge;
    public static int numberResetDisplayList;
    static boolean needResetModels;
    private static int renderDisplayWidth;
    private static int renderDisplayHeight;
    public static int renderWidth;
    public static int renderHeight;
    public static boolean isRenderingWorld;
    public static boolean isRenderingSky;
    public static boolean isCompositeRendered;
    public static boolean isRenderingDfb;
    public static boolean isShadowPass;
    public static boolean isSleeping;
    private static boolean isHandRenderedMain;
    public static boolean renderItemKeepDepthMask;
    public static boolean itemToRenderMainTranslucent;
    static float[] sunPosition;
    static float[] moonPosition;
    static float[] shadowLightPosition;
    static float[] upPosition;
    static float[] shadowLightPositionVector;
    static float[] upPosModelView;
    static float[] sunPosModelView;
    static float[] moonPosModelView;
    private static float[] tempMat;
    static float clearColorR;
    static float clearColorG;
    static float clearColorB;
    static float skyColorR;
    static float skyColorG;
    static float skyColorB;
    static long worldTime;
    static long lastWorldTime;
    static long diffWorldTime;
    static float celestialAngle;
    static float sunAngle;
    static float shadowAngle;
    static int moonPhase;
    static long systemTime;
    static long lastSystemTime;
    static long diffSystemTime;
    static int frameCounter;
    static float frameTime;
    static float frameTimeCounter;
    static int systemTimeInt32;
    static float rainStrength;
    static float wetness;
    public static float wetnessHalfLife;
    public static float drynessHalfLife;
    public static float eyeBrightnessHalflife;
    static boolean usewetness;
    static int isEyeInWater;
    static int eyeBrightness;
    static float eyeBrightnessFadeX;
    static float eyeBrightnessFadeY;
    static float eyePosY;
    static float centerDepth;
    static float centerDepthSmooth;
    static float centerDepthSmoothHalflife;
    static boolean centerDepthSmoothEnabled;
    static int superSamplingLevel;
    static float nightVision;
    static float blindness;
    static boolean updateChunksErrorRecorded;
    static boolean lightmapEnabled;
    static boolean fogEnabled;
    public static int entityAttrib;
    public static int midTexCoordAttrib;
    public static int tangentAttrib;
    public static boolean useEntityAttrib;
    public static boolean useMidTexCoordAttrib;
    public static boolean useMultiTexCoord3Attrib;
    public static boolean useTangentAttrib;
    public static boolean progUseEntityAttrib;
    public static boolean progUseMidTexCoordAttrib;
    public static boolean progUseTangentAttrib;
    public static int atlasSizeX;
    public static int atlasSizeY;
    public static ShaderUniformFloat4 uniformEntityColor;
    public static ShaderUniformInt uniformEntityId;
    public static ShaderUniformInt uniformBlockEntityId;
    static double previousCameraPositionX;
    static double previousCameraPositionY;
    static double previousCameraPositionZ;
    static double cameraPositionX;
    static double cameraPositionY;
    static double cameraPositionZ;
    static int shadowPassInterval;
    public static boolean needResizeShadow;
    static int shadowMapWidth;
    static int shadowMapHeight;
    static int spShadowMapWidth;
    static int spShadowMapHeight;
    static float shadowMapFOV;
    static float shadowMapHalfPlane;
    static boolean shadowMapIsOrtho;
    static float shadowDistanceRenderMul;
    static int shadowPassCounter;
    static int preShadowPassThirdPersonView;
    public static boolean shouldSkipDefaultShadow;
    static boolean waterShadowEnabled;
    static final int MaxDrawBuffers = 8;
    static final int MaxColorBuffers = 8;
    static final int MaxDepthBuffers = 3;
    static final int MaxShadowColorBuffers = 8;
    static final int MaxShadowDepthBuffers = 2;
    static int usedColorBuffers;
    static int usedDepthBuffers;
    static int usedShadowColorBuffers;
    static int usedShadowDepthBuffers;
    static int usedColorAttachs;
    static int usedDrawBuffers;
    static int dfb;
    static int sfb;
    private static int[] gbuffersFormat;
    private static boolean[] gbuffersClear;
    public static int activeProgram;
    public static final int ProgramNone = 0;
    public static final int ProgramBasic = 1;
    public static final int ProgramTextured = 2;
    public static final int ProgramTexturedLit = 3;
    public static final int ProgramSkyBasic = 4;
    public static final int ProgramSkyTextured = 5;
    public static final int ProgramClouds = 6;
    public static final int ProgramTerrain = 7;
    public static final int ProgramTerrainSolid = 8;
    public static final int ProgramTerrainCutoutMip = 9;
    public static final int ProgramTerrainCutout = 10;
    public static final int ProgramDamagedBlock = 11;
    public static final int ProgramWater = 12;
    public static final int ProgramBlock = 13;
    public static final int ProgramBeaconBeam = 14;
    public static final int ProgramItem = 15;
    public static final int ProgramEntities = 16;
    public static final int ProgramArmorGlint = 17;
    public static final int ProgramSpiderEyes = 18;
    public static final int ProgramHand = 19;
    public static final int ProgramWeather = 20;
    public static final int ProgramComposite = 21;
    public static final int ProgramComposite1 = 22;
    public static final int ProgramComposite2 = 23;
    public static final int ProgramComposite3 = 24;
    public static final int ProgramComposite4 = 25;
    public static final int ProgramComposite5 = 26;
    public static final int ProgramComposite6 = 27;
    public static final int ProgramComposite7 = 28;
    public static final int ProgramFinal = 29;
    public static final int ProgramShadow = 30;
    public static final int ProgramShadowSolid = 31;
    public static final int ProgramShadowCutout = 32;
    public static final int ProgramCount = 33;
    public static final int MaxCompositePasses = 8;
    private static final String[] programNames;
    private static final int[] programBackups;
    static int[] programsID;
    private static int[] programsRef;
    private static int programIDCopyDepth;
    private static String[] programsDrawBufSettings;
    private static String newDrawBufSetting;
    static IntBuffer[] programsDrawBuffers;
    static IntBuffer activeDrawBuffers;
    private static String[] programsColorAtmSettings;
    private static String newColorAtmSetting;
    private static String activeColorAtmSettings;
    private static int[] programsCompositeMipmapSetting;
    private static int newCompositeMipmapSetting;
    private static int activeCompositeMipmapSetting;
    public static Properties loadedShaders;
    public static Properties shadersConfig;
    public static ITextureObject defaultTexture;
    public static boolean normalMapEnabled;
    public static boolean[] shadowHardwareFilteringEnabled;
    public static boolean[] shadowMipmapEnabled;
    public static boolean[] shadowFilterNearest;
    public static boolean[] shadowColorMipmapEnabled;
    public static boolean[] shadowColorFilterNearest;
    public static boolean configTweakBlockDamage;
    public static boolean configCloudShadow;
    public static float configHandDepthMul;
    public static float configRenderResMul;
    public static float configShadowResMul;
    public static int configTexMinFilB;
    public static int configTexMinFilN;
    public static int configTexMinFilS;
    public static int configTexMagFilB;
    public static int configTexMagFilN;
    public static int configTexMagFilS;
    public static boolean configShadowClipFrustrum;
    public static boolean configNormalMap;
    public static boolean configSpecularMap;
    public static PropertyDefaultTrueFalse configOldLighting;
    public static PropertyDefaultTrueFalse configOldHandLight;
    public static int configAntialiasingLevel;
    public static final int texMinFilRange = 3;
    public static final int texMagFilRange = 2;
    public static final String[] texMinFilDesc;
    public static final String[] texMagFilDesc;
    public static final int[] texMinFilValue;
    public static final int[] texMagFilValue;
    static IShaderPack shaderPack;
    public static boolean shaderPackLoaded;
    static File currentshader;
    static String currentshadername;
    public static String packNameNone;
    static String packNameDefault;
    static String shaderpacksdirname;
    static String optionsfilename;
    static File shadersdir;
    static File shaderpacksdir;
    static File configFile;
    static ShaderOption[] shaderPackOptions;
    static ShaderProfile[] shaderPackProfiles;
    static Map<String, ShaderOption[]> shaderPackGuiScreens;
    public static PropertyDefaultFastFancyOff shaderPackClouds;
    public static PropertyDefaultTrueFalse shaderPackOldLighting;
    public static PropertyDefaultTrueFalse shaderPackOldHandLight;
    public static PropertyDefaultTrueFalse shaderPackDynamicHandLight;
    public static PropertyDefaultTrueFalse shaderPackShadowTranslucent;
    public static PropertyDefaultTrueFalse shaderPackUnderwaterOverlay;
    public static PropertyDefaultTrueFalse shaderPackSun;
    public static PropertyDefaultTrueFalse shaderPackMoon;
    public static PropertyDefaultTrueFalse shaderPackVignette;
    public static PropertyDefaultTrueFalse shaderPackBackFaceSolid;
    public static PropertyDefaultTrueFalse shaderPackBackFaceCutout;
    public static PropertyDefaultTrueFalse shaderPackBackFaceCutoutMipped;
    public static PropertyDefaultTrueFalse shaderPackBackFaceTranslucent;
    private static Map<String, String> shaderPackResources;
    private static World currentWorld;
    private static List<Integer> shaderPackDimensions;
    private static CustomTexture[] customTexturesGbuffers;
    private static CustomTexture[] customTexturesComposite;
    private static final int STAGE_GBUFFERS = 0;
    private static final int STAGE_COMPOSITE = 1;
    private static final String[] STAGE_NAMES;
    public static final boolean enableShadersOption = true;
    private static final boolean enableShadersDebug = true;
    private static final boolean saveFinalShaders;
    public static float blockLightLevel05;
    public static float blockLightLevel06;
    public static float blockLightLevel08;
    public static float aoLevel;
    public static float sunPathRotation;
    public static float shadowAngleInterval;
    public static int fogMode;
    public static float fogColorR;
    public static float fogColorG;
    public static float fogColorB;
    public static float shadowIntervalSize;
    public static int terrainIconSize;
    public static int[] terrainTextureSize;
    private static HFNoiseTexture noiseTexture;
    private static boolean noiseTextureEnabled;
    private static int noiseTextureResolution;
    static final int[] dfbColorTexturesA;
    static final int[] colorTexturesToggle;
    static final int[] colorTextureTextureImageUnit;
    static final boolean[][] programsToggleColorTextures;
    private static final int bigBufferSize = 2196;
    private static final ByteBuffer bigBuffer;
    static final float[] faProjection;
    static final float[] faProjectionInverse;
    static final float[] faModelView;
    static final float[] faModelViewInverse;
    static final float[] faShadowProjection;
    static final float[] faShadowProjectionInverse;
    static final float[] faShadowModelView;
    static final float[] faShadowModelViewInverse;
    static final FloatBuffer projection;
    static final FloatBuffer projectionInverse;
    static final FloatBuffer modelView;
    static final FloatBuffer modelViewInverse;
    static final FloatBuffer shadowProjection;
    static final FloatBuffer shadowProjectionInverse;
    static final FloatBuffer shadowModelView;
    static final FloatBuffer shadowModelViewInverse;
    static final FloatBuffer previousProjection;
    static final FloatBuffer previousModelView;
    static final FloatBuffer tempMatrixDirectBuffer;
    static final FloatBuffer tempDirectFloatBuffer;
    static final IntBuffer dfbColorTextures;
    static final IntBuffer dfbDepthTextures;
    static final IntBuffer sfbColorTextures;
    static final IntBuffer sfbDepthTextures;
    static final IntBuffer dfbDrawBuffers;
    static final IntBuffer sfbDrawBuffers;
    static final IntBuffer drawBuffersNone;
    static final IntBuffer drawBuffersAll;
    static final IntBuffer drawBuffersClear0;
    static final IntBuffer drawBuffersClear1;
    static final IntBuffer drawBuffersClearColor;
    static final IntBuffer drawBuffersColorAtt0;
    static final IntBuffer[] drawBuffersBuffer;
    static Map<Block, Integer> mapBlockToEntityData;
    private static final Pattern gbufferFormatPattern;
    private static final Pattern gbufferClearPattern;
    private static final Pattern gbufferMipmapEnabledPattern;
    private static final String[] formatNames;
    private static final int[] formatIds;
    private static final Pattern patternLoadEntityDataMap;
    public static int[] entityData;
    public static int entityDataIndex;

    private static ByteBuffer nextByteBuffer(int size) {
        ByteBuffer bytebuffer = bigBuffer;
        int i2 = bytebuffer.limit();
        bytebuffer.position(i2).limit(i2 + size);
        return bytebuffer.slice();
    }

    private static IntBuffer nextIntBuffer(int size) {
        ByteBuffer bytebuffer = bigBuffer;
        int i2 = bytebuffer.limit();
        bytebuffer.position(i2).limit(i2 + size * 4);
        return bytebuffer.asIntBuffer();
    }

    private static FloatBuffer nextFloatBuffer(int size) {
        ByteBuffer bytebuffer = bigBuffer;
        int i2 = bytebuffer.limit();
        bytebuffer.position(i2).limit(i2 + size * 4);
        return bytebuffer.asFloatBuffer();
    }

    private static IntBuffer[] nextIntBufferArray(int count, int size) {
        IntBuffer[] aintbuffer = new IntBuffer[count];
        for (int i2 = 0; i2 < count; ++i2) {
            aintbuffer[i2] = Shaders.nextIntBuffer(size);
        }
        return aintbuffer;
    }

    public static void loadConfig() {
        SMCLog.info("Load ShadersMod configuration.");
        try {
            if (!shaderpacksdir.exists()) {
                shaderpacksdir.mkdir();
            }
        }
        catch (Exception var8) {
            SMCLog.severe("Failed to open the shaderpacks directory: " + shaderpacksdir);
        }
        shadersConfig = new PropertiesOrdered();
        shadersConfig.setProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), "");
        if (configFile.exists()) {
            try {
                FileReader filereader = new FileReader(configFile);
                shadersConfig.load(filereader);
                filereader.close();
            }
            catch (Exception filereader) {
                // empty catch block
            }
        }
        if (!configFile.exists()) {
            try {
                Shaders.storeConfig();
            }
            catch (Exception filereader) {
                // empty catch block
            }
        }
        EnumShaderOption[] aenumshaderoption = EnumShaderOption.values();
        for (int i2 = 0; i2 < aenumshaderoption.length; ++i2) {
            EnumShaderOption enumshaderoption = aenumshaderoption[i2];
            String s2 = enumshaderoption.getPropertyKey();
            String s1 = enumshaderoption.getValueDefault();
            String s22 = shadersConfig.getProperty(s2, s1);
            Shaders.setEnumShaderOption(enumshaderoption, s22);
        }
        Shaders.loadShaderPack();
    }

    private static void setEnumShaderOption(EnumShaderOption eso, String str) {
        if (str == null) {
            str = eso.getValueDefault();
        }
        switch (eso) {
            case ANTIALIASING: {
                configAntialiasingLevel = Config.parseInt(str, 0);
                break;
            }
            case NORMAL_MAP: {
                configNormalMap = Config.parseBoolean(str, true);
                break;
            }
            case SPECULAR_MAP: {
                configSpecularMap = Config.parseBoolean(str, true);
                break;
            }
            case RENDER_RES_MUL: {
                configRenderResMul = Config.parseFloat(str, 1.0f);
                break;
            }
            case SHADOW_RES_MUL: {
                configShadowResMul = Config.parseFloat(str, 1.0f);
                break;
            }
            case HAND_DEPTH_MUL: {
                configHandDepthMul = Config.parseFloat(str, 0.125f);
                break;
            }
            case CLOUD_SHADOW: {
                configCloudShadow = Config.parseBoolean(str, true);
                break;
            }
            case OLD_HAND_LIGHT: {
                configOldHandLight.setPropertyValue(str);
                break;
            }
            case OLD_LIGHTING: {
                configOldLighting.setPropertyValue(str);
                break;
            }
            case SHADER_PACK: {
                currentshadername = str;
                break;
            }
            case TWEAK_BLOCK_DAMAGE: {
                configTweakBlockDamage = Config.parseBoolean(str, true);
                break;
            }
            case SHADOW_CLIP_FRUSTRUM: {
                configShadowClipFrustrum = Config.parseBoolean(str, true);
                break;
            }
            case TEX_MIN_FIL_B: {
                configTexMinFilB = Config.parseInt(str, 0);
                break;
            }
            case TEX_MIN_FIL_N: {
                configTexMinFilN = Config.parseInt(str, 0);
                break;
            }
            case TEX_MIN_FIL_S: {
                configTexMinFilS = Config.parseInt(str, 0);
                break;
            }
            case TEX_MAG_FIL_B: {
                configTexMagFilB = Config.parseInt(str, 0);
                break;
            }
            case TEX_MAG_FIL_N: {
                configTexMagFilB = Config.parseInt(str, 0);
                break;
            }
            case TEX_MAG_FIL_S: {
                configTexMagFilB = Config.parseInt(str, 0);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown option: " + (Object)((Object)eso));
            }
        }
    }

    public static void storeConfig() {
        SMCLog.info("Save ShadersMod configuration.");
        if (shadersConfig == null) {
            shadersConfig = new PropertiesOrdered();
        }
        EnumShaderOption[] aenumshaderoption = EnumShaderOption.values();
        for (int i2 = 0; i2 < aenumshaderoption.length; ++i2) {
            EnumShaderOption enumshaderoption = aenumshaderoption[i2];
            String s2 = enumshaderoption.getPropertyKey();
            String s1 = Shaders.getEnumShaderOption(enumshaderoption);
            shadersConfig.setProperty(s2, s1);
        }
        try {
            FileWriter filewriter = new FileWriter(configFile);
            shadersConfig.store(filewriter, (String)null);
            filewriter.close();
        }
        catch (Exception exception) {
            SMCLog.severe("Error saving configuration: " + exception.getClass().getName() + ": " + exception.getMessage());
        }
    }

    public static String getEnumShaderOption(EnumShaderOption eso) {
        switch (eso) {
            case ANTIALIASING: {
                return Integer.toString(configAntialiasingLevel);
            }
            case NORMAL_MAP: {
                return Boolean.toString(configNormalMap);
            }
            case SPECULAR_MAP: {
                return Boolean.toString(configSpecularMap);
            }
            case RENDER_RES_MUL: {
                return Float.toString(configRenderResMul);
            }
            case SHADOW_RES_MUL: {
                return Float.toString(configShadowResMul);
            }
            case HAND_DEPTH_MUL: {
                return Float.toString(configHandDepthMul);
            }
            case CLOUD_SHADOW: {
                return Boolean.toString(configCloudShadow);
            }
            case OLD_HAND_LIGHT: {
                return configOldHandLight.getPropertyValue();
            }
            case OLD_LIGHTING: {
                return configOldLighting.getPropertyValue();
            }
            case SHADER_PACK: {
                return currentshadername;
            }
            case TWEAK_BLOCK_DAMAGE: {
                return Boolean.toString(configTweakBlockDamage);
            }
            case SHADOW_CLIP_FRUSTRUM: {
                return Boolean.toString(configShadowClipFrustrum);
            }
            case TEX_MIN_FIL_B: {
                return Integer.toString(configTexMinFilB);
            }
            case TEX_MIN_FIL_N: {
                return Integer.toString(configTexMinFilN);
            }
            case TEX_MIN_FIL_S: {
                return Integer.toString(configTexMinFilS);
            }
            case TEX_MAG_FIL_B: {
                return Integer.toString(configTexMagFilB);
            }
            case TEX_MAG_FIL_N: {
                return Integer.toString(configTexMagFilB);
            }
            case TEX_MAG_FIL_S: {
                return Integer.toString(configTexMagFilB);
            }
        }
        throw new IllegalArgumentException("Unknown option: " + (Object)((Object)eso));
    }

    public static void setShaderPack(String par1name) {
        currentshadername = par1name;
        shadersConfig.setProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), par1name);
        Shaders.loadShaderPack();
    }

    public static void loadShaderPack() {
        boolean flag3;
        String s2;
        boolean flag = shaderPackLoaded;
        boolean flag1 = Shaders.isOldLighting();
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
            shaderPackOldHandLight.resetValue();
            shaderPackDynamicHandLight.resetValue();
            shaderPackOldLighting.resetValue();
            Shaders.resetCustomTextures();
        }
        boolean flag2 = false;
        if (Config.isAntialiasing()) {
            SMCLog.info("Shaders can not be loaded, Antialiasing is enabled: " + Config.getAntialiasingLevel() + "x");
            flag2 = true;
        }
        if (Config.isAnisotropicFiltering()) {
            SMCLog.info("Shaders can not be loaded, Anisotropic Filtering is enabled: " + Config.getAnisotropicFilterLevel() + "x");
            flag2 = true;
        }
        if (Config.isFastRender()) {
            SMCLog.info("Shaders can not be loaded, Fast Render is enabled.");
            flag2 = true;
        }
        if (!((s2 = shadersConfig.getProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), packNameDefault)).isEmpty() || s2.equals(packNameNone) || flag2)) {
            if (s2.equals(packNameDefault)) {
                shaderPack = new ShaderPackDefault();
                shaderPackLoaded = true;
            } else {
                try {
                    File file1 = new File(shaderpacksdir, s2);
                    if (file1.isDirectory()) {
                        shaderPack = new ShaderPackFolder(s2, file1);
                        shaderPackLoaded = true;
                    } else if (file1.isFile() && s2.toLowerCase().endsWith(".zip")) {
                        shaderPack = new ShaderPackZip(s2, file1);
                        shaderPackLoaded = true;
                    }
                }
                catch (Exception file1) {
                    // empty catch block
                }
            }
        }
        if (shaderPack != null) {
            SMCLog.info("Loaded shaderpack: " + Shaders.getShaderPackName());
        } else {
            SMCLog.info("No shaderpack loaded.");
            shaderPack = new ShaderPackNone();
        }
        Shaders.loadShaderPackResources();
        Shaders.loadShaderPackDimensions();
        shaderPackOptions = Shaders.loadShaderPackOptions();
        Shaders.loadShaderPackProperties();
        boolean flag4 = shaderPackLoaded != flag;
        boolean bl2 = flag3 = Shaders.isOldLighting() != flag1;
        if (flag4 || flag3) {
            DefaultVertexFormats.updateVertexFormats();
            if (Reflector.LightUtil.exists()) {
                Reflector.LightUtil_itemConsumer.setValue(null);
                Reflector.LightUtil_tessellator.setValue(null);
            }
            Shaders.updateBlockLightLevel();
            mc.scheduleResourcesRefresh();
        }
    }

    private static void loadShaderPackDimensions() {
        shaderPackDimensions.clear();
        for (int i2 = -128; i2 <= 128; ++i2) {
            String s2 = "/shaders/world" + i2;
            if (!shaderPack.hasDirectory(s2)) continue;
            shaderPackDimensions.add(i2);
        }
        if (shaderPackDimensions.size() > 0) {
            Integer[] ainteger = shaderPackDimensions.toArray(new Integer[shaderPackDimensions.size()]);
            Config.dbg("[Shaders] Worlds: " + Config.arrayToString((Object[])ainteger));
        }
    }

    private static void loadShaderPackProperties() {
        shaderPackClouds.resetValue();
        shaderPackOldHandLight.resetValue();
        shaderPackDynamicHandLight.resetValue();
        shaderPackOldLighting.resetValue();
        shaderPackShadowTranslucent.resetValue();
        shaderPackUnderwaterOverlay.resetValue();
        shaderPackSun.resetValue();
        shaderPackMoon.resetValue();
        shaderPackVignette.resetValue();
        shaderPackBackFaceSolid.resetValue();
        shaderPackBackFaceCutout.resetValue();
        shaderPackBackFaceCutoutMipped.resetValue();
        shaderPackBackFaceTranslucent.resetValue();
        BlockAliases.reset();
        if (shaderPack != null) {
            BlockAliases.update(shaderPack);
            String s2 = "/shaders/shaders.properties";
            try {
                InputStream inputstream = shaderPack.getResourceAsStream(s2);
                if (inputstream == null) {
                    return;
                }
                PropertiesOrdered properties = new PropertiesOrdered();
                properties.load(inputstream);
                inputstream.close();
                shaderPackClouds.loadFrom(properties);
                shaderPackOldHandLight.loadFrom(properties);
                shaderPackDynamicHandLight.loadFrom(properties);
                shaderPackOldLighting.loadFrom(properties);
                shaderPackShadowTranslucent.loadFrom(properties);
                shaderPackUnderwaterOverlay.loadFrom(properties);
                shaderPackSun.loadFrom(properties);
                shaderPackVignette.loadFrom(properties);
                shaderPackMoon.loadFrom(properties);
                shaderPackBackFaceSolid.loadFrom(properties);
                shaderPackBackFaceCutout.loadFrom(properties);
                shaderPackBackFaceCutoutMipped.loadFrom(properties);
                shaderPackBackFaceTranslucent.loadFrom(properties);
                shaderPackProfiles = ShaderPackParser.parseProfiles(properties, shaderPackOptions);
                shaderPackGuiScreens = ShaderPackParser.parseGuiScreens(properties, shaderPackProfiles, shaderPackOptions);
                customTexturesGbuffers = Shaders.loadCustomTextures(properties, 0);
                customTexturesComposite = Shaders.loadCustomTextures(properties, 1);
            }
            catch (IOException var3) {
                Config.warn("[Shaders] Error reading: " + s2);
            }
        }
    }

    private static CustomTexture[] loadCustomTextures(Properties props, int stage) {
        String s2 = "texture." + STAGE_NAMES[stage] + ".";
        Set set = props.keySet();
        ArrayList<CustomTexture> list = new ArrayList<CustomTexture>();
        for (Object s10 : set) {
            String s1 = (String)s10;
            if (!s1.startsWith(s2)) continue;
            String s22 = s1.substring(s2.length());
            String s3 = props.getProperty(s1).trim();
            int i2 = Shaders.getTextureIndex(stage, s22);
            if (i2 < 0) {
                SMCLog.warning("Invalid texture name: " + s1);
                continue;
            }
            try {
                String s4 = "shaders/" + StrUtils.removePrefix(s3, "/");
                InputStream inputstream = shaderPack.getResourceAsStream(s4);
                if (inputstream == null) {
                    SMCLog.warning("Texture not found: " + s3);
                    continue;
                }
                IOUtils.closeQuietly(inputstream);
                SimpleShaderTexture simpleshadertexture = new SimpleShaderTexture(s4);
                simpleshadertexture.loadTexture(mc.getResourceManager());
                CustomTexture customtexture = new CustomTexture(i2, s4, simpleshadertexture);
                list.add(customtexture);
            }
            catch (IOException ioexception) {
                SMCLog.warning("Error loading texture: " + s3);
                SMCLog.warning("" + ioexception.getClass().getName() + ": " + ioexception.getMessage());
            }
        }
        if (list.size() <= 0) {
            return null;
        }
        CustomTexture[] acustomtexture = list.toArray(new CustomTexture[list.size()]);
        return acustomtexture;
    }

    private static int getTextureIndex(int stage, String name) {
        if (stage == 0) {
            if (name.equals("texture")) {
                return 0;
            }
            if (name.equals("lightmap")) {
                return 1;
            }
            if (name.equals("normals")) {
                return 2;
            }
            if (name.equals("specular")) {
                return 3;
            }
            if (name.equals("shadowtex0") || name.equals("watershadow")) {
                return 4;
            }
            if (name.equals("shadow")) {
                return waterShadowEnabled ? 5 : 4;
            }
            if (name.equals("shadowtex1")) {
                return 5;
            }
            if (name.equals("depthtex0")) {
                return 6;
            }
            if (name.equals("gaux1")) {
                return 7;
            }
            if (name.equals("gaux2")) {
                return 8;
            }
            if (name.equals("gaux3")) {
                return 9;
            }
            if (name.equals("gaux4")) {
                return 10;
            }
            if (name.equals("depthtex1")) {
                return 12;
            }
            if (name.equals("shadowcolor0") || name.equals("shadowcolor")) {
                return 13;
            }
            if (name.equals("shadowcolor1")) {
                return 14;
            }
            if (name.equals("noisetex")) {
                return 15;
            }
        }
        if (stage == 1) {
            if (name.equals("colortex0") || name.equals("colortex0")) {
                return 0;
            }
            if (name.equals("colortex1") || name.equals("gdepth")) {
                return 1;
            }
            if (name.equals("colortex2") || name.equals("gnormal")) {
                return 2;
            }
            if (name.equals("colortex3") || name.equals("composite")) {
                return 3;
            }
            if (name.equals("shadowtex0") || name.equals("watershadow")) {
                return 4;
            }
            if (name.equals("shadow")) {
                return waterShadowEnabled ? 5 : 4;
            }
            if (name.equals("shadowtex1")) {
                return 5;
            }
            if (name.equals("depthtex0") || name.equals("gdepthtex")) {
                return 6;
            }
            if (name.equals("colortex4") || name.equals("gaux1")) {
                return 7;
            }
            if (name.equals("colortex5") || name.equals("gaux2")) {
                return 8;
            }
            if (name.equals("colortex6") || name.equals("gaux3")) {
                return 9;
            }
            if (name.equals("colortex7") || name.equals("gaux4")) {
                return 10;
            }
            if (name.equals("depthtex1")) {
                return 11;
            }
            if (name.equals("depthtex2")) {
                return 12;
            }
            if (name.equals("shadowcolor0") || name.equals("shadowcolor")) {
                return 13;
            }
            if (name.equals("shadowcolor1")) {
                return 14;
            }
            if (name.equals("noisetex")) {
                return 15;
            }
        }
        return -1;
    }

    private static void bindCustomTextures(CustomTexture[] cts) {
        if (cts != null) {
            for (int i2 = 0; i2 < cts.length; ++i2) {
                CustomTexture customtexture = cts[i2];
                GlStateManager.setActiveTexture(33984 + customtexture.getTextureUnit());
                ITextureObject itextureobject = customtexture.getTexture();
                GlStateManager.bindTexture(itextureobject.getGlTextureId());
            }
        }
    }

    private static void resetCustomTextures() {
        Shaders.deleteCustomTextures(customTexturesGbuffers);
        Shaders.deleteCustomTextures(customTexturesComposite);
        customTexturesGbuffers = null;
        customTexturesComposite = null;
    }

    private static void deleteCustomTextures(CustomTexture[] cts) {
        if (cts != null) {
            for (int i2 = 0; i2 < cts.length; ++i2) {
                CustomTexture customtexture = cts[i2];
                ITextureObject itextureobject = customtexture.getTexture();
                TextureUtil.deleteTexture(itextureobject.getGlTextureId());
            }
        }
    }

    public static ShaderOption[] getShaderPackOptions(String screenName) {
        Object[] ashaderoption = (ShaderOption[])shaderPackOptions.clone();
        if (shaderPackGuiScreens == null) {
            if (shaderPackProfiles != null) {
                ShaderOptionProfile shaderoptionprofile = new ShaderOptionProfile(shaderPackProfiles, (ShaderOption[])ashaderoption);
                ashaderoption = (ShaderOption[])Config.addObjectToArray(ashaderoption, shaderoptionprofile, 0);
            }
            ashaderoption = Shaders.getVisibleOptions((ShaderOption[])ashaderoption);
            return ashaderoption;
        }
        String s2 = screenName != null ? "screen." + screenName : "screen";
        ShaderOption[] ashaderoption1 = shaderPackGuiScreens.get(s2);
        if (ashaderoption1 == null) {
            return new ShaderOption[0];
        }
        ArrayList<ShaderOption> list = new ArrayList<ShaderOption>();
        for (int i2 = 0; i2 < ashaderoption1.length; ++i2) {
            ShaderOption shaderoption = ashaderoption1[i2];
            if (shaderoption == null) {
                list.add(null);
                continue;
            }
            if (shaderoption instanceof ShaderOptionRest) {
                ShaderOption[] ashaderoption2 = Shaders.getShaderOptionsRest(shaderPackGuiScreens, (ShaderOption[])ashaderoption);
                list.addAll(Arrays.asList(ashaderoption2));
                continue;
            }
            list.add(shaderoption);
        }
        ShaderOption[] ashaderoption3 = list.toArray(new ShaderOption[list.size()]);
        return ashaderoption3;
    }

    private static ShaderOption[] getShaderOptionsRest(Map<String, ShaderOption[]> mapScreens, ShaderOption[] ops) {
        HashSet<String> set = new HashSet<String>();
        for (String s2 : mapScreens.keySet()) {
            ShaderOption[] ashaderoption = mapScreens.get(s2);
            for (int i2 = 0; i2 < ashaderoption.length; ++i2) {
                ShaderOption shaderoption = ashaderoption[i2];
                if (shaderoption == null) continue;
                set.add(shaderoption.getName());
            }
        }
        ArrayList<ShaderOption> list = new ArrayList<ShaderOption>();
        for (int j2 = 0; j2 < ops.length; ++j2) {
            String s1;
            ShaderOption shaderoption1 = ops[j2];
            if (!shaderoption1.isVisible() || set.contains(s1 = shaderoption1.getName())) continue;
            list.add(shaderoption1);
        }
        ShaderOption[] ashaderoption1 = list.toArray(new ShaderOption[list.size()]);
        return ashaderoption1;
    }

    public static ShaderOption getShaderOption(String name) {
        return ShaderUtils.getShaderOption(name, shaderPackOptions);
    }

    public static ShaderOption[] getShaderPackOptions() {
        return shaderPackOptions;
    }

    private static ShaderOption[] getVisibleOptions(ShaderOption[] ops) {
        ArrayList<ShaderOption> list = new ArrayList<ShaderOption>();
        for (int i2 = 0; i2 < ops.length; ++i2) {
            ShaderOption shaderoption = ops[i2];
            if (!shaderoption.isVisible()) continue;
            list.add(shaderoption);
        }
        ShaderOption[] ashaderoption = list.toArray(new ShaderOption[list.size()]);
        return ashaderoption;
    }

    public static void saveShaderPackOptions() {
        Shaders.saveShaderPackOptions(shaderPackOptions, shaderPack);
    }

    private static void saveShaderPackOptions(ShaderOption[] sos, IShaderPack sp2) {
        Properties properties = new Properties();
        if (shaderPackOptions != null) {
            for (int i2 = 0; i2 < sos.length; ++i2) {
                ShaderOption shaderoption = sos[i2];
                if (!shaderoption.isChanged() || !shaderoption.isEnabled()) continue;
                properties.setProperty(shaderoption.getName(), shaderoption.getValue());
            }
        }
        try {
            Shaders.saveOptionProperties(sp2, properties);
        }
        catch (IOException ioexception) {
            Config.warn("[Shaders] Error saving configuration for " + shaderPack.getName());
            ioexception.printStackTrace();
        }
    }

    private static void saveOptionProperties(IShaderPack sp2, Properties props) throws IOException {
        String s2 = shaderpacksdirname + "/" + sp2.getName() + ".txt";
        File file1 = new File(Minecraft.getMinecraft().mcDataDir, s2);
        if (props.isEmpty()) {
            file1.delete();
        } else {
            FileOutputStream fileoutputstream = new FileOutputStream(file1);
            props.store(fileoutputstream, (String)null);
            fileoutputstream.flush();
            fileoutputstream.close();
        }
    }

    private static ShaderOption[] loadShaderPackOptions() {
        try {
            ShaderOption[] ashaderoption = ShaderPackParser.parseShaderPackOptions(shaderPack, programNames, shaderPackDimensions);
            Properties properties = Shaders.loadOptionProperties(shaderPack);
            for (int i2 = 0; i2 < ashaderoption.length; ++i2) {
                ShaderOption shaderoption = ashaderoption[i2];
                String s2 = properties.getProperty(shaderoption.getName());
                if (s2 == null) continue;
                shaderoption.resetValue();
                if (shaderoption.setValue(s2)) continue;
                Config.warn("[Shaders] Invalid value, option: " + shaderoption.getName() + ", value: " + s2);
            }
            return ashaderoption;
        }
        catch (IOException ioexception) {
            Config.warn("[Shaders] Error reading configuration for " + shaderPack.getName());
            ioexception.printStackTrace();
            return null;
        }
    }

    private static Properties loadOptionProperties(IShaderPack sp2) throws IOException {
        Properties properties = new Properties();
        String s2 = shaderpacksdirname + "/" + sp2.getName() + ".txt";
        File file1 = new File(Minecraft.getMinecraft().mcDataDir, s2);
        if (file1.exists() && file1.isFile() && file1.canRead()) {
            FileInputStream fileinputstream = new FileInputStream(file1);
            properties.load(fileinputstream);
            fileinputstream.close();
            return properties;
        }
        return properties;
    }

    public static ShaderOption[] getChangedOptions(ShaderOption[] ops) {
        ArrayList<ShaderOption> list = new ArrayList<ShaderOption>();
        for (int i2 = 0; i2 < ops.length; ++i2) {
            ShaderOption shaderoption = ops[i2];
            if (!shaderoption.isEnabled() || !shaderoption.isChanged()) continue;
            list.add(shaderoption);
        }
        ShaderOption[] ashaderoption = list.toArray(new ShaderOption[list.size()]);
        return ashaderoption;
    }

    private static String applyOptions(String line, ShaderOption[] ops) {
        if (ops != null && ops.length > 0) {
            for (int i2 = 0; i2 < ops.length; ++i2) {
                ShaderOption shaderoption = ops[i2];
                String s2 = shaderoption.getName();
                if (!shaderoption.matchesLine(line)) continue;
                line = shaderoption.getSourceLine();
                break;
            }
            return line;
        }
        return line;
    }

    static ArrayList listOfShaders() {
        ArrayList<String> arraylist = new ArrayList<String>();
        arraylist.add(packNameNone);
        arraylist.add(packNameDefault);
        try {
            if (!shaderpacksdir.exists()) {
                shaderpacksdir.mkdir();
            }
            File[] afile = shaderpacksdir.listFiles();
            for (int i2 = 0; i2 < afile.length; ++i2) {
                File file1 = afile[i2];
                String s2 = file1.getName();
                if (file1.isDirectory()) {
                    File file2 = new File(file1, "shaders");
                    if (!file2.exists() || !file2.isDirectory()) continue;
                    arraylist.add(s2);
                    continue;
                }
                if (!file1.isFile() || !s2.toLowerCase().endsWith(".zip")) continue;
                arraylist.add(s2);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return arraylist;
    }

    static String versiontostring(int vv2) {
        String s2 = Integer.toString(vv2);
        return Integer.toString(Integer.parseInt(s2.substring(1, 3))) + "." + Integer.toString(Integer.parseInt(s2.substring(3, 5))) + "." + Integer.toString(Integer.parseInt(s2.substring(5)));
    }

    static void checkOptifine() {
    }

    public static int checkFramebufferStatus(String location) {
        int i2 = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
        if (i2 != 36053) {
            System.err.format("FramebufferStatus 0x%04X at %s\n", i2, location);
        }
        return i2;
    }

    public static int checkGLError(String location) {
        boolean flag;
        int i2 = GL11.glGetError();
        if (i2 != 0 && !(flag = false)) {
            if (i2 == 1286) {
                int j2 = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
                System.err.format("GL error 0x%04X: %s (Fb status 0x%04X) at %s\n", i2, GLU.gluErrorString(i2), j2, location);
            } else {
                System.err.format("GL error 0x%04X: %s at %s\n", i2, GLU.gluErrorString(i2), location);
            }
        }
        return i2;
    }

    public static int checkGLError(String location, String info) {
        int i2 = GL11.glGetError();
        if (i2 != 0) {
            System.err.format("GL error 0x%04x: %s at %s %s\n", i2, GLU.gluErrorString(i2), location, info);
        }
        return i2;
    }

    public static int checkGLError(String location, String info1, String info2) {
        int i2 = GL11.glGetError();
        if (i2 != 0) {
            System.err.format("GL error 0x%04x: %s at %s %s %s\n", i2, GLU.gluErrorString(i2), location, info1, info2);
        }
        return i2;
    }

    private static void printChat(String str) {
        Shaders.mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(str));
    }

    private static void printChatAndLogError(String str) {
        SMCLog.severe(str);
        Shaders.mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(str));
    }

    public static void printIntBuffer(String title, IntBuffer buf) {
        StringBuilder stringbuilder = new StringBuilder(128);
        stringbuilder.append(title).append(" [pos ").append(buf.position()).append(" lim ").append(buf.limit()).append(" cap ").append(buf.capacity()).append(" :");
        int i2 = buf.limit();
        for (int j2 = 0; j2 < i2; ++j2) {
            stringbuilder.append(" ").append(buf.get(j2));
        }
        stringbuilder.append("]");
        SMCLog.info(stringbuilder.toString());
    }

    public static void startup(Minecraft mc2) {
        Shaders.checkShadersModInstalled();
        mc = mc2;
        mc2 = Minecraft.getMinecraft();
        capabilities = GLContext.getCapabilities();
        glVersionString = GL11.glGetString(7938);
        glVendorString = GL11.glGetString(7936);
        glRendererString = GL11.glGetString(7937);
        SMCLog.info("ShadersMod version: 2.4.12");
        SMCLog.info("OpenGL Version: " + glVersionString);
        SMCLog.info("Vendor:  " + glVendorString);
        SMCLog.info("Renderer: " + glRendererString);
        SMCLog.info("Capabilities: " + (Shaders.capabilities.OpenGL20 ? " 2.0 " : " - ") + (Shaders.capabilities.OpenGL21 ? " 2.1 " : " - ") + (Shaders.capabilities.OpenGL30 ? " 3.0 " : " - ") + (Shaders.capabilities.OpenGL32 ? " 3.2 " : " - ") + (Shaders.capabilities.OpenGL40 ? " 4.0 " : " - "));
        SMCLog.info("GL_MAX_DRAW_BUFFERS: " + GL11.glGetInteger(34852));
        SMCLog.info("GL_MAX_COLOR_ATTACHMENTS_EXT: " + GL11.glGetInteger(36063));
        SMCLog.info("GL_MAX_TEXTURE_IMAGE_UNITS: " + GL11.glGetInteger(34930));
        hasGlGenMipmap = Shaders.capabilities.OpenGL30;
        Shaders.loadConfig();
    }

    private static String toStringYN(boolean b2) {
        return b2 ? "Y" : "N";
    }

    public static void updateBlockLightLevel() {
        if (Shaders.isOldLighting()) {
            blockLightLevel05 = 0.5f;
            blockLightLevel06 = 0.6f;
            blockLightLevel08 = 0.8f;
        } else {
            blockLightLevel05 = 1.0f;
            blockLightLevel06 = 1.0f;
            blockLightLevel08 = 1.0f;
        }
    }

    public static boolean isOldHandLight() {
        return !configOldHandLight.isDefault() ? configOldHandLight.isTrue() : (!shaderPackOldHandLight.isDefault() ? shaderPackOldHandLight.isTrue() : true);
    }

    public static boolean isDynamicHandLight() {
        return !shaderPackDynamicHandLight.isDefault() ? shaderPackDynamicHandLight.isTrue() : true;
    }

    public static boolean isOldLighting() {
        return !configOldLighting.isDefault() ? configOldLighting.isTrue() : (!shaderPackOldLighting.isDefault() ? shaderPackOldLighting.isTrue() : true);
    }

    public static boolean isRenderShadowTranslucent() {
        return !shaderPackShadowTranslucent.isFalse();
    }

    public static boolean isUnderwaterOverlay() {
        return !shaderPackUnderwaterOverlay.isFalse();
    }

    public static boolean isSun() {
        return !shaderPackSun.isFalse();
    }

    public static boolean isMoon() {
        return !shaderPackMoon.isFalse();
    }

    public static boolean isVignette() {
        return !shaderPackVignette.isFalse();
    }

    public static boolean isRenderBackFace(EnumWorldBlockLayer blockLayerIn) {
        switch (blockLayerIn) {
            case SOLID: {
                return shaderPackBackFaceSolid.isTrue();
            }
            case CUTOUT: {
                return shaderPackBackFaceCutout.isTrue();
            }
            case CUTOUT_MIPPED: {
                return shaderPackBackFaceCutoutMipped.isTrue();
            }
            case TRANSLUCENT: {
                return shaderPackBackFaceTranslucent.isTrue();
            }
        }
        return false;
    }

    public static void init() {
        boolean flag;
        if (!isInitializedOnce) {
            isInitializedOnce = true;
            flag = true;
        } else {
            flag = false;
        }
        if (!isShaderPackInitialized) {
            int i2;
            Shaders.checkGLError("Shaders.init pre");
            if (Shaders.getShaderPackName() != null) {
                // empty if block
            }
            if (!Shaders.capabilities.OpenGL20) {
                Shaders.printChatAndLogError("No OpenGL 2.0");
            }
            if (!Shaders.capabilities.GL_EXT_framebuffer_object) {
                Shaders.printChatAndLogError("No EXT_framebuffer_object");
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
            Arrays.fill(gbuffersClear, true);
            Arrays.fill(shadowHardwareFilteringEnabled, false);
            Arrays.fill(shadowMipmapEnabled, false);
            Arrays.fill(shadowFilterNearest, false);
            Arrays.fill(shadowColorMipmapEnabled, false);
            Arrays.fill(shadowColorFilterNearest, false);
            centerDepthSmoothEnabled = false;
            noiseTextureEnabled = false;
            sunPathRotation = 0.0f;
            shadowIntervalSize = 2.0f;
            shadowDistanceRenderMul = -1.0f;
            aoLevel = -1.0f;
            useEntityAttrib = false;
            useMidTexCoordAttrib = false;
            useMultiTexCoord3Attrib = false;
            useTangentAttrib = false;
            waterShadowEnabled = false;
            updateChunksErrorRecorded = false;
            Shaders.updateBlockLightLevel();
            ShaderProfile shaderprofile = ShaderUtils.detectProfile(shaderPackProfiles, shaderPackOptions, false);
            String s2 = "";
            if (currentWorld != null && shaderPackDimensions.contains(i2 = Shaders.currentWorld.provider.getDimensionId())) {
                s2 = "world" + i2 + "/";
            }
            if (saveFinalShaders) {
                Shaders.clearDirectory(new File(shaderpacksdir, "debug"));
            }
            for (int k1 = 0; k1 < 33; ++k1) {
                String s3;
                int j2;
                String s1 = programNames[k1];
                if (s1.equals("")) {
                    Shaders.programsRef[k1] = 0;
                    Shaders.programsID[k1] = 0;
                    Shaders.programsDrawBufSettings[k1] = null;
                    Shaders.programsColorAtmSettings[k1] = null;
                    Shaders.programsCompositeMipmapSetting[k1] = 0;
                    continue;
                }
                newDrawBufSetting = null;
                newColorAtmSetting = null;
                newCompositeMipmapSetting = 0;
                String s22 = s2 + s1;
                if (shaderprofile != null && shaderprofile.isProgramDisabled(s22)) {
                    SMCLog.info("Program disabled: " + s22);
                    s1 = "<disabled>";
                    s22 = s2 + s1;
                }
                if ((j2 = Shaders.setupProgram(k1, (s3 = "/shaders/" + s22) + ".vsh", s3 + ".fsh")) > 0) {
                    SMCLog.info("Program loaded: " + s22);
                }
                Shaders.programsID[k1] = Shaders.programsRef[k1] = j2;
                Shaders.programsDrawBufSettings[k1] = j2 != 0 ? newDrawBufSetting : null;
                Shaders.programsColorAtmSettings[k1] = j2 != 0 ? newColorAtmSetting : null;
                Shaders.programsCompositeMipmapSetting[k1] = j2 != 0 ? newCompositeMipmapSetting : 0;
            }
            int l1 = GL11.glGetInteger(34852);
            new HashMap();
            for (int i22 = 0; i22 < 33; ++i22) {
                Arrays.fill(programsToggleColorTextures[i22], false);
                if (i22 == 29) {
                    Shaders.programsDrawBuffers[i22] = null;
                    continue;
                }
                if (programsID[i22] == 0) {
                    if (i22 == 30) {
                        Shaders.programsDrawBuffers[i22] = drawBuffersNone;
                        continue;
                    }
                    Shaders.programsDrawBuffers[i22] = drawBuffersColorAtt0;
                    continue;
                }
                String s4 = programsDrawBufSettings[i22];
                if (s4 != null) {
                    IntBuffer intbuffer = drawBuffersBuffer[i22];
                    int k2 = s4.length();
                    if (k2 > usedDrawBuffers) {
                        usedDrawBuffers = k2;
                    }
                    if (k2 > l1) {
                        k2 = l1;
                    }
                    Shaders.programsDrawBuffers[i22] = intbuffer;
                    intbuffer.limit(k2);
                    for (int l2 = 0; l2 < k2; ++l2) {
                        int i1 = 0;
                        if (s4.length() > l2) {
                            int j1 = s4.charAt(l2) - 48;
                            if (i22 != 30) {
                                if (j1 >= 0 && j1 <= 7) {
                                    Shaders.programsToggleColorTextures[i22][j1] = true;
                                    i1 = j1 + 36064;
                                    if (j1 > usedColorAttachs) {
                                        usedColorAttachs = j1;
                                    }
                                    if (j1 > usedColorBuffers) {
                                        usedColorBuffers = j1;
                                    }
                                }
                            } else if (j1 >= 0 && j1 <= 1) {
                                i1 = j1 + 36064;
                                if (j1 > usedShadowColorBuffers) {
                                    usedShadowColorBuffers = j1;
                                }
                            }
                        }
                        intbuffer.put(l2, i1);
                    }
                    continue;
                }
                if (i22 != 30 && i22 != 31 && i22 != 32) {
                    Shaders.programsDrawBuffers[i22] = dfbDrawBuffers;
                    usedDrawBuffers = usedColorBuffers;
                    Arrays.fill(programsToggleColorTextures[i22], 0, usedColorBuffers, true);
                    continue;
                }
                Shaders.programsDrawBuffers[i22] = sfbDrawBuffers;
            }
            usedColorAttachs = usedColorBuffers;
            shadowPassInterval = usedShadowDepthBuffers > 0 ? 1 : 0;
            shouldSkipDefaultShadow = usedShadowDepthBuffers > 0;
            SMCLog.info("usedColorBuffers: " + usedColorBuffers);
            SMCLog.info("usedDepthBuffers: " + usedDepthBuffers);
            SMCLog.info("usedShadowColorBuffers: " + usedShadowColorBuffers);
            SMCLog.info("usedShadowDepthBuffers: " + usedShadowDepthBuffers);
            SMCLog.info("usedColorAttachs: " + usedColorAttachs);
            SMCLog.info("usedDrawBuffers: " + usedDrawBuffers);
            dfbDrawBuffers.position(0).limit(usedDrawBuffers);
            dfbColorTextures.position(0).limit(usedColorBuffers * 2);
            for (int j2 = 0; j2 < usedDrawBuffers; ++j2) {
                dfbDrawBuffers.put(j2, 36064 + j2);
            }
            if (usedDrawBuffers > l1) {
                Shaders.printChatAndLogError("[Shaders] Error: Not enough draw buffers, needed: " + usedDrawBuffers + ", available: " + l1);
            }
            sfbDrawBuffers.position(0).limit(usedShadowColorBuffers);
            for (int k2 = 0; k2 < usedShadowColorBuffers; ++k2) {
                sfbDrawBuffers.put(k2, 36064 + k2);
            }
            for (int l2 = 0; l2 < 33; ++l2) {
                int i3 = l2;
                while (programsID[i3] == 0 && programBackups[i3] != i3) {
                    i3 = programBackups[i3];
                }
                if (i3 == l2 || l2 == 30) continue;
                Shaders.programsID[l2] = programsID[i3];
                Shaders.programsDrawBufSettings[l2] = programsDrawBufSettings[i3];
                Shaders.programsDrawBuffers[l2] = programsDrawBuffers[i3];
            }
            Shaders.resize();
            Shaders.resizeShadow();
            if (noiseTextureEnabled) {
                Shaders.setupNoiseTexture();
            }
            if (defaultTexture == null) {
                defaultTexture = ShadersTex.createDefaultTexture();
            }
            GlStateManager.pushMatrix();
            GlStateManager.rotate(-90.0f, 0.0f, 1.0f, 0.0f);
            Shaders.preCelestialRotate();
            Shaders.postCelestialRotate();
            GlStateManager.popMatrix();
            isShaderPackInitialized = true;
            Shaders.loadEntityDataMap();
            Shaders.resetDisplayList();
            if (!flag) {
                // empty if block
            }
            Shaders.checkGLError("Shaders.init");
        }
    }

    public static void resetDisplayList() {
        ++numberResetDisplayList;
        needResetModels = true;
        SMCLog.info("Reset world renderers");
        Shaders.mc.renderGlobal.loadRenderers();
    }

    public static void resetDisplayListModels() {
        if (needResetModels) {
            needResetModels = false;
            SMCLog.info("Reset model renderers");
            for (Object render : mc.getRenderManager().getEntityRenderMap().values()) {
                if (!(render instanceof RendererLivingEntity)) continue;
                RendererLivingEntity rendererlivingentity = (RendererLivingEntity)render;
                Shaders.resetDisplayListModel(rendererlivingentity.getMainModel());
            }
        }
    }

    public static void resetDisplayListModel(ModelBase model) {
        if (model != null) {
            for (ModelRenderer object : model.boxList) {
                if (!(object instanceof ModelRenderer)) continue;
                Shaders.resetDisplayListModelRenderer(object);
            }
        }
    }

    public static void resetDisplayListModelRenderer(ModelRenderer mrr) {
        mrr.resetDisplayList();
        if (mrr.childModels != null) {
            int j2 = mrr.childModels.size();
            for (int i2 = 0; i2 < j2; ++i2) {
                Shaders.resetDisplayListModelRenderer((ModelRenderer)mrr.childModels.get(i2));
            }
        }
    }

    private static int setupProgram(int program, String vShaderPath, String fShaderPath) {
        Shaders.checkGLError("pre setupProgram");
        int i2 = ARBShaderObjects.glCreateProgramObjectARB();
        Shaders.checkGLError("create");
        if (i2 != 0) {
            progUseEntityAttrib = false;
            progUseMidTexCoordAttrib = false;
            progUseTangentAttrib = false;
            int j2 = Shaders.createVertShader(vShaderPath);
            int k2 = Shaders.createFragShader(fShaderPath);
            Shaders.checkGLError("create");
            if (j2 == 0 && k2 == 0) {
                ARBShaderObjects.glDeleteObjectARB(i2);
                i2 = 0;
            } else {
                if (j2 != 0) {
                    ARBShaderObjects.glAttachObjectARB(i2, j2);
                    Shaders.checkGLError("attach");
                }
                if (k2 != 0) {
                    ARBShaderObjects.glAttachObjectARB(i2, k2);
                    Shaders.checkGLError("attach");
                }
                if (progUseEntityAttrib) {
                    ARBVertexShader.glBindAttribLocationARB(i2, entityAttrib, "mc_Entity");
                    Shaders.checkGLError("mc_Entity");
                }
                if (progUseMidTexCoordAttrib) {
                    ARBVertexShader.glBindAttribLocationARB(i2, midTexCoordAttrib, "mc_midTexCoord");
                    Shaders.checkGLError("mc_midTexCoord");
                }
                if (progUseTangentAttrib) {
                    ARBVertexShader.glBindAttribLocationARB(i2, tangentAttrib, "at_tangent");
                    Shaders.checkGLError("at_tangent");
                }
                ARBShaderObjects.glLinkProgramARB(i2);
                if (GL20.glGetProgrami(i2, 35714) != 1) {
                    SMCLog.severe("Error linking program: " + i2);
                }
                Shaders.printLogInfo(i2, vShaderPath + ", " + fShaderPath);
                if (j2 != 0) {
                    ARBShaderObjects.glDetachObjectARB(i2, j2);
                    ARBShaderObjects.glDeleteObjectARB(j2);
                }
                if (k2 != 0) {
                    ARBShaderObjects.glDetachObjectARB(i2, k2);
                    ARBShaderObjects.glDeleteObjectARB(k2);
                }
                Shaders.programsID[program] = i2;
                Shaders.useProgram(program);
                ARBShaderObjects.glValidateProgramARB(i2);
                Shaders.useProgram(0);
                Shaders.printLogInfo(i2, vShaderPath + ", " + fShaderPath);
                int l2 = GL20.glGetProgrami(i2, 35715);
                if (l2 != 1) {
                    String s2 = "\"";
                    Shaders.printChatAndLogError("[Shaders] Error: Invalid program " + s2 + programNames[program] + s2);
                    ARBShaderObjects.glDeleteObjectARB(i2);
                    i2 = 0;
                }
            }
        }
        return i2;
    }

    private static int createVertShader(String filename) {
        int i2 = ARBShaderObjects.glCreateShaderObjectARB(35633);
        if (i2 == 0) {
            return 0;
        }
        StringBuilder stringbuilder = new StringBuilder(131072);
        BufferedReader bufferedreader = null;
        try {
            bufferedreader = new BufferedReader(new InputStreamReader(shaderPack.getResourceAsStream(filename)));
        }
        catch (Exception var8) {
            try {
                bufferedreader = new BufferedReader(new FileReader(new File(filename)));
            }
            catch (Exception var7) {
                ARBShaderObjects.glDeleteObjectARB(i2);
                return 0;
            }
        }
        ShaderOption[] ashaderoption = Shaders.getChangedOptions(shaderPackOptions);
        ArrayList<String> list = new ArrayList<String>();
        if (bufferedreader != null) {
            try {
                bufferedreader = ShaderPackParser.resolveIncludes(bufferedreader, filename, shaderPack, 0, list, 0);
                while (true) {
                    String s2;
                    if ((s2 = bufferedreader.readLine()) == null) {
                        bufferedreader.close();
                        break;
                    }
                    s2 = Shaders.applyOptions(s2, ashaderoption);
                    stringbuilder.append(s2).append('\n');
                    if (s2.matches("attribute [_a-zA-Z0-9]+ mc_Entity.*")) {
                        useEntityAttrib = true;
                        progUseEntityAttrib = true;
                        continue;
                    }
                    if (s2.matches("attribute [_a-zA-Z0-9]+ mc_midTexCoord.*")) {
                        useMidTexCoordAttrib = true;
                        progUseMidTexCoordAttrib = true;
                        continue;
                    }
                    if (s2.matches(".*gl_MultiTexCoord3.*")) {
                        useMultiTexCoord3Attrib = true;
                        continue;
                    }
                    if (!s2.matches("attribute [_a-zA-Z0-9]+ at_tangent.*")) continue;
                    useTangentAttrib = true;
                    progUseTangentAttrib = true;
                }
            }
            catch (Exception exception) {
                SMCLog.severe("Couldn't read " + filename + "!");
                exception.printStackTrace();
                ARBShaderObjects.glDeleteObjectARB(i2);
                return 0;
            }
        }
        if (saveFinalShaders) {
            Shaders.saveShader(filename, stringbuilder.toString());
        }
        ARBShaderObjects.glShaderSourceARB(i2, stringbuilder);
        ARBShaderObjects.glCompileShaderARB(i2);
        if (GL20.glGetShaderi(i2, 35713) != 1) {
            SMCLog.severe("Error compiling vertex shader: " + filename);
        }
        Shaders.printShaderLogInfo(i2, filename, list);
        return i2;
    }

    private static int createFragShader(String filename) {
        int i2 = ARBShaderObjects.glCreateShaderObjectARB(35632);
        if (i2 == 0) {
            return 0;
        }
        StringBuilder stringbuilder = new StringBuilder(131072);
        BufferedReader bufferedreader = null;
        try {
            bufferedreader = new BufferedReader(new InputStreamReader(shaderPack.getResourceAsStream(filename)));
        }
        catch (Exception var13) {
            try {
                bufferedreader = new BufferedReader(new FileReader(new File(filename)));
            }
            catch (Exception var12) {
                ARBShaderObjects.glDeleteObjectARB(i2);
                return 0;
            }
        }
        ShaderOption[] ashaderoption = Shaders.getChangedOptions(shaderPackOptions);
        ArrayList<String> list = new ArrayList<String>();
        if (bufferedreader != null) {
            try {
                bufferedreader = ShaderPackParser.resolveIncludes(bufferedreader, filename, shaderPack, 0, list, 0);
                while (true) {
                    String s2;
                    if ((s2 = bufferedreader.readLine()) == null) {
                        bufferedreader.close();
                        break;
                    }
                    s2 = Shaders.applyOptions(s2, ashaderoption);
                    stringbuilder.append(s2).append('\n');
                    if (s2.matches("#version .*")) continue;
                    if (s2.matches("uniform [ _a-zA-Z0-9]+ shadow;.*")) {
                        if (usedShadowDepthBuffers >= 1) continue;
                        usedShadowDepthBuffers = 1;
                        continue;
                    }
                    if (s2.matches("uniform [ _a-zA-Z0-9]+ watershadow;.*")) {
                        waterShadowEnabled = true;
                        if (usedShadowDepthBuffers >= 2) continue;
                        usedShadowDepthBuffers = 2;
                        continue;
                    }
                    if (s2.matches("uniform [ _a-zA-Z0-9]+ shadowtex0;.*")) {
                        if (usedShadowDepthBuffers >= 1) continue;
                        usedShadowDepthBuffers = 1;
                        continue;
                    }
                    if (s2.matches("uniform [ _a-zA-Z0-9]+ shadowtex1;.*")) {
                        if (usedShadowDepthBuffers >= 2) continue;
                        usedShadowDepthBuffers = 2;
                        continue;
                    }
                    if (s2.matches("uniform [ _a-zA-Z0-9]+ shadowcolor;.*")) {
                        if (usedShadowColorBuffers >= 1) continue;
                        usedShadowColorBuffers = 1;
                        continue;
                    }
                    if (s2.matches("uniform [ _a-zA-Z0-9]+ shadowcolor0;.*")) {
                        if (usedShadowColorBuffers >= 1) continue;
                        usedShadowColorBuffers = 1;
                        continue;
                    }
                    if (s2.matches("uniform [ _a-zA-Z0-9]+ shadowcolor1;.*")) {
                        if (usedShadowColorBuffers >= 2) continue;
                        usedShadowColorBuffers = 2;
                        continue;
                    }
                    if (s2.matches("uniform [ _a-zA-Z0-9]+ depthtex0;.*")) {
                        if (usedDepthBuffers >= 1) continue;
                        usedDepthBuffers = 1;
                        continue;
                    }
                    if (s2.matches("uniform [ _a-zA-Z0-9]+ depthtex1;.*")) {
                        if (usedDepthBuffers >= 2) continue;
                        usedDepthBuffers = 2;
                        continue;
                    }
                    if (s2.matches("uniform [ _a-zA-Z0-9]+ depthtex2;.*")) {
                        if (usedDepthBuffers >= 3) continue;
                        usedDepthBuffers = 3;
                        continue;
                    }
                    if (s2.matches("uniform [ _a-zA-Z0-9]+ gdepth;.*")) {
                        if (gbuffersFormat[1] != 6408) continue;
                        Shaders.gbuffersFormat[1] = 34836;
                        continue;
                    }
                    if (usedColorBuffers < 5 && s2.matches("uniform [ _a-zA-Z0-9]+ gaux1;.*")) {
                        usedColorBuffers = 5;
                        continue;
                    }
                    if (usedColorBuffers < 6 && s2.matches("uniform [ _a-zA-Z0-9]+ gaux2;.*")) {
                        usedColorBuffers = 6;
                        continue;
                    }
                    if (usedColorBuffers < 7 && s2.matches("uniform [ _a-zA-Z0-9]+ gaux3;.*")) {
                        usedColorBuffers = 7;
                        continue;
                    }
                    if (usedColorBuffers < 8 && s2.matches("uniform [ _a-zA-Z0-9]+ gaux4;.*")) {
                        usedColorBuffers = 8;
                        continue;
                    }
                    if (usedColorBuffers < 5 && s2.matches("uniform [ _a-zA-Z0-9]+ colortex4;.*")) {
                        usedColorBuffers = 5;
                        continue;
                    }
                    if (usedColorBuffers < 6 && s2.matches("uniform [ _a-zA-Z0-9]+ colortex5;.*")) {
                        usedColorBuffers = 6;
                        continue;
                    }
                    if (usedColorBuffers < 7 && s2.matches("uniform [ _a-zA-Z0-9]+ colortex6;.*")) {
                        usedColorBuffers = 7;
                        continue;
                    }
                    if (usedColorBuffers < 8 && s2.matches("uniform [ _a-zA-Z0-9]+ colortex7;.*")) {
                        usedColorBuffers = 8;
                        continue;
                    }
                    if (s2.matches("uniform [ _a-zA-Z0-9]+ centerDepthSmooth;.*")) {
                        centerDepthSmoothEnabled = true;
                        continue;
                    }
                    if (s2.matches("/\\* SHADOWRES:[0-9]+ \\*/.*")) {
                        String[] astring17 = s2.split("(:| )", 4);
                        SMCLog.info("Shadow map resolution: " + astring17[2]);
                        spShadowMapWidth = spShadowMapHeight = Integer.parseInt(astring17[2]);
                        shadowMapWidth = shadowMapHeight = Math.round((float)spShadowMapWidth * configShadowResMul);
                        continue;
                    }
                    if (s2.matches("[ \t]*const[ \t]*int[ \t]*shadowMapResolution[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                        String[] astring16 = s2.split("(=[ \t]*|;)");
                        SMCLog.info("Shadow map resolution: " + astring16[1]);
                        spShadowMapWidth = spShadowMapHeight = Integer.parseInt(astring16[1]);
                        shadowMapWidth = shadowMapHeight = Math.round((float)spShadowMapWidth * configShadowResMul);
                        continue;
                    }
                    if (s2.matches("/\\* SHADOWFOV:[0-9\\.]+ \\*/.*")) {
                        String[] astring15 = s2.split("(:| )", 4);
                        SMCLog.info("Shadow map field of view: " + astring15[2]);
                        shadowMapFOV = Float.parseFloat(astring15[2]);
                        shadowMapIsOrtho = false;
                        continue;
                    }
                    if (s2.matches("/\\* SHADOWHPL:[0-9\\.]+ \\*/.*")) {
                        String[] astring14 = s2.split("(:| )", 4);
                        SMCLog.info("Shadow map half-plane: " + astring14[2]);
                        shadowMapHalfPlane = Float.parseFloat(astring14[2]);
                        shadowMapIsOrtho = true;
                        continue;
                    }
                    if (s2.matches("[ \t]*const[ \t]*float[ \t]*shadowDistance[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                        String[] astring13 = s2.split("(=[ \t]*|;)");
                        SMCLog.info("Shadow map distance: " + astring13[1]);
                        shadowMapHalfPlane = Float.parseFloat(astring13[1]);
                        shadowMapIsOrtho = true;
                        continue;
                    }
                    if (s2.matches("[ \t]*const[ \t]*float[ \t]*shadowDistanceRenderMul[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                        String[] astring12 = s2.split("(=[ \t]*|;)");
                        SMCLog.info("Shadow distance render mul: " + astring12[1]);
                        shadowDistanceRenderMul = Float.parseFloat(astring12[1]);
                        continue;
                    }
                    if (s2.matches("[ \t]*const[ \t]*float[ \t]*shadowIntervalSize[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                        String[] astring11 = s2.split("(=[ \t]*|;)");
                        SMCLog.info("Shadow map interval size: " + astring11[1]);
                        shadowIntervalSize = Float.parseFloat(astring11[1]);
                        continue;
                    }
                    if (s2.matches("[ \t]*const[ \t]*bool[ \t]*generateShadowMipmap[ \t]*=[ \t]*true[ \t]*;.*")) {
                        SMCLog.info("Generate shadow mipmap");
                        Arrays.fill(shadowMipmapEnabled, true);
                        continue;
                    }
                    if (s2.matches("[ \t]*const[ \t]*bool[ \t]*generateShadowColorMipmap[ \t]*=[ \t]*true[ \t]*;.*")) {
                        SMCLog.info("Generate shadow color mipmap");
                        Arrays.fill(shadowColorMipmapEnabled, true);
                        continue;
                    }
                    if (s2.matches("[ \t]*const[ \t]*bool[ \t]*shadowHardwareFiltering[ \t]*=[ \t]*true[ \t]*;.*")) {
                        SMCLog.info("Hardware shadow filtering enabled.");
                        Arrays.fill(shadowHardwareFilteringEnabled, true);
                        continue;
                    }
                    if (s2.matches("[ \t]*const[ \t]*bool[ \t]*shadowHardwareFiltering0[ \t]*=[ \t]*true[ \t]*;.*")) {
                        SMCLog.info("shadowHardwareFiltering0");
                        Shaders.shadowHardwareFilteringEnabled[0] = true;
                        continue;
                    }
                    if (s2.matches("[ \t]*const[ \t]*bool[ \t]*shadowHardwareFiltering1[ \t]*=[ \t]*true[ \t]*;.*")) {
                        SMCLog.info("shadowHardwareFiltering1");
                        Shaders.shadowHardwareFilteringEnabled[1] = true;
                        continue;
                    }
                    if (s2.matches("[ \t]*const[ \t]*bool[ \t]*(shadowtex0Mipmap|shadowtexMipmap)[ \t]*=[ \t]*true[ \t]*;.*")) {
                        SMCLog.info("shadowtex0Mipmap");
                        Shaders.shadowMipmapEnabled[0] = true;
                        continue;
                    }
                    if (s2.matches("[ \t]*const[ \t]*bool[ \t]*(shadowtex1Mipmap)[ \t]*=[ \t]*true[ \t]*;.*")) {
                        SMCLog.info("shadowtex1Mipmap");
                        Shaders.shadowMipmapEnabled[1] = true;
                        continue;
                    }
                    if (s2.matches("[ \t]*const[ \t]*bool[ \t]*(shadowcolor0Mipmap|shadowColor0Mipmap)[ \t]*=[ \t]*true[ \t]*;.*")) {
                        SMCLog.info("shadowcolor0Mipmap");
                        Shaders.shadowColorMipmapEnabled[0] = true;
                        continue;
                    }
                    if (s2.matches("[ \t]*const[ \t]*bool[ \t]*(shadowcolor1Mipmap|shadowColor1Mipmap)[ \t]*=[ \t]*true[ \t]*;.*")) {
                        SMCLog.info("shadowcolor1Mipmap");
                        Shaders.shadowColorMipmapEnabled[1] = true;
                        continue;
                    }
                    if (s2.matches("[ \t]*const[ \t]*bool[ \t]*(shadowtex0Nearest|shadowtexNearest|shadow0MinMagNearest)[ \t]*=[ \t]*true[ \t]*;.*")) {
                        SMCLog.info("shadowtex0Nearest");
                        Shaders.shadowFilterNearest[0] = true;
                        continue;
                    }
                    if (s2.matches("[ \t]*const[ \t]*bool[ \t]*(shadowtex1Nearest|shadow1MinMagNearest)[ \t]*=[ \t]*true[ \t]*;.*")) {
                        SMCLog.info("shadowtex1Nearest");
                        Shaders.shadowFilterNearest[1] = true;
                        continue;
                    }
                    if (s2.matches("[ \t]*const[ \t]*bool[ \t]*(shadowcolor0Nearest|shadowColor0Nearest|shadowColor0MinMagNearest)[ \t]*=[ \t]*true[ \t]*;.*")) {
                        SMCLog.info("shadowcolor0Nearest");
                        Shaders.shadowColorFilterNearest[0] = true;
                        continue;
                    }
                    if (s2.matches("[ \t]*const[ \t]*bool[ \t]*(shadowcolor1Nearest|shadowColor1Nearest|shadowColor1MinMagNearest)[ \t]*=[ \t]*true[ \t]*;.*")) {
                        SMCLog.info("shadowcolor1Nearest");
                        Shaders.shadowColorFilterNearest[1] = true;
                        continue;
                    }
                    if (s2.matches("/\\* WETNESSHL:[0-9\\.]+ \\*/.*")) {
                        String[] astring10 = s2.split("(:| )", 4);
                        SMCLog.info("Wetness halflife: " + astring10[2]);
                        wetnessHalfLife = Float.parseFloat(astring10[2]);
                        continue;
                    }
                    if (s2.matches("[ \t]*const[ \t]*float[ \t]*wetnessHalflife[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                        String[] astring9 = s2.split("(=[ \t]*|;)");
                        SMCLog.info("Wetness halflife: " + astring9[1]);
                        wetnessHalfLife = Float.parseFloat(astring9[1]);
                        continue;
                    }
                    if (s2.matches("/\\* DRYNESSHL:[0-9\\.]+ \\*/.*")) {
                        String[] astring8 = s2.split("(:| )", 4);
                        SMCLog.info("Dryness halflife: " + astring8[2]);
                        drynessHalfLife = Float.parseFloat(astring8[2]);
                        continue;
                    }
                    if (s2.matches("[ \t]*const[ \t]*float[ \t]*drynessHalflife[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                        String[] astring7 = s2.split("(=[ \t]*|;)");
                        SMCLog.info("Dryness halflife: " + astring7[1]);
                        drynessHalfLife = Float.parseFloat(astring7[1]);
                        continue;
                    }
                    if (s2.matches("[ \t]*const[ \t]*float[ \t]*eyeBrightnessHalflife[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                        String[] astring6 = s2.split("(=[ \t]*|;)");
                        SMCLog.info("Eye brightness halflife: " + astring6[1]);
                        eyeBrightnessHalflife = Float.parseFloat(astring6[1]);
                        continue;
                    }
                    if (s2.matches("[ \t]*const[ \t]*float[ \t]*centerDepthHalflife[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                        String[] astring5 = s2.split("(=[ \t]*|;)");
                        SMCLog.info("Center depth halflife: " + astring5[1]);
                        centerDepthSmoothHalflife = Float.parseFloat(astring5[1]);
                        continue;
                    }
                    if (s2.matches("[ \t]*const[ \t]*float[ \t]*sunPathRotation[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                        String[] astring4 = s2.split("(=[ \t]*|;)");
                        SMCLog.info("Sun path rotation: " + astring4[1]);
                        sunPathRotation = Float.parseFloat(astring4[1]);
                        continue;
                    }
                    if (s2.matches("[ \t]*const[ \t]*float[ \t]*ambientOcclusionLevel[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                        String[] astring3 = s2.split("(=[ \t]*|;)");
                        SMCLog.info("AO Level: " + astring3[1]);
                        aoLevel = Config.limit(Float.parseFloat(astring3[1]), 0.0f, 1.0f);
                        continue;
                    }
                    if (s2.matches("[ \t]*const[ \t]*int[ \t]*superSamplingLevel[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                        String[] astring2 = s2.split("(=[ \t]*|;)");
                        int i1 = Integer.parseInt(astring2[1]);
                        if (i1 > 1) {
                            SMCLog.info("Super sampling level: " + i1 + "x");
                            superSamplingLevel = i1;
                            continue;
                        }
                        superSamplingLevel = 1;
                        continue;
                    }
                    if (s2.matches("[ \t]*const[ \t]*int[ \t]*noiseTextureResolution[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                        String[] astring1 = s2.split("(=[ \t]*|;)");
                        SMCLog.info("Noise texture enabled");
                        SMCLog.info("Noise texture resolution: " + astring1[1]);
                        noiseTextureResolution = Integer.parseInt(astring1[1]);
                        noiseTextureEnabled = true;
                        continue;
                    }
                    if (s2.matches("[ \t]*const[ \t]*int[ \t]*\\w+Format[ \t]*=[ \t]*[RGBA0123456789FUI_SNORM]*[ \t]*;.*")) {
                        Matcher matcher2 = gbufferFormatPattern.matcher(s2);
                        matcher2.matches();
                        String s3 = matcher2.group(1);
                        String s4 = matcher2.group(2);
                        int k2 = Shaders.getBufferIndexFromString(s3);
                        int l2 = Shaders.getTextureFormatFromString(s4);
                        if (k2 < 0 || l2 == 0) continue;
                        Shaders.gbuffersFormat[k2] = l2;
                        SMCLog.info("%s format: %s", s3, s4);
                        continue;
                    }
                    if (s2.matches("[ \t]*const[ \t]*bool[ \t]*\\w+Clear[ \t]*=[ \t]*false[ \t]*;.*")) {
                        if (!filename.matches(".*composite[0-9]?.fsh")) continue;
                        Matcher matcher1 = gbufferClearPattern.matcher(s2);
                        matcher1.matches();
                        String s22 = matcher1.group(1);
                        int j1 = Shaders.getBufferIndexFromString(s22);
                        if (j1 < 0) continue;
                        Shaders.gbuffersClear[j1] = false;
                        SMCLog.info("%s clear disabled", s22);
                        continue;
                    }
                    if (s2.matches("/\\* GAUX4FORMAT:RGBA32F \\*/.*")) {
                        SMCLog.info("gaux4 format : RGB32AF");
                        Shaders.gbuffersFormat[7] = 34836;
                        continue;
                    }
                    if (s2.matches("/\\* GAUX4FORMAT:RGB32F \\*/.*")) {
                        SMCLog.info("gaux4 format : RGB32F");
                        Shaders.gbuffersFormat[7] = 34837;
                        continue;
                    }
                    if (s2.matches("/\\* GAUX4FORMAT:RGB16 \\*/.*")) {
                        SMCLog.info("gaux4 format : RGB16");
                        Shaders.gbuffersFormat[7] = 32852;
                        continue;
                    }
                    if (s2.matches("[ \t]*const[ \t]*bool[ \t]*\\w+MipmapEnabled[ \t]*=[ \t]*true[ \t]*;.*")) {
                        if (!filename.matches(".*composite[0-9]?.fsh") && !filename.matches(".*final.fsh")) continue;
                        Matcher matcher = gbufferMipmapEnabledPattern.matcher(s2);
                        matcher.matches();
                        String s1 = matcher.group(1);
                        int j2 = Shaders.getBufferIndexFromString(s1);
                        if (j2 < 0) continue;
                        newCompositeMipmapSetting |= 1 << j2;
                        SMCLog.info("%s mipmap enabled", s1);
                        continue;
                    }
                    if (!s2.matches("/\\* DRAWBUFFERS:[0-7N]* \\*/.*")) continue;
                    String[] astring = s2.split("(:| )", 4);
                    newDrawBufSetting = astring[2];
                }
            }
            catch (Exception exception) {
                SMCLog.severe("Couldn't read " + filename + "!");
                exception.printStackTrace();
                ARBShaderObjects.glDeleteObjectARB(i2);
                return 0;
            }
        }
        if (saveFinalShaders) {
            Shaders.saveShader(filename, stringbuilder.toString());
        }
        ARBShaderObjects.glShaderSourceARB(i2, stringbuilder);
        ARBShaderObjects.glCompileShaderARB(i2);
        if (GL20.glGetShaderi(i2, 35713) != 1) {
            SMCLog.severe("Error compiling fragment shader: " + filename);
        }
        Shaders.printShaderLogInfo(i2, filename, list);
        return i2;
    }

    private static void saveShader(String filename, String code) {
        try {
            File file1 = new File(shaderpacksdir, "debug/" + filename);
            file1.getParentFile().mkdirs();
            Config.writeFile(file1, code);
        }
        catch (IOException ioexception) {
            Config.warn("Error saving: " + filename);
            ioexception.printStackTrace();
        }
    }

    private static void clearDirectory(File dir) {
        File[] afile;
        if (dir.exists() && dir.isDirectory() && (afile = dir.listFiles()) != null) {
            for (int i2 = 0; i2 < afile.length; ++i2) {
                File file1 = afile[i2];
                if (file1.isDirectory()) {
                    Shaders.clearDirectory(file1);
                }
                file1.delete();
            }
        }
    }

    private static boolean printLogInfo(int obj, String name) {
        IntBuffer intbuffer = BufferUtils.createIntBuffer(1);
        ARBShaderObjects.glGetObjectParameterARB(obj, 35716, intbuffer);
        int i2 = intbuffer.get();
        if (i2 > 1) {
            ByteBuffer bytebuffer = BufferUtils.createByteBuffer(i2);
            intbuffer.flip();
            ARBShaderObjects.glGetInfoLogARB(obj, intbuffer, bytebuffer);
            byte[] abyte = new byte[i2];
            bytebuffer.get(abyte);
            if (abyte[i2 - 1] == 0) {
                abyte[i2 - 1] = 10;
            }
            String s2 = new String(abyte);
            SMCLog.info("Info log: " + name + "\n" + s2);
            return false;
        }
        return true;
    }

    private static boolean printShaderLogInfo(int shader, String name, List<String> listFiles) {
        IntBuffer intbuffer = BufferUtils.createIntBuffer(1);
        int i2 = GL20.glGetShaderi(shader, 35716);
        if (i2 <= 1) {
            return true;
        }
        for (int j2 = 0; j2 < listFiles.size(); ++j2) {
            String s2 = listFiles.get(j2);
            SMCLog.info("File: " + (j2 + 1) + " = " + s2);
        }
        String s1 = GL20.glGetShaderInfoLog(shader, i2);
        SMCLog.info("Shader info log: " + name + "\n" + s1);
        return false;
    }

    public static void setDrawBuffers(IntBuffer drawBuffers) {
        if (drawBuffers == null) {
            drawBuffers = drawBuffersNone;
        }
        if (activeDrawBuffers != drawBuffers) {
            activeDrawBuffers = drawBuffers;
            GL20.glDrawBuffers(drawBuffers);
        }
    }

    public static void useProgram(int program) {
        Shaders.checkGLError("pre-useProgram");
        if (isShadowPass) {
            program = 30;
            if (programsID[30] == 0) {
                normalMapEnabled = false;
                return;
            }
        }
        if (activeProgram != program) {
            activeProgram = program;
            ARBShaderObjects.glUseProgramObjectARB(programsID[program]);
            if (programsID[program] == 0) {
                normalMapEnabled = false;
            } else {
                if (Shaders.checkGLError("useProgram ", programNames[program]) != 0) {
                    Shaders.programsID[program] = 0;
                }
                IntBuffer intbuffer = programsDrawBuffers[program];
                if (isRenderingDfb) {
                    Shaders.setDrawBuffers(intbuffer);
                    Shaders.checkGLError(programNames[program], " draw buffers = ", programsDrawBufSettings[program]);
                }
                activeCompositeMipmapSetting = programsCompositeMipmapSetting[program];
                uniformEntityColor.setProgram(programsID[activeProgram]);
                uniformEntityId.setProgram(programsID[activeProgram]);
                uniformBlockEntityId.setProgram(programsID[activeProgram]);
                switch (program) {
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
                    case 20: {
                        normalMapEnabled = true;
                        Shaders.setProgramUniform1i("texture", 0);
                        Shaders.setProgramUniform1i("lightmap", 1);
                        Shaders.setProgramUniform1i("normals", 2);
                        Shaders.setProgramUniform1i("specular", 3);
                        Shaders.setProgramUniform1i("shadow", waterShadowEnabled ? 5 : 4);
                        Shaders.setProgramUniform1i("watershadow", 4);
                        Shaders.setProgramUniform1i("shadowtex0", 4);
                        Shaders.setProgramUniform1i("shadowtex1", 5);
                        Shaders.setProgramUniform1i("depthtex0", 6);
                        if (customTexturesGbuffers != null) {
                            Shaders.setProgramUniform1i("gaux1", 7);
                            Shaders.setProgramUniform1i("gaux2", 8);
                            Shaders.setProgramUniform1i("gaux3", 9);
                            Shaders.setProgramUniform1i("gaux4", 10);
                        }
                        Shaders.setProgramUniform1i("depthtex1", 12);
                        Shaders.setProgramUniform1i("shadowcolor", 13);
                        Shaders.setProgramUniform1i("shadowcolor0", 13);
                        Shaders.setProgramUniform1i("shadowcolor1", 14);
                        Shaders.setProgramUniform1i("noisetex", 15);
                        break;
                    }
                    default: {
                        normalMapEnabled = false;
                        break;
                    }
                    case 21: 
                    case 22: 
                    case 23: 
                    case 24: 
                    case 25: 
                    case 26: 
                    case 27: 
                    case 28: 
                    case 29: {
                        normalMapEnabled = false;
                        Shaders.setProgramUniform1i("gcolor", 0);
                        Shaders.setProgramUniform1i("gdepth", 1);
                        Shaders.setProgramUniform1i("gnormal", 2);
                        Shaders.setProgramUniform1i("composite", 3);
                        Shaders.setProgramUniform1i("gaux1", 7);
                        Shaders.setProgramUniform1i("gaux2", 8);
                        Shaders.setProgramUniform1i("gaux3", 9);
                        Shaders.setProgramUniform1i("gaux4", 10);
                        Shaders.setProgramUniform1i("colortex0", 0);
                        Shaders.setProgramUniform1i("colortex1", 1);
                        Shaders.setProgramUniform1i("colortex2", 2);
                        Shaders.setProgramUniform1i("colortex3", 3);
                        Shaders.setProgramUniform1i("colortex4", 7);
                        Shaders.setProgramUniform1i("colortex5", 8);
                        Shaders.setProgramUniform1i("colortex6", 9);
                        Shaders.setProgramUniform1i("colortex7", 10);
                        Shaders.setProgramUniform1i("shadow", waterShadowEnabled ? 5 : 4);
                        Shaders.setProgramUniform1i("watershadow", 4);
                        Shaders.setProgramUniform1i("shadowtex0", 4);
                        Shaders.setProgramUniform1i("shadowtex1", 5);
                        Shaders.setProgramUniform1i("gdepthtex", 6);
                        Shaders.setProgramUniform1i("depthtex0", 6);
                        Shaders.setProgramUniform1i("depthtex1", 11);
                        Shaders.setProgramUniform1i("depthtex2", 12);
                        Shaders.setProgramUniform1i("shadowcolor", 13);
                        Shaders.setProgramUniform1i("shadowcolor0", 13);
                        Shaders.setProgramUniform1i("shadowcolor1", 14);
                        Shaders.setProgramUniform1i("noisetex", 15);
                        break;
                    }
                    case 30: 
                    case 31: 
                    case 32: {
                        Shaders.setProgramUniform1i("tex", 0);
                        Shaders.setProgramUniform1i("texture", 0);
                        Shaders.setProgramUniform1i("lightmap", 1);
                        Shaders.setProgramUniform1i("normals", 2);
                        Shaders.setProgramUniform1i("specular", 3);
                        Shaders.setProgramUniform1i("shadow", waterShadowEnabled ? 5 : 4);
                        Shaders.setProgramUniform1i("watershadow", 4);
                        Shaders.setProgramUniform1i("shadowtex0", 4);
                        Shaders.setProgramUniform1i("shadowtex1", 5);
                        if (customTexturesGbuffers != null) {
                            Shaders.setProgramUniform1i("gaux1", 7);
                            Shaders.setProgramUniform1i("gaux2", 8);
                            Shaders.setProgramUniform1i("gaux3", 9);
                            Shaders.setProgramUniform1i("gaux4", 10);
                        }
                        Shaders.setProgramUniform1i("shadowcolor", 13);
                        Shaders.setProgramUniform1i("shadowcolor0", 13);
                        Shaders.setProgramUniform1i("shadowcolor1", 14);
                        Shaders.setProgramUniform1i("noisetex", 15);
                    }
                }
                ItemStack itemstack = Shaders.mc.thePlayer != null ? Shaders.mc.thePlayer.getHeldItem() : null;
                Item item = itemstack != null ? itemstack.getItem() : null;
                int i2 = -1;
                Block block = null;
                if (item != null) {
                    i2 = Item.itemRegistry.getIDForObject(item);
                    block = Block.blockRegistry.getObjectById(i2);
                }
                int j2 = block != null ? block.getLightValue() : 0;
                Shaders.setProgramUniform1i("heldItemId", i2);
                Shaders.setProgramUniform1i("heldBlockLightValue", j2);
                Shaders.setProgramUniform1i("fogMode", fogEnabled ? fogMode : 0);
                Shaders.setProgramUniform3f("fogColor", fogColorR, fogColorG, fogColorB);
                Shaders.setProgramUniform3f("skyColor", skyColorR, skyColorG, skyColorB);
                Shaders.setProgramUniform1i("worldTime", (int)(worldTime % 24000L));
                Shaders.setProgramUniform1i("worldDay", (int)(worldTime / 24000L));
                Shaders.setProgramUniform1i("moonPhase", moonPhase);
                Shaders.setProgramUniform1i("frameCounter", frameCounter);
                Shaders.setProgramUniform1f("frameTime", frameTime);
                Shaders.setProgramUniform1f("frameTimeCounter", frameTimeCounter);
                Shaders.setProgramUniform1f("sunAngle", sunAngle);
                Shaders.setProgramUniform1f("shadowAngle", shadowAngle);
                Shaders.setProgramUniform1f("rainStrength", rainStrength);
                Shaders.setProgramUniform1f("aspectRatio", (float)renderWidth / (float)renderHeight);
                Shaders.setProgramUniform1f("viewWidth", renderWidth);
                Shaders.setProgramUniform1f("viewHeight", renderHeight);
                Shaders.setProgramUniform1f("near", 0.05f);
                Shaders.setProgramUniform1f("far", Shaders.mc.gameSettings.renderDistanceChunks * 16);
                Shaders.setProgramUniform3f("sunPosition", sunPosition[0], sunPosition[1], sunPosition[2]);
                Shaders.setProgramUniform3f("moonPosition", moonPosition[0], moonPosition[1], moonPosition[2]);
                Shaders.setProgramUniform3f("shadowLightPosition", shadowLightPosition[0], shadowLightPosition[1], shadowLightPosition[2]);
                Shaders.setProgramUniform3f("upPosition", upPosition[0], upPosition[1], upPosition[2]);
                Shaders.setProgramUniform3f("previousCameraPosition", (float)previousCameraPositionX, (float)previousCameraPositionY, (float)previousCameraPositionZ);
                Shaders.setProgramUniform3f("cameraPosition", (float)cameraPositionX, (float)cameraPositionY, (float)cameraPositionZ);
                Shaders.setProgramUniformMatrix4ARB("gbufferModelView", false, modelView);
                Shaders.setProgramUniformMatrix4ARB("gbufferModelViewInverse", false, modelViewInverse);
                Shaders.setProgramUniformMatrix4ARB("gbufferPreviousProjection", false, previousProjection);
                Shaders.setProgramUniformMatrix4ARB("gbufferProjection", false, projection);
                Shaders.setProgramUniformMatrix4ARB("gbufferProjectionInverse", false, projectionInverse);
                Shaders.setProgramUniformMatrix4ARB("gbufferPreviousModelView", false, previousModelView);
                if (usedShadowDepthBuffers > 0) {
                    Shaders.setProgramUniformMatrix4ARB("shadowProjection", false, shadowProjection);
                    Shaders.setProgramUniformMatrix4ARB("shadowProjectionInverse", false, shadowProjectionInverse);
                    Shaders.setProgramUniformMatrix4ARB("shadowModelView", false, shadowModelView);
                    Shaders.setProgramUniformMatrix4ARB("shadowModelViewInverse", false, shadowModelViewInverse);
                }
                Shaders.setProgramUniform1f("wetness", wetness);
                Shaders.setProgramUniform1f("eyeAltitude", eyePosY);
                Shaders.setProgramUniform2i("eyeBrightness", eyeBrightness & 0xFFFF, eyeBrightness >> 16);
                Shaders.setProgramUniform2i("eyeBrightnessSmooth", Math.round(eyeBrightnessFadeX), Math.round(eyeBrightnessFadeY));
                Shaders.setProgramUniform2i("terrainTextureSize", terrainTextureSize[0], terrainTextureSize[1]);
                Shaders.setProgramUniform1i("terrainIconSize", terrainIconSize);
                Shaders.setProgramUniform1i("isEyeInWater", isEyeInWater);
                Shaders.setProgramUniform1f("nightVision", nightVision);
                Shaders.setProgramUniform1f("blindness", blindness);
                Shaders.setProgramUniform1f("screenBrightness", Shaders.mc.gameSettings.gammaSetting);
                Shaders.setProgramUniform1i("hideGUI", Shaders.mc.gameSettings.hideGUI ? 1 : 0);
                Shaders.setProgramUniform1f("centerDepthSmooth", centerDepthSmooth);
                Shaders.setProgramUniform2i("atlasSize", atlasSizeX, atlasSizeY);
                Shaders.checkGLError("useProgram ", programNames[program]);
            }
        }
    }

    public static void setProgramUniform1i(String name, int x2) {
        int i2 = programsID[activeProgram];
        if (i2 != 0) {
            int j2 = ARBShaderObjects.glGetUniformLocationARB(i2, name);
            ARBShaderObjects.glUniform1iARB(j2, x2);
            Shaders.checkGLError(programNames[activeProgram], name);
        }
    }

    public static void setProgramUniform2i(String name, int x2, int y2) {
        int i2 = programsID[activeProgram];
        if (i2 != 0) {
            int j2 = ARBShaderObjects.glGetUniformLocationARB(i2, name);
            ARBShaderObjects.glUniform2iARB(j2, x2, y2);
            Shaders.checkGLError(programNames[activeProgram], name);
        }
    }

    public static void setProgramUniform1f(String name, float x2) {
        int i2 = programsID[activeProgram];
        if (i2 != 0) {
            int j2 = ARBShaderObjects.glGetUniformLocationARB(i2, name);
            ARBShaderObjects.glUniform1fARB(j2, x2);
            Shaders.checkGLError(programNames[activeProgram], name);
        }
    }

    public static void setProgramUniform3f(String name, float x2, float y2, float z2) {
        int i2 = programsID[activeProgram];
        if (i2 != 0) {
            int j2 = ARBShaderObjects.glGetUniformLocationARB(i2, name);
            ARBShaderObjects.glUniform3fARB(j2, x2, y2, z2);
            Shaders.checkGLError(programNames[activeProgram], name);
        }
    }

    public static void setProgramUniformMatrix4ARB(String name, boolean transpose, FloatBuffer matrix) {
        int i2 = programsID[activeProgram];
        if (i2 != 0 && matrix != null) {
            int j2 = ARBShaderObjects.glGetUniformLocationARB(i2, name);
            ARBShaderObjects.glUniformMatrix4ARB(j2, transpose, matrix);
            Shaders.checkGLError(programNames[activeProgram], name);
        }
    }

    private static int getBufferIndexFromString(String name) {
        return !name.equals("colortex0") && !name.equals("gcolor") ? (!name.equals("colortex1") && !name.equals("gdepth") ? (!name.equals("colortex2") && !name.equals("gnormal") ? (!name.equals("colortex3") && !name.equals("composite") ? (!name.equals("colortex4") && !name.equals("gaux1") ? (!name.equals("colortex5") && !name.equals("gaux2") ? (!name.equals("colortex6") && !name.equals("gaux3") ? (!name.equals("colortex7") && !name.equals("gaux4") ? -1 : 7) : 6) : 5) : 4) : 3) : 2) : 1) : 0;
    }

    private static int getTextureFormatFromString(String par) {
        par = par.trim();
        for (int i2 = 0; i2 < formatNames.length; ++i2) {
            String s2 = formatNames[i2];
            if (!par.equals(s2)) continue;
            return formatIds[i2];
        }
        return 0;
    }

    private static void setupNoiseTexture() {
        if (noiseTexture == null) {
            noiseTexture = new HFNoiseTexture(noiseTextureResolution, noiseTextureResolution);
        }
    }

    private static void loadEntityDataMap() {
        mapBlockToEntityData = new IdentityHashMap<Block, Integer>(300);
        if (mapBlockToEntityData.isEmpty()) {
            for (ResourceLocation resourcelocation : Block.blockRegistry.getKeys()) {
                Block block = Block.blockRegistry.getObject(resourcelocation);
                int i2 = Block.blockRegistry.getIDForObject(block);
                mapBlockToEntityData.put(block, i2);
            }
        }
        BufferedReader bufferedreader = null;
        try {
            bufferedreader = new BufferedReader(new InputStreamReader(shaderPack.getResourceAsStream("/mc_Entity_x.txt")));
        }
        catch (Exception resourcelocation) {
            // empty catch block
        }
        if (bufferedreader != null) {
            try {
                String s1;
                while ((s1 = bufferedreader.readLine()) != null) {
                    Matcher matcher = patternLoadEntityDataMap.matcher(s1);
                    if (matcher.matches()) {
                        String s2 = matcher.group(1);
                        String s3 = matcher.group(2);
                        int j2 = Integer.parseInt(s3);
                        Block block1 = Block.getBlockFromName(s2);
                        if (block1 != null) {
                            mapBlockToEntityData.put(block1, j2);
                            continue;
                        }
                        SMCLog.warning("Unknown block name %s", s2);
                        continue;
                    }
                    SMCLog.warning("unmatched %s\n", s1);
                }
            }
            catch (Exception var9) {
                SMCLog.warning("Error parsing mc_Entity_x.txt");
            }
        }
        if (bufferedreader != null) {
            try {
                bufferedreader.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    private static IntBuffer fillIntBufferZero(IntBuffer buf) {
        int i2 = buf.limit();
        for (int j2 = buf.position(); j2 < i2; ++j2) {
            buf.put(j2, 0);
        }
        return buf;
    }

    public static void uninit() {
        if (isShaderPackInitialized) {
            Shaders.checkGLError("Shaders.uninit pre");
            for (int i2 = 0; i2 < 33; ++i2) {
                if (programsRef[i2] != 0) {
                    ARBShaderObjects.glDeleteObjectARB(programsRef[i2]);
                    Shaders.checkGLError("del programRef");
                }
                Shaders.programsRef[i2] = 0;
                Shaders.programsID[i2] = 0;
                Shaders.programsDrawBufSettings[i2] = null;
                Shaders.programsDrawBuffers[i2] = null;
                Shaders.programsCompositeMipmapSetting[i2] = 0;
            }
            if (dfb != 0) {
                EXTFramebufferObject.glDeleteFramebuffersEXT(dfb);
                dfb = 0;
                Shaders.checkGLError("del dfb");
            }
            if (sfb != 0) {
                EXTFramebufferObject.glDeleteFramebuffersEXT(sfb);
                sfb = 0;
                Shaders.checkGLError("del sfb");
            }
            if (dfbDepthTextures != null) {
                GlStateManager.deleteTextures(dfbDepthTextures);
                Shaders.fillIntBufferZero(dfbDepthTextures);
                Shaders.checkGLError("del dfbDepthTextures");
            }
            if (dfbColorTextures != null) {
                GlStateManager.deleteTextures(dfbColorTextures);
                Shaders.fillIntBufferZero(dfbColorTextures);
                Shaders.checkGLError("del dfbTextures");
            }
            if (sfbDepthTextures != null) {
                GlStateManager.deleteTextures(sfbDepthTextures);
                Shaders.fillIntBufferZero(sfbDepthTextures);
                Shaders.checkGLError("del shadow depth");
            }
            if (sfbColorTextures != null) {
                GlStateManager.deleteTextures(sfbColorTextures);
                Shaders.fillIntBufferZero(sfbColorTextures);
                Shaders.checkGLError("del shadow color");
            }
            if (dfbDrawBuffers != null) {
                Shaders.fillIntBufferZero(dfbDrawBuffers);
            }
            if (noiseTexture != null) {
                noiseTexture.destroy();
                noiseTexture = null;
            }
            SMCLog.info("Uninit");
            shadowPassInterval = 0;
            shouldSkipDefaultShadow = false;
            isShaderPackInitialized = false;
            Shaders.checkGLError("Shaders.uninit");
        }
    }

    public static void scheduleResize() {
        renderDisplayHeight = 0;
    }

    public static void scheduleResizeShadow() {
        needResizeShadow = true;
    }

    private static void resize() {
        renderDisplayWidth = Shaders.mc.displayWidth;
        renderDisplayHeight = Shaders.mc.displayHeight;
        renderWidth = Math.round((float)renderDisplayWidth * configRenderResMul);
        renderHeight = Math.round((float)renderDisplayHeight * configRenderResMul);
        Shaders.setupFrameBuffer();
    }

    private static void resizeShadow() {
        needResizeShadow = false;
        shadowMapWidth = Math.round((float)spShadowMapWidth * configShadowResMul);
        shadowMapHeight = Math.round((float)spShadowMapHeight * configShadowResMul);
        Shaders.setupShadowFrameBuffer();
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
        for (int i2 = 0; i2 < usedDepthBuffers; ++i2) {
            GlStateManager.bindTexture(dfbDepthTextures.get(i2));
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
        Shaders.checkGLError("FT d");
        for (int k2 = 0; k2 < usedColorBuffers; ++k2) {
            GlStateManager.bindTexture(dfbColorTexturesA[k2]);
            GL11.glTexParameteri(3553, 10242, 10496);
            GL11.glTexParameteri(3553, 10243, 10496);
            GL11.glTexParameteri(3553, 10241, 9729);
            GL11.glTexParameteri(3553, 10240, 9729);
            GL11.glTexImage2D(3553, 0, gbuffersFormat[k2], renderWidth, renderHeight, 0, 32993, 33639, (ByteBuffer)null);
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + k2, 3553, dfbColorTexturesA[k2], 0);
            Shaders.checkGLError("FT c");
        }
        for (int l2 = 0; l2 < usedColorBuffers; ++l2) {
            GlStateManager.bindTexture(dfbColorTexturesA[8 + l2]);
            GL11.glTexParameteri(3553, 10242, 10496);
            GL11.glTexParameteri(3553, 10243, 10496);
            GL11.glTexParameteri(3553, 10241, 9729);
            GL11.glTexParameteri(3553, 10240, 9729);
            GL11.glTexImage2D(3553, 0, gbuffersFormat[l2], renderWidth, renderHeight, 0, 32993, 33639, (ByteBuffer)null);
            Shaders.checkGLError("FT ca");
        }
        int i1 = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
        if (i1 == 36058) {
            Shaders.printChatAndLogError("[Shaders] Error: Failed framebuffer incomplete formats");
            for (int j2 = 0; j2 < usedColorBuffers; ++j2) {
                GlStateManager.bindTexture(dfbColorTextures.get(j2));
                GL11.glTexImage2D(3553, 0, 6408, renderWidth, renderHeight, 0, 32993, 33639, (ByteBuffer)null);
                EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + j2, 3553, dfbColorTextures.get(j2), 0);
                Shaders.checkGLError("FT c");
            }
            i1 = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
            if (i1 == 36053) {
                SMCLog.info("complete");
            }
        }
        GlStateManager.bindTexture(0);
        if (i1 != 36053) {
            Shaders.printChatAndLogError("[Shaders] Error: Failed creating framebuffer! (Status " + i1 + ")");
        } else {
            SMCLog.info("Framebuffer created.");
        }
    }

    private static void setupShadowFrameBuffer() {
        if (usedShadowDepthBuffers != 0) {
            int l2;
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
            for (int i2 = 0; i2 < usedShadowDepthBuffers; ++i2) {
                GlStateManager.bindTexture(sfbDepthTextures.get(i2));
                GL11.glTexParameterf(3553, 10242, 10496.0f);
                GL11.glTexParameterf(3553, 10243, 10496.0f);
                int j2 = shadowFilterNearest[i2] ? 9728 : 9729;
                GL11.glTexParameteri(3553, 10241, j2);
                GL11.glTexParameteri(3553, 10240, j2);
                if (shadowHardwareFilteringEnabled[i2]) {
                    GL11.glTexParameteri(3553, 34892, 34894);
                }
                GL11.glTexImage2D(3553, 0, 6402, shadowMapWidth, shadowMapHeight, 0, 6402, 5126, (FloatBuffer)null);
            }
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, sfbDepthTextures.get(0), 0);
            Shaders.checkGLError("FT sd");
            for (int k2 = 0; k2 < usedShadowColorBuffers; ++k2) {
                GlStateManager.bindTexture(sfbColorTextures.get(k2));
                GL11.glTexParameterf(3553, 10242, 10496.0f);
                GL11.glTexParameterf(3553, 10243, 10496.0f);
                int i1 = shadowColorFilterNearest[k2] ? 9728 : 9729;
                GL11.glTexParameteri(3553, 10241, i1);
                GL11.glTexParameteri(3553, 10240, i1);
                GL11.glTexImage2D(3553, 0, 6408, shadowMapWidth, shadowMapHeight, 0, 32993, 33639, (ByteBuffer)null);
                EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + k2, 3553, sfbColorTextures.get(k2), 0);
                Shaders.checkGLError("FT sc");
            }
            GlStateManager.bindTexture(0);
            if (usedShadowColorBuffers > 0) {
                GL20.glDrawBuffers(sfbDrawBuffers);
            }
            if ((l2 = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160)) != 36053) {
                Shaders.printChatAndLogError("[Shaders] Error: Failed creating shadow framebuffer! (Status " + l2 + ")");
            } else {
                SMCLog.info("Shadow framebuffer created.");
            }
        }
    }

    public static void beginRender(Minecraft minecraft, float partialTicks, long finishTimeNano) {
        block21: {
            Shaders.checkGLError("pre beginRender");
            Shaders.checkWorldChanged(Shaders.mc.theWorld);
            mc = minecraft;
            Shaders.mc.mcProfiler.startSection("init");
            entityRenderer = Shaders.mc.entityRenderer;
            if (!isShaderPackInitialized) {
                try {
                    Shaders.init();
                }
                catch (IllegalStateException illegalstateexception) {
                    if (!Config.normalize(illegalstateexception.getMessage()).equals("Function is not supported")) break block21;
                    Shaders.printChatAndLogError("[Shaders] Error: " + illegalstateexception.getMessage());
                    illegalstateexception.printStackTrace();
                    Shaders.setShaderPack(packNameNone);
                    return;
                }
            }
        }
        if (Shaders.mc.displayWidth != renderDisplayWidth || Shaders.mc.displayHeight != renderDisplayHeight) {
            Shaders.resize();
        }
        if (needResizeShadow) {
            Shaders.resizeShadow();
        }
        if ((diffWorldTime = ((worldTime = Shaders.mc.theWorld.getWorldTime()) - lastWorldTime) % 24000L) < 0L) {
            diffWorldTime += 24000L;
        }
        lastWorldTime = worldTime;
        moonPhase = Shaders.mc.theWorld.getMoonPhase();
        if (++frameCounter >= 720720) {
            frameCounter = 0;
        }
        systemTime = System.currentTimeMillis();
        if (lastSystemTime == 0L) {
            lastSystemTime = systemTime;
        }
        diffSystemTime = systemTime - lastSystemTime;
        lastSystemTime = systemTime;
        frameTime = (float)diffSystemTime / 1000.0f;
        frameTimeCounter += frameTime;
        frameTimeCounter %= 3600.0f;
        rainStrength = minecraft.theWorld.getRainStrength(partialTicks);
        float f2 = (float)diffSystemTime * 0.01f;
        float f1 = (float)Math.exp(Math.log(0.5) * (double)f2 / (double)(wetness < rainStrength ? drynessHalfLife : wetnessHalfLife));
        wetness = wetness * f1 + rainStrength * (1.0f - f1);
        Entity entity = mc.getRenderViewEntity();
        if (entity != null) {
            isSleeping = entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPlayerSleeping();
            eyePosY = (float)entity.posY * partialTicks + (float)entity.lastTickPosY * (1.0f - partialTicks);
            eyeBrightness = entity.getBrightnessForRender(partialTicks);
            f1 = (float)diffSystemTime * 0.01f;
            float f22 = (float)Math.exp(Math.log(0.5) * (double)f1 / (double)eyeBrightnessHalflife);
            eyeBrightnessFadeX = eyeBrightnessFadeX * f22 + (float)(eyeBrightness & 0xFFFF) * (1.0f - f22);
            eyeBrightnessFadeY = eyeBrightnessFadeY * f22 + (float)(eyeBrightness >> 16) * (1.0f - f22);
            int n2 = isEyeInWater = Shaders.mc.gameSettings.thirdPersonView == 0 && !isSleeping && Shaders.mc.thePlayer.isInsideOfMaterial(Material.water) ? 1 : 0;
            if (Shaders.mc.thePlayer != null) {
                nightVision = 0.0f;
                if (Shaders.mc.thePlayer.isPotionActive(Potion.nightVision)) {
                    nightVision = Config.getMinecraft().entityRenderer.getNightVisionBrightness(Shaders.mc.thePlayer, partialTicks);
                }
                blindness = 0.0f;
                if (Shaders.mc.thePlayer.isPotionActive(Potion.blindness)) {
                    int i2 = Shaders.mc.thePlayer.getActivePotionEffect(Potion.blindness).getDuration();
                    blindness = Config.limit((float)i2 / 20.0f, 0.0f, 1.0f);
                }
            }
            Vec3 vec3 = Shaders.mc.theWorld.getSkyColor(entity, partialTicks);
            vec3 = CustomColors.getWorldSkyColor(vec3, currentWorld, entity, partialTicks);
            skyColorR = (float)vec3.xCoord;
            skyColorG = (float)vec3.yCoord;
            skyColorB = (float)vec3.zCoord;
        }
        isRenderingWorld = true;
        isCompositeRendered = false;
        isHandRenderedMain = false;
        if (usedShadowDepthBuffers >= 1) {
            GlStateManager.setActiveTexture(33988);
            GlStateManager.bindTexture(sfbDepthTextures.get(0));
            if (usedShadowDepthBuffers >= 2) {
                GlStateManager.setActiveTexture(33989);
                GlStateManager.bindTexture(sfbDepthTextures.get(1));
            }
        }
        GlStateManager.setActiveTexture(33984);
        for (int j2 = 0; j2 < usedColorBuffers; ++j2) {
            GlStateManager.bindTexture(dfbColorTexturesA[j2]);
            GL11.glTexParameteri(3553, 10240, 9729);
            GL11.glTexParameteri(3553, 10241, 9729);
            GlStateManager.bindTexture(dfbColorTexturesA[8 + j2]);
            GL11.glTexParameteri(3553, 10240, 9729);
            GL11.glTexParameteri(3553, 10241, 9729);
        }
        GlStateManager.bindTexture(0);
        for (int k2 = 0; k2 < 4 && 4 + k2 < usedColorBuffers; ++k2) {
            GlStateManager.setActiveTexture(33991 + k2);
            GlStateManager.bindTexture(dfbColorTextures.get(4 + k2));
        }
        GlStateManager.setActiveTexture(33990);
        GlStateManager.bindTexture(dfbDepthTextures.get(0));
        if (usedDepthBuffers >= 2) {
            GlStateManager.setActiveTexture(33995);
            GlStateManager.bindTexture(dfbDepthTextures.get(1));
            if (usedDepthBuffers >= 3) {
                GlStateManager.setActiveTexture(33996);
                GlStateManager.bindTexture(dfbDepthTextures.get(2));
            }
        }
        for (int l2 = 0; l2 < usedShadowColorBuffers; ++l2) {
            GlStateManager.setActiveTexture(33997 + l2);
            GlStateManager.bindTexture(sfbColorTextures.get(l2));
        }
        if (noiseTextureEnabled) {
            GlStateManager.setActiveTexture(33984 + Shaders.noiseTexture.textureUnit);
            GlStateManager.bindTexture(noiseTexture.getID());
            GL11.glTexParameteri(3553, 10242, 10497);
            GL11.glTexParameteri(3553, 10243, 10497);
            GL11.glTexParameteri(3553, 10240, 9729);
            GL11.glTexParameteri(3553, 10241, 9729);
        }
        Shaders.bindCustomTextures(customTexturesGbuffers);
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
        Shaders.checkGLError("beginRender");
        ShadersRender.renderShadowMap(entityRenderer, 0, partialTicks, finishTimeNano);
        Shaders.mc.mcProfiler.endSection();
        EXTFramebufferObject.glBindFramebufferEXT(36160, dfb);
        for (int i1 = 0; i1 < usedColorBuffers; ++i1) {
            Shaders.colorTexturesToggle[i1] = 0;
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + i1, 3553, dfbColorTexturesA[i1], 0);
        }
        Shaders.checkGLError("end beginRender");
    }

    private static void checkWorldChanged(World worldd) {
        if (currentWorld != worldd) {
            World world = currentWorld;
            currentWorld = worldd;
            if (world != null && worldd != null) {
                int i2 = world.provider.getDimensionId();
                int j2 = worldd.provider.getDimensionId();
                boolean flag = shaderPackDimensions.contains(i2);
                boolean flag1 = shaderPackDimensions.contains(j2);
                if (flag || flag1) {
                    Shaders.uninit();
                }
            }
        }
    }

    public static void beginRenderPass(int pass, float partialTicks, long finishTimeNano) {
        if (!isShadowPass) {
            EXTFramebufferObject.glBindFramebufferEXT(36160, dfb);
            GL11.glViewport(0, 0, renderWidth, renderHeight);
            activeDrawBuffers = null;
            ShadersTex.bindNSTextures(defaultTexture.getMultiTexID());
            Shaders.useProgram(2);
            Shaders.checkGLError("end beginRenderPass");
        }
    }

    public static void setViewport(int vx2, int vy2, int vw2, int vh2) {
        GlStateManager.colorMask(true, true, true, true);
        if (isShadowPass) {
            GL11.glViewport(0, 0, shadowMapWidth, shadowMapHeight);
        } else {
            GL11.glViewport(0, 0, renderWidth, renderHeight);
            EXTFramebufferObject.glBindFramebufferEXT(36160, dfb);
            isRenderingDfb = true;
            GlStateManager.enableCull();
            GlStateManager.enableDepth();
            Shaders.setDrawBuffers(drawBuffersNone);
            Shaders.useProgram(2);
            Shaders.checkGLError("beginRenderPass");
        }
    }

    public static int setFogMode(int val) {
        fogMode = val;
        return val;
    }

    public static void setFogColor(float r2, float g2, float b2) {
        fogColorR = r2;
        fogColorG = g2;
        fogColorB = b2;
    }

    public static void setClearColor(float red, float green, float blue, float alpha) {
        GlStateManager.clearColor(red, green, blue, alpha);
        clearColorR = red;
        clearColorG = green;
        clearColorB = blue;
    }

    public static void clearRenderBuffer() {
        if (isShadowPass) {
            Shaders.checkGLError("shadow clear pre");
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, sfbDepthTextures.get(0), 0);
            GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            GL20.glDrawBuffers(programsDrawBuffers[30]);
            Shaders.checkFramebufferStatus("shadow clear");
            GL11.glClear(16640);
            Shaders.checkGLError("shadow clear");
        } else {
            Shaders.checkGLError("clear pre");
            if (gbuffersClear[0]) {
                GL20.glDrawBuffers(36064);
                GL11.glClear(16384);
            }
            if (gbuffersClear[1]) {
                GL20.glDrawBuffers(36065);
                GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glClear(16384);
            }
            for (int i2 = 2; i2 < usedColorBuffers; ++i2) {
                if (!gbuffersClear[i2]) continue;
                GL20.glDrawBuffers(36064 + i2);
                GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                GL11.glClear(16384);
            }
            Shaders.setDrawBuffers(dfbDrawBuffers);
            Shaders.checkFramebufferStatus("clear");
            Shaders.checkGLError("clear");
        }
    }

    public static void setCamera(float partialTicks) {
        Entity entity = mc.getRenderViewEntity();
        double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
        double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
        double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
        cameraPositionX = d0;
        cameraPositionY = d1;
        cameraPositionZ = d2;
        GL11.glGetFloat(2983, (FloatBuffer)projection.position(0));
        SMath.invertMat4FBFA((FloatBuffer)projectionInverse.position(0), (FloatBuffer)projection.position(0), faProjectionInverse, faProjection);
        projection.position(0);
        projectionInverse.position(0);
        GL11.glGetFloat(2982, (FloatBuffer)modelView.position(0));
        SMath.invertMat4FBFA((FloatBuffer)modelViewInverse.position(0), (FloatBuffer)modelView.position(0), faModelViewInverse, faModelView);
        modelView.position(0);
        modelViewInverse.position(0);
        Shaders.checkGLError("setCamera");
    }

    public static void setCameraShadow(float partialTicks) {
        float f1;
        Entity entity = mc.getRenderViewEntity();
        double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
        double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
        double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
        cameraPositionX = d0;
        cameraPositionY = d1;
        cameraPositionZ = d2;
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
            GL11.glOrtho(-shadowMapHalfPlane, shadowMapHalfPlane, -shadowMapHalfPlane, shadowMapHalfPlane, 0.05f, 256.0);
        } else {
            GLU.gluPerspective(shadowMapFOV, (float)shadowMapWidth / (float)shadowMapHeight, 0.05f, 256.0f);
        }
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0f, 0.0f, -100.0f);
        GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
        celestialAngle = Shaders.mc.theWorld.getCelestialAngle(partialTicks);
        sunAngle = celestialAngle < 0.75f ? celestialAngle + 0.25f : celestialAngle - 0.75f;
        float f2 = celestialAngle * -360.0f;
        float f3 = f1 = shadowAngleInterval > 0.0f ? f2 % shadowAngleInterval - shadowAngleInterval * 0.5f : 0.0f;
        if ((double)sunAngle <= 0.5) {
            GL11.glRotatef(f2 - f1, 0.0f, 0.0f, 1.0f);
            GL11.glRotatef(sunPathRotation, 1.0f, 0.0f, 0.0f);
            shadowAngle = sunAngle;
        } else {
            GL11.glRotatef(f2 + 180.0f - f1, 0.0f, 0.0f, 1.0f);
            GL11.glRotatef(sunPathRotation, 1.0f, 0.0f, 0.0f);
            shadowAngle = sunAngle - 0.5f;
        }
        if (shadowMapIsOrtho) {
            float f22 = shadowIntervalSize;
            float f32 = f22 / 2.0f;
            GL11.glTranslatef((float)d0 % f22 - f32, (float)d1 % f22 - f32, (float)d2 % f22 - f32);
        }
        float f9 = sunAngle * ((float)Math.PI * 2);
        float f10 = (float)Math.cos(f9);
        float f4 = (float)Math.sin(f9);
        float f5 = sunPathRotation * ((float)Math.PI * 2);
        float f6 = f10;
        float f7 = f4 * (float)Math.cos(f5);
        float f8 = f4 * (float)Math.sin(f5);
        if ((double)sunAngle > 0.5) {
            f6 = -f10;
            f7 = -f7;
            f8 = -f8;
        }
        Shaders.shadowLightPositionVector[0] = f6;
        Shaders.shadowLightPositionVector[1] = f7;
        Shaders.shadowLightPositionVector[2] = f8;
        Shaders.shadowLightPositionVector[3] = 0.0f;
        GL11.glGetFloat(2983, (FloatBuffer)shadowProjection.position(0));
        SMath.invertMat4FBFA((FloatBuffer)shadowProjectionInverse.position(0), (FloatBuffer)shadowProjection.position(0), faShadowProjectionInverse, faShadowProjection);
        shadowProjection.position(0);
        shadowProjectionInverse.position(0);
        GL11.glGetFloat(2982, (FloatBuffer)shadowModelView.position(0));
        SMath.invertMat4FBFA((FloatBuffer)shadowModelViewInverse.position(0), (FloatBuffer)shadowModelView.position(0), faShadowModelViewInverse, faShadowModelView);
        shadowModelView.position(0);
        shadowModelViewInverse.position(0);
        Shaders.setProgramUniformMatrix4ARB("gbufferProjection", false, projection);
        Shaders.setProgramUniformMatrix4ARB("gbufferProjectionInverse", false, projectionInverse);
        Shaders.setProgramUniformMatrix4ARB("gbufferPreviousProjection", false, previousProjection);
        Shaders.setProgramUniformMatrix4ARB("gbufferModelView", false, modelView);
        Shaders.setProgramUniformMatrix4ARB("gbufferModelViewInverse", false, modelViewInverse);
        Shaders.setProgramUniformMatrix4ARB("gbufferPreviousModelView", false, previousModelView);
        Shaders.setProgramUniformMatrix4ARB("shadowProjection", false, shadowProjection);
        Shaders.setProgramUniformMatrix4ARB("shadowProjectionInverse", false, shadowProjectionInverse);
        Shaders.setProgramUniformMatrix4ARB("shadowModelView", false, shadowModelView);
        Shaders.setProgramUniformMatrix4ARB("shadowModelViewInverse", false, shadowModelViewInverse);
        Shaders.mc.gameSettings.thirdPersonView = 1;
        Shaders.checkGLError("setCamera");
    }

    public static void preCelestialRotate() {
        GL11.glRotatef(sunPathRotation * 1.0f, 0.0f, 0.0f, 1.0f);
        Shaders.checkGLError("preCelestialRotate");
    }

    public static void postCelestialRotate() {
        FloatBuffer floatbuffer = tempMatrixDirectBuffer;
        floatbuffer.clear();
        GL11.glGetFloat(2982, floatbuffer);
        floatbuffer.get(tempMat, 0, 16);
        SMath.multiplyMat4xVec4(sunPosition, tempMat, sunPosModelView);
        SMath.multiplyMat4xVec4(moonPosition, tempMat, moonPosModelView);
        System.arraycopy(shadowAngle == sunAngle ? sunPosition : moonPosition, 0, shadowLightPosition, 0, 3);
        Shaders.setProgramUniform3f("sunPosition", sunPosition[0], sunPosition[1], sunPosition[2]);
        Shaders.setProgramUniform3f("moonPosition", moonPosition[0], moonPosition[1], moonPosition[2]);
        Shaders.setProgramUniform3f("shadowLightPosition", shadowLightPosition[0], shadowLightPosition[1], shadowLightPosition[2]);
        Shaders.checkGLError("postCelestialRotate");
    }

    public static void setUpPosition() {
        FloatBuffer floatbuffer = tempMatrixDirectBuffer;
        floatbuffer.clear();
        GL11.glGetFloat(2982, floatbuffer);
        floatbuffer.get(tempMat, 0, 16);
        SMath.multiplyMat4xVec4(upPosition, tempMat, upPosModelView);
        Shaders.setProgramUniform3f("upPosition", upPosition[0], upPosition[1], upPosition[2]);
    }

    public static void genCompositeMipmap() {
        if (hasGlGenMipmap) {
            for (int i2 = 0; i2 < usedColorBuffers; ++i2) {
                if ((activeCompositeMipmapSetting & 1 << i2) == 0) continue;
                GlStateManager.setActiveTexture(33984 + colorTextureTextureImageUnit[i2]);
                GL11.glTexParameteri(3553, 10241, 9987);
                GL30.glGenerateMipmap(3553);
            }
            GlStateManager.setActiveTexture(33984);
        }
    }

    public static void drawComposite() {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(0.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(1.0f, 0.0f, 0.0f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(1.0f, 1.0f, 0.0f);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(0.0f, 1.0f, 0.0f);
        GL11.glEnd();
    }

    public static void renderCompositeFinal() {
        if (!isShadowPass) {
            Shaders.checkGLError("pre-renderCompositeFinal");
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glMatrixMode(5889);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glOrtho(0.0, 1.0, 0.0, 1.0, 0.0, 1.0);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.enableTexture2D();
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.enableDepth();
            GlStateManager.depthFunc(519);
            GlStateManager.depthMask(false);
            GlStateManager.disableLighting();
            if (usedShadowDepthBuffers >= 1) {
                GlStateManager.setActiveTexture(33988);
                GlStateManager.bindTexture(sfbDepthTextures.get(0));
                if (usedShadowDepthBuffers >= 2) {
                    GlStateManager.setActiveTexture(33989);
                    GlStateManager.bindTexture(sfbDepthTextures.get(1));
                }
            }
            for (int i2 = 0; i2 < usedColorBuffers; ++i2) {
                GlStateManager.setActiveTexture(33984 + colorTextureTextureImageUnit[i2]);
                GlStateManager.bindTexture(dfbColorTexturesA[i2]);
            }
            GlStateManager.setActiveTexture(33990);
            GlStateManager.bindTexture(dfbDepthTextures.get(0));
            if (usedDepthBuffers >= 2) {
                GlStateManager.setActiveTexture(33995);
                GlStateManager.bindTexture(dfbDepthTextures.get(1));
                if (usedDepthBuffers >= 3) {
                    GlStateManager.setActiveTexture(33996);
                    GlStateManager.bindTexture(dfbDepthTextures.get(2));
                }
            }
            for (int j1 = 0; j1 < usedShadowColorBuffers; ++j1) {
                GlStateManager.setActiveTexture(33997 + j1);
                GlStateManager.bindTexture(sfbColorTextures.get(j1));
            }
            if (noiseTextureEnabled) {
                GlStateManager.setActiveTexture(33984 + Shaders.noiseTexture.textureUnit);
                GlStateManager.bindTexture(noiseTexture.getID());
                GL11.glTexParameteri(3553, 10242, 10497);
                GL11.glTexParameteri(3553, 10243, 10497);
                GL11.glTexParameteri(3553, 10240, 9729);
                GL11.glTexParameteri(3553, 10241, 9729);
            }
            Shaders.bindCustomTextures(customTexturesComposite);
            GlStateManager.setActiveTexture(33984);
            boolean flag = true;
            for (int j2 = 0; j2 < usedColorBuffers; ++j2) {
                EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + j2, 3553, dfbColorTexturesA[8 + j2], 0);
            }
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, dfbDepthTextures.get(0), 0);
            GL20.glDrawBuffers(dfbDrawBuffers);
            Shaders.checkGLError("pre-composite");
            for (int k1 = 0; k1 < 8; ++k1) {
                if (programsID[21 + k1] == 0) continue;
                Shaders.useProgram(21 + k1);
                Shaders.checkGLError(programNames[21 + k1]);
                if (activeCompositeMipmapSetting != 0) {
                    Shaders.genCompositeMipmap();
                }
                Shaders.drawComposite();
                for (int k2 = 0; k2 < usedColorBuffers; ++k2) {
                    if (!programsToggleColorTextures[21 + k1][k2]) continue;
                    int l2 = colorTexturesToggle[k2];
                    int i1 = Shaders.colorTexturesToggle[k2] = 8 - l2;
                    GlStateManager.setActiveTexture(33984 + colorTextureTextureImageUnit[k2]);
                    GlStateManager.bindTexture(dfbColorTexturesA[i1 + k2]);
                    EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + k2, 3553, dfbColorTexturesA[l2 + k2], 0);
                }
                GlStateManager.setActiveTexture(33984);
            }
            Shaders.checkGLError("composite");
            isRenderingDfb = false;
            mc.getFramebuffer().bindFramebuffer(true);
            OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0, 3553, Shaders.mc.getFramebuffer().framebufferTexture, 0);
            GL11.glViewport(0, 0, Shaders.mc.displayWidth, Shaders.mc.displayHeight);
            if (EntityRenderer.anaglyphEnable) {
                boolean flag1 = EntityRenderer.anaglyphField != 0;
                GlStateManager.colorMask(flag1, !flag1, !flag1, true);
            }
            GlStateManager.depthMask(true);
            GL11.glClearColor(clearColorR, clearColorG, clearColorB, 1.0f);
            GL11.glClear(16640);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.enableTexture2D();
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.enableDepth();
            GlStateManager.depthFunc(519);
            GlStateManager.depthMask(false);
            Shaders.checkGLError("pre-final");
            Shaders.useProgram(29);
            Shaders.checkGLError("final");
            if (activeCompositeMipmapSetting != 0) {
                Shaders.genCompositeMipmap();
            }
            Shaders.drawComposite();
            Shaders.checkGLError("renderCompositeFinal");
            isCompositeRendered = true;
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.depthFunc(515);
            GlStateManager.depthMask(true);
            GL11.glPopMatrix();
            GL11.glMatrixMode(5888);
            GL11.glPopMatrix();
            Shaders.useProgram(0);
        }
    }

    public static void endRender() {
        if (isShadowPass) {
            Shaders.checkGLError("shadow endRender");
        } else {
            if (!isCompositeRendered) {
                Shaders.renderCompositeFinal();
            }
            isRenderingWorld = false;
            GlStateManager.colorMask(true, true, true, true);
            Shaders.useProgram(0);
            RenderHelper.disableStandardItemLighting();
            Shaders.checkGLError("endRender end");
        }
    }

    public static void beginSky() {
        isRenderingSky = true;
        fogEnabled = true;
        Shaders.setDrawBuffers(dfbDrawBuffers);
        Shaders.useProgram(5);
        Shaders.pushEntity(-2, 0);
    }

    public static void setSkyColor(Vec3 v3color) {
        skyColorR = (float)v3color.xCoord;
        skyColorG = (float)v3color.yCoord;
        skyColorB = (float)v3color.zCoord;
        Shaders.setProgramUniform3f("skyColor", skyColorR, skyColorG, skyColorB);
    }

    public static void drawHorizon() {
        WorldRenderer worldrenderer = Tessellator.getInstance().getWorldRenderer();
        float f2 = Shaders.mc.gameSettings.renderDistanceChunks * 16;
        double d0 = (double)f2 * 0.9238;
        double d1 = (double)f2 * 0.3826;
        double d2 = -d1;
        double d3 = -d0;
        double d4 = 16.0;
        double d5 = -cameraPositionY;
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(d2, d5, d3).endVertex();
        worldrenderer.pos(d2, d4, d3).endVertex();
        worldrenderer.pos(d3, d4, d2).endVertex();
        worldrenderer.pos(d3, d5, d2).endVertex();
        worldrenderer.pos(d3, d5, d2).endVertex();
        worldrenderer.pos(d3, d4, d2).endVertex();
        worldrenderer.pos(d3, d4, d1).endVertex();
        worldrenderer.pos(d3, d5, d1).endVertex();
        worldrenderer.pos(d3, d5, d1).endVertex();
        worldrenderer.pos(d3, d4, d1).endVertex();
        worldrenderer.pos(d2, d4, d1).endVertex();
        worldrenderer.pos(d2, d5, d1).endVertex();
        worldrenderer.pos(d2, d5, d1).endVertex();
        worldrenderer.pos(d2, d4, d1).endVertex();
        worldrenderer.pos(d1, d4, d0).endVertex();
        worldrenderer.pos(d1, d5, d0).endVertex();
        worldrenderer.pos(d1, d5, d0).endVertex();
        worldrenderer.pos(d1, d4, d0).endVertex();
        worldrenderer.pos(d0, d4, d1).endVertex();
        worldrenderer.pos(d0, d5, d1).endVertex();
        worldrenderer.pos(d0, d5, d1).endVertex();
        worldrenderer.pos(d0, d4, d1).endVertex();
        worldrenderer.pos(d0, d4, d2).endVertex();
        worldrenderer.pos(d0, d5, d2).endVertex();
        worldrenderer.pos(d0, d5, d2).endVertex();
        worldrenderer.pos(d0, d4, d2).endVertex();
        worldrenderer.pos(d1, d4, d3).endVertex();
        worldrenderer.pos(d1, d5, d3).endVertex();
        worldrenderer.pos(d1, d5, d3).endVertex();
        worldrenderer.pos(d1, d4, d3).endVertex();
        worldrenderer.pos(d2, d4, d3).endVertex();
        worldrenderer.pos(d2, d5, d3).endVertex();
        Tessellator.getInstance().draw();
    }

    public static void preSkyList() {
        Shaders.setUpPosition();
        GL11.glColor3f(fogColorR, fogColorG, fogColorB);
        Shaders.drawHorizon();
        GL11.glColor3f(skyColorR, skyColorG, skyColorB);
    }

    public static void endSky() {
        isRenderingSky = false;
        Shaders.setDrawBuffers(dfbDrawBuffers);
        Shaders.useProgram(lightmapEnabled ? 3 : 2);
        Shaders.popEntity();
    }

    public static void beginUpdateChunks() {
        Shaders.checkGLError("beginUpdateChunks1");
        Shaders.checkFramebufferStatus("beginUpdateChunks1");
        if (!isShadowPass) {
            Shaders.useProgram(7);
        }
        Shaders.checkGLError("beginUpdateChunks2");
        Shaders.checkFramebufferStatus("beginUpdateChunks2");
    }

    public static void endUpdateChunks() {
        Shaders.checkGLError("endUpdateChunks1");
        Shaders.checkFramebufferStatus("endUpdateChunks1");
        if (!isShadowPass) {
            Shaders.useProgram(7);
        }
        Shaders.checkGLError("endUpdateChunks2");
        Shaders.checkFramebufferStatus("endUpdateChunks2");
    }

    public static boolean shouldRenderClouds(GameSettings gs2) {
        if (!shaderPackLoaded) {
            return true;
        }
        Shaders.checkGLError("shouldRenderClouds");
        return isShadowPass ? configCloudShadow : gs2.clouds > 0;
    }

    public static void beginClouds() {
        fogEnabled = true;
        Shaders.pushEntity(-3, 0);
        Shaders.useProgram(6);
    }

    public static void endClouds() {
        Shaders.disableFog();
        Shaders.popEntity();
        Shaders.useProgram(lightmapEnabled ? 3 : 2);
    }

    public static void beginEntities() {
        if (isRenderingWorld) {
            Shaders.useProgram(16);
            Shaders.resetDisplayListModels();
        }
    }

    public static void nextEntity(Entity entity) {
        if (isRenderingWorld) {
            Shaders.useProgram(16);
            Shaders.setEntityId(entity);
        }
    }

    public static void setEntityId(Entity entity) {
        if (isRenderingWorld && !isShadowPass && uniformEntityId.isDefined()) {
            int i2 = EntityList.getEntityID(entity);
            uniformEntityId.setValue(i2);
        }
    }

    public static void beginSpiderEyes() {
        if (isRenderingWorld && programsID[18] != programsID[0]) {
            Shaders.useProgram(18);
            GlStateManager.enableAlpha();
            GlStateManager.alphaFunc(516, 0.0f);
            GlStateManager.blendFunc(770, 771);
        }
    }

    public static void endEntities() {
        if (isRenderingWorld) {
            Shaders.useProgram(lightmapEnabled ? 3 : 2);
        }
    }

    public static void setEntityColor(float r2, float g2, float b2, float a2) {
        if (isRenderingWorld && !isShadowPass) {
            uniformEntityColor.setValue(r2, g2, b2, a2);
        }
    }

    public static void beginLivingDamage() {
        if (isRenderingWorld) {
            ShadersTex.bindTexture(defaultTexture);
            if (!isShadowPass) {
                Shaders.setDrawBuffers(drawBuffersColorAtt0);
            }
        }
    }

    public static void endLivingDamage() {
        if (isRenderingWorld && !isShadowPass) {
            Shaders.setDrawBuffers(programsDrawBuffers[16]);
        }
    }

    public static void beginBlockEntities() {
        if (isRenderingWorld) {
            Shaders.checkGLError("beginBlockEntities");
            Shaders.useProgram(13);
        }
    }

    public static void nextBlockEntity(TileEntity tileEntity) {
        if (isRenderingWorld) {
            Shaders.checkGLError("nextBlockEntity");
            Shaders.useProgram(13);
            Shaders.setBlockEntityId(tileEntity);
        }
    }

    public static void setBlockEntityId(TileEntity tileEntity) {
        if (isRenderingWorld && !isShadowPass && uniformBlockEntityId.isDefined()) {
            Block block = tileEntity.getBlockType();
            int i2 = Block.getIdFromBlock(block);
            uniformBlockEntityId.setValue(i2);
        }
    }

    public static void endBlockEntities() {
        if (isRenderingWorld) {
            Shaders.checkGLError("endBlockEntities");
            Shaders.useProgram(lightmapEnabled ? 3 : 2);
            ShadersTex.bindNSTextures(defaultTexture.getMultiTexID());
        }
    }

    public static void beginLitParticles() {
        Shaders.useProgram(3);
    }

    public static void beginParticles() {
        Shaders.useProgram(2);
    }

    public static void endParticles() {
        Shaders.useProgram(3);
    }

    public static void readCenterDepth() {
        if (!isShadowPass && centerDepthSmoothEnabled) {
            tempDirectFloatBuffer.clear();
            GL11.glReadPixels(renderWidth / 2, renderHeight / 2, 1, 1, 6402, 5126, tempDirectFloatBuffer);
            centerDepth = tempDirectFloatBuffer.get(0);
            float f2 = (float)diffSystemTime * 0.01f;
            float f1 = (float)Math.exp(Math.log(0.5) * (double)f2 / (double)centerDepthSmoothHalflife);
            centerDepthSmooth = centerDepthSmooth * f1 + centerDepth * (1.0f - f1);
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
            Shaders.useProgram(20);
        }
    }

    public static void endWeather() {
        GlStateManager.disableBlend();
        Shaders.useProgram(3);
    }

    public static void preWater() {
        if (usedDepthBuffers >= 2) {
            GlStateManager.setActiveTexture(33995);
            Shaders.checkGLError("pre copy depth");
            GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, renderWidth, renderHeight);
            Shaders.checkGLError("copy depth");
            GlStateManager.setActiveTexture(33984);
        }
        ShadersTex.bindNSTextures(defaultTexture.getMultiTexID());
    }

    public static void beginWater() {
        if (isRenderingWorld) {
            if (!isShadowPass) {
                Shaders.useProgram(12);
                GlStateManager.enableBlend();
                GlStateManager.depthMask(true);
            } else {
                GlStateManager.depthMask(true);
            }
        }
    }

    public static void endWater() {
        if (isRenderingWorld) {
            if (isShadowPass) {
                // empty if block
            }
            Shaders.useProgram(lightmapEnabled ? 3 : 2);
        }
    }

    public static void beginProjectRedHalo() {
        if (isRenderingWorld) {
            Shaders.useProgram(1);
        }
    }

    public static void endProjectRedHalo() {
        if (isRenderingWorld) {
            Shaders.useProgram(3);
        }
    }

    public static void applyHandDepth() {
        if ((double)configHandDepthMul != 1.0) {
            GL11.glScaled(1.0, 1.0, configHandDepthMul);
        }
    }

    public static void beginHand() {
        GL11.glMatrixMode(5888);
        GL11.glPushMatrix();
        GL11.glMatrixMode(5889);
        GL11.glPushMatrix();
        GL11.glMatrixMode(5888);
        Shaders.useProgram(19);
        Shaders.checkGLError("beginHand");
        Shaders.checkFramebufferStatus("beginHand");
    }

    public static void endHand() {
        Shaders.checkGLError("pre endHand");
        Shaders.checkFramebufferStatus("pre endHand");
        GL11.glMatrixMode(5889);
        GL11.glPopMatrix();
        GL11.glMatrixMode(5888);
        GL11.glPopMatrix();
        GlStateManager.blendFunc(770, 771);
        Shaders.checkGLError("endHand");
    }

    public static void beginFPOverlay() {
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
    }

    public static void endFPOverlay() {
    }

    public static void glEnableWrapper(int cap) {
        GL11.glEnable(cap);
        if (cap == 3553) {
            Shaders.enableTexture2D();
        } else if (cap == 2912) {
            Shaders.enableFog();
        }
    }

    public static void glDisableWrapper(int cap) {
        GL11.glDisable(cap);
        if (cap == 3553) {
            Shaders.disableTexture2D();
        } else if (cap == 2912) {
            Shaders.disableFog();
        }
    }

    public static void sglEnableT2D(int cap) {
        GL11.glEnable(cap);
        Shaders.enableTexture2D();
    }

    public static void sglDisableT2D(int cap) {
        GL11.glDisable(cap);
        Shaders.disableTexture2D();
    }

    public static void sglEnableFog(int cap) {
        GL11.glEnable(cap);
        Shaders.enableFog();
    }

    public static void sglDisableFog(int cap) {
        GL11.glDisable(cap);
        Shaders.disableFog();
    }

    public static void enableTexture2D() {
        if (isRenderingSky) {
            Shaders.useProgram(5);
        } else if (activeProgram == 1) {
            Shaders.useProgram(lightmapEnabled ? 3 : 2);
        }
    }

    public static void disableTexture2D() {
        if (isRenderingSky) {
            Shaders.useProgram(4);
        } else if (activeProgram == 2 || activeProgram == 3) {
            Shaders.useProgram(1);
        }
    }

    public static void beginLeash() {
        Shaders.useProgram(1);
    }

    public static void endLeash() {
        Shaders.useProgram(16);
    }

    public static void enableFog() {
        fogEnabled = true;
        Shaders.setProgramUniform1i("fogMode", fogMode);
    }

    public static void disableFog() {
        fogEnabled = false;
        Shaders.setProgramUniform1i("fogMode", 0);
    }

    public static void setFog(int fogMode) {
        GlStateManager.setFog(fogMode);
        if (fogEnabled) {
            Shaders.setProgramUniform1i("fogMode", fogMode);
        }
    }

    public static void sglFogi(int pname, int param) {
        GL11.glFogi(pname, param);
        if (pname == 2917) {
            fogMode = param;
            if (fogEnabled) {
                Shaders.setProgramUniform1i("fogMode", fogMode);
            }
        }
    }

    public static void enableLightmap() {
        lightmapEnabled = true;
        if (activeProgram == 2) {
            Shaders.useProgram(3);
        }
    }

    public static void disableLightmap() {
        lightmapEnabled = false;
        if (activeProgram == 3) {
            Shaders.useProgram(2);
        }
    }

    public static int getEntityData() {
        return entityData[entityDataIndex * 2];
    }

    public static int getEntityData2() {
        return entityData[entityDataIndex * 2 + 1];
    }

    public static int setEntityData1(int data1) {
        Shaders.entityData[Shaders.entityDataIndex * 2] = entityData[entityDataIndex * 2] & 0xFFFF | data1 << 16;
        return data1;
    }

    public static int setEntityData2(int data2) {
        Shaders.entityData[Shaders.entityDataIndex * 2 + 1] = entityData[entityDataIndex * 2 + 1] & 0xFFFF0000 | data2 & 0xFFFF;
        return data2;
    }

    public static void pushEntity(int data0, int data1) {
        Shaders.entityData[++Shaders.entityDataIndex * 2] = data0 & 0xFFFF | data1 << 16;
        Shaders.entityData[Shaders.entityDataIndex * 2 + 1] = 0;
    }

    public static void pushEntity(int data0) {
        Shaders.entityData[++Shaders.entityDataIndex * 2] = data0 & 0xFFFF;
        Shaders.entityData[Shaders.entityDataIndex * 2 + 1] = 0;
    }

    public static void pushEntity(Block block) {
        Shaders.entityData[++Shaders.entityDataIndex * 2] = Block.blockRegistry.getIDForObject(block) & 0xFFFF | block.getRenderType() << 16;
        Shaders.entityData[Shaders.entityDataIndex * 2 + 1] = 0;
    }

    public static void popEntity() {
        Shaders.entityData[Shaders.entityDataIndex * 2] = 0;
        Shaders.entityData[Shaders.entityDataIndex * 2 + 1] = 0;
        --entityDataIndex;
    }

    public static void mcProfilerEndSection() {
        Shaders.mc.mcProfiler.endSection();
    }

    public static String getShaderPackName() {
        return shaderPack == null ? null : (shaderPack instanceof ShaderPackNone ? null : shaderPack.getName());
    }

    public static InputStream getShaderPackResourceStream(String path) {
        return shaderPack == null ? null : shaderPack.getResourceAsStream(path);
    }

    public static void nextAntialiasingLevel() {
        configAntialiasingLevel += 2;
        if ((configAntialiasingLevel = configAntialiasingLevel / 2 * 2) > 4) {
            configAntialiasingLevel = 0;
        }
        configAntialiasingLevel = Config.limit(configAntialiasingLevel, 0, 4);
    }

    public static void checkShadersModInstalled() {
        try {
            Class<?> clazz = Class.forName("shadersmod.transform.SMCClassTransformer");
        }
        catch (Throwable var1) {
            return;
        }
        throw new RuntimeException("Shaders Mod detected. Please remove it, OptiFine has built-in support for shaders.");
    }

    public static void resourcesReloaded() {
        Shaders.loadShaderPackResources();
    }

    private static void loadShaderPackResources() {
        shaderPackResources = new HashMap<String, String>();
        if (shaderPackLoaded) {
            ArrayList<String> list = new ArrayList<String>();
            String s2 = "/shaders/lang/";
            String s1 = "en_US";
            String s22 = ".lang";
            list.add(s2 + s1 + s22);
            if (!Config.getGameSettings().language.equals(s1)) {
                list.add(s2 + Config.getGameSettings().language + s22);
            }
            try {
                for (String s3 : list) {
                    InputStream inputstream = shaderPack.getResourceAsStream(s3);
                    if (inputstream == null) continue;
                    Properties properties = new Properties();
                    Lang.loadLocaleData(inputstream, properties);
                    inputstream.close();
                    for (Object s4 : properties.keySet()) {
                        String s5 = properties.getProperty((String)s4);
                        shaderPackResources.put((String)s4, s5);
                    }
                }
            }
            catch (IOException ioexception) {
                ioexception.printStackTrace();
            }
        }
    }

    public static String translate(String key, String def) {
        String s2 = shaderPackResources.get(key);
        return s2 == null ? def : s2;
    }

    public static boolean isProgramPath(String program) {
        if (program == null) {
            return false;
        }
        if (program.length() <= 0) {
            return false;
        }
        int i2 = program.lastIndexOf("/");
        if (i2 >= 0) {
            program = program.substring(i2 + 1);
        }
        return Arrays.asList(programNames).contains(program);
    }

    public static void setItemToRenderMain(ItemStack itemToRenderMain) {
        itemToRenderMainTranslucent = Shaders.isTranslucentBlock(itemToRenderMain);
    }

    public static boolean isItemToRenderMainTranslucent() {
        return itemToRenderMainTranslucent;
    }

    private static boolean isTranslucentBlock(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        Item item = stack.getItem();
        if (item == null) {
            return false;
        }
        if (!(item instanceof ItemBlock)) {
            return false;
        }
        ItemBlock itemblock = (ItemBlock)item;
        Block block = itemblock.getBlock();
        if (block == null) {
            return false;
        }
        EnumWorldBlockLayer enumworldblocklayer = block.getBlockLayer();
        return enumworldblocklayer == EnumWorldBlockLayer.TRANSLUCENT;
    }

    public static boolean isRenderBothHands() {
        return false;
    }

    public static boolean isHandRenderedMain() {
        return isHandRenderedMain;
    }

    public static void setHandRenderedMain(boolean isHandRenderedMain) {
    }

    public static float getShadowRenderDistance() {
        return shadowDistanceRenderMul < 0.0f ? -1.0f : shadowMapHalfPlane * shadowDistanceRenderMul;
    }

    static {
        isInitializedOnce = false;
        isShaderPackInitialized = false;
        hasGlGenMipmap = false;
        hasForge = false;
        numberResetDisplayList = 0;
        needResetModels = false;
        renderDisplayWidth = 0;
        renderDisplayHeight = 0;
        renderWidth = 0;
        renderHeight = 0;
        isRenderingWorld = false;
        isRenderingSky = false;
        isCompositeRendered = false;
        isRenderingDfb = false;
        isShadowPass = false;
        renderItemKeepDepthMask = false;
        itemToRenderMainTranslucent = false;
        sunPosition = new float[4];
        moonPosition = new float[4];
        shadowLightPosition = new float[4];
        upPosition = new float[4];
        shadowLightPositionVector = new float[4];
        upPosModelView = new float[]{0.0f, 100.0f, 0.0f, 0.0f};
        sunPosModelView = new float[]{0.0f, 100.0f, 0.0f, 0.0f};
        moonPosModelView = new float[]{0.0f, -100.0f, 0.0f, 0.0f};
        tempMat = new float[16];
        worldTime = 0L;
        lastWorldTime = 0L;
        diffWorldTime = 0L;
        celestialAngle = 0.0f;
        sunAngle = 0.0f;
        shadowAngle = 0.0f;
        moonPhase = 0;
        systemTime = 0L;
        lastSystemTime = 0L;
        diffSystemTime = 0L;
        frameCounter = 0;
        frameTime = 0.0f;
        frameTimeCounter = 0.0f;
        systemTimeInt32 = 0;
        rainStrength = 0.0f;
        wetness = 0.0f;
        wetnessHalfLife = 600.0f;
        drynessHalfLife = 200.0f;
        eyeBrightnessHalflife = 10.0f;
        usewetness = false;
        isEyeInWater = 0;
        eyeBrightness = 0;
        eyeBrightnessFadeX = 0.0f;
        eyeBrightnessFadeY = 0.0f;
        eyePosY = 0.0f;
        centerDepth = 0.0f;
        centerDepthSmooth = 0.0f;
        centerDepthSmoothHalflife = 1.0f;
        centerDepthSmoothEnabled = false;
        superSamplingLevel = 1;
        nightVision = 0.0f;
        blindness = 0.0f;
        updateChunksErrorRecorded = false;
        lightmapEnabled = false;
        fogEnabled = true;
        entityAttrib = 10;
        midTexCoordAttrib = 11;
        tangentAttrib = 12;
        useEntityAttrib = false;
        useMidTexCoordAttrib = false;
        useMultiTexCoord3Attrib = false;
        useTangentAttrib = false;
        progUseEntityAttrib = false;
        progUseMidTexCoordAttrib = false;
        progUseTangentAttrib = false;
        atlasSizeX = 0;
        atlasSizeY = 0;
        uniformEntityColor = new ShaderUniformFloat4("entityColor");
        uniformEntityId = new ShaderUniformInt("entityId");
        uniformBlockEntityId = new ShaderUniformInt("blockEntityId");
        shadowPassInterval = 0;
        needResizeShadow = false;
        shadowMapWidth = 1024;
        shadowMapHeight = 1024;
        spShadowMapWidth = 1024;
        spShadowMapHeight = 1024;
        shadowMapFOV = 90.0f;
        shadowMapHalfPlane = 160.0f;
        shadowMapIsOrtho = true;
        shadowDistanceRenderMul = -1.0f;
        shadowPassCounter = 0;
        shouldSkipDefaultShadow = false;
        waterShadowEnabled = false;
        usedColorBuffers = 0;
        usedDepthBuffers = 0;
        usedShadowColorBuffers = 0;
        usedShadowDepthBuffers = 0;
        usedColorAttachs = 0;
        usedDrawBuffers = 0;
        dfb = 0;
        sfb = 0;
        gbuffersFormat = new int[8];
        gbuffersClear = new boolean[8];
        activeProgram = 0;
        programNames = new String[]{"", "gbuffers_basic", "gbuffers_textured", "gbuffers_textured_lit", "gbuffers_skybasic", "gbuffers_skytextured", "gbuffers_clouds", "gbuffers_terrain", "gbuffers_terrain_solid", "gbuffers_terrain_cutout_mip", "gbuffers_terrain_cutout", "gbuffers_damagedblock", "gbuffers_water", "gbuffers_block", "gbuffers_beaconbeam", "gbuffers_item", "gbuffers_entities", "gbuffers_armor_glint", "gbuffers_spidereyes", "gbuffers_hand", "gbuffers_weather", "composite", "composite1", "composite2", "composite3", "composite4", "composite5", "composite6", "composite7", "final", "shadow", "shadow_solid", "shadow_cutout"};
        programBackups = new int[]{0, 0, 1, 2, 1, 2, 2, 3, 7, 7, 7, 7, 7, 7, 2, 3, 3, 2, 2, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 30, 30};
        programsID = new int[33];
        programsRef = new int[33];
        programIDCopyDepth = 0;
        programsDrawBufSettings = new String[33];
        newDrawBufSetting = null;
        programsDrawBuffers = new IntBuffer[33];
        activeDrawBuffers = null;
        programsColorAtmSettings = new String[33];
        newColorAtmSetting = null;
        activeColorAtmSettings = null;
        programsCompositeMipmapSetting = new int[33];
        newCompositeMipmapSetting = 0;
        activeCompositeMipmapSetting = 0;
        loadedShaders = null;
        shadersConfig = null;
        defaultTexture = null;
        normalMapEnabled = false;
        shadowHardwareFilteringEnabled = new boolean[2];
        shadowMipmapEnabled = new boolean[2];
        shadowFilterNearest = new boolean[2];
        shadowColorMipmapEnabled = new boolean[8];
        shadowColorFilterNearest = new boolean[8];
        configTweakBlockDamage = true;
        configCloudShadow = true;
        configHandDepthMul = 0.125f;
        configRenderResMul = 1.0f;
        configShadowResMul = 1.0f;
        configTexMinFilB = 0;
        configTexMinFilN = 0;
        configTexMinFilS = 0;
        configTexMagFilB = 0;
        configTexMagFilN = 0;
        configTexMagFilS = 0;
        configShadowClipFrustrum = true;
        configNormalMap = true;
        configSpecularMap = true;
        configOldLighting = new PropertyDefaultTrueFalse("oldLighting", "Classic Lighting", 0);
        configOldHandLight = new PropertyDefaultTrueFalse("oldHandLight", "Old Hand Light", 0);
        configAntialiasingLevel = 0;
        texMinFilDesc = new String[]{"Nearest", "Nearest-Nearest", "Nearest-Linear"};
        texMagFilDesc = new String[]{"Nearest", "Linear"};
        texMinFilValue = new int[]{9728, 9984, 9986};
        texMagFilValue = new int[]{9728, 9729};
        shaderPack = null;
        shaderPackLoaded = false;
        packNameNone = "OFF";
        packNameDefault = "(internal)";
        shaderpacksdirname = "shaderpacks";
        optionsfilename = "optionsshaders.txt";
        shaderPackOptions = null;
        shaderPackProfiles = null;
        shaderPackGuiScreens = null;
        shaderPackClouds = new PropertyDefaultFastFancyOff("clouds", "Clouds", 0);
        shaderPackOldLighting = new PropertyDefaultTrueFalse("oldLighting", "Classic Lighting", 0);
        shaderPackOldHandLight = new PropertyDefaultTrueFalse("oldHandLight", "Old Hand Light", 0);
        shaderPackDynamicHandLight = new PropertyDefaultTrueFalse("dynamicHandLight", "Dynamic Hand Light", 0);
        shaderPackShadowTranslucent = new PropertyDefaultTrueFalse("shadowTranslucent", "Shadow Translucent", 0);
        shaderPackUnderwaterOverlay = new PropertyDefaultTrueFalse("underwaterOverlay", "Underwater Overlay", 0);
        shaderPackSun = new PropertyDefaultTrueFalse("sun", "Sun", 0);
        shaderPackMoon = new PropertyDefaultTrueFalse("moon", "Moon", 0);
        shaderPackVignette = new PropertyDefaultTrueFalse("vignette", "Vignette", 0);
        shaderPackBackFaceSolid = new PropertyDefaultTrueFalse("backFace.solid", "Back-face Solid", 0);
        shaderPackBackFaceCutout = new PropertyDefaultTrueFalse("backFace.cutout", "Back-face Cutout", 0);
        shaderPackBackFaceCutoutMipped = new PropertyDefaultTrueFalse("backFace.cutoutMipped", "Back-face Cutout Mipped", 0);
        shaderPackBackFaceTranslucent = new PropertyDefaultTrueFalse("backFace.translucent", "Back-face Translucent", 0);
        shaderPackResources = new HashMap<String, String>();
        currentWorld = null;
        shaderPackDimensions = new ArrayList<Integer>();
        customTexturesGbuffers = null;
        customTexturesComposite = null;
        STAGE_NAMES = new String[]{"gbuffers", "composite"};
        saveFinalShaders = System.getProperty("shaders.debug.save", "false").equals("true");
        blockLightLevel05 = 0.5f;
        blockLightLevel06 = 0.6f;
        blockLightLevel08 = 0.8f;
        aoLevel = -1.0f;
        sunPathRotation = 0.0f;
        shadowAngleInterval = 0.0f;
        fogMode = 0;
        shadowIntervalSize = 2.0f;
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
        projection = Shaders.nextFloatBuffer(16);
        projectionInverse = Shaders.nextFloatBuffer(16);
        modelView = Shaders.nextFloatBuffer(16);
        modelViewInverse = Shaders.nextFloatBuffer(16);
        shadowProjection = Shaders.nextFloatBuffer(16);
        shadowProjectionInverse = Shaders.nextFloatBuffer(16);
        shadowModelView = Shaders.nextFloatBuffer(16);
        shadowModelViewInverse = Shaders.nextFloatBuffer(16);
        previousProjection = Shaders.nextFloatBuffer(16);
        previousModelView = Shaders.nextFloatBuffer(16);
        tempMatrixDirectBuffer = Shaders.nextFloatBuffer(16);
        tempDirectFloatBuffer = Shaders.nextFloatBuffer(16);
        dfbColorTextures = Shaders.nextIntBuffer(16);
        dfbDepthTextures = Shaders.nextIntBuffer(3);
        sfbColorTextures = Shaders.nextIntBuffer(8);
        sfbDepthTextures = Shaders.nextIntBuffer(2);
        dfbDrawBuffers = Shaders.nextIntBuffer(8);
        sfbDrawBuffers = Shaders.nextIntBuffer(8);
        drawBuffersNone = Shaders.nextIntBuffer(8);
        drawBuffersAll = Shaders.nextIntBuffer(8);
        drawBuffersClear0 = Shaders.nextIntBuffer(8);
        drawBuffersClear1 = Shaders.nextIntBuffer(8);
        drawBuffersClearColor = Shaders.nextIntBuffer(8);
        drawBuffersColorAtt0 = Shaders.nextIntBuffer(8);
        drawBuffersBuffer = Shaders.nextIntBufferArray(33, 8);
        gbufferFormatPattern = Pattern.compile("[ \t]*const[ \t]*int[ \t]*(\\w+)Format[ \t]*=[ \t]*([RGBA0123456789FUI_SNORM]*)[ \t]*;.*");
        gbufferClearPattern = Pattern.compile("[ \t]*const[ \t]*bool[ \t]*(\\w+)Clear[ \t]*=[ \t]*false[ \t]*;.*");
        gbufferMipmapEnabledPattern = Pattern.compile("[ \t]*const[ \t]*bool[ \t]*(\\w+)MipmapEnabled[ \t]*=[ \t]*true[ \t]*;.*");
        formatNames = new String[]{"R8", "RG8", "RGB8", "RGBA8", "R8_SNORM", "RG8_SNORM", "RGB8_SNORM", "RGBA8_SNORM", "R16", "RG16", "RGB16", "RGBA16", "R16_SNORM", "RG16_SNORM", "RGB16_SNORM", "RGBA16_SNORM", "R32F", "RG32F", "RGB32F", "RGBA32F", "R32I", "RG32I", "RGB32I", "RGBA32I", "R32UI", "RG32UI", "RGB32UI", "RGBA32UI", "R3_G3_B2", "RGB5_A1", "RGB10_A2", "R11F_G11F_B10F"};
        formatIds = new int[]{33321, 33323, 32849, 32856, 36756, 36757, 36758, 36759, 33322, 33324, 32852, 32859, 36760, 36761, 36762, 36763, 33326, 33328, 34837, 34836, 33333, 33339, 36227, 36226, 33334, 33340, 36209, 36208, 10768, 32855, 32857, 35898};
        patternLoadEntityDataMap = Pattern.compile("\\s*([\\w:]+)\\s*=\\s*([-]?\\d+)\\s*");
        entityData = new int[32];
        entityDataIndex = 0;
        shadersdir = new File(Minecraft.getMinecraft().mcDataDir, "shaders");
        shaderpacksdir = new File(Minecraft.getMinecraft().mcDataDir, shaderpacksdirname);
        configFile = new File(Minecraft.getMinecraft().mcDataDir, optionsfilename);
        drawBuffersNone.limit(0);
        drawBuffersColorAtt0.put(36064).position(0).limit(1);
    }
}

