package shadersmod.client;

import java.nio.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.*;
import net.minecraft.block.*;
import net.minecraft.world.*;
import shadersmod.common.*;
import net.minecraft.client.renderer.vertex.*;
import org.lwjgl.util.glu.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.model.*;
import java.io.*;
import java.util.regex.*;
import org.lwjgl.*;
import net.minecraft.item.*;
import java.util.*;
import net.minecraft.block.material.*;
import net.minecraft.util.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.settings.*;
import net.minecraft.entity.*;
import net.minecraft.tileentity.*;
import optifine.*;

public class Shaders
{
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
    public static final int texMinFilRange = 3;
    public static final int texMagFilRange = 2;
    public static final String[] texMinFilDesc;
    public static final String[] texMagFilDesc;
    public static final int[] texMinFilValue;
    public static final int[] texMagFilValue;
    public static final boolean enableShadersOption = true;
    static final int MaxDrawBuffers = 8;
    static final int MaxColorBuffers = 8;
    static final int MaxDepthBuffers = 3;
    static final int MaxShadowColorBuffers = 8;
    static final int MaxShadowDepthBuffers = 2;
    static final int[] dfbColorTexturesA;
    static final int[] colorTexturesToggle;
    static final int[] colorTextureTextureImageUnit;
    static final boolean[][] programsToggleColorTextures;
    static final float[] faProjection;
    static final float[] faProjectionInverse;
    static final float[] faModelView;
    static final float[] faModelViewInverse;
    static final float[] faShadowProjection;
    static final float[] faShadowProjectionInverse;
    static final float[] faShadowModelView;
    static final float[] faShadowModelViewInverse;
    private static final String[] programNames;
    private static final int[] programBackups;
    private static final boolean enableShadersDebug = true;
    private static final boolean saveFinalShaders;
    private static final int bigBufferSize = 2196;
    private static final ByteBuffer bigBuffer;
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
    private static final Pattern gbufferFormatPattern;
    private static final Pattern gbufferMipmapEnabledPattern;
    private static final String[] formatNames;
    private static final int[] formatIds;
    private static final Pattern patternLoadEntityDataMap;
    public static boolean isInitializedOnce;
    public static boolean isShaderPackInitialized;
    public static ContextCapabilities capabilities;
    public static String glVersionString;
    public static String glVendorString;
    public static String glRendererString;
    public static boolean hasGlGenMipmap;
    public static boolean hasForge;
    public static int numberResetDisplayList;
    public static int renderWidth;
    public static int renderHeight;
    public static boolean isRenderingWorld;
    public static boolean isRenderingSky;
    public static boolean isCompositeRendered;
    public static boolean isRenderingDfb;
    public static boolean isShadowPass;
    public static boolean isSleeping;
    public static boolean isHandRendered;
    public static boolean renderItemPass1DepthMask;
    public static ItemStack itemToRender;
    public static float wetnessHalfLife;
    public static float drynessHalfLife;
    public static float eyeBrightnessHalflife;
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
    public static boolean needResizeShadow;
    public static boolean shouldSkipDefaultShadow;
    public static int activeProgram;
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
    public static int configAntialiasingLevel;
    public static boolean shaderPackLoaded;
    public static String packNameNone;
    public static PropertyDefaultFastFancyOff shaderPackClouds;
    public static PropertyDefaultTrueFalse shaderPackOldLighting;
    public static PropertyDefaultTrueFalse shaderPackDynamicHandLight;
    public static float blockLightLevel05;
    public static float blockLightLevel06;
    public static float blockLightLevel08;
    public static float aoLevel;
    public static float blockAoLight;
    public static float sunPathRotation;
    public static float shadowAngleInterval;
    public static int fogMode;
    public static float fogColorR;
    public static float fogColorG;
    public static float fogColorB;
    public static float shadowIntervalSize;
    public static int terrainIconSize;
    public static int[] terrainTextureSize;
    public static int[] entityData;
    public static int entityDataIndex;
    static Minecraft mc;
    static EntityRenderer entityRenderer;
    static boolean needResetModels;
    static float[] sunPosition;
    static float[] moonPosition;
    static float[] shadowLightPosition;
    static float[] upPosition;
    static float[] shadowLightPositionVector;
    static float[] upPosModelView;
    static float[] sunPosModelView;
    static float[] moonPosModelView;
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
    static float frameTimeCounter;
    static int systemTimeInt32;
    static float rainStrength;
    static float wetness;
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
    static boolean updateChunksErrorRecorded;
    static boolean lightmapEnabled;
    static boolean fogEnabled;
    static double previousCameraPositionX;
    static double previousCameraPositionY;
    static double previousCameraPositionZ;
    static double cameraPositionX;
    static double cameraPositionY;
    static double cameraPositionZ;
    static int shadowPassInterval;
    static int shadowMapWidth;
    static int shadowMapHeight;
    static int spShadowMapWidth;
    static int spShadowMapHeight;
    static float shadowMapFOV;
    static float shadowMapHalfPlane;
    static boolean shadowMapIsOrtho;
    static int shadowPassCounter;
    static int preShadowPassThirdPersonView;
    static boolean waterShadowEnabled;
    static int usedColorBuffers;
    static int usedDepthBuffers;
    static int usedShadowColorBuffers;
    static int usedShadowDepthBuffers;
    static int usedColorAttachs;
    static int usedDrawBuffers;
    static int dfb;
    static int sfb;
    static int[] programsID;
    static IntBuffer[] programsDrawBuffers;
    static IntBuffer activeDrawBuffers;
    static IShaderPack shaderPack;
    static File currentshader;
    static String currentshadername;
    static String packNameDefault;
    static String shaderpacksdirname;
    static String optionsfilename;
    static File shadersdir;
    static File shaderpacksdir;
    static File configFile;
    static ShaderOption[] shaderPackOptions;
    static ShaderProfile[] shaderPackProfiles;
    static Map<String, ShaderOption[]> shaderPackGuiScreens;
    static Map<Block, Integer> mapBlockToEntityData;
    private static int renderDisplayWidth;
    private static int renderDisplayHeight;
    private static float[] tempMat;
    private static int[] gbuffersFormat;
    private static int[] programsRef;
    private static int programIDCopyDepth;
    private static String[] programsDrawBufSettings;
    private static String newDrawBufSetting;
    private static String[] programsColorAtmSettings;
    private static String newColorAtmSetting;
    private static String activeColorAtmSettings;
    private static int[] programsCompositeMipmapSetting;
    private static int newCompositeMipmapSetting;
    private static int activeCompositeMipmapSetting;
    private static Map<String, String> shaderPackResources;
    private static World currentWorld;
    private static List<Integer> shaderPackDimensions;
    private static HFNoiseTexture noiseTexture;
    private static boolean noiseTextureEnabled;
    private static int noiseTextureResolution;
    
    private static ByteBuffer nextByteBuffer(final int size) {
        final ByteBuffer buffer = Shaders.bigBuffer;
        final int pos = buffer.limit();
        buffer.position(pos).limit(pos + size);
        return buffer.slice();
    }
    
    private static IntBuffer nextIntBuffer(final int size) {
        final ByteBuffer buffer = Shaders.bigBuffer;
        final int pos = buffer.limit();
        buffer.position(pos).limit(pos + size * 4);
        return buffer.asIntBuffer();
    }
    
    private static FloatBuffer nextFloatBuffer(final int size) {
        final ByteBuffer buffer = Shaders.bigBuffer;
        final int pos = buffer.limit();
        buffer.position(pos).limit(pos + size * 4);
        return buffer.asFloatBuffer();
    }
    
    private static IntBuffer[] nextIntBufferArray(final int count, final int size) {
        final IntBuffer[] aib = new IntBuffer[count];
        for (int i = 0; i < count; ++i) {
            aib[i] = nextIntBuffer(size);
        }
        return aib;
    }
    
    public static void loadConfig() {
        SMCLog.info("Load ShadersMod configuration.");
        try {
            if (!Shaders.shaderpacksdir.exists()) {
                Shaders.shaderpacksdir.mkdir();
            }
        }
        catch (Exception var10) {
            SMCLog.severe("Failed to open the shaderpacks directory: " + Shaders.shaderpacksdir);
        }
        (Shaders.shadersConfig = new PropertiesOrdered()).setProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), "");
        if (Shaders.configFile.exists()) {
            try {
                final FileReader ops = new FileReader(Shaders.configFile);
                Shaders.shadersConfig.load(ops);
                ops.close();
            }
            catch (Exception ex) {}
        }
        if (!Shaders.configFile.exists()) {
            try {
                storeConfig();
            }
            catch (Exception ex2) {}
        }
        final EnumShaderOption[] var9 = EnumShaderOption.values();
        for (int i = 0; i < var9.length; ++i) {
            final EnumShaderOption op = var9[i];
            final String key = op.getPropertyKey();
            final String def = op.getValueDefault();
            final String val = Shaders.shadersConfig.getProperty(key, def);
            setEnumShaderOption(op, val);
        }
        loadShaderPack();
    }
    
    private static void setEnumShaderOption(final EnumShaderOption eso, String str) {
        if (str == null) {
            str = eso.getValueDefault();
        }
        switch (NamelessClass341846571.$SwitchMap$shadersmod$client$EnumShaderOption[eso.ordinal()]) {
            case 1: {
                Shaders.configAntialiasingLevel = Config.parseInt(str, 0);
                break;
            }
            case 2: {
                Shaders.configNormalMap = Config.parseBoolean(str, true);
                break;
            }
            case 3: {
                Shaders.configSpecularMap = Config.parseBoolean(str, true);
                break;
            }
            case 4: {
                Shaders.configRenderResMul = Config.parseFloat(str, 1.0f);
                break;
            }
            case 5: {
                Shaders.configShadowResMul = Config.parseFloat(str, 1.0f);
                break;
            }
            case 6: {
                Shaders.configHandDepthMul = Config.parseFloat(str, 0.125f);
                break;
            }
            case 7: {
                Shaders.configCloudShadow = Config.parseBoolean(str, true);
                break;
            }
            case 8: {
                Shaders.configOldLighting.setPropertyValue(str);
                break;
            }
            case 9: {
                Shaders.currentshadername = str;
                break;
            }
            case 10: {
                Shaders.configTweakBlockDamage = Config.parseBoolean(str, true);
                break;
            }
            case 11: {
                Shaders.configShadowClipFrustrum = Config.parseBoolean(str, true);
                break;
            }
            case 12: {
                Shaders.configTexMinFilB = Config.parseInt(str, 0);
                break;
            }
            case 13: {
                Shaders.configTexMinFilN = Config.parseInt(str, 0);
                break;
            }
            case 14: {
                Shaders.configTexMinFilS = Config.parseInt(str, 0);
                break;
            }
            case 15: {
                Shaders.configTexMagFilB = Config.parseInt(str, 0);
                break;
            }
            case 16: {
                Shaders.configTexMagFilB = Config.parseInt(str, 0);
                break;
            }
            case 17: {
                Shaders.configTexMagFilB = Config.parseInt(str, 0);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown option: " + eso);
            }
        }
    }
    
    public static void storeConfig() {
        SMCLog.info("Save ShadersMod configuration.");
        if (Shaders.shadersConfig == null) {
            Shaders.shadersConfig = new PropertiesOrdered();
        }
        final EnumShaderOption[] ops = EnumShaderOption.values();
        for (int ex = 0; ex < ops.length; ++ex) {
            final EnumShaderOption op = ops[ex];
            final String key = op.getPropertyKey();
            final String val = getEnumShaderOption(op);
            Shaders.shadersConfig.setProperty(key, val);
        }
        try {
            final FileWriter var6 = new FileWriter(Shaders.configFile);
            Shaders.shadersConfig.store(var6, null);
            var6.close();
        }
        catch (Exception var7) {
            SMCLog.severe("Error saving configuration: " + var7.getClass().getName() + ": " + var7.getMessage());
        }
    }
    
    public static String getEnumShaderOption(final EnumShaderOption eso) {
        switch (NamelessClass341846571.$SwitchMap$shadersmod$client$EnumShaderOption[eso.ordinal()]) {
            case 1: {
                return Integer.toString(Shaders.configAntialiasingLevel);
            }
            case 2: {
                return Boolean.toString(Shaders.configNormalMap);
            }
            case 3: {
                return Boolean.toString(Shaders.configSpecularMap);
            }
            case 4: {
                return Float.toString(Shaders.configRenderResMul);
            }
            case 5: {
                return Float.toString(Shaders.configShadowResMul);
            }
            case 6: {
                return Float.toString(Shaders.configHandDepthMul);
            }
            case 7: {
                return Boolean.toString(Shaders.configCloudShadow);
            }
            case 8: {
                return Shaders.configOldLighting.getPropertyValue();
            }
            case 9: {
                return Shaders.currentshadername;
            }
            case 10: {
                return Boolean.toString(Shaders.configTweakBlockDamage);
            }
            case 11: {
                return Boolean.toString(Shaders.configShadowClipFrustrum);
            }
            case 12: {
                return Integer.toString(Shaders.configTexMinFilB);
            }
            case 13: {
                return Integer.toString(Shaders.configTexMinFilN);
            }
            case 14: {
                return Integer.toString(Shaders.configTexMinFilS);
            }
            case 15: {
                return Integer.toString(Shaders.configTexMagFilB);
            }
            case 16: {
                return Integer.toString(Shaders.configTexMagFilB);
            }
            case 17: {
                return Integer.toString(Shaders.configTexMagFilB);
            }
            default: {
                throw new IllegalArgumentException("Unknown option: " + eso);
            }
        }
    }
    
    public static void setShaderPack(final String par1name) {
        Shaders.currentshadername = par1name;
        Shaders.shadersConfig.setProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), par1name);
        loadShaderPack();
    }
    
    public static void loadShaderPack() {
        final boolean shaderPackLoadedPrev = Shaders.shaderPackLoaded;
        final boolean oldLightingPrev = isOldLighting();
        Shaders.shaderPackLoaded = false;
        if (Shaders.shaderPack != null) {
            Shaders.shaderPack.close();
            Shaders.shaderPack = null;
            Shaders.shaderPackResources.clear();
            Shaders.shaderPackDimensions.clear();
            Shaders.shaderPackOptions = null;
            Shaders.shaderPackProfiles = null;
            Shaders.shaderPackGuiScreens = null;
            Shaders.shaderPackClouds.resetValue();
            Shaders.shaderPackDynamicHandLight.resetValue();
            Shaders.shaderPackOldLighting.resetValue();
        }
        boolean shadersBlocked = false;
        if (Config.isAntialiasing()) {
            SMCLog.info("Shaders can not be loaded, Antialiasing is enabled: " + Config.getAntialiasingLevel() + "x");
            shadersBlocked = true;
        }
        if (Config.isAnisotropicFiltering()) {
            SMCLog.info("Shaders can not be loaded, Anisotropic Filtering is enabled: " + Config.getAnisotropicFilterLevel() + "x");
            shadersBlocked = true;
        }
        if (Config.isFastRender()) {
            SMCLog.info("Shaders can not be loaded, Fast Render is enabled.");
            shadersBlocked = true;
        }
        final String packName = Shaders.shadersConfig.getProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), Shaders.packNameDefault);
        if (!packName.isEmpty() && !packName.equals(Shaders.packNameNone) && !shadersBlocked) {
            if (packName.equals(Shaders.packNameDefault)) {
                Shaders.shaderPack = new ShaderPackDefault();
                Shaders.shaderPackLoaded = true;
            }
            else {
                try {
                    final File formatChanged = new File(Shaders.shaderpacksdir, packName);
                    if (formatChanged.isDirectory()) {
                        Shaders.shaderPack = new ShaderPackFolder(packName, formatChanged);
                        Shaders.shaderPackLoaded = true;
                    }
                    else if (formatChanged.isFile() && packName.toLowerCase().endsWith(".zip")) {
                        Shaders.shaderPack = new ShaderPackZip(packName, formatChanged);
                        Shaders.shaderPackLoaded = true;
                    }
                }
                catch (Exception ex) {}
            }
        }
        if (Shaders.shaderPack != null) {
            SMCLog.info("Loaded shaderpack: " + getShaderPackName());
        }
        else {
            SMCLog.info("No shaderpack loaded.");
            Shaders.shaderPack = new ShaderPackNone();
        }
        loadShaderPackResources();
        loadShaderPackDimensions();
        Shaders.shaderPackOptions = loadShaderPackOptions();
        loadShaderPackProperties();
        final boolean formatChanged2 = Shaders.shaderPackLoaded != shaderPackLoadedPrev;
        final boolean oldLightingChanged = isOldLighting() != oldLightingPrev;
        if (formatChanged2 || oldLightingChanged) {
            DefaultVertexFormats.updateVertexFormats();
            if (Reflector.LightUtil.exists()) {
                Reflector.LightUtil_itemConsumer.setValue(null);
                Reflector.LightUtil_tessellator.setValue(null);
            }
            updateBlockLightLevel();
            Shaders.mc.func_175603_A();
        }
    }
    
    private static void loadShaderPackDimensions() {
        Shaders.shaderPackDimensions.clear();
        final StringBuffer sb = new StringBuffer();
        for (int i = -128; i <= 128; ++i) {
            final String worldDir = "/shaders/world" + i;
            if (Shaders.shaderPack.hasDirectory(worldDir)) {
                Shaders.shaderPackDimensions.add(i);
                sb.append(" " + i);
            }
        }
        if (sb.length() > 0) {
            Config.dbg("[Shaders] Dimensions:" + (Object)sb);
        }
    }
    
    private static void loadShaderPackProperties() {
        Shaders.shaderPackClouds.resetValue();
        Shaders.shaderPackDynamicHandLight.resetValue();
        Shaders.shaderPackOldLighting.resetValue();
        if (Shaders.shaderPack != null) {
            final String path = "/shaders/shaders.properties";
            try {
                final InputStream e = Shaders.shaderPack.getResourceAsStream(path);
                if (e == null) {
                    return;
                }
                final PropertiesOrdered props = new PropertiesOrdered();
                props.load(e);
                e.close();
                Shaders.shaderPackClouds.loadFrom(props);
                Shaders.shaderPackDynamicHandLight.loadFrom(props);
                Shaders.shaderPackOldLighting.loadFrom(props);
                Shaders.shaderPackProfiles = ShaderPackParser.parseProfiles(props, Shaders.shaderPackOptions);
                Shaders.shaderPackGuiScreens = ShaderPackParser.parseGuiScreens(props, Shaders.shaderPackProfiles, Shaders.shaderPackOptions);
            }
            catch (IOException var3) {
                Config.warn("[Shaders] Error reading: " + path);
            }
        }
    }
    
    public static ShaderOption[] getShaderPackOptions(final String screenName) {
        ShaderOption[] ops = Shaders.shaderPackOptions.clone();
        if (Shaders.shaderPackGuiScreens == null) {
            if (Shaders.shaderPackProfiles != null) {
                final ShaderOptionProfile var8 = new ShaderOptionProfile(Shaders.shaderPackProfiles, ops);
                ops = (ShaderOption[])Config.addObjectToArray(ops, var8, 0);
            }
            ops = getVisibleOptions(ops);
            return ops;
        }
        final String key = (screenName != null) ? ("screen." + screenName) : "screen";
        final ShaderOption[] sos = Shaders.shaderPackGuiScreens.get(key);
        if (sos == null) {
            return new ShaderOption[0];
        }
        final ArrayList list = new ArrayList();
        for (int sosExp = 0; sosExp < sos.length; ++sosExp) {
            final ShaderOption so = sos[sosExp];
            if (so == null) {
                list.add(null);
            }
            else if (so instanceof ShaderOptionRest) {
                final ShaderOption[] restOps = getShaderOptionsRest(Shaders.shaderPackGuiScreens, ops);
                list.addAll(Arrays.asList(restOps));
            }
            else {
                list.add(so);
            }
        }
        final ShaderOption[] var9 = list.toArray(new ShaderOption[list.size()]);
        return var9;
    }
    
    private static ShaderOption[] getShaderOptionsRest(final Map<String, ShaderOption[]> mapScreens, final ShaderOption[] ops) {
        final HashSet setNames = new HashSet();
        final Set keys = mapScreens.keySet();
        for (final String sos : keys) {
            final ShaderOption[] so = mapScreens.get(sos);
            for (int name = 0; name < so.length; ++name) {
                final ShaderOption so2 = so[name];
                if (so2 != null) {
                    setNames.add(so2.getName());
                }
            }
        }
        final ArrayList var9 = new ArrayList();
        for (int var10 = 0; var10 < ops.length; ++var10) {
            final ShaderOption var11 = ops[var10];
            if (var11.isVisible()) {
                final String var12 = var11.getName();
                if (!setNames.contains(var12)) {
                    var9.add(var11);
                }
            }
        }
        final ShaderOption[] var13 = var9.toArray(new ShaderOption[var9.size()]);
        return var13;
    }
    
    public static ShaderOption getShaderOption(final String name) {
        return ShaderUtils.getShaderOption(name, Shaders.shaderPackOptions);
    }
    
    public static ShaderOption[] getShaderPackOptions() {
        return Shaders.shaderPackOptions;
    }
    
    private static ShaderOption[] getVisibleOptions(final ShaderOption[] ops) {
        final ArrayList list = new ArrayList();
        for (int sos = 0; sos < ops.length; ++sos) {
            final ShaderOption so = ops[sos];
            if (so.isVisible()) {
                list.add(so);
            }
        }
        final ShaderOption[] var4 = list.toArray(new ShaderOption[list.size()]);
        return var4;
    }
    
    public static void saveShaderPackOptions() {
        saveShaderPackOptions(Shaders.shaderPackOptions, Shaders.shaderPack);
    }
    
    private static void saveShaderPackOptions(final ShaderOption[] sos, final IShaderPack sp) {
        final Properties props = new Properties();
        if (Shaders.shaderPackOptions != null) {
            for (int e = 0; e < sos.length; ++e) {
                final ShaderOption so = sos[e];
                if (so.isChanged() && so.isEnabled()) {
                    props.setProperty(so.getName(), so.getValue());
                }
            }
        }
        try {
            saveOptionProperties(sp, props);
        }
        catch (IOException var5) {
            Config.warn("[Shaders] Error saving configuration for " + Shaders.shaderPack.getName());
            var5.printStackTrace();
        }
    }
    
    private static void saveOptionProperties(final IShaderPack sp, final Properties props) throws IOException {
        final String path = Shaders.shaderpacksdirname + "/" + sp.getName() + ".txt";
        final File propFile = new File(Minecraft.getMinecraft().mcDataDir, path);
        if (props.isEmpty()) {
            propFile.delete();
        }
        else {
            final FileOutputStream fos = new FileOutputStream(propFile);
            props.store(fos, null);
            fos.flush();
            fos.close();
        }
    }
    
    private static ShaderOption[] loadShaderPackOptions() {
        try {
            final ShaderOption[] e = ShaderPackParser.parseShaderPackOptions(Shaders.shaderPack, Shaders.programNames, Shaders.shaderPackDimensions);
            final Properties props = loadOptionProperties(Shaders.shaderPack);
            for (int i = 0; i < e.length; ++i) {
                final ShaderOption so = e[i];
                final String val = props.getProperty(so.getName());
                if (val != null) {
                    so.resetValue();
                    if (!so.setValue(val)) {
                        Config.warn("[Shaders] Invalid value, option: " + so.getName() + ", value: " + val);
                    }
                }
            }
            return e;
        }
        catch (IOException var5) {
            Config.warn("[Shaders] Error reading configuration for " + Shaders.shaderPack.getName());
            var5.printStackTrace();
            return null;
        }
    }
    
    private static Properties loadOptionProperties(final IShaderPack sp) throws IOException {
        final Properties props = new Properties();
        final String path = Shaders.shaderpacksdirname + "/" + sp.getName() + ".txt";
        final File propFile = new File(Minecraft.getMinecraft().mcDataDir, path);
        if (propFile.exists() && propFile.isFile() && propFile.canRead()) {
            final FileInputStream fis = new FileInputStream(propFile);
            props.load(fis);
            fis.close();
            return props;
        }
        return props;
    }
    
    public static ShaderOption[] getChangedOptions(final ShaderOption[] ops) {
        final ArrayList list = new ArrayList();
        for (int cops = 0; cops < ops.length; ++cops) {
            final ShaderOption op = ops[cops];
            if (op.isEnabled() && op.isChanged()) {
                list.add(op);
            }
        }
        final ShaderOption[] var4 = list.toArray(new ShaderOption[list.size()]);
        return var4;
    }
    
    private static String applyOptions(String line, final ShaderOption[] ops) {
        if (ops != null && ops.length > 0) {
            for (int i = 0; i < ops.length; ++i) {
                final ShaderOption op = ops[i];
                final String opName = op.getName();
                if (op.matchesLine(line)) {
                    line = op.getSourceLine();
                    break;
                }
            }
            return line;
        }
        return line;
    }
    
    static ArrayList listOfShaders() {
        final ArrayList list = new ArrayList();
        list.add(Shaders.packNameNone);
        list.add(Shaders.packNameDefault);
        try {
            if (!Shaders.shaderpacksdir.exists()) {
                Shaders.shaderpacksdir.mkdir();
            }
            final File[] e = Shaders.shaderpacksdir.listFiles();
            for (int i = 0; i < e.length; ++i) {
                final File file = e[i];
                final String name = file.getName();
                if (file.isDirectory()) {
                    final File subDir = new File(file, "shaders");
                    if (subDir.exists() && subDir.isDirectory()) {
                        list.add(name);
                    }
                }
                else if (file.isFile() && name.toLowerCase().endsWith(".zip")) {
                    list.add(name);
                }
            }
        }
        catch (Exception ex) {}
        return list;
    }
    
    static String versiontostring(final int vv) {
        final String vs = Integer.toString(vv);
        return Integer.toString(Integer.parseInt(vs.substring(1, 3))) + "." + Integer.toString(Integer.parseInt(vs.substring(3, 5))) + "." + Integer.toString(Integer.parseInt(vs.substring(5)));
    }
    
    static void checkOptifine() {
    }
    
    public static int checkFramebufferStatus(final String location) {
        final int status = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
        if (status != 36053) {
            System.err.format("FramebufferStatus 0x%04X at %s\n", status, location);
        }
        return status;
    }
    
    public static int checkGLError(final String location) {
        final int errorCode = GL11.glGetError();
        if (errorCode != 0) {
            final boolean skipPrint = false;
            if (!skipPrint) {
                if (errorCode == 1286) {
                    final int status = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
                    System.err.format("GL error 0x%04X: %s (Fb status 0x%04X) at %s\n", errorCode, GLU.gluErrorString(errorCode), status, location);
                }
                else {
                    System.err.format("GL error 0x%04X: %s at %s\n", errorCode, GLU.gluErrorString(errorCode), location);
                }
            }
        }
        return errorCode;
    }
    
    public static int checkGLError(final String location, final String info) {
        final int errorCode = GL11.glGetError();
        if (errorCode != 0) {
            System.err.format("GL error 0x%04x: %s at %s %s\n", errorCode, GLU.gluErrorString(errorCode), location, info);
        }
        return errorCode;
    }
    
    public static int checkGLError(final String location, final String info1, final String info2) {
        final int errorCode = GL11.glGetError();
        if (errorCode != 0) {
            System.err.format("GL error 0x%04x: %s at %s %s %s\n", errorCode, GLU.gluErrorString(errorCode), location, info1, info2);
        }
        return errorCode;
    }
    
    private static void printChat(final String str) {
        Shaders.mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(str));
    }
    
    private static void printChatAndLogError(final String str) {
        SMCLog.severe(str);
        Shaders.mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(str));
    }
    
    public static void printIntBuffer(final String title, final IntBuffer buf) {
        final StringBuilder sb = new StringBuilder(128);
        sb.append(title).append(" [pos ").append(buf.position()).append(" lim ").append(buf.limit()).append(" cap ").append(buf.capacity()).append(" :");
        for (int lim = buf.limit(), i = 0; i < lim; ++i) {
            sb.append(" ").append(buf.get(i));
        }
        sb.append("]");
        SMCLog.info(sb.toString());
    }
    
    public static void startup(final Minecraft mc) {
        checkShadersModInstalled();
        Shaders.mc = mc;
        Shaders.capabilities = GLContext.getCapabilities();
        Shaders.glVersionString = GL11.glGetString(7938);
        Shaders.glVendorString = GL11.glGetString(7936);
        Shaders.glRendererString = GL11.glGetString(7937);
        SMCLog.info("ShadersMod version: 2.4.12");
        SMCLog.info("OpenGL Version: " + Shaders.glVersionString);
        SMCLog.info("Vendor:  " + Shaders.glVendorString);
        SMCLog.info("Renderer: " + Shaders.glRendererString);
        SMCLog.info("Capabilities: " + (Shaders.capabilities.OpenGL20 ? " 2.0 " : " - ") + (Shaders.capabilities.OpenGL21 ? " 2.1 " : " - ") + (Shaders.capabilities.OpenGL30 ? " 3.0 " : " - ") + (Shaders.capabilities.OpenGL32 ? " 3.2 " : " - ") + (Shaders.capabilities.OpenGL40 ? " 4.0 " : " - "));
        SMCLog.info("GL_MAX_DRAW_BUFFERS: " + GL11.glGetInteger(34852));
        SMCLog.info("GL_MAX_COLOR_ATTACHMENTS_EXT: " + GL11.glGetInteger(36063));
        SMCLog.info("GL_MAX_TEXTURE_IMAGE_UNITS: " + GL11.glGetInteger(34930));
        Shaders.hasGlGenMipmap = Shaders.capabilities.OpenGL30;
        loadConfig();
    }
    
    private static String toStringYN(final boolean b) {
        return b ? "Y" : "N";
    }
    
    public static void updateBlockLightLevel() {
        if (isOldLighting()) {
            Shaders.blockLightLevel05 = 0.5f;
            Shaders.blockLightLevel06 = 0.6f;
            Shaders.blockLightLevel08 = 0.8f;
        }
        else {
            Shaders.blockLightLevel05 = 1.0f;
            Shaders.blockLightLevel06 = 1.0f;
            Shaders.blockLightLevel08 = 1.0f;
        }
    }
    
    public static boolean isDynamicHandLight() {
        return Shaders.shaderPackDynamicHandLight.isDefault() || Shaders.shaderPackDynamicHandLight.isTrue();
    }
    
    public static boolean isOldLighting() {
        return Shaders.configOldLighting.isDefault() ? (Shaders.shaderPackOldLighting.isDefault() || Shaders.shaderPackOldLighting.isTrue()) : Shaders.configOldLighting.isTrue();
    }
    
    public static void init() {
        boolean firstInit;
        if (!Shaders.isInitializedOnce) {
            Shaders.isInitializedOnce = true;
            firstInit = true;
        }
        else {
            firstInit = false;
        }
        if (!Shaders.isShaderPackInitialized) {
            checkGLError("Shaders.init pre");
            if (getShaderPackName() != null) {}
            if (!Shaders.capabilities.OpenGL20) {
                printChatAndLogError("No OpenGL 2.0");
            }
            if (!Shaders.capabilities.GL_EXT_framebuffer_object) {
                printChatAndLogError("No EXT_framebuffer_object");
            }
            Shaders.dfbDrawBuffers.position(0).limit(8);
            Shaders.dfbColorTextures.position(0).limit(16);
            Shaders.dfbDepthTextures.position(0).limit(3);
            Shaders.sfbDrawBuffers.position(0).limit(8);
            Shaders.sfbDepthTextures.position(0).limit(2);
            Shaders.sfbColorTextures.position(0).limit(8);
            Shaders.usedColorBuffers = 4;
            Shaders.usedDepthBuffers = 1;
            Shaders.usedShadowColorBuffers = 0;
            Shaders.usedShadowDepthBuffers = 0;
            Shaders.usedColorAttachs = 1;
            Shaders.usedDrawBuffers = 1;
            Arrays.fill(Shaders.gbuffersFormat, 6408);
            Arrays.fill(Shaders.shadowHardwareFilteringEnabled, false);
            Arrays.fill(Shaders.shadowMipmapEnabled, false);
            Arrays.fill(Shaders.shadowFilterNearest, false);
            Arrays.fill(Shaders.shadowColorMipmapEnabled, false);
            Arrays.fill(Shaders.shadowColorFilterNearest, false);
            Shaders.centerDepthSmoothEnabled = false;
            Shaders.noiseTextureEnabled = false;
            Shaders.sunPathRotation = 0.0f;
            Shaders.shadowIntervalSize = 2.0f;
            Shaders.aoLevel = 0.8f;
            Shaders.blockAoLight = 1.0f - Shaders.aoLevel;
            Shaders.useEntityAttrib = false;
            Shaders.useMidTexCoordAttrib = false;
            Shaders.useMultiTexCoord3Attrib = false;
            Shaders.useTangentAttrib = false;
            Shaders.waterShadowEnabled = false;
            Shaders.updateChunksErrorRecorded = false;
            updateBlockLightLevel();
            final ShaderProfile activeProfile = ShaderUtils.detectProfile(Shaders.shaderPackProfiles, Shaders.shaderPackOptions, false);
            String worldPrefix = "";
            if (Shaders.currentWorld != null) {
                final int maxDrawBuffers = Shaders.currentWorld.provider.getDimensionId();
                if (Shaders.shaderPackDimensions.contains(maxDrawBuffers)) {
                    worldPrefix = "world" + maxDrawBuffers + "/";
                }
            }
            if (Shaders.saveFinalShaders) {
                clearDirectory(new File(Shaders.shaderpacksdir, "debug"));
            }
            for (int maxDrawBuffers = 0; maxDrawBuffers < 33; ++maxDrawBuffers) {
                String drawBuffersMap = Shaders.programNames[maxDrawBuffers];
                if (drawBuffersMap.equals("")) {
                    Shaders.programsID[maxDrawBuffers] = (Shaders.programsRef[maxDrawBuffers] = 0);
                    Shaders.programsDrawBufSettings[maxDrawBuffers] = null;
                    Shaders.programsColorAtmSettings[maxDrawBuffers] = null;
                    Shaders.programsCompositeMipmapSetting[maxDrawBuffers] = 0;
                }
                else {
                    Shaders.newDrawBufSetting = null;
                    Shaders.newColorAtmSetting = null;
                    Shaders.newCompositeMipmapSetting = 0;
                    String i = worldPrefix + drawBuffersMap;
                    if (activeProfile != null && activeProfile.isProgramDisabled(i)) {
                        SMCLog.info("Program disabled: " + i);
                        drawBuffersMap = "<disabled>";
                        i = worldPrefix + drawBuffersMap;
                    }
                    final String n = "/shaders/" + i;
                    final int intbuf = setupProgram(maxDrawBuffers, n + ".vsh", n + ".fsh");
                    if (intbuf > 0) {
                        SMCLog.info("Program loaded: " + i);
                    }
                    Shaders.programsID[maxDrawBuffers] = (Shaders.programsRef[maxDrawBuffers] = intbuf);
                    Shaders.programsDrawBufSettings[maxDrawBuffers] = ((intbuf != 0) ? Shaders.newDrawBufSetting : null);
                    Shaders.programsColorAtmSettings[maxDrawBuffers] = ((intbuf != 0) ? Shaders.newColorAtmSetting : null);
                    Shaders.programsCompositeMipmapSetting[maxDrawBuffers] = ((intbuf != 0) ? Shaders.newCompositeMipmapSetting : 0);
                }
            }
            int maxDrawBuffers = GL11.glGetInteger(34852);
            new HashMap();
            for (int var12 = 0; var12 < 33; ++var12) {
                Arrays.fill(Shaders.programsToggleColorTextures[var12], false);
                if (var12 == 29) {
                    Shaders.programsDrawBuffers[var12] = null;
                }
                else if (Shaders.programsID[var12] == 0) {
                    if (var12 == 30) {
                        Shaders.programsDrawBuffers[var12] = Shaders.drawBuffersNone;
                    }
                    else {
                        Shaders.programsDrawBuffers[var12] = Shaders.drawBuffersColorAtt0;
                    }
                }
                else {
                    final String n = Shaders.programsDrawBufSettings[var12];
                    if (n != null) {
                        final IntBuffer var13 = Shaders.drawBuffersBuffer[var12];
                        int numDB = n.length();
                        if (numDB > Shaders.usedDrawBuffers) {
                            Shaders.usedDrawBuffers = numDB;
                        }
                        if (numDB > maxDrawBuffers) {
                            numDB = maxDrawBuffers;
                        }
                        (Shaders.programsDrawBuffers[var12] = var13).limit(numDB);
                        for (int i2 = 0; i2 < numDB; ++i2) {
                            int drawBuffer = 0;
                            if (n.length() > i2) {
                                final int ca = n.charAt(i2) - '0';
                                if (var12 != 30) {
                                    if (ca >= 0 && ca <= 7) {
                                        Shaders.programsToggleColorTextures[var12][ca] = true;
                                        drawBuffer = ca + 36064;
                                        if (ca > Shaders.usedColorAttachs) {
                                            Shaders.usedColorAttachs = ca;
                                        }
                                        if (ca > Shaders.usedColorBuffers) {
                                            Shaders.usedColorBuffers = ca;
                                        }
                                    }
                                }
                                else if (ca >= 0 && ca <= 1) {
                                    drawBuffer = ca + 36064;
                                    if (ca > Shaders.usedShadowColorBuffers) {
                                        Shaders.usedShadowColorBuffers = ca;
                                    }
                                }
                            }
                            var13.put(i2, drawBuffer);
                        }
                    }
                    else if (var12 != 30 && var12 != 31 && var12 != 32) {
                        Shaders.programsDrawBuffers[var12] = Shaders.dfbDrawBuffers;
                        Shaders.usedDrawBuffers = Shaders.usedColorBuffers;
                        Arrays.fill(Shaders.programsToggleColorTextures[var12], 0, Shaders.usedColorBuffers, true);
                    }
                    else {
                        Shaders.programsDrawBuffers[var12] = Shaders.sfbDrawBuffers;
                    }
                }
            }
            Shaders.usedColorAttachs = Shaders.usedColorBuffers;
            Shaders.shadowPassInterval = ((Shaders.usedShadowDepthBuffers > 0) ? 1 : 0);
            Shaders.shouldSkipDefaultShadow = (Shaders.usedShadowDepthBuffers > 0);
            SMCLog.info("usedColorBuffers: " + Shaders.usedColorBuffers);
            SMCLog.info("usedDepthBuffers: " + Shaders.usedDepthBuffers);
            SMCLog.info("usedShadowColorBuffers: " + Shaders.usedShadowColorBuffers);
            SMCLog.info("usedShadowDepthBuffers: " + Shaders.usedShadowDepthBuffers);
            SMCLog.info("usedColorAttachs: " + Shaders.usedColorAttachs);
            SMCLog.info("usedDrawBuffers: " + Shaders.usedDrawBuffers);
            Shaders.dfbDrawBuffers.position(0).limit(Shaders.usedDrawBuffers);
            Shaders.dfbColorTextures.position(0).limit(Shaders.usedColorBuffers * 2);
            for (int var12 = 0; var12 < Shaders.usedDrawBuffers; ++var12) {
                Shaders.dfbDrawBuffers.put(var12, 36064 + var12);
            }
            if (Shaders.usedDrawBuffers > maxDrawBuffers) {
                printChatAndLogError("[Shaders] Error: Not enough draw buffers, needed: " + Shaders.usedDrawBuffers + ", available: " + maxDrawBuffers);
            }
            Shaders.sfbDrawBuffers.position(0).limit(Shaders.usedShadowColorBuffers);
            for (int var12 = 0; var12 < Shaders.usedShadowColorBuffers; ++var12) {
                Shaders.sfbDrawBuffers.put(var12, 36064 + var12);
            }
            for (int var12 = 0; var12 < 33; ++var12) {
                int var14;
                for (var14 = var12; Shaders.programsID[var14] == 0 && Shaders.programBackups[var14] != var14; var14 = Shaders.programBackups[var14]) {}
                if (var14 != var12 && var12 != 30) {
                    Shaders.programsID[var12] = Shaders.programsID[var14];
                    Shaders.programsDrawBufSettings[var12] = Shaders.programsDrawBufSettings[var14];
                    Shaders.programsDrawBuffers[var12] = Shaders.programsDrawBuffers[var14];
                }
            }
            resize();
            resizeShadow();
            if (Shaders.noiseTextureEnabled) {
                setupNoiseTexture();
            }
            if (Shaders.defaultTexture == null) {
                Shaders.defaultTexture = ShadersTex.createDefaultTexture();
            }
            GlStateManager.pushMatrix();
            GlStateManager.rotate(-90.0f, 0.0f, 1.0f, 0.0f);
            preCelestialRotate();
            postCelestialRotate();
            GlStateManager.popMatrix();
            Shaders.isShaderPackInitialized = true;
            loadEntityDataMap();
            resetDisplayList();
            if (!firstInit) {}
            checkGLError("Shaders.init");
        }
    }
    
    public static void resetDisplayList() {
        ++Shaders.numberResetDisplayList;
        Shaders.needResetModels = true;
        SMCLog.info("Reset world renderers");
        Shaders.mc.renderGlobal.loadRenderers();
    }
    
    public static void resetDisplayListModels() {
        if (Shaders.needResetModels) {
            Shaders.needResetModels = false;
            SMCLog.info("Reset model renderers");
            for (final Render ren : Shaders.mc.getRenderManager().getEntityRenderMap().values()) {
                if (ren instanceof RendererLivingEntity) {
                    final RendererLivingEntity rle = (RendererLivingEntity)ren;
                    resetDisplayListModel(rle.getMainModel());
                }
            }
        }
    }
    
    public static void resetDisplayListModel(final ModelBase model) {
        if (model != null) {
            for (final Object obj : model.boxList) {
                if (obj instanceof ModelRenderer) {
                    resetDisplayListModelRenderer((ModelRenderer)obj);
                }
            }
        }
    }
    
    public static void resetDisplayListModelRenderer(final ModelRenderer mrr) {
        mrr.resetDisplayList();
        if (mrr.childModels != null) {
            for (int i = 0, n = mrr.childModels.size(); i < n; ++i) {
                resetDisplayListModelRenderer(mrr.childModels.get(i));
            }
        }
    }
    
    private static int setupProgram(final int program, final String vShaderPath, final String fShaderPath) {
        checkGLError("pre setupProgram");
        int programid = ARBShaderObjects.glCreateProgramObjectARB();
        checkGLError("create");
        if (programid != 0) {
            Shaders.progUseEntityAttrib = false;
            Shaders.progUseMidTexCoordAttrib = false;
            Shaders.progUseTangentAttrib = false;
            final int vShader = createVertShader(vShaderPath);
            final int fShader = createFragShader(fShaderPath);
            checkGLError("create");
            if (vShader == 0 && fShader == 0) {
                ARBShaderObjects.glDeleteObjectARB(programid);
                programid = 0;
            }
            else {
                if (vShader != 0) {
                    ARBShaderObjects.glAttachObjectARB(programid, vShader);
                    checkGLError("attach");
                }
                if (fShader != 0) {
                    ARBShaderObjects.glAttachObjectARB(programid, fShader);
                    checkGLError("attach");
                }
                if (Shaders.progUseEntityAttrib) {
                    ARBVertexShader.glBindAttribLocationARB(programid, Shaders.entityAttrib, (CharSequence)"mc_Entity");
                    checkGLError("mc_Entity");
                }
                if (Shaders.progUseMidTexCoordAttrib) {
                    ARBVertexShader.glBindAttribLocationARB(programid, Shaders.midTexCoordAttrib, (CharSequence)"mc_midTexCoord");
                    checkGLError("mc_midTexCoord");
                }
                if (Shaders.progUseTangentAttrib) {
                    ARBVertexShader.glBindAttribLocationARB(programid, Shaders.tangentAttrib, (CharSequence)"at_tangent");
                    checkGLError("at_tangent");
                }
                ARBShaderObjects.glLinkProgramARB(programid);
                if (GL20.glGetProgrami(programid, 35714) != 1) {
                    SMCLog.severe("Error linking program: " + programid);
                }
                printLogInfo(programid, vShaderPath + ", " + fShaderPath);
                if (vShader != 0) {
                    ARBShaderObjects.glDetachObjectARB(programid, vShader);
                    ARBShaderObjects.glDeleteObjectARB(vShader);
                }
                if (fShader != 0) {
                    ARBShaderObjects.glDetachObjectARB(programid, fShader);
                    ARBShaderObjects.glDeleteObjectARB(fShader);
                }
                Shaders.programsID[program] = programid;
                useProgram(program);
                ARBShaderObjects.glValidateProgramARB(programid);
                useProgram(0);
                printLogInfo(programid, vShaderPath + ", " + fShaderPath);
                final int valid = GL20.glGetProgrami(programid, 35715);
                if (valid != 1) {
                    final String Q = "\"";
                    printChatAndLogError("[Shaders] Error: Invalid program " + Q + Shaders.programNames[program] + Q);
                    ARBShaderObjects.glDeleteObjectARB(programid);
                    programid = 0;
                }
            }
        }
        return programid;
    }
    
    private static int createVertShader(final String filename) {
        final int vertShader = ARBShaderObjects.glCreateShaderObjectARB(35633);
        if (vertShader == 0) {
            return 0;
        }
        final StringBuilder vertexCode = new StringBuilder(131072);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(Shaders.shaderPack.getResourceAsStream(filename)));
        }
        catch (Exception var10) {
            try {
                reader = new BufferedReader(new FileReader(new File(filename)));
            }
            catch (Exception var11) {
                ARBShaderObjects.glDeleteObjectARB(vertShader);
                return 0;
            }
        }
        final ShaderOption[] activeOptions = getChangedOptions(Shaders.shaderPackOptions);
        if (reader != null) {
            try {
                reader = ShaderPackParser.resolveIncludes(reader, filename, Shaders.shaderPack, 0);
                String line;
                while ((line = reader.readLine()) != null) {
                    line = applyOptions(line, activeOptions);
                    vertexCode.append(line).append('\n');
                    if (line.matches("attribute [_a-zA-Z0-9]+ mc_Entity.*")) {
                        Shaders.useEntityAttrib = true;
                        Shaders.progUseEntityAttrib = true;
                    }
                    else if (line.matches("attribute [_a-zA-Z0-9]+ mc_midTexCoord.*")) {
                        Shaders.useMidTexCoordAttrib = true;
                        Shaders.progUseMidTexCoordAttrib = true;
                    }
                    else if (line.matches(".*gl_MultiTexCoord3.*")) {
                        Shaders.useMultiTexCoord3Attrib = true;
                    }
                    else {
                        if (!line.matches("attribute [_a-zA-Z0-9]+ at_tangent.*")) {
                            continue;
                        }
                        Shaders.useTangentAttrib = true;
                        Shaders.progUseTangentAttrib = true;
                    }
                }
                reader.close();
            }
            catch (Exception var9) {
                SMCLog.severe("Couldn't read " + filename + "!");
                var9.printStackTrace();
                ARBShaderObjects.glDeleteObjectARB(vertShader);
                return 0;
            }
        }
        if (Shaders.saveFinalShaders) {
            saveShader(filename, vertexCode.toString());
        }
        ARBShaderObjects.glShaderSourceARB(vertShader, (CharSequence)vertexCode);
        ARBShaderObjects.glCompileShaderARB(vertShader);
        if (GL20.glGetShaderi(vertShader, 35713) != 1) {
            SMCLog.severe("Error compiling vertex shader: " + filename);
        }
        printShaderLogInfo(vertShader, filename);
        return vertShader;
    }
    
    private static int createFragShader(final String filename) {
        final int fragShader = ARBShaderObjects.glCreateShaderObjectARB(35632);
        if (fragShader == 0) {
            return 0;
        }
        final StringBuilder fragCode = new StringBuilder(131072);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(Shaders.shaderPack.getResourceAsStream(filename)));
        }
        catch (Exception var14) {
            try {
                reader = new BufferedReader(new FileReader(new File(filename)));
            }
            catch (Exception var15) {
                ARBShaderObjects.glDeleteObjectARB(fragShader);
                return 0;
            }
        }
        final ShaderOption[] activeOptions = getChangedOptions(Shaders.shaderPackOptions);
        if (reader != null) {
            try {
                reader = ShaderPackParser.resolveIncludes(reader, filename, Shaders.shaderPack, 0);
                String line;
                while ((line = reader.readLine()) != null) {
                    line = applyOptions(line, activeOptions);
                    fragCode.append(line).append('\n');
                    if (!line.matches("#version .*")) {
                        if (line.matches("uniform [ _a-zA-Z0-9]+ shadow;.*")) {
                            if (Shaders.usedShadowDepthBuffers >= 1) {
                                continue;
                            }
                            Shaders.usedShadowDepthBuffers = 1;
                        }
                        else if (line.matches("uniform [ _a-zA-Z0-9]+ watershadow;.*")) {
                            Shaders.waterShadowEnabled = true;
                            if (Shaders.usedShadowDepthBuffers >= 2) {
                                continue;
                            }
                            Shaders.usedShadowDepthBuffers = 2;
                        }
                        else if (line.matches("uniform [ _a-zA-Z0-9]+ shadowtex0;.*")) {
                            if (Shaders.usedShadowDepthBuffers >= 1) {
                                continue;
                            }
                            Shaders.usedShadowDepthBuffers = 1;
                        }
                        else if (line.matches("uniform [ _a-zA-Z0-9]+ shadowtex1;.*")) {
                            if (Shaders.usedShadowDepthBuffers >= 2) {
                                continue;
                            }
                            Shaders.usedShadowDepthBuffers = 2;
                        }
                        else if (line.matches("uniform [ _a-zA-Z0-9]+ shadowcolor;.*")) {
                            if (Shaders.usedShadowColorBuffers >= 1) {
                                continue;
                            }
                            Shaders.usedShadowColorBuffers = 1;
                        }
                        else if (line.matches("uniform [ _a-zA-Z0-9]+ shadowcolor0;.*")) {
                            if (Shaders.usedShadowColorBuffers >= 1) {
                                continue;
                            }
                            Shaders.usedShadowColorBuffers = 1;
                        }
                        else if (line.matches("uniform [ _a-zA-Z0-9]+ shadowcolor1;.*")) {
                            if (Shaders.usedShadowColorBuffers >= 2) {
                                continue;
                            }
                            Shaders.usedShadowColorBuffers = 2;
                        }
                        else if (line.matches("uniform [ _a-zA-Z0-9]+ shadowcolor2;.*")) {
                            if (Shaders.usedShadowColorBuffers >= 3) {
                                continue;
                            }
                            Shaders.usedShadowColorBuffers = 3;
                        }
                        else if (line.matches("uniform [ _a-zA-Z0-9]+ shadowcolor3;.*")) {
                            if (Shaders.usedShadowColorBuffers >= 4) {
                                continue;
                            }
                            Shaders.usedShadowColorBuffers = 4;
                        }
                        else if (line.matches("uniform [ _a-zA-Z0-9]+ depthtex0;.*")) {
                            if (Shaders.usedDepthBuffers >= 1) {
                                continue;
                            }
                            Shaders.usedDepthBuffers = 1;
                        }
                        else if (line.matches("uniform [ _a-zA-Z0-9]+ depthtex1;.*")) {
                            if (Shaders.usedDepthBuffers >= 2) {
                                continue;
                            }
                            Shaders.usedDepthBuffers = 2;
                        }
                        else if (line.matches("uniform [ _a-zA-Z0-9]+ depthtex2;.*")) {
                            if (Shaders.usedDepthBuffers >= 3) {
                                continue;
                            }
                            Shaders.usedDepthBuffers = 3;
                        }
                        else if (line.matches("uniform [ _a-zA-Z0-9]+ gdepth;.*")) {
                            if (Shaders.gbuffersFormat[1] != 6408) {
                                continue;
                            }
                            Shaders.gbuffersFormat[1] = 34836;
                        }
                        else if (Shaders.usedColorBuffers < 5 && line.matches("uniform [ _a-zA-Z0-9]+ gaux1;.*")) {
                            Shaders.usedColorBuffers = 5;
                        }
                        else if (Shaders.usedColorBuffers < 6 && line.matches("uniform [ _a-zA-Z0-9]+ gaux2;.*")) {
                            Shaders.usedColorBuffers = 6;
                        }
                        else if (Shaders.usedColorBuffers < 7 && line.matches("uniform [ _a-zA-Z0-9]+ gaux3;.*")) {
                            Shaders.usedColorBuffers = 7;
                        }
                        else if (Shaders.usedColorBuffers < 8 && line.matches("uniform [ _a-zA-Z0-9]+ gaux4;.*")) {
                            Shaders.usedColorBuffers = 8;
                        }
                        else if (Shaders.usedColorBuffers < 5 && line.matches("uniform [ _a-zA-Z0-9]+ colortex4;.*")) {
                            Shaders.usedColorBuffers = 5;
                        }
                        else if (Shaders.usedColorBuffers < 6 && line.matches("uniform [ _a-zA-Z0-9]+ colortex5;.*")) {
                            Shaders.usedColorBuffers = 6;
                        }
                        else if (Shaders.usedColorBuffers < 7 && line.matches("uniform [ _a-zA-Z0-9]+ colortex6;.*")) {
                            Shaders.usedColorBuffers = 7;
                        }
                        else if (Shaders.usedColorBuffers < 8 && line.matches("uniform [ _a-zA-Z0-9]+ colortex7;.*")) {
                            Shaders.usedColorBuffers = 8;
                        }
                        else if (Shaders.usedColorBuffers < 8 && line.matches("uniform [ _a-zA-Z0-9]+ centerDepthSmooth;.*")) {
                            Shaders.centerDepthSmoothEnabled = true;
                        }
                        else if (line.matches("/\\* SHADOWRES:[0-9]+ \\*/.*")) {
                            final String[] e = line.split("(:| )", 4);
                            SMCLog.info("Shadow map resolution: " + e[2]);
                            Shaders.spShadowMapWidth = (Shaders.spShadowMapHeight = Integer.parseInt(e[2]));
                            Shaders.shadowMapWidth = (Shaders.shadowMapHeight = Math.round(Shaders.spShadowMapWidth * Shaders.configShadowResMul));
                        }
                        else if (line.matches("[ \t]*const[ \t]*int[ \t]*shadowMapResolution[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                            final String[] e = line.split("(=[ \t]*|;)");
                            SMCLog.info("Shadow map resolution: " + e[1]);
                            Shaders.spShadowMapWidth = (Shaders.spShadowMapHeight = Integer.parseInt(e[1]));
                            Shaders.shadowMapWidth = (Shaders.shadowMapHeight = Math.round(Shaders.spShadowMapWidth * Shaders.configShadowResMul));
                        }
                        else if (line.matches("/\\* SHADOWFOV:[0-9\\.]+ \\*/.*")) {
                            final String[] e = line.split("(:| )", 4);
                            SMCLog.info("Shadow map field of view: " + e[2]);
                            Shaders.shadowMapFOV = Float.parseFloat(e[2]);
                            Shaders.shadowMapIsOrtho = false;
                        }
                        else if (line.matches("/\\* SHADOWHPL:[0-9\\.]+ \\*/.*")) {
                            final String[] e = line.split("(:| )", 4);
                            SMCLog.info("Shadow map half-plane: " + e[2]);
                            Shaders.shadowMapHalfPlane = Float.parseFloat(e[2]);
                            Shaders.shadowMapIsOrtho = true;
                        }
                        else if (line.matches("[ \t]*const[ \t]*float[ \t]*shadowDistance[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                            final String[] e = line.split("(=[ \t]*|;)");
                            SMCLog.info("Shadow map distance: " + e[1]);
                            Shaders.shadowMapHalfPlane = Float.parseFloat(e[1]);
                            Shaders.shadowMapIsOrtho = true;
                        }
                        else if (line.matches("[ \t]*const[ \t]*float[ \t]*shadowIntervalSize[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                            final String[] e = line.split("(=[ \t]*|;)");
                            SMCLog.info("Shadow map interval size: " + e[1]);
                            Shaders.shadowIntervalSize = Float.parseFloat(e[1]);
                        }
                        else if (line.matches("[ \t]*const[ \t]*bool[ \t]*generateShadowMipmap[ \t]*=[ \t]*true[ \t]*;.*")) {
                            SMCLog.info("Generate shadow mipmap");
                            Arrays.fill(Shaders.shadowMipmapEnabled, true);
                        }
                        else if (line.matches("[ \t]*const[ \t]*bool[ \t]*generateShadowColorMipmap[ \t]*=[ \t]*true[ \t]*;.*")) {
                            SMCLog.info("Generate shadow color mipmap");
                            Arrays.fill(Shaders.shadowColorMipmapEnabled, true);
                        }
                        else if (line.matches("[ \t]*const[ \t]*bool[ \t]*shadowHardwareFiltering[ \t]*=[ \t]*true[ \t]*;.*")) {
                            SMCLog.info("Hardware shadow filtering enabled.");
                            Arrays.fill(Shaders.shadowHardwareFilteringEnabled, true);
                        }
                        else if (line.matches("[ \t]*const[ \t]*bool[ \t]*shadowHardwareFiltering0[ \t]*=[ \t]*true[ \t]*;.*")) {
                            SMCLog.info("shadowHardwareFiltering0");
                            Shaders.shadowHardwareFilteringEnabled[0] = true;
                        }
                        else if (line.matches("[ \t]*const[ \t]*bool[ \t]*shadowHardwareFiltering1[ \t]*=[ \t]*true[ \t]*;.*")) {
                            SMCLog.info("shadowHardwareFiltering1");
                            Shaders.shadowHardwareFilteringEnabled[1] = true;
                        }
                        else if (line.matches("[ \t]*const[ \t]*bool[ \t]*(shadowtex0Mipmap|shadowtexMipmap)[ \t]*=[ \t]*true[ \t]*;.*")) {
                            SMCLog.info("shadowtex0Mipmap");
                            Shaders.shadowMipmapEnabled[0] = true;
                        }
                        else if (line.matches("[ \t]*const[ \t]*bool[ \t]*(shadowtex1Mipmap)[ \t]*=[ \t]*true[ \t]*;.*")) {
                            SMCLog.info("shadowtex1Mipmap");
                            Shaders.shadowMipmapEnabled[1] = true;
                        }
                        else if (line.matches("[ \t]*const[ \t]*bool[ \t]*(shadowcolor0Mipmap|shadowColor0Mipmap)[ \t]*=[ \t]*true[ \t]*;.*")) {
                            SMCLog.info("shadowcolor0Mipmap");
                            Shaders.shadowColorMipmapEnabled[0] = true;
                        }
                        else if (line.matches("[ \t]*const[ \t]*bool[ \t]*(shadowcolor1Mipmap|shadowColor1Mipmap)[ \t]*=[ \t]*true[ \t]*;.*")) {
                            SMCLog.info("shadowcolor1Mipmap");
                            Shaders.shadowColorMipmapEnabled[1] = true;
                        }
                        else if (line.matches("[ \t]*const[ \t]*bool[ \t]*(shadowtex0Nearest|shadowtexNearest|shadow0MinMagNearest)[ \t]*=[ \t]*true[ \t]*;.*")) {
                            SMCLog.info("shadowtex0Nearest");
                            Shaders.shadowFilterNearest[0] = true;
                        }
                        else if (line.matches("[ \t]*const[ \t]*bool[ \t]*(shadowtex1Nearest|shadow1MinMagNearest)[ \t]*=[ \t]*true[ \t]*;.*")) {
                            SMCLog.info("shadowtex1Nearest");
                            Shaders.shadowFilterNearest[1] = true;
                        }
                        else if (line.matches("[ \t]*const[ \t]*bool[ \t]*(shadowcolor0Nearest|shadowColor0Nearest|shadowColor0MinMagNearest)[ \t]*=[ \t]*true[ \t]*;.*")) {
                            SMCLog.info("shadowcolor0Nearest");
                            Shaders.shadowColorFilterNearest[0] = true;
                        }
                        else if (line.matches("[ \t]*const[ \t]*bool[ \t]*(shadowcolor1Nearest|shadowColor1Nearest|shadowColor1MinMagNearest)[ \t]*=[ \t]*true[ \t]*;.*")) {
                            SMCLog.info("shadowcolor1Nearest");
                            Shaders.shadowColorFilterNearest[1] = true;
                        }
                        else if (line.matches("/\\* WETNESSHL:[0-9\\.]+ \\*/.*")) {
                            final String[] e = line.split("(:| )", 4);
                            SMCLog.info("Wetness halflife: " + e[2]);
                            Shaders.wetnessHalfLife = Float.parseFloat(e[2]);
                        }
                        else if (line.matches("[ \t]*const[ \t]*float[ \t]*wetnessHalflife[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                            final String[] e = line.split("(=[ \t]*|;)");
                            SMCLog.info("Wetness halflife: " + e[1]);
                            Shaders.wetnessHalfLife = Float.parseFloat(e[1]);
                        }
                        else if (line.matches("/\\* DRYNESSHL:[0-9\\.]+ \\*/.*")) {
                            final String[] e = line.split("(:| )", 4);
                            SMCLog.info("Dryness halflife: " + e[2]);
                            Shaders.drynessHalfLife = Float.parseFloat(e[2]);
                        }
                        else if (line.matches("[ \t]*const[ \t]*float[ \t]*drynessHalflife[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                            final String[] e = line.split("(=[ \t]*|;)");
                            SMCLog.info("Dryness halflife: " + e[1]);
                            Shaders.drynessHalfLife = Float.parseFloat(e[1]);
                        }
                        else if (line.matches("[ \t]*const[ \t]*float[ \t]*eyeBrightnessHalflife[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                            final String[] e = line.split("(=[ \t]*|;)");
                            SMCLog.info("Eye brightness halflife: " + e[1]);
                            Shaders.eyeBrightnessHalflife = Float.parseFloat(e[1]);
                        }
                        else if (line.matches("[ \t]*const[ \t]*float[ \t]*centerDepthHalflife[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                            final String[] e = line.split("(=[ \t]*|;)");
                            SMCLog.info("Center depth halflife: " + e[1]);
                            Shaders.centerDepthSmoothHalflife = Float.parseFloat(e[1]);
                        }
                        else if (line.matches("[ \t]*const[ \t]*float[ \t]*sunPathRotation[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                            final String[] e = line.split("(=[ \t]*|;)");
                            SMCLog.info("Sun path rotation: " + e[1]);
                            Shaders.sunPathRotation = Float.parseFloat(e[1]);
                        }
                        else if (line.matches("[ \t]*const[ \t]*float[ \t]*ambientOcclusionLevel[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                            final String[] e = line.split("(=[ \t]*|;)");
                            SMCLog.info("AO Level: " + e[1]);
                            Shaders.aoLevel = Float.parseFloat(e[1]);
                            Shaders.blockAoLight = 1.0f - Shaders.aoLevel;
                        }
                        else if (line.matches("[ \t]*const[ \t]*int[ \t]*superSamplingLevel[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                            final String[] e = line.split("(=[ \t]*|;)");
                            final int name1 = Integer.parseInt(e[1]);
                            if (name1 > 1) {
                                SMCLog.info("Super sampling level: " + name1 + "x");
                                Shaders.superSamplingLevel = name1;
                            }
                            else {
                                Shaders.superSamplingLevel = 1;
                            }
                        }
                        else if (line.matches("[ \t]*const[ \t]*int[ \t]*noiseTextureResolution[ \t]*=[ \t]*-?[0-9.]+f?;.*")) {
                            final String[] e = line.split("(=[ \t]*|;)");
                            SMCLog.info("Noise texture enabled");
                            SMCLog.info("Noise texture resolution: " + e[1]);
                            Shaders.noiseTextureResolution = Integer.parseInt(e[1]);
                            Shaders.noiseTextureEnabled = true;
                        }
                        else if (line.matches("[ \t]*const[ \t]*int[ \t]*\\w+Format[ \t]*=[ \t]*[RGBA81632FUI_SNORM]*[ \t]*;.*")) {
                            final Matcher e2 = Shaders.gbufferFormatPattern.matcher(line);
                            e2.matches();
                            final String name2 = e2.group(1);
                            final String bufferindex2 = e2.group(2);
                            final int bufferindex3 = getBufferIndexFromString(name2);
                            final int format = getTextureFormatFromString(bufferindex2);
                            if (bufferindex3 < 0 || format == 0) {
                                continue;
                            }
                            Shaders.gbuffersFormat[bufferindex3] = format;
                            SMCLog.info("%s format: %s", name2, bufferindex2);
                        }
                        else if (line.matches("/\\* GAUX4FORMAT:RGBA32F \\*/.*")) {
                            SMCLog.info("gaux4 format : RGB32AF");
                            Shaders.gbuffersFormat[7] = 34836;
                        }
                        else if (line.matches("/\\* GAUX4FORMAT:RGB32F \\*/.*")) {
                            SMCLog.info("gaux4 format : RGB32F");
                            Shaders.gbuffersFormat[7] = 34837;
                        }
                        else if (line.matches("/\\* GAUX4FORMAT:RGB16 \\*/.*")) {
                            SMCLog.info("gaux4 format : RGB16");
                            Shaders.gbuffersFormat[7] = 32852;
                        }
                        else if (line.matches("[ \t]*const[ \t]*bool[ \t]*\\w+MipmapEnabled[ \t]*=[ \t]*true[ \t]*;.*")) {
                            if (!filename.matches(".*composite[0-9]?.fsh") && !filename.matches(".*final.fsh")) {
                                continue;
                            }
                            final Matcher e2 = Shaders.gbufferMipmapEnabledPattern.matcher(line);
                            e2.matches();
                            final String name2 = e2.group(1);
                            final int bufferindex4 = getBufferIndexFromString(name2);
                            if (bufferindex4 < 0) {
                                continue;
                            }
                            Shaders.newCompositeMipmapSetting |= 1 << bufferindex4;
                            SMCLog.info("%s mipmap enabled for %s", name2, filename);
                        }
                        else {
                            if (!line.matches("/\\* DRAWBUFFERS:[0-7N]* \\*/.*")) {
                                continue;
                            }
                            final String[] e = line.split("(:| )", 4);
                            Shaders.newDrawBufSetting = e[2];
                        }
                    }
                }
                reader.close();
            }
            catch (Exception var13) {
                SMCLog.severe("Couldn't read " + filename + "!");
                var13.printStackTrace();
                ARBShaderObjects.glDeleteObjectARB(fragShader);
                return 0;
            }
        }
        if (Shaders.saveFinalShaders) {
            saveShader(filename, fragCode.toString());
        }
        ARBShaderObjects.glShaderSourceARB(fragShader, (CharSequence)fragCode);
        ARBShaderObjects.glCompileShaderARB(fragShader);
        if (GL20.glGetShaderi(fragShader, 35713) != 1) {
            SMCLog.severe("Error compiling fragment shader: " + filename);
        }
        printShaderLogInfo(fragShader, filename);
        return fragShader;
    }
    
    private static void saveShader(final String filename, final String code) {
        try {
            final File e = new File(Shaders.shaderpacksdir, "debug/" + filename);
            e.getParentFile().mkdirs();
            Config.writeFile(e, code);
        }
        catch (IOException var3) {
            Config.warn("Error saving: " + filename);
            var3.printStackTrace();
        }
    }
    
    private static void clearDirectory(final File dir) {
        if (dir.exists() && dir.isDirectory()) {
            final File[] files = dir.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; ++i) {
                    final File file = files[i];
                    if (file.isDirectory()) {
                        clearDirectory(file);
                    }
                    file.delete();
                }
            }
        }
    }
    
    private static boolean printLogInfo(final int obj, final String name) {
        final IntBuffer iVal = BufferUtils.createIntBuffer(1);
        ARBShaderObjects.glGetObjectParameterARB(obj, 35716, iVal);
        final int length = iVal.get();
        if (length > 1) {
            final ByteBuffer infoLog = BufferUtils.createByteBuffer(length);
            iVal.flip();
            ARBShaderObjects.glGetInfoLogARB(obj, iVal, infoLog);
            final byte[] infoBytes = new byte[length];
            infoLog.get(infoBytes);
            if (infoBytes[length - 1] == 0) {
                infoBytes[length - 1] = 10;
            }
            final String out = new String(infoBytes);
            SMCLog.info("Info log: " + name + "\n" + out);
            return false;
        }
        return true;
    }
    
    private static boolean printShaderLogInfo(final int shader, final String name) {
        final IntBuffer iVal = BufferUtils.createIntBuffer(1);
        final int length = GL20.glGetShaderi(shader, 35716);
        if (length > 1) {
            final String log = GL20.glGetShaderInfoLog(shader, length);
            SMCLog.info("Shader info log: " + name + "\n" + log);
            return false;
        }
        return true;
    }
    
    public static void setDrawBuffers(IntBuffer drawBuffers) {
        if (drawBuffers == null) {
            drawBuffers = Shaders.drawBuffersNone;
        }
        if (Shaders.activeDrawBuffers != drawBuffers) {
            GL20.glDrawBuffers(Shaders.activeDrawBuffers = drawBuffers);
        }
    }
    
    public static void useProgram(int program) {
        checkGLError("pre-useProgram");
        if (Shaders.isShadowPass) {
            program = 30;
            if (Shaders.programsID[30] == 0) {
                Shaders.normalMapEnabled = false;
                return;
            }
        }
        if (Shaders.activeProgram != program) {
            Shaders.activeProgram = program;
            ARBShaderObjects.glUseProgramObjectARB(Shaders.programsID[program]);
            if (Shaders.programsID[program] == 0) {
                Shaders.normalMapEnabled = false;
            }
            else {
                if (checkGLError("useProgram ", Shaders.programNames[program]) != 0) {
                    Shaders.programsID[program] = 0;
                }
                final IntBuffer drawBuffers = Shaders.programsDrawBuffers[program];
                if (Shaders.isRenderingDfb) {
                    setDrawBuffers(drawBuffers);
                    checkGLError(Shaders.programNames[program], " draw buffers = ", Shaders.programsDrawBufSettings[program]);
                }
                Shaders.activeCompositeMipmapSetting = Shaders.programsCompositeMipmapSetting[program];
                Shaders.uniformEntityColor.setProgram(Shaders.programsID[Shaders.activeProgram]);
                Shaders.uniformEntityId.setProgram(Shaders.programsID[Shaders.activeProgram]);
                Shaders.uniformBlockEntityId.setProgram(Shaders.programsID[Shaders.activeProgram]);
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
                        Shaders.normalMapEnabled = true;
                        setProgramUniform1i("texture", 0);
                        setProgramUniform1i("lightmap", 1);
                        setProgramUniform1i("normals", 2);
                        setProgramUniform1i("specular", 3);
                        setProgramUniform1i("shadow", Shaders.waterShadowEnabled ? 5 : 4);
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
                    }
                    default: {
                        Shaders.normalMapEnabled = false;
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
                        Shaders.normalMapEnabled = false;
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
                        setProgramUniform1i("shadow", Shaders.waterShadowEnabled ? 5 : 4);
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
                    }
                    case 30:
                    case 31:
                    case 32: {
                        setProgramUniform1i("tex", 0);
                        setProgramUniform1i("texture", 0);
                        setProgramUniform1i("lightmap", 1);
                        setProgramUniform1i("normals", 2);
                        setProgramUniform1i("specular", 3);
                        setProgramUniform1i("shadow", Shaders.waterShadowEnabled ? 5 : 4);
                        setProgramUniform1i("watershadow", 4);
                        setProgramUniform1i("shadowtex0", 4);
                        setProgramUniform1i("shadowtex1", 5);
                        setProgramUniform1i("shadowcolor", 13);
                        setProgramUniform1i("shadowcolor0", 13);
                        setProgramUniform1i("shadowcolor1", 14);
                        setProgramUniform1i("noisetex", 15);
                        break;
                    }
                }
                final ItemStack stack = Shaders.mc.thePlayer.getCurrentEquippedItem();
                final Item item = (stack != null) ? stack.getItem() : null;
                int itemID;
                Block block;
                if (item != null) {
                    itemID = Item.itemRegistry.getIDForObject(item);
                    block = (Block)Block.blockRegistry.getObjectById(itemID);
                }
                else {
                    itemID = -1;
                    block = null;
                }
                final int blockLight = (block != null) ? block.getLightValue() : 0;
                setProgramUniform1i("heldItemId", itemID);
                setProgramUniform1i("heldBlockLightValue", blockLight);
                setProgramUniform1i("fogMode", Shaders.fogEnabled ? Shaders.fogMode : 0);
                setProgramUniform3f("fogColor", Shaders.fogColorR, Shaders.fogColorG, Shaders.fogColorB);
                setProgramUniform3f("skyColor", Shaders.skyColorR, Shaders.skyColorG, Shaders.skyColorB);
                setProgramUniform1i("worldTime", (int)Shaders.worldTime % 24000);
                setProgramUniform1i("moonPhase", Shaders.moonPhase);
                setProgramUniform1f("frameTimeCounter", Shaders.frameTimeCounter);
                setProgramUniform1f("sunAngle", Shaders.sunAngle);
                setProgramUniform1f("shadowAngle", Shaders.shadowAngle);
                setProgramUniform1f("rainStrength", Shaders.rainStrength);
                setProgramUniform1f("aspectRatio", Shaders.renderWidth / (float)Shaders.renderHeight);
                setProgramUniform1f("viewWidth", (float)Shaders.renderWidth);
                setProgramUniform1f("viewHeight", (float)Shaders.renderHeight);
                setProgramUniform1f("near", 0.05f);
                setProgramUniform1f("far", (float)(Shaders.mc.gameSettings.renderDistanceChunks * 16));
                setProgramUniform3f("sunPosition", Shaders.sunPosition[0], Shaders.sunPosition[1], Shaders.sunPosition[2]);
                setProgramUniform3f("moonPosition", Shaders.moonPosition[0], Shaders.moonPosition[1], Shaders.moonPosition[2]);
                setProgramUniform3f("shadowLightPosition", Shaders.shadowLightPosition[0], Shaders.shadowLightPosition[1], Shaders.shadowLightPosition[2]);
                setProgramUniform3f("upPosition", Shaders.upPosition[0], Shaders.upPosition[1], Shaders.upPosition[2]);
                setProgramUniform3f("previousCameraPosition", (float)Shaders.previousCameraPositionX, (float)Shaders.previousCameraPositionY, (float)Shaders.previousCameraPositionZ);
                setProgramUniform3f("cameraPosition", (float)Shaders.cameraPositionX, (float)Shaders.cameraPositionY, (float)Shaders.cameraPositionZ);
                setProgramUniformMatrix4ARB("gbufferModelView", false, Shaders.modelView);
                setProgramUniformMatrix4ARB("gbufferModelViewInverse", false, Shaders.modelViewInverse);
                setProgramUniformMatrix4ARB("gbufferPreviousProjection", false, Shaders.previousProjection);
                setProgramUniformMatrix4ARB("gbufferProjection", false, Shaders.projection);
                setProgramUniformMatrix4ARB("gbufferProjectionInverse", false, Shaders.projectionInverse);
                setProgramUniformMatrix4ARB("gbufferPreviousModelView", false, Shaders.previousModelView);
                if (Shaders.usedShadowDepthBuffers > 0) {
                    setProgramUniformMatrix4ARB("shadowProjection", false, Shaders.shadowProjection);
                    setProgramUniformMatrix4ARB("shadowProjectionInverse", false, Shaders.shadowProjectionInverse);
                    setProgramUniformMatrix4ARB("shadowModelView", false, Shaders.shadowModelView);
                    setProgramUniformMatrix4ARB("shadowModelViewInverse", false, Shaders.shadowModelViewInverse);
                }
                setProgramUniform1f("wetness", Shaders.wetness);
                setProgramUniform1f("eyeAltitude", Shaders.eyePosY);
                setProgramUniform2i("eyeBrightness", Shaders.eyeBrightness & 0xFFFF, Shaders.eyeBrightness >> 16);
                setProgramUniform2i("eyeBrightnessSmooth", Math.round(Shaders.eyeBrightnessFadeX), Math.round(Shaders.eyeBrightnessFadeY));
                setProgramUniform2i("terrainTextureSize", Shaders.terrainTextureSize[0], Shaders.terrainTextureSize[1]);
                setProgramUniform1i("terrainIconSize", Shaders.terrainIconSize);
                setProgramUniform1i("isEyeInWater", Shaders.isEyeInWater);
                setProgramUniform1i("hideGUI", Shaders.mc.gameSettings.hideGUI ? 1 : 0);
                setProgramUniform1f("centerDepthSmooth", Shaders.centerDepthSmooth);
                setProgramUniform2i("atlasSize", Shaders.atlasSizeX, Shaders.atlasSizeY);
                checkGLError("useProgram ", Shaders.programNames[program]);
            }
        }
    }
    
    public static void setProgramUniform1i(final String name, final int x) {
        final int gp = Shaders.programsID[Shaders.activeProgram];
        if (gp != 0) {
            final int uniform = ARBShaderObjects.glGetUniformLocationARB(gp, (CharSequence)name);
            ARBShaderObjects.glUniform1iARB(uniform, x);
            checkGLError(Shaders.programNames[Shaders.activeProgram], name);
        }
    }
    
    public static void setProgramUniform2i(final String name, final int x, final int y) {
        final int gp = Shaders.programsID[Shaders.activeProgram];
        if (gp != 0) {
            final int uniform = ARBShaderObjects.glGetUniformLocationARB(gp, (CharSequence)name);
            ARBShaderObjects.glUniform2iARB(uniform, x, y);
            checkGLError(Shaders.programNames[Shaders.activeProgram], name);
        }
    }
    
    public static void setProgramUniform1f(final String name, final float x) {
        final int gp = Shaders.programsID[Shaders.activeProgram];
        if (gp != 0) {
            final int uniform = ARBShaderObjects.glGetUniformLocationARB(gp, (CharSequence)name);
            ARBShaderObjects.glUniform1fARB(uniform, x);
            checkGLError(Shaders.programNames[Shaders.activeProgram], name);
        }
    }
    
    public static void setProgramUniform3f(final String name, final float x, final float y, final float z) {
        final int gp = Shaders.programsID[Shaders.activeProgram];
        if (gp != 0) {
            final int uniform = ARBShaderObjects.glGetUniformLocationARB(gp, (CharSequence)name);
            ARBShaderObjects.glUniform3fARB(uniform, x, y, z);
            checkGLError(Shaders.programNames[Shaders.activeProgram], name);
        }
    }
    
    public static void setProgramUniformMatrix4ARB(final String name, final boolean transpose, final FloatBuffer matrix) {
        final int gp = Shaders.programsID[Shaders.activeProgram];
        if (gp != 0 && matrix != null) {
            final int uniform = ARBShaderObjects.glGetUniformLocationARB(gp, (CharSequence)name);
            ARBShaderObjects.glUniformMatrix4ARB(uniform, transpose, matrix);
            checkGLError(Shaders.programNames[Shaders.activeProgram], name);
        }
    }
    
    private static int getBufferIndexFromString(final String name) {
        return (!name.equals("colortex0") && !name.equals("gcolor")) ? ((!name.equals("colortex1") && !name.equals("gdepth")) ? ((!name.equals("colortex2") && !name.equals("gnormal")) ? ((!name.equals("colortex3") && !name.equals("composite")) ? ((!name.equals("colortex4") && !name.equals("gaux1")) ? ((!name.equals("colortex5") && !name.equals("gaux2")) ? ((!name.equals("colortex6") && !name.equals("gaux3")) ? ((!name.equals("colortex7") && !name.equals("gaux4")) ? -1 : 7) : 6) : 5) : 4) : 3) : 2) : 1) : 0;
    }
    
    private static int getTextureFormatFromString(String par) {
        par = par.trim();
        for (int i = 0; i < Shaders.formatNames.length; ++i) {
            final String name = Shaders.formatNames[i];
            if (par.equals(name)) {
                return Shaders.formatIds[i];
            }
        }
        return 0;
    }
    
    private static void setupNoiseTexture() {
        if (Shaders.noiseTexture == null) {
            Shaders.noiseTexture = new HFNoiseTexture(Shaders.noiseTextureResolution, Shaders.noiseTextureResolution);
        }
    }
    
    private static void loadEntityDataMap() {
        Shaders.mapBlockToEntityData = new IdentityHashMap<Block, Integer>(300);
        if (Shaders.mapBlockToEntityData.isEmpty()) {
            for (final ResourceLocation e : Block.blockRegistry.getKeys()) {
                final Block m = (Block)Block.blockRegistry.getObject(e);
                final int name = Block.blockRegistry.getIDForObject(m);
                Shaders.mapBlockToEntityData.put(m, name);
            }
        }
        BufferedReader reader2 = null;
        try {
            reader2 = new BufferedReader(new InputStreamReader(Shaders.shaderPack.getResourceAsStream("/mc_Entity_x.txt")));
        }
        catch (Exception ex) {}
        if (reader2 != null) {
            try {
                String e2;
                while ((e2 = reader2.readLine()) != null) {
                    final Matcher m2 = Shaders.patternLoadEntityDataMap.matcher(e2);
                    if (m2.matches()) {
                        final String name2 = m2.group(1);
                        final String value = m2.group(2);
                        final int id = Integer.parseInt(value);
                        final Block block = Block.getBlockFromName(name2);
                        if (block != null) {
                            Shaders.mapBlockToEntityData.put(block, id);
                        }
                        else {
                            SMCLog.warning("Unknown block name %s", name2);
                        }
                    }
                    else {
                        SMCLog.warning("unmatched %s\n", e2);
                    }
                }
            }
            catch (Exception var9) {
                SMCLog.warning("Error parsing mc_Entity_x.txt");
            }
        }
        if (reader2 != null) {
            try {
                reader2.close();
            }
            catch (Exception ex2) {}
        }
    }
    
    private static IntBuffer fillIntBufferZero(final IntBuffer buf) {
        for (int limit = buf.limit(), i = buf.position(); i < limit; ++i) {
            buf.put(i, 0);
        }
        return buf;
    }
    
    public static void uninit() {
        if (Shaders.isShaderPackInitialized) {
            checkGLError("Shaders.uninit pre");
            for (int i = 0; i < 33; ++i) {
                if (Shaders.programsRef[i] != 0) {
                    ARBShaderObjects.glDeleteObjectARB(Shaders.programsRef[i]);
                    checkGLError("del programRef");
                }
                Shaders.programsRef[i] = 0;
                Shaders.programsID[i] = 0;
                Shaders.programsDrawBufSettings[i] = null;
                Shaders.programsDrawBuffers[i] = null;
                Shaders.programsCompositeMipmapSetting[i] = 0;
            }
            if (Shaders.dfb != 0) {
                EXTFramebufferObject.glDeleteFramebuffersEXT(Shaders.dfb);
                Shaders.dfb = 0;
                checkGLError("del dfb");
            }
            if (Shaders.sfb != 0) {
                EXTFramebufferObject.glDeleteFramebuffersEXT(Shaders.sfb);
                Shaders.sfb = 0;
                checkGLError("del sfb");
            }
            if (Shaders.dfbDepthTextures != null) {
                GlStateManager.deleteTextures(Shaders.dfbDepthTextures);
                fillIntBufferZero(Shaders.dfbDepthTextures);
                checkGLError("del dfbDepthTextures");
            }
            if (Shaders.dfbColorTextures != null) {
                GlStateManager.deleteTextures(Shaders.dfbColorTextures);
                fillIntBufferZero(Shaders.dfbColorTextures);
                checkGLError("del dfbTextures");
            }
            if (Shaders.sfbDepthTextures != null) {
                GlStateManager.deleteTextures(Shaders.sfbDepthTextures);
                fillIntBufferZero(Shaders.sfbDepthTextures);
                checkGLError("del shadow depth");
            }
            if (Shaders.sfbColorTextures != null) {
                GlStateManager.deleteTextures(Shaders.sfbColorTextures);
                fillIntBufferZero(Shaders.sfbColorTextures);
                checkGLError("del shadow color");
            }
            if (Shaders.dfbDrawBuffers != null) {
                fillIntBufferZero(Shaders.dfbDrawBuffers);
            }
            if (Shaders.noiseTexture != null) {
                Shaders.noiseTexture.destroy();
                Shaders.noiseTexture = null;
            }
            SMCLog.info("Uninit");
            Shaders.shadowPassInterval = 0;
            Shaders.shouldSkipDefaultShadow = false;
            Shaders.isShaderPackInitialized = false;
            checkGLError("Shaders.uninit");
        }
    }
    
    public static void scheduleResize() {
        Shaders.renderDisplayHeight = 0;
    }
    
    public static void scheduleResizeShadow() {
        Shaders.needResizeShadow = true;
    }
    
    private static void resize() {
        Shaders.renderDisplayWidth = Shaders.mc.displayWidth;
        Shaders.renderDisplayHeight = Shaders.mc.displayHeight;
        Shaders.renderWidth = Math.round(Shaders.renderDisplayWidth * Shaders.configRenderResMul);
        Shaders.renderHeight = Math.round(Shaders.renderDisplayHeight * Shaders.configRenderResMul);
        setupFrameBuffer();
    }
    
    private static void resizeShadow() {
        Shaders.needResizeShadow = false;
        Shaders.shadowMapWidth = Math.round(Shaders.spShadowMapWidth * Shaders.configShadowResMul);
        Shaders.shadowMapHeight = Math.round(Shaders.spShadowMapHeight * Shaders.configShadowResMul);
        setupShadowFrameBuffer();
    }
    
    private static void setupFrameBuffer() {
        if (Shaders.dfb != 0) {
            EXTFramebufferObject.glDeleteFramebuffersEXT(Shaders.dfb);
            GlStateManager.deleteTextures(Shaders.dfbDepthTextures);
            GlStateManager.deleteTextures(Shaders.dfbColorTextures);
        }
        Shaders.dfb = EXTFramebufferObject.glGenFramebuffersEXT();
        GL11.glGenTextures((IntBuffer)Shaders.dfbDepthTextures.clear().limit(Shaders.usedDepthBuffers));
        GL11.glGenTextures((IntBuffer)Shaders.dfbColorTextures.clear().limit(16));
        Shaders.dfbDepthTextures.position(0);
        Shaders.dfbColorTextures.position(0);
        Shaders.dfbColorTextures.get(Shaders.dfbColorTexturesA).position(0);
        EXTFramebufferObject.glBindFramebufferEXT(36160, Shaders.dfb);
        GL20.glDrawBuffers(0);
        GL11.glReadBuffer(0);
        for (int status = 0; status < Shaders.usedDepthBuffers; ++status) {
            GlStateManager.func_179144_i(Shaders.dfbDepthTextures.get(status));
            GL11.glTexParameteri(3553, 10242, 10496);
            GL11.glTexParameteri(3553, 10243, 10496);
            GL11.glTexParameteri(3553, 10241, 9728);
            GL11.glTexParameteri(3553, 10240, 9728);
            GL11.glTexParameteri(3553, 34891, 6409);
            GL11.glTexImage2D(3553, 0, 6402, Shaders.renderWidth, Shaders.renderHeight, 0, 6402, 5126, (FloatBuffer)null);
        }
        EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, Shaders.dfbDepthTextures.get(0), 0);
        GL20.glDrawBuffers(Shaders.dfbDrawBuffers);
        GL11.glReadBuffer(0);
        checkGLError("FT d");
        for (int status = 0; status < Shaders.usedColorBuffers; ++status) {
            GlStateManager.func_179144_i(Shaders.dfbColorTexturesA[status]);
            GL11.glTexParameteri(3553, 10242, 10496);
            GL11.glTexParameteri(3553, 10243, 10496);
            GL11.glTexParameteri(3553, 10241, 9729);
            GL11.glTexParameteri(3553, 10240, 9729);
            GL11.glTexImage2D(3553, 0, Shaders.gbuffersFormat[status], Shaders.renderWidth, Shaders.renderHeight, 0, 32993, 33639, (ByteBuffer)null);
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + status, 3553, Shaders.dfbColorTexturesA[status], 0);
            checkGLError("FT c");
        }
        for (int status = 0; status < Shaders.usedColorBuffers; ++status) {
            GlStateManager.func_179144_i(Shaders.dfbColorTexturesA[8 + status]);
            GL11.glTexParameteri(3553, 10242, 10496);
            GL11.glTexParameteri(3553, 10243, 10496);
            GL11.glTexParameteri(3553, 10241, 9729);
            GL11.glTexParameteri(3553, 10240, 9729);
            GL11.glTexImage2D(3553, 0, Shaders.gbuffersFormat[status], Shaders.renderWidth, Shaders.renderHeight, 0, 32993, 33639, (ByteBuffer)null);
            checkGLError("FT ca");
        }
        int status = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
        if (status == 36058) {
            printChatAndLogError("[Shaders] Error: Failed framebuffer incomplete formats");
            for (int i = 0; i < Shaders.usedColorBuffers; ++i) {
                GlStateManager.func_179144_i(Shaders.dfbColorTextures.get(i));
                GL11.glTexImage2D(3553, 0, 6408, Shaders.renderWidth, Shaders.renderHeight, 0, 32993, 33639, (ByteBuffer)null);
                EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + i, 3553, Shaders.dfbColorTextures.get(i), 0);
                checkGLError("FT c");
            }
            status = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
            if (status == 36053) {
                SMCLog.info("complete");
            }
        }
        GlStateManager.func_179144_i(0);
        if (status != 36053) {
            printChatAndLogError("[Shaders] Error: Failed creating framebuffer! (Status " + status + ")");
        }
        else {
            SMCLog.info("Framebuffer created.");
        }
    }
    
    private static void setupShadowFrameBuffer() {
        if (Shaders.usedShadowDepthBuffers != 0) {
            if (Shaders.sfb != 0) {
                EXTFramebufferObject.glDeleteFramebuffersEXT(Shaders.sfb);
                GlStateManager.deleteTextures(Shaders.sfbDepthTextures);
                GlStateManager.deleteTextures(Shaders.sfbColorTextures);
            }
            EXTFramebufferObject.glBindFramebufferEXT(36160, Shaders.sfb = EXTFramebufferObject.glGenFramebuffersEXT());
            GL11.glDrawBuffer(0);
            GL11.glReadBuffer(0);
            GL11.glGenTextures((IntBuffer)Shaders.sfbDepthTextures.clear().limit(Shaders.usedShadowDepthBuffers));
            GL11.glGenTextures((IntBuffer)Shaders.sfbColorTextures.clear().limit(Shaders.usedShadowColorBuffers));
            Shaders.sfbDepthTextures.position(0);
            Shaders.sfbColorTextures.position(0);
            for (int status = 0; status < Shaders.usedShadowDepthBuffers; ++status) {
                GlStateManager.func_179144_i(Shaders.sfbDepthTextures.get(status));
                GL11.glTexParameterf(3553, 10242, 10496.0f);
                GL11.glTexParameterf(3553, 10243, 10496.0f);
                final int filter = Shaders.shadowFilterNearest[status] ? 9728 : 9729;
                GL11.glTexParameteri(3553, 10241, filter);
                GL11.glTexParameteri(3553, 10240, filter);
                if (Shaders.shadowHardwareFilteringEnabled[status]) {
                    GL11.glTexParameteri(3553, 34892, 34894);
                }
                GL11.glTexImage2D(3553, 0, 6402, Shaders.shadowMapWidth, Shaders.shadowMapHeight, 0, 6402, 5126, (FloatBuffer)null);
            }
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, Shaders.sfbDepthTextures.get(0), 0);
            checkGLError("FT sd");
            for (int status = 0; status < Shaders.usedShadowColorBuffers; ++status) {
                GlStateManager.func_179144_i(Shaders.sfbColorTextures.get(status));
                GL11.glTexParameterf(3553, 10242, 10496.0f);
                GL11.glTexParameterf(3553, 10243, 10496.0f);
                final int filter = Shaders.shadowColorFilterNearest[status] ? 9728 : 9729;
                GL11.glTexParameteri(3553, 10241, filter);
                GL11.glTexParameteri(3553, 10240, filter);
                GL11.glTexImage2D(3553, 0, 6408, Shaders.shadowMapWidth, Shaders.shadowMapHeight, 0, 32993, 33639, (ByteBuffer)null);
                EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + status, 3553, Shaders.sfbColorTextures.get(status), 0);
                checkGLError("FT sc");
            }
            GlStateManager.func_179144_i(0);
            if (Shaders.usedShadowColorBuffers > 0) {
                GL20.glDrawBuffers(Shaders.sfbDrawBuffers);
            }
            int status = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
            if (status != 36053) {
                printChatAndLogError("[Shaders] Error: Failed creating shadow framebuffer! (Status " + status + ")");
            }
            else {
                SMCLog.info("Shadow framebuffer created.");
            }
        }
    }
    
    public static void beginRender(final Minecraft minecraft, final float partialTicks, final long finishTimeNano) {
        checkGLError("pre beginRender");
        checkWorldChanged(Shaders.mc.theWorld);
        Shaders.mc = minecraft;
        Shaders.mc.mcProfiler.startSection("init");
        Shaders.entityRenderer = Shaders.mc.entityRenderer;
        if (!Shaders.isShaderPackInitialized) {
            try {
                init();
            }
            catch (IllegalStateException var7) {
                if (Config.normalize(var7.getMessage()).equals("Function is not supported")) {
                    printChatAndLogError("[Shaders] Error: " + var7.getMessage());
                    var7.printStackTrace();
                    setShaderPack(Shaders.packNameNone);
                    return;
                }
            }
        }
        if (Shaders.mc.displayWidth != Shaders.renderDisplayWidth || Shaders.mc.displayHeight != Shaders.renderDisplayHeight) {
            resize();
        }
        if (Shaders.needResizeShadow) {
            resizeShadow();
        }
        Shaders.worldTime = Shaders.mc.theWorld.getWorldTime();
        Shaders.diffWorldTime = (Shaders.worldTime - Shaders.lastWorldTime) % 24000L;
        if (Shaders.diffWorldTime < 0L) {
            Shaders.diffWorldTime += 24000L;
        }
        Shaders.lastWorldTime = Shaders.worldTime;
        Shaders.moonPhase = Shaders.mc.theWorld.getMoonPhase();
        Shaders.systemTime = System.currentTimeMillis();
        if (Shaders.lastSystemTime == 0L) {
            Shaders.lastSystemTime = Shaders.systemTime;
        }
        Shaders.diffSystemTime = Shaders.systemTime - Shaders.lastSystemTime;
        Shaders.lastSystemTime = Shaders.systemTime;
        Shaders.frameTimeCounter += Shaders.diffSystemTime * 0.001f;
        Shaders.frameTimeCounter %= 3600.0f;
        Shaders.rainStrength = minecraft.theWorld.getRainStrength(partialTicks);
        final float renderViewEntity = Shaders.diffSystemTime * 0.01f;
        float i = (float)Math.exp(Math.log(0.5) * renderViewEntity / ((Shaders.wetness < Shaders.rainStrength) ? Shaders.drynessHalfLife : Shaders.wetnessHalfLife));
        Shaders.wetness = Shaders.wetness * i + Shaders.rainStrength * (1.0f - i);
        final Entity var8 = Shaders.mc.getRenderViewEntity();
        Shaders.isSleeping = (var8 instanceof EntityLivingBase && ((EntityLivingBase)var8).isPlayerSleeping());
        Shaders.eyePosY = (float)var8.posY * partialTicks + (float)var8.lastTickPosY * (1.0f - partialTicks);
        Shaders.eyeBrightness = var8.getBrightnessForRender(partialTicks);
        i = Shaders.diffSystemTime * 0.01f;
        final float temp2 = (float)Math.exp(Math.log(0.5) * i / Shaders.eyeBrightnessHalflife);
        Shaders.eyeBrightnessFadeX = Shaders.eyeBrightnessFadeX * temp2 + (Shaders.eyeBrightness & 0xFFFF) * (1.0f - temp2);
        Shaders.eyeBrightnessFadeY = Shaders.eyeBrightnessFadeY * temp2 + (Shaders.eyeBrightness >> 16) * (1.0f - temp2);
        Shaders.isEyeInWater = ((Shaders.mc.gameSettings.thirdPersonView == 0 && !Shaders.isSleeping && Shaders.mc.thePlayer.isInsideOfMaterial(Material.water)) ? 1 : 0);
        final Vec3 var9 = Shaders.mc.theWorld.getSkyColor(Shaders.mc.getRenderViewEntity(), partialTicks);
        Shaders.skyColorR = (float)var9.xCoord;
        Shaders.skyColorG = (float)var9.yCoord;
        Shaders.skyColorB = (float)var9.zCoord;
        Shaders.isRenderingWorld = true;
        Shaders.isCompositeRendered = false;
        Shaders.isHandRendered = false;
        if (Shaders.usedShadowDepthBuffers >= 1) {
            GlStateManager.setActiveTexture(33988);
            GlStateManager.func_179144_i(Shaders.sfbDepthTextures.get(0));
            if (Shaders.usedShadowDepthBuffers >= 2) {
                GlStateManager.setActiveTexture(33989);
                GlStateManager.func_179144_i(Shaders.sfbDepthTextures.get(1));
            }
        }
        GlStateManager.setActiveTexture(33984);
        for (int var10 = 0; var10 < Shaders.usedColorBuffers; ++var10) {
            GlStateManager.func_179144_i(Shaders.dfbColorTexturesA[var10]);
            GL11.glTexParameteri(3553, 10240, 9729);
            GL11.glTexParameteri(3553, 10241, 9729);
            GlStateManager.func_179144_i(Shaders.dfbColorTexturesA[8 + var10]);
            GL11.glTexParameteri(3553, 10240, 9729);
            GL11.glTexParameteri(3553, 10241, 9729);
        }
        GlStateManager.func_179144_i(0);
        for (int var10 = 0; var10 < 4 && 4 + var10 < Shaders.usedColorBuffers; ++var10) {
            GlStateManager.setActiveTexture(33991 + var10);
            GlStateManager.func_179144_i(Shaders.dfbColorTextures.get(4 + var10));
        }
        GlStateManager.setActiveTexture(33990);
        GlStateManager.func_179144_i(Shaders.dfbDepthTextures.get(0));
        if (Shaders.usedDepthBuffers >= 2) {
            GlStateManager.setActiveTexture(33995);
            GlStateManager.func_179144_i(Shaders.dfbDepthTextures.get(1));
            if (Shaders.usedDepthBuffers >= 3) {
                GlStateManager.setActiveTexture(33996);
                GlStateManager.func_179144_i(Shaders.dfbDepthTextures.get(2));
            }
        }
        for (int var10 = 0; var10 < Shaders.usedShadowColorBuffers; ++var10) {
            GlStateManager.setActiveTexture(33997 + var10);
            GlStateManager.func_179144_i(Shaders.sfbColorTextures.get(var10));
        }
        if (Shaders.noiseTextureEnabled) {
            GlStateManager.setActiveTexture(33984 + Shaders.noiseTexture.textureUnit);
            GlStateManager.func_179144_i(Shaders.noiseTexture.getID());
            GL11.glTexParameteri(3553, 10242, 10497);
            GL11.glTexParameteri(3553, 10243, 10497);
            GL11.glTexParameteri(3553, 10240, 9729);
            GL11.glTexParameteri(3553, 10241, 9729);
        }
        GlStateManager.setActiveTexture(33984);
        Shaders.previousCameraPositionX = Shaders.cameraPositionX;
        Shaders.previousCameraPositionY = Shaders.cameraPositionY;
        Shaders.previousCameraPositionZ = Shaders.cameraPositionZ;
        Shaders.previousProjection.position(0);
        Shaders.projection.position(0);
        Shaders.previousProjection.put(Shaders.projection);
        Shaders.previousProjection.position(0);
        Shaders.projection.position(0);
        Shaders.previousModelView.position(0);
        Shaders.modelView.position(0);
        Shaders.previousModelView.put(Shaders.modelView);
        Shaders.previousModelView.position(0);
        Shaders.modelView.position(0);
        checkGLError("beginRender");
        ShadersRender.renderShadowMap(Shaders.entityRenderer, 0, partialTicks, finishTimeNano);
        Shaders.mc.mcProfiler.endSection();
        EXTFramebufferObject.glBindFramebufferEXT(36160, Shaders.dfb);
        for (int var10 = 0; var10 < Shaders.usedColorBuffers; ++var10) {
            Shaders.colorTexturesToggle[var10] = 0;
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + var10, 3553, Shaders.dfbColorTexturesA[var10], 0);
        }
        checkGLError("end beginRender");
    }
    
    private static void checkWorldChanged(final World world) {
        if (Shaders.currentWorld != world) {
            final World oldWorld = Shaders.currentWorld;
            Shaders.currentWorld = world;
            if (oldWorld != null && world != null) {
                final int dimIdOld = oldWorld.provider.getDimensionId();
                final int dimIdNew = world.provider.getDimensionId();
                final boolean dimShadersOld = Shaders.shaderPackDimensions.contains(dimIdOld);
                final boolean dimShadersNew = Shaders.shaderPackDimensions.contains(dimIdNew);
                if (dimShadersOld || dimShadersNew) {
                    uninit();
                }
            }
        }
    }
    
    public static void beginRenderPass(final int pass, final float partialTicks, final long finishTimeNano) {
        if (!Shaders.isShadowPass) {
            EXTFramebufferObject.glBindFramebufferEXT(36160, Shaders.dfb);
            GL11.glViewport(0, 0, Shaders.renderWidth, Shaders.renderHeight);
            Shaders.activeDrawBuffers = null;
            ShadersTex.bindNSTextures(Shaders.defaultTexture.getMultiTexID());
            useProgram(2);
            checkGLError("end beginRenderPass");
        }
    }
    
    public static void setViewport(final int vx, final int vy, final int vw, final int vh) {
        GlStateManager.colorMask(true, true, true, true);
        if (Shaders.isShadowPass) {
            GL11.glViewport(0, 0, Shaders.shadowMapWidth, Shaders.shadowMapHeight);
        }
        else {
            GL11.glViewport(0, 0, Shaders.renderWidth, Shaders.renderHeight);
            EXTFramebufferObject.glBindFramebufferEXT(36160, Shaders.dfb);
            Shaders.isRenderingDfb = true;
            GlStateManager.enableCull();
            GlStateManager.enableDepth();
            setDrawBuffers(Shaders.drawBuffersNone);
            useProgram(2);
            checkGLError("beginRenderPass");
        }
    }
    
    public static int setFogMode(final int val) {
        return Shaders.fogMode = val;
    }
    
    public static void setFogColor(final float r, final float g, final float b) {
        Shaders.fogColorR = r;
        Shaders.fogColorG = g;
        Shaders.fogColorB = b;
    }
    
    public static void setClearColor(final float red, final float green, final float blue, final float alpha) {
        GlStateManager.clearColor(red, green, blue, alpha);
        Shaders.clearColorR = red;
        Shaders.clearColorG = green;
        Shaders.clearColorB = blue;
    }
    
    public static void clearRenderBuffer() {
        if (Shaders.isShadowPass) {
            checkGLError("shadow clear pre");
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, Shaders.sfbDepthTextures.get(0), 0);
            GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            GL20.glDrawBuffers(Shaders.programsDrawBuffers[30]);
            checkFramebufferStatus("shadow clear");
            GL11.glClear(16640);
            checkGLError("shadow clear");
        }
        else {
            checkGLError("clear pre");
            GL20.glDrawBuffers(36064);
            GL11.glClear(16384);
            GL20.glDrawBuffers(36065);
            GL11.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glClear(16384);
            for (int i = 2; i < Shaders.usedColorBuffers; ++i) {
                GL20.glDrawBuffers(36064 + i);
                GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                GL11.glClear(16384);
            }
            setDrawBuffers(Shaders.dfbDrawBuffers);
            checkFramebufferStatus("clear");
            checkGLError("clear");
        }
    }
    
    public static void setCamera(final float partialTicks) {
        final Entity viewEntity = Shaders.mc.getRenderViewEntity();
        final double x = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * partialTicks;
        final double y = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * partialTicks;
        final double z = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * partialTicks;
        Shaders.cameraPositionX = x;
        Shaders.cameraPositionY = y;
        Shaders.cameraPositionZ = z;
        GL11.glGetFloat(2983, (FloatBuffer)Shaders.projection.position(0));
        SMath.invertMat4FBFA((FloatBuffer)Shaders.projectionInverse.position(0), (FloatBuffer)Shaders.projection.position(0), Shaders.faProjectionInverse, Shaders.faProjection);
        Shaders.projection.position(0);
        Shaders.projectionInverse.position(0);
        GL11.glGetFloat(2982, (FloatBuffer)Shaders.modelView.position(0));
        SMath.invertMat4FBFA((FloatBuffer)Shaders.modelViewInverse.position(0), (FloatBuffer)Shaders.modelView.position(0), Shaders.faModelViewInverse, Shaders.faModelView);
        Shaders.modelView.position(0);
        Shaders.modelViewInverse.position(0);
        checkGLError("setCamera");
    }
    
    public static void setCameraShadow(final float partialTicks) {
        final Entity viewEntity = Shaders.mc.getRenderViewEntity();
        final double x = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * partialTicks;
        final double y = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * partialTicks;
        final double z = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * partialTicks;
        Shaders.cameraPositionX = x;
        Shaders.cameraPositionY = y;
        Shaders.cameraPositionZ = z;
        GL11.glGetFloat(2983, (FloatBuffer)Shaders.projection.position(0));
        SMath.invertMat4FBFA((FloatBuffer)Shaders.projectionInverse.position(0), (FloatBuffer)Shaders.projection.position(0), Shaders.faProjectionInverse, Shaders.faProjection);
        Shaders.projection.position(0);
        Shaders.projectionInverse.position(0);
        GL11.glGetFloat(2982, (FloatBuffer)Shaders.modelView.position(0));
        SMath.invertMat4FBFA((FloatBuffer)Shaders.modelViewInverse.position(0), (FloatBuffer)Shaders.modelView.position(0), Shaders.faModelViewInverse, Shaders.faModelView);
        Shaders.modelView.position(0);
        Shaders.modelViewInverse.position(0);
        GL11.glViewport(0, 0, Shaders.shadowMapWidth, Shaders.shadowMapHeight);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        if (Shaders.shadowMapIsOrtho) {
            GL11.glOrtho((double)(-Shaders.shadowMapHalfPlane), (double)Shaders.shadowMapHalfPlane, (double)(-Shaders.shadowMapHalfPlane), (double)Shaders.shadowMapHalfPlane, 0.05000000074505806, 256.0);
        }
        else {
            GLU.gluPerspective(Shaders.shadowMapFOV, Shaders.shadowMapWidth / (float)Shaders.shadowMapHeight, 0.05f, 256.0f);
        }
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0f, 0.0f, -100.0f);
        GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
        Shaders.celestialAngle = Shaders.mc.theWorld.getCelestialAngle(partialTicks);
        Shaders.sunAngle = ((Shaders.celestialAngle < 0.75f) ? (Shaders.celestialAngle + 0.25f) : (Shaders.celestialAngle - 0.75f));
        final float angle = Shaders.celestialAngle * -360.0f;
        final float angleInterval = (Shaders.shadowAngleInterval > 0.0f) ? (angle % Shaders.shadowAngleInterval - Shaders.shadowAngleInterval * 0.5f) : 0.0f;
        if (Shaders.sunAngle <= 0.5) {
            GL11.glRotatef(angle - angleInterval, 0.0f, 0.0f, 1.0f);
            GL11.glRotatef(Shaders.sunPathRotation, 1.0f, 0.0f, 0.0f);
            Shaders.shadowAngle = Shaders.sunAngle;
        }
        else {
            GL11.glRotatef(angle + 180.0f - angleInterval, 0.0f, 0.0f, 1.0f);
            GL11.glRotatef(Shaders.sunPathRotation, 1.0f, 0.0f, 0.0f);
            Shaders.shadowAngle = Shaders.sunAngle - 0.5f;
        }
        if (Shaders.shadowMapIsOrtho) {
            final float raSun = Shaders.shadowIntervalSize;
            final float x2 = raSun / 2.0f;
            GL11.glTranslatef((float)x % raSun - x2, (float)y % raSun - x2, (float)z % raSun - x2);
        }
        final float raSun = Shaders.sunAngle * 6.2831855f;
        final float x2 = (float)Math.cos(raSun);
        final float y2 = (float)Math.sin(raSun);
        final float raTilt = Shaders.sunPathRotation * 6.2831855f;
        float x3 = x2;
        float y3 = y2 * (float)Math.cos(raTilt);
        float z2 = y2 * (float)Math.sin(raTilt);
        if (Shaders.sunAngle > 0.5) {
            x3 = -x2;
            y3 = -y3;
            z2 = -z2;
        }
        Shaders.shadowLightPositionVector[0] = x3;
        Shaders.shadowLightPositionVector[1] = y3;
        Shaders.shadowLightPositionVector[2] = z2;
        Shaders.shadowLightPositionVector[3] = 0.0f;
        GL11.glGetFloat(2983, (FloatBuffer)Shaders.shadowProjection.position(0));
        SMath.invertMat4FBFA((FloatBuffer)Shaders.shadowProjectionInverse.position(0), (FloatBuffer)Shaders.shadowProjection.position(0), Shaders.faShadowProjectionInverse, Shaders.faShadowProjection);
        Shaders.shadowProjection.position(0);
        Shaders.shadowProjectionInverse.position(0);
        GL11.glGetFloat(2982, (FloatBuffer)Shaders.shadowModelView.position(0));
        SMath.invertMat4FBFA((FloatBuffer)Shaders.shadowModelViewInverse.position(0), (FloatBuffer)Shaders.shadowModelView.position(0), Shaders.faShadowModelViewInverse, Shaders.faShadowModelView);
        Shaders.shadowModelView.position(0);
        Shaders.shadowModelViewInverse.position(0);
        setProgramUniformMatrix4ARB("gbufferProjection", false, Shaders.projection);
        setProgramUniformMatrix4ARB("gbufferProjectionInverse", false, Shaders.projectionInverse);
        setProgramUniformMatrix4ARB("gbufferPreviousProjection", false, Shaders.previousProjection);
        setProgramUniformMatrix4ARB("gbufferModelView", false, Shaders.modelView);
        setProgramUniformMatrix4ARB("gbufferModelViewInverse", false, Shaders.modelViewInverse);
        setProgramUniformMatrix4ARB("gbufferPreviousModelView", false, Shaders.previousModelView);
        setProgramUniformMatrix4ARB("shadowProjection", false, Shaders.shadowProjection);
        setProgramUniformMatrix4ARB("shadowProjectionInverse", false, Shaders.shadowProjectionInverse);
        setProgramUniformMatrix4ARB("shadowModelView", false, Shaders.shadowModelView);
        setProgramUniformMatrix4ARB("shadowModelViewInverse", false, Shaders.shadowModelViewInverse);
        Shaders.mc.gameSettings.thirdPersonView = 1;
        checkGLError("setCamera");
    }
    
    public static void preCelestialRotate() {
        setUpPosition();
        GL11.glRotatef(Shaders.sunPathRotation * 1.0f, 0.0f, 0.0f, 1.0f);
        checkGLError("preCelestialRotate");
    }
    
    public static void postCelestialRotate() {
        final FloatBuffer modelView = Shaders.tempMatrixDirectBuffer;
        modelView.clear();
        GL11.glGetFloat(2982, modelView);
        modelView.get(Shaders.tempMat, 0, 16);
        SMath.multiplyMat4xVec4(Shaders.sunPosition, Shaders.tempMat, Shaders.sunPosModelView);
        SMath.multiplyMat4xVec4(Shaders.moonPosition, Shaders.tempMat, Shaders.moonPosModelView);
        System.arraycopy((Shaders.shadowAngle == Shaders.sunAngle) ? Shaders.sunPosition : Shaders.moonPosition, 0, Shaders.shadowLightPosition, 0, 3);
        checkGLError("postCelestialRotate");
    }
    
    public static void setUpPosition() {
        final FloatBuffer modelView = Shaders.tempMatrixDirectBuffer;
        modelView.clear();
        GL11.glGetFloat(2982, modelView);
        modelView.get(Shaders.tempMat, 0, 16);
        SMath.multiplyMat4xVec4(Shaders.upPosition, Shaders.tempMat, Shaders.upPosModelView);
    }
    
    public static void genCompositeMipmap() {
        if (Shaders.hasGlGenMipmap) {
            for (int i = 0; i < Shaders.usedColorBuffers; ++i) {
                if ((Shaders.activeCompositeMipmapSetting & 1 << i) != 0x0) {
                    GlStateManager.setActiveTexture(33984 + Shaders.colorTextureTextureImageUnit[i]);
                    GL11.glTexParameteri(3553, 10241, 9987);
                    GL30.glGenerateMipmap(3553);
                }
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
        if (!Shaders.isShadowPass) {
            checkGLError("pre-renderCompositeFinal");
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glMatrixMode(5889);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glOrtho(0.0, 1.0, 0.0, 1.0, 0.0, 1.0);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.func_179098_w();
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.enableDepth();
            GlStateManager.depthFunc(519);
            GlStateManager.depthMask(false);
            GlStateManager.disableLighting();
            if (Shaders.usedShadowDepthBuffers >= 1) {
                GlStateManager.setActiveTexture(33988);
                GlStateManager.func_179144_i(Shaders.sfbDepthTextures.get(0));
                if (Shaders.usedShadowDepthBuffers >= 2) {
                    GlStateManager.setActiveTexture(33989);
                    GlStateManager.func_179144_i(Shaders.sfbDepthTextures.get(1));
                }
            }
            for (int enableAltBuffers = 0; enableAltBuffers < Shaders.usedColorBuffers; ++enableAltBuffers) {
                GlStateManager.setActiveTexture(33984 + Shaders.colorTextureTextureImageUnit[enableAltBuffers]);
                GlStateManager.func_179144_i(Shaders.dfbColorTexturesA[enableAltBuffers]);
            }
            GlStateManager.setActiveTexture(33990);
            GlStateManager.func_179144_i(Shaders.dfbDepthTextures.get(0));
            if (Shaders.usedDepthBuffers >= 2) {
                GlStateManager.setActiveTexture(33995);
                GlStateManager.func_179144_i(Shaders.dfbDepthTextures.get(1));
                if (Shaders.usedDepthBuffers >= 3) {
                    GlStateManager.setActiveTexture(33996);
                    GlStateManager.func_179144_i(Shaders.dfbDepthTextures.get(2));
                }
            }
            for (int enableAltBuffers = 0; enableAltBuffers < Shaders.usedShadowColorBuffers; ++enableAltBuffers) {
                GlStateManager.setActiveTexture(33997 + enableAltBuffers);
                GlStateManager.func_179144_i(Shaders.sfbColorTextures.get(enableAltBuffers));
            }
            if (Shaders.noiseTextureEnabled) {
                GlStateManager.setActiveTexture(33984 + Shaders.noiseTexture.textureUnit);
                GlStateManager.func_179144_i(Shaders.noiseTexture.getID());
                GL11.glTexParameteri(3553, 10242, 10497);
                GL11.glTexParameteri(3553, 10243, 10497);
                GL11.glTexParameteri(3553, 10240, 9729);
                GL11.glTexParameteri(3553, 10241, 9729);
            }
            GlStateManager.setActiveTexture(33984);
            final boolean var5 = true;
            for (int maskR = 0; maskR < Shaders.usedColorBuffers; ++maskR) {
                EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + maskR, 3553, Shaders.dfbColorTexturesA[8 + maskR], 0);
            }
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, Shaders.dfbDepthTextures.get(0), 0);
            GL20.glDrawBuffers(Shaders.dfbDrawBuffers);
            checkGLError("pre-composite");
            for (int maskR = 0; maskR < 8; ++maskR) {
                if (Shaders.programsID[21 + maskR] != 0) {
                    useProgram(21 + maskR);
                    checkGLError(Shaders.programNames[21 + maskR]);
                    if (Shaders.activeCompositeMipmapSetting != 0) {
                        genCompositeMipmap();
                    }
                    drawComposite();
                    for (int i = 0; i < Shaders.usedColorBuffers; ++i) {
                        if (Shaders.programsToggleColorTextures[21 + maskR][i]) {
                            final int t0 = Shaders.colorTexturesToggle[i];
                            final int[] colorTexturesToggle = Shaders.colorTexturesToggle;
                            final int n = i;
                            final int n2 = 8 - t0;
                            colorTexturesToggle[n] = n2;
                            final int t2 = n2;
                            GlStateManager.setActiveTexture(33984 + Shaders.colorTextureTextureImageUnit[i]);
                            GlStateManager.func_179144_i(Shaders.dfbColorTexturesA[t2 + i]);
                            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + i, 3553, Shaders.dfbColorTexturesA[t0 + i], 0);
                        }
                    }
                    GlStateManager.setActiveTexture(33984);
                }
            }
            checkGLError("composite");
            Shaders.isRenderingDfb = false;
            Shaders.mc.getFramebuffer().bindFramebuffer(true);
            OpenGlHelper.func_153188_a(OpenGlHelper.field_153198_e, OpenGlHelper.field_153200_g, 3553, Shaders.mc.getFramebuffer().framebufferTexture, 0);
            GL11.glViewport(0, 0, Shaders.mc.displayWidth, Shaders.mc.displayHeight);
            if (EntityRenderer.anaglyphEnable) {
                final boolean var6 = EntityRenderer.anaglyphField != 0;
                GlStateManager.colorMask(var6, !var6, !var6, true);
            }
            GlStateManager.depthMask(true);
            GL11.glClearColor(Shaders.clearColorR, Shaders.clearColorG, Shaders.clearColorB, 1.0f);
            GL11.glClear(16640);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.func_179098_w();
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.enableDepth();
            GlStateManager.depthFunc(519);
            GlStateManager.depthMask(false);
            checkGLError("pre-final");
            useProgram(29);
            checkGLError("final");
            if (Shaders.activeCompositeMipmapSetting != 0) {
                genCompositeMipmap();
            }
            drawComposite();
            checkGLError("renderCompositeFinal");
            Shaders.isCompositeRendered = true;
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
    
    public static void endRender() {
        if (Shaders.isShadowPass) {
            checkGLError("shadow endRender");
        }
        else {
            if (!Shaders.isCompositeRendered) {
                renderCompositeFinal();
            }
            Shaders.isRenderingWorld = false;
            GlStateManager.colorMask(true, true, true, true);
            useProgram(0);
            RenderHelper.disableStandardItemLighting();
            checkGLError("endRender end");
        }
    }
    
    public static void beginSky() {
        Shaders.isRenderingSky = true;
        Shaders.fogEnabled = true;
        setDrawBuffers(Shaders.dfbDrawBuffers);
        useProgram(5);
        pushEntity(-2, 0);
    }
    
    public static void setSkyColor(final Vec3 v3color) {
        Shaders.skyColorR = (float)v3color.xCoord;
        Shaders.skyColorG = (float)v3color.yCoord;
        Shaders.skyColorB = (float)v3color.zCoord;
        setProgramUniform3f("skyColor", Shaders.skyColorR, Shaders.skyColorG, Shaders.skyColorB);
    }
    
    public static void drawHorizon() {
        final WorldRenderer tess = Tessellator.getInstance().getWorldRenderer();
        final float farDistance = (float)(Shaders.mc.gameSettings.renderDistanceChunks * 16);
        final double xzq = farDistance * 0.9238;
        final double xzp = farDistance * 0.3826;
        final double xzn = -xzp;
        final double xzm = -xzq;
        final double top = 16.0;
        final double bot = -Shaders.cameraPositionY;
        tess.startDrawingQuads();
        tess.addVertex(xzn, bot, xzm);
        tess.addVertex(xzn, top, xzm);
        tess.addVertex(xzm, top, xzn);
        tess.addVertex(xzm, bot, xzn);
        tess.addVertex(xzm, bot, xzn);
        tess.addVertex(xzm, top, xzn);
        tess.addVertex(xzm, top, xzp);
        tess.addVertex(xzm, bot, xzp);
        tess.addVertex(xzm, bot, xzp);
        tess.addVertex(xzm, top, xzp);
        tess.addVertex(xzn, top, xzp);
        tess.addVertex(xzn, bot, xzp);
        tess.addVertex(xzn, bot, xzp);
        tess.addVertex(xzn, top, xzp);
        tess.addVertex(xzp, top, xzq);
        tess.addVertex(xzp, bot, xzq);
        tess.addVertex(xzp, bot, xzq);
        tess.addVertex(xzp, top, xzq);
        tess.addVertex(xzq, top, xzp);
        tess.addVertex(xzq, bot, xzp);
        tess.addVertex(xzq, bot, xzp);
        tess.addVertex(xzq, top, xzp);
        tess.addVertex(xzq, top, xzn);
        tess.addVertex(xzq, bot, xzn);
        tess.addVertex(xzq, bot, xzn);
        tess.addVertex(xzq, top, xzn);
        tess.addVertex(xzp, top, xzm);
        tess.addVertex(xzp, bot, xzm);
        tess.addVertex(xzp, bot, xzm);
        tess.addVertex(xzp, top, xzm);
        tess.addVertex(xzn, top, xzm);
        tess.addVertex(xzn, bot, xzm);
        Tessellator.getInstance().draw();
    }
    
    public static void preSkyList() {
        GL11.glColor3f(Shaders.fogColorR, Shaders.fogColorG, Shaders.fogColorB);
        drawHorizon();
        GL11.glColor3f(Shaders.skyColorR, Shaders.skyColorG, Shaders.skyColorB);
    }
    
    public static void endSky() {
        Shaders.isRenderingSky = false;
        setDrawBuffers(Shaders.dfbDrawBuffers);
        useProgram(Shaders.lightmapEnabled ? 3 : 2);
        popEntity();
    }
    
    public static void beginUpdateChunks() {
        checkGLError("beginUpdateChunks1");
        checkFramebufferStatus("beginUpdateChunks1");
        if (!Shaders.isShadowPass) {
            useProgram(7);
        }
        checkGLError("beginUpdateChunks2");
        checkFramebufferStatus("beginUpdateChunks2");
    }
    
    public static void endUpdateChunks() {
        checkGLError("endUpdateChunks1");
        checkFramebufferStatus("endUpdateChunks1");
        if (!Shaders.isShadowPass) {
            useProgram(7);
        }
        checkGLError("endUpdateChunks2");
        checkFramebufferStatus("endUpdateChunks2");
    }
    
    public static boolean shouldRenderClouds(final GameSettings gs) {
        if (!Shaders.shaderPackLoaded) {
            return true;
        }
        checkGLError("shouldRenderClouds");
        return Shaders.isShadowPass ? Shaders.configCloudShadow : gs.clouds;
    }
    
    public static void beginClouds() {
        Shaders.fogEnabled = true;
        pushEntity(-3, 0);
        useProgram(6);
    }
    
    public static void endClouds() {
        disableFog();
        popEntity();
        useProgram(Shaders.lightmapEnabled ? 3 : 2);
    }
    
    public static void beginEntities() {
        if (Shaders.isRenderingWorld) {
            useProgram(16);
            resetDisplayListModels();
        }
    }
    
    public static void nextEntity(final Entity entity) {
        if (Shaders.isRenderingWorld) {
            useProgram(16);
            setEntityId(entity);
        }
    }
    
    public static void setEntityId(final Entity entity) {
        if (Shaders.isRenderingWorld && !Shaders.isShadowPass && Shaders.uniformEntityId.isDefined()) {
            final int id = EntityList.getEntityID(entity);
            Shaders.uniformEntityId.setValue(id);
        }
    }
    
    public static void beginSpiderEyes() {
        if (Shaders.isRenderingWorld && Shaders.programsID[18] != Shaders.programsID[0]) {
            useProgram(18);
            GlStateManager.enableAlpha();
            GlStateManager.alphaFunc(516, 0.0f);
            GlStateManager.blendFunc(770, 771);
        }
    }
    
    public static void endEntities() {
        if (Shaders.isRenderingWorld) {
            useProgram(Shaders.lightmapEnabled ? 3 : 2);
        }
    }
    
    public static void setEntityColor(final float r, final float g, final float b, final float a) {
        if (Shaders.isRenderingWorld && !Shaders.isShadowPass) {
            Shaders.uniformEntityColor.setValue(r, g, b, a);
        }
    }
    
    public static void beginLivingDamage() {
        if (Shaders.isRenderingWorld) {
            ShadersTex.bindTexture(Shaders.defaultTexture);
            if (!Shaders.isShadowPass) {
                setDrawBuffers(Shaders.drawBuffersColorAtt0);
            }
        }
    }
    
    public static void endLivingDamage() {
        if (Shaders.isRenderingWorld && !Shaders.isShadowPass) {
            setDrawBuffers(Shaders.programsDrawBuffers[16]);
        }
    }
    
    public static void beginBlockEntities() {
        if (Shaders.isRenderingWorld) {
            checkGLError("beginBlockEntities");
            useProgram(13);
        }
    }
    
    public static void nextBlockEntity(final TileEntity tileEntity) {
        if (Shaders.isRenderingWorld) {
            checkGLError("nextBlockEntity");
            useProgram(13);
            setBlockEntityId(tileEntity);
        }
    }
    
    public static void setBlockEntityId(final TileEntity tileEntity) {
        if (Shaders.isRenderingWorld && !Shaders.isShadowPass && Shaders.uniformBlockEntityId.isDefined()) {
            final Block block = tileEntity.getBlockType();
            final int blockId = Block.getIdFromBlock(block);
            Shaders.uniformBlockEntityId.setValue(blockId);
        }
    }
    
    public static void endBlockEntities() {
        if (Shaders.isRenderingWorld) {
            checkGLError("endBlockEntities");
            useProgram(Shaders.lightmapEnabled ? 3 : 2);
            ShadersTex.bindNSTextures(Shaders.defaultTexture.getMultiTexID());
        }
    }
    
    public static void beginLitParticles() {
        useProgram(3);
    }
    
    public static void beginParticles() {
        useProgram(2);
    }
    
    public static void endParticles() {
        useProgram(3);
    }
    
    public static void readCenterDepth() {
        if (!Shaders.isShadowPass && Shaders.centerDepthSmoothEnabled) {
            Shaders.tempDirectFloatBuffer.clear();
            GL11.glReadPixels(Shaders.renderWidth / 2, Shaders.renderHeight / 2, 1, 1, 6402, 5126, Shaders.tempDirectFloatBuffer);
            Shaders.centerDepth = Shaders.tempDirectFloatBuffer.get(0);
            final float fadeScalar = Shaders.diffSystemTime * 0.01f;
            final float fadeFactor = (float)Math.exp(Math.log(0.5) * fadeScalar / Shaders.centerDepthSmoothHalflife);
            Shaders.centerDepthSmooth = Shaders.centerDepthSmooth * fadeFactor + Shaders.centerDepth * (1.0f - fadeFactor);
        }
    }
    
    public static void beginWeather() {
        if (!Shaders.isShadowPass) {
            if (Shaders.usedDepthBuffers >= 3) {
                GlStateManager.setActiveTexture(33996);
                GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, Shaders.renderWidth, Shaders.renderHeight);
                GlStateManager.setActiveTexture(33984);
            }
            GlStateManager.enableDepth();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.enableAlpha();
            useProgram(20);
        }
    }
    
    public static void endWeather() {
        GlStateManager.disableBlend();
        useProgram(3);
    }
    
    public static void preWater() {
        if (Shaders.usedDepthBuffers >= 2) {
            GlStateManager.setActiveTexture(33995);
            checkGLError("pre copy depth");
            GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, Shaders.renderWidth, Shaders.renderHeight);
            checkGLError("copy depth");
            GlStateManager.setActiveTexture(33984);
        }
        ShadersTex.bindNSTextures(Shaders.defaultTexture.getMultiTexID());
    }
    
    public static void beginWater() {
        if (Shaders.isRenderingWorld) {
            if (!Shaders.isShadowPass) {
                useProgram(12);
                GlStateManager.enableBlend();
                GlStateManager.depthMask(true);
            }
            else {
                GlStateManager.depthMask(true);
            }
        }
    }
    
    public static void endWater() {
        if (Shaders.isRenderingWorld) {
            if (Shaders.isShadowPass) {}
            useProgram(Shaders.lightmapEnabled ? 3 : 2);
        }
    }
    
    public static void beginProjectRedHalo() {
        if (Shaders.isRenderingWorld) {
            useProgram(1);
        }
    }
    
    public static void endProjectRedHalo() {
        if (Shaders.isRenderingWorld) {
            useProgram(3);
        }
    }
    
    public static void applyHandDepth() {
        if (Shaders.configHandDepthMul != 1.0) {
            GL11.glScaled(1.0, 1.0, (double)Shaders.configHandDepthMul);
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
    
    public static void beginFPOverlay() {
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
    }
    
    public static void endFPOverlay() {
    }
    
    public static void glEnableWrapper(final int cap) {
        GL11.glEnable(cap);
        if (cap == 3553) {
            enableTexture2D();
        }
        else if (cap == 2912) {
            enableFog();
        }
    }
    
    public static void glDisableWrapper(final int cap) {
        GL11.glDisable(cap);
        if (cap == 3553) {
            disableTexture2D();
        }
        else if (cap == 2912) {
            disableFog();
        }
    }
    
    public static void sglEnableT2D(final int cap) {
        GL11.glEnable(cap);
        enableTexture2D();
    }
    
    public static void sglDisableT2D(final int cap) {
        GL11.glDisable(cap);
        disableTexture2D();
    }
    
    public static void sglEnableFog(final int cap) {
        GL11.glEnable(cap);
        enableFog();
    }
    
    public static void sglDisableFog(final int cap) {
        GL11.glDisable(cap);
        disableFog();
    }
    
    public static void enableTexture2D() {
        if (Shaders.isRenderingSky) {
            useProgram(5);
        }
        else if (Shaders.activeProgram == 1) {
            useProgram(Shaders.lightmapEnabled ? 3 : 2);
        }
    }
    
    public static void disableTexture2D() {
        if (Shaders.isRenderingSky) {
            useProgram(4);
        }
        else if (Shaders.activeProgram == 2 || Shaders.activeProgram == 3) {
            useProgram(1);
        }
    }
    
    public static void beginLeash() {
        useProgram(1);
    }
    
    public static void endLeash() {
        useProgram(16);
    }
    
    public static void enableFog() {
        Shaders.fogEnabled = true;
        setProgramUniform1i("fogMode", Shaders.fogMode);
    }
    
    public static void disableFog() {
        Shaders.fogEnabled = false;
        setProgramUniform1i("fogMode", 0);
    }
    
    public static void setFog(int fogMode) {
        GlStateManager.setFog(fogMode);
        fogMode = fogMode;
        if (Shaders.fogEnabled) {
            setProgramUniform1i("fogMode", fogMode);
        }
    }
    
    public static void sglFogi(final int pname, final int param) {
        GL11.glFogi(pname, param);
        if (pname == 2917) {
            Shaders.fogMode = param;
            if (Shaders.fogEnabled) {
                setProgramUniform1i("fogMode", Shaders.fogMode);
            }
        }
    }
    
    public static void enableLightmap() {
        Shaders.lightmapEnabled = true;
        if (Shaders.activeProgram == 2) {
            useProgram(3);
        }
    }
    
    public static void disableLightmap() {
        Shaders.lightmapEnabled = false;
        if (Shaders.activeProgram == 3) {
            useProgram(2);
        }
    }
    
    public static int getEntityData() {
        return Shaders.entityData[Shaders.entityDataIndex * 2];
    }
    
    public static int getEntityData2() {
        return Shaders.entityData[Shaders.entityDataIndex * 2 + 1];
    }
    
    public static int setEntityData1(final int data1) {
        Shaders.entityData[Shaders.entityDataIndex * 2] = ((Shaders.entityData[Shaders.entityDataIndex * 2] & 0xFFFF) | data1 << 16);
        return data1;
    }
    
    public static int setEntityData2(final int data2) {
        Shaders.entityData[Shaders.entityDataIndex * 2 + 1] = ((Shaders.entityData[Shaders.entityDataIndex * 2 + 1] & 0xFFFF0000) | (data2 & 0xFFFF));
        return data2;
    }
    
    public static void pushEntity(final int data0, final int data1) {
        ++Shaders.entityDataIndex;
        Shaders.entityData[Shaders.entityDataIndex * 2] = ((data0 & 0xFFFF) | data1 << 16);
        Shaders.entityData[Shaders.entityDataIndex * 2 + 1] = 0;
    }
    
    public static void pushEntity(final int data0) {
        ++Shaders.entityDataIndex;
        Shaders.entityData[Shaders.entityDataIndex * 2] = (data0 & 0xFFFF);
        Shaders.entityData[Shaders.entityDataIndex * 2 + 1] = 0;
    }
    
    public static void pushEntity(final Block block) {
        ++Shaders.entityDataIndex;
        Shaders.entityData[Shaders.entityDataIndex * 2] = ((Block.blockRegistry.getIDForObject(block) & 0xFFFF) | block.getRenderType() << 16);
        Shaders.entityData[Shaders.entityDataIndex * 2 + 1] = 0;
    }
    
    public static void popEntity() {
        Shaders.entityData[Shaders.entityDataIndex * 2] = 0;
        Shaders.entityData[Shaders.entityDataIndex * 2 + 1] = 0;
        --Shaders.entityDataIndex;
    }
    
    public static void mcProfilerEndSection() {
        Shaders.mc.mcProfiler.endSection();
    }
    
    public static String getShaderPackName() {
        return (Shaders.shaderPack == null) ? null : ((Shaders.shaderPack instanceof ShaderPackNone) ? null : Shaders.shaderPack.getName());
    }
    
    public static void nextAntialiasingLevel() {
        Shaders.configAntialiasingLevel += 2;
        Shaders.configAntialiasingLevel = Shaders.configAntialiasingLevel / 2 * 2;
        if (Shaders.configAntialiasingLevel > 4) {
            Shaders.configAntialiasingLevel = 0;
        }
        Shaders.configAntialiasingLevel = Config.limit(Shaders.configAntialiasingLevel, 0, 4);
    }
    
    public static void checkShadersModInstalled() {
        try {
            Class.forName("shadersmod.transform.SMCClassTransformer");
        }
        catch (Throwable var1) {
            return;
        }
        throw new RuntimeException("Shaders Mod detected. Please remove it, OptiFine has built-in support for shaders.");
    }
    
    public static void resourcesReloaded() {
        loadShaderPackResources();
    }
    
    private static void loadShaderPackResources() {
        Shaders.shaderPackResources = new HashMap<String, String>();
        if (Shaders.shaderPackLoaded) {
            final ArrayList listFiles = new ArrayList();
            final String PREFIX = "/shaders/lang/";
            final String EN_US = "en_US";
            final String SUFFIX = ".lang";
            listFiles.add(PREFIX + EN_US + SUFFIX);
            if (!Config.getGameSettings().language.equals(EN_US)) {
                listFiles.add(PREFIX + Config.getGameSettings().language + SUFFIX);
            }
            try {
                for (final String file : listFiles) {
                    final InputStream in = Shaders.shaderPack.getResourceAsStream(file);
                    if (in != null) {
                        final Properties props = new Properties();
                        Lang.loadLocaleData(in, props);
                        in.close();
                        final Set keys = props.keySet();
                        for (final String key : keys) {
                            final String value = props.getProperty(key);
                            Shaders.shaderPackResources.put(key, value);
                        }
                    }
                }
            }
            catch (IOException var12) {
                var12.printStackTrace();
            }
        }
    }
    
    public static String translate(final String key, final String def) {
        final String str = Shaders.shaderPackResources.get(key);
        return (str == null) ? def : str;
    }
    
    public static boolean isProgramPath(String program) {
        if (program == null) {
            return false;
        }
        if (program.length() <= 0) {
            return false;
        }
        final int pos = program.lastIndexOf("/");
        if (pos >= 0) {
            program = program.substring(pos + 1);
        }
        return Arrays.asList(Shaders.programNames).contains(program);
    }
    
    static {
        texMinFilDesc = new String[] { "Nearest", "Nearest-Nearest", "Nearest-Linear" };
        texMagFilDesc = new String[] { "Nearest", "Linear" };
        texMinFilValue = new int[] { 9728, 9984, 9986 };
        texMagFilValue = new int[] { 9728, 9729 };
        dfbColorTexturesA = new int[16];
        colorTexturesToggle = new int[8];
        colorTextureTextureImageUnit = new int[] { 0, 1, 2, 3, 7, 8, 9, 10 };
        programsToggleColorTextures = new boolean[33][8];
        faProjection = new float[16];
        faProjectionInverse = new float[16];
        faModelView = new float[16];
        faModelViewInverse = new float[16];
        faShadowProjection = new float[16];
        faShadowProjectionInverse = new float[16];
        faShadowModelView = new float[16];
        faShadowModelViewInverse = new float[16];
        programNames = new String[] { "", "gbuffers_basic", "gbuffers_textured", "gbuffers_textured_lit", "gbuffers_skybasic", "gbuffers_skytextured", "gbuffers_clouds", "gbuffers_terrain", "gbuffers_terrain_solid", "gbuffers_terrain_cutout_mip", "gbuffers_terrain_cutout", "gbuffers_damagedblock", "gbuffers_water", "gbuffers_block", "gbuffers_beaconbeam", "gbuffers_item", "gbuffers_entities", "gbuffers_armor_glint", "gbuffers_spidereyes", "gbuffers_hand", "gbuffers_weather", "composite", "composite1", "composite2", "composite3", "composite4", "composite5", "composite6", "composite7", "final", "shadow", "shadow_solid", "shadow_cutout" };
        programBackups = new int[] { 0, 0, 1, 2, 1, 2, 2, 3, 7, 7, 7, 7, 7, 7, 2, 3, 3, 2, 2, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 30, 30 };
        saveFinalShaders = System.getProperty("shaders.debug.save", "false").equals("true");
        bigBuffer = (ByteBuffer)BufferUtils.createByteBuffer(2196).limit(0);
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
        Shaders.isInitializedOnce = false;
        Shaders.isShaderPackInitialized = false;
        Shaders.hasGlGenMipmap = false;
        Shaders.hasForge = false;
        Shaders.numberResetDisplayList = 0;
        Shaders.renderWidth = 0;
        Shaders.renderHeight = 0;
        Shaders.isRenderingWorld = false;
        Shaders.isRenderingSky = false;
        Shaders.isCompositeRendered = false;
        Shaders.isRenderingDfb = false;
        Shaders.isShadowPass = false;
        Shaders.renderItemPass1DepthMask = false;
        Shaders.wetnessHalfLife = 600.0f;
        Shaders.drynessHalfLife = 200.0f;
        Shaders.eyeBrightnessHalflife = 10.0f;
        Shaders.entityAttrib = 10;
        Shaders.midTexCoordAttrib = 11;
        Shaders.tangentAttrib = 12;
        Shaders.useEntityAttrib = false;
        Shaders.useMidTexCoordAttrib = false;
        Shaders.useMultiTexCoord3Attrib = false;
        Shaders.useTangentAttrib = false;
        Shaders.progUseEntityAttrib = false;
        Shaders.progUseMidTexCoordAttrib = false;
        Shaders.progUseTangentAttrib = false;
        Shaders.atlasSizeX = 0;
        Shaders.atlasSizeY = 0;
        Shaders.uniformEntityColor = new ShaderUniformFloat4("entityColor");
        Shaders.uniformEntityId = new ShaderUniformInt("entityId");
        Shaders.uniformBlockEntityId = new ShaderUniformInt("blockEntityId");
        Shaders.needResizeShadow = false;
        Shaders.shouldSkipDefaultShadow = false;
        Shaders.activeProgram = 0;
        Shaders.loadedShaders = null;
        Shaders.shadersConfig = null;
        Shaders.defaultTexture = null;
        Shaders.normalMapEnabled = false;
        Shaders.shadowHardwareFilteringEnabled = new boolean[2];
        Shaders.shadowMipmapEnabled = new boolean[2];
        Shaders.shadowFilterNearest = new boolean[2];
        Shaders.shadowColorMipmapEnabled = new boolean[8];
        Shaders.shadowColorFilterNearest = new boolean[8];
        Shaders.configTweakBlockDamage = true;
        Shaders.configCloudShadow = true;
        Shaders.configHandDepthMul = 0.125f;
        Shaders.configRenderResMul = 1.0f;
        Shaders.configShadowResMul = 1.0f;
        Shaders.configTexMinFilB = 0;
        Shaders.configTexMinFilN = 0;
        Shaders.configTexMinFilS = 0;
        Shaders.configTexMagFilB = 0;
        Shaders.configTexMagFilN = 0;
        Shaders.configTexMagFilS = 0;
        Shaders.configShadowClipFrustrum = true;
        Shaders.configNormalMap = true;
        Shaders.configSpecularMap = true;
        Shaders.configOldLighting = new PropertyDefaultTrueFalse("oldLighting", "Classic Lighting", 2);
        Shaders.configAntialiasingLevel = 0;
        Shaders.shaderPackLoaded = false;
        Shaders.packNameNone = "OFF";
        Shaders.shaderPackClouds = new PropertyDefaultFastFancyOff("clouds", "Clouds", 0);
        Shaders.shaderPackOldLighting = new PropertyDefaultTrueFalse("oldLighting", "Classic Lighting", 0);
        Shaders.shaderPackDynamicHandLight = new PropertyDefaultTrueFalse("dynamicHandLight", "Dynamic Hand Light", 0);
        Shaders.blockLightLevel05 = 0.5f;
        Shaders.blockLightLevel06 = 0.6f;
        Shaders.blockLightLevel08 = 0.8f;
        Shaders.aoLevel = 0.8f;
        Shaders.blockAoLight = 1.0f - Shaders.aoLevel;
        Shaders.sunPathRotation = 0.0f;
        Shaders.shadowAngleInterval = 0.0f;
        Shaders.fogMode = 0;
        Shaders.shadowIntervalSize = 2.0f;
        Shaders.terrainIconSize = 16;
        Shaders.terrainTextureSize = new int[2];
        Shaders.mc = Minecraft.getMinecraft();
        Shaders.needResetModels = false;
        Shaders.sunPosition = new float[4];
        Shaders.moonPosition = new float[4];
        Shaders.shadowLightPosition = new float[4];
        Shaders.upPosition = new float[4];
        Shaders.shadowLightPositionVector = new float[4];
        Shaders.upPosModelView = new float[] { 0.0f, 100.0f, 0.0f, 0.0f };
        Shaders.sunPosModelView = new float[] { 0.0f, 100.0f, 0.0f, 0.0f };
        Shaders.moonPosModelView = new float[] { 0.0f, -100.0f, 0.0f, 0.0f };
        Shaders.worldTime = 0L;
        Shaders.lastWorldTime = 0L;
        Shaders.diffWorldTime = 0L;
        Shaders.celestialAngle = 0.0f;
        Shaders.sunAngle = 0.0f;
        Shaders.shadowAngle = 0.0f;
        Shaders.moonPhase = 0;
        Shaders.systemTime = 0L;
        Shaders.lastSystemTime = 0L;
        Shaders.diffSystemTime = 0L;
        Shaders.frameCounter = 0;
        Shaders.frameTimeCounter = 0.0f;
        Shaders.systemTimeInt32 = 0;
        Shaders.rainStrength = 0.0f;
        Shaders.wetness = 0.0f;
        Shaders.usewetness = false;
        Shaders.isEyeInWater = 0;
        Shaders.eyeBrightness = 0;
        Shaders.eyeBrightnessFadeX = 0.0f;
        Shaders.eyeBrightnessFadeY = 0.0f;
        Shaders.eyePosY = 0.0f;
        Shaders.centerDepth = 0.0f;
        Shaders.centerDepthSmooth = 0.0f;
        Shaders.centerDepthSmoothHalflife = 1.0f;
        Shaders.centerDepthSmoothEnabled = false;
        Shaders.superSamplingLevel = 1;
        Shaders.updateChunksErrorRecorded = false;
        Shaders.lightmapEnabled = false;
        Shaders.fogEnabled = true;
        Shaders.shadowPassInterval = 0;
        Shaders.shadowMapWidth = 1024;
        Shaders.shadowMapHeight = 1024;
        Shaders.spShadowMapWidth = 1024;
        Shaders.spShadowMapHeight = 1024;
        Shaders.shadowMapFOV = 90.0f;
        Shaders.shadowMapHalfPlane = 160.0f;
        Shaders.shadowMapIsOrtho = true;
        Shaders.shadowPassCounter = 0;
        Shaders.waterShadowEnabled = false;
        Shaders.usedColorBuffers = 0;
        Shaders.usedDepthBuffers = 0;
        Shaders.usedShadowColorBuffers = 0;
        Shaders.usedShadowDepthBuffers = 0;
        Shaders.usedColorAttachs = 0;
        Shaders.usedDrawBuffers = 0;
        Shaders.dfb = 0;
        Shaders.sfb = 0;
        Shaders.programsID = new int[33];
        Shaders.programsDrawBuffers = new IntBuffer[33];
        Shaders.activeDrawBuffers = null;
        Shaders.shaderPack = null;
        Shaders.packNameDefault = "(internal)";
        Shaders.shaderpacksdirname = "shaderpacks";
        Shaders.optionsfilename = "optionsshaders.txt";
        Shaders.shadersdir = new File(Minecraft.getMinecraft().mcDataDir, "shaders");
        Shaders.shaderpacksdir = new File(Minecraft.getMinecraft().mcDataDir, Shaders.shaderpacksdirname);
        Shaders.configFile = new File(Minecraft.getMinecraft().mcDataDir, Shaders.optionsfilename);
        Shaders.shaderPackOptions = null;
        Shaders.shaderPackProfiles = null;
        Shaders.shaderPackGuiScreens = null;
        Shaders.renderDisplayWidth = 0;
        Shaders.renderDisplayHeight = 0;
        Shaders.tempMat = new float[16];
        Shaders.gbuffersFormat = new int[8];
        Shaders.programsRef = new int[33];
        Shaders.programIDCopyDepth = 0;
        Shaders.programsDrawBufSettings = new String[33];
        Shaders.newDrawBufSetting = null;
        Shaders.programsColorAtmSettings = new String[33];
        Shaders.newColorAtmSetting = null;
        Shaders.activeColorAtmSettings = null;
        Shaders.programsCompositeMipmapSetting = new int[33];
        Shaders.newCompositeMipmapSetting = 0;
        Shaders.activeCompositeMipmapSetting = 0;
        Shaders.shaderPackResources = new HashMap<String, String>();
        Shaders.currentWorld = null;
        Shaders.shaderPackDimensions = new ArrayList<Integer>();
        Shaders.noiseTextureEnabled = false;
        Shaders.noiseTextureResolution = 256;
        Shaders.drawBuffersNone.limit(0);
        Shaders.drawBuffersColorAtt0.put(36064).position(0).limit(1);
        gbufferFormatPattern = Pattern.compile("[ \t]*const[ \t]*int[ \t]*(\\w+)Format[ \t]*=[ \t]*([RGBA81632FUI_SNORM]*)[ \t]*;.*");
        gbufferMipmapEnabledPattern = Pattern.compile("[ \t]*const[ \t]*bool[ \t]*(\\w+)MipmapEnabled[ \t]*=[ \t]*true[ \t]*;.*");
        formatNames = new String[] { "R8", "RG8", "RGB8", "RGBA8", "R8_SNORM", "RG8_SNORM", "RGB8_SNORM", "RGBA8_SNORM", "R16", "RG16", "RGB16", "RGBA16", "R16_SNORM", "RG16_SNORM", "RGB16_SNORM", "RGBA16_SNORM", "R32F", "RG32F", "RGB32F", "RGBA32F", "R32I", "RG32I", "RGB32I", "RGBA32I", "R32UI", "RG32UI", "RGB32UI", "RGBA32UI" };
        formatIds = new int[] { 33321, 33323, 32849, 32856, 36756, 36757, 36758, 36759, 33322, 33324, 32852, 32859, 36760, 36761, 36762, 36763, 33326, 33328, 34837, 34836, 33333, 33339, 36227, 36226, 33334, 33340, 36209, 36208 };
        patternLoadEntityDataMap = Pattern.compile("\\s*([\\w:]+)\\s*=\\s*([-]?\\d+)\\s*");
        Shaders.entityData = new int[32];
        Shaders.entityDataIndex = 0;
    }
    
    static class NamelessClass341846571
    {
        static final int[] $SwitchMap$shadersmod$client$EnumShaderOption;
        
        static {
            $SwitchMap$shadersmod$client$EnumShaderOption = new int[EnumShaderOption.values().length];
            try {
                NamelessClass341846571.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.ANTIALIASING.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {}
            try {
                NamelessClass341846571.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.NORMAL_MAP.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError2) {}
            try {
                NamelessClass341846571.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.SPECULAR_MAP.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError3) {}
            try {
                NamelessClass341846571.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.RENDER_RES_MUL.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError4) {}
            try {
                NamelessClass341846571.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.SHADOW_RES_MUL.ordinal()] = 5;
            }
            catch (NoSuchFieldError noSuchFieldError5) {}
            try {
                NamelessClass341846571.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.HAND_DEPTH_MUL.ordinal()] = 6;
            }
            catch (NoSuchFieldError noSuchFieldError6) {}
            try {
                NamelessClass341846571.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.CLOUD_SHADOW.ordinal()] = 7;
            }
            catch (NoSuchFieldError noSuchFieldError7) {}
            try {
                NamelessClass341846571.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.OLD_LIGHTING.ordinal()] = 8;
            }
            catch (NoSuchFieldError noSuchFieldError8) {}
            try {
                NamelessClass341846571.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.SHADER_PACK.ordinal()] = 9;
            }
            catch (NoSuchFieldError noSuchFieldError9) {}
            try {
                NamelessClass341846571.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TWEAK_BLOCK_DAMAGE.ordinal()] = 10;
            }
            catch (NoSuchFieldError noSuchFieldError10) {}
            try {
                NamelessClass341846571.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.SHADOW_CLIP_FRUSTRUM.ordinal()] = 11;
            }
            catch (NoSuchFieldError noSuchFieldError11) {}
            try {
                NamelessClass341846571.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TEX_MIN_FIL_B.ordinal()] = 12;
            }
            catch (NoSuchFieldError noSuchFieldError12) {}
            try {
                NamelessClass341846571.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TEX_MIN_FIL_N.ordinal()] = 13;
            }
            catch (NoSuchFieldError noSuchFieldError13) {}
            try {
                NamelessClass341846571.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TEX_MIN_FIL_S.ordinal()] = 14;
            }
            catch (NoSuchFieldError noSuchFieldError14) {}
            try {
                NamelessClass341846571.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TEX_MAG_FIL_B.ordinal()] = 15;
            }
            catch (NoSuchFieldError noSuchFieldError15) {}
            try {
                NamelessClass341846571.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TEX_MAG_FIL_N.ordinal()] = 16;
            }
            catch (NoSuchFieldError noSuchFieldError16) {}
            try {
                NamelessClass341846571.$SwitchMap$shadersmod$client$EnumShaderOption[EnumShaderOption.TEX_MAG_FIL_S.ordinal()] = 17;
            }
            catch (NoSuchFieldError noSuchFieldError17) {}
        }
    }
}
