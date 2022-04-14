/*
 * Decompiled with CFR 0.152.
 */
package optifine;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import optifine.BlockPosM;
import optifine.Config;
import optifine.ConnectedParser;
import optifine.CustomColors;
import optifine.MatchBlock;
import optifine.Matches;
import optifine.TextureUtils;

public class CustomColormap
implements CustomColors.IColorizer {
    public String name = null;
    public String basePath = null;
    private int format = -1;
    private MatchBlock[] matchBlocks = null;
    private String source = null;
    private int color = -1;
    private int yVariance = 0;
    private int yOffset = 0;
    private int width = 0;
    private int height = 0;
    private int[] colors = null;
    private float[][] colorsRgb = null;
    private static final int FORMAT_UNKNOWN = -1;
    private static final int FORMAT_VANILLA = 0;
    private static final int FORMAT_GRID = 1;
    private static final int FORMAT_FIXED = 2;
    public static final String FORMAT_VANILLA_STRING = "vanilla";
    public static final String FORMAT_GRID_STRING = "grid";
    public static final String FORMAT_FIXED_STRING = "fixed";
    public static final String[] FORMAT_STRINGS = new String[]{"vanilla", "grid", "fixed"};
    public static final String KEY_FORMAT = "format";
    public static final String KEY_BLOCKS = "blocks";
    public static final String KEY_SOURCE = "source";
    public static final String KEY_COLOR = "color";
    public static final String KEY_Y_VARIANCE = "yVariance";
    public static final String KEY_Y_OFFSET = "yOffset";

    public CustomColormap(Properties p_i33_1_, String p_i33_2_, int p_i33_3_, int p_i33_4_, String p_i33_5_) {
        ConnectedParser connectedparser = new ConnectedParser("Colormap");
        this.name = connectedparser.parseName(p_i33_2_);
        this.basePath = connectedparser.parseBasePath(p_i33_2_);
        this.format = this.parseFormat(p_i33_1_.getProperty(KEY_FORMAT, p_i33_5_));
        this.matchBlocks = connectedparser.parseMatchBlocks(p_i33_1_.getProperty(KEY_BLOCKS));
        this.source = CustomColormap.parseTexture(p_i33_1_.getProperty(KEY_SOURCE), p_i33_2_, this.basePath);
        this.color = ConnectedParser.parseColor(p_i33_1_.getProperty(KEY_COLOR), -1);
        this.yVariance = connectedparser.parseInt(p_i33_1_.getProperty(KEY_Y_VARIANCE), 0);
        this.yOffset = connectedparser.parseInt(p_i33_1_.getProperty(KEY_Y_OFFSET), 0);
        this.width = p_i33_3_;
        this.height = p_i33_4_;
    }

    private int parseFormat(String p_parseFormat_1_) {
        if (p_parseFormat_1_ == null) {
            return 0;
        }
        if (p_parseFormat_1_.equals(FORMAT_VANILLA_STRING)) {
            return 0;
        }
        if (p_parseFormat_1_.equals(FORMAT_GRID_STRING)) {
            return 1;
        }
        if (p_parseFormat_1_.equals(FORMAT_FIXED_STRING)) {
            return 2;
        }
        CustomColormap.warn("Unknown format: " + p_parseFormat_1_);
        return -1;
    }

    public boolean isValid(String p_isValid_1_) {
        if (this.format != 0 && this.format != 1) {
            if (this.format != 2) {
                return false;
            }
            if (this.color < 0) {
                this.color = 0xFFFFFF;
            }
        } else {
            if (this.source == null) {
                CustomColormap.warn("Source not defined: " + p_isValid_1_);
                return false;
            }
            this.readColors();
            if (this.colors == null) {
                return false;
            }
            if (this.color < 0) {
                if (this.format == 0) {
                    this.color = this.getColor(127, 127);
                }
                if (this.format == 1) {
                    this.color = this.getColorGrid(BiomeGenBase.plains, new BlockPos(0, 64, 0));
                }
            }
        }
        return true;
    }

    public boolean isValidMatchBlocks(String p_isValidMatchBlocks_1_) {
        if (this.matchBlocks == null) {
            this.matchBlocks = this.detectMatchBlocks();
            if (this.matchBlocks == null) {
                CustomColormap.warn("Match blocks not defined: " + p_isValidMatchBlocks_1_);
                return false;
            }
        }
        return true;
    }

    private MatchBlock[] detectMatchBlocks() {
        String s2;
        int i2;
        Block block = Block.getBlockFromName(this.name);
        if (block != null) {
            return new MatchBlock[]{new MatchBlock(Block.getIdFromBlock(block))};
        }
        Pattern pattern = Pattern.compile("^block([0-9]+).*$");
        Matcher matcher = pattern.matcher(this.name);
        if (matcher.matches() && (i2 = Config.parseInt(s2 = matcher.group(1), -1)) >= 0) {
            return new MatchBlock[]{new MatchBlock(i2)};
        }
        ConnectedParser connectedparser = new ConnectedParser("Colormap");
        MatchBlock[] amatchblock = connectedparser.parseMatchBlock(this.name);
        return amatchblock != null ? amatchblock : null;
    }

    private void readColors() {
        try {
            boolean flag1;
            this.colors = null;
            if (this.source == null) {
                return;
            }
            String s2 = this.source + ".png";
            ResourceLocation resourcelocation = new ResourceLocation(s2);
            InputStream inputstream = Config.getResourceStream(resourcelocation);
            if (inputstream == null) {
                return;
            }
            BufferedImage bufferedimage = TextureUtil.readBufferedImage(inputstream);
            if (bufferedimage == null) {
                return;
            }
            int i2 = bufferedimage.getWidth();
            int j2 = bufferedimage.getHeight();
            boolean flag = this.width < 0 || this.width == i2;
            boolean bl2 = flag1 = this.height < 0 || this.height == j2;
            if (!flag || !flag1) {
                CustomColormap.dbg("Non-standard palette size: " + i2 + "x" + j2 + ", should be: " + this.width + "x" + this.height + ", path: " + s2);
            }
            this.width = i2;
            this.height = j2;
            if (this.width <= 0 || this.height <= 0) {
                CustomColormap.warn("Invalid palette size: " + i2 + "x" + j2 + ", path: " + s2);
                return;
            }
            this.colors = new int[i2 * j2];
            bufferedimage.getRGB(0, 0, i2, j2, this.colors, 0, i2);
        }
        catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

    private static void dbg(String p_dbg_0_) {
        Config.dbg("CustomColors: " + p_dbg_0_);
    }

    private static void warn(String p_warn_0_) {
        Config.warn("CustomColors: " + p_warn_0_);
    }

    private static String parseTexture(String p_parseTexture_0_, String p_parseTexture_1_, String p_parseTexture_2_) {
        int j2;
        if (p_parseTexture_0_ != null) {
            String s1 = ".png";
            if (p_parseTexture_0_.endsWith(s1)) {
                p_parseTexture_0_ = p_parseTexture_0_.substring(0, p_parseTexture_0_.length() - s1.length());
            }
            p_parseTexture_0_ = CustomColormap.fixTextureName(p_parseTexture_0_, p_parseTexture_2_);
            return p_parseTexture_0_;
        }
        String s2 = p_parseTexture_1_;
        int i2 = p_parseTexture_1_.lastIndexOf(47);
        if (i2 >= 0) {
            s2 = p_parseTexture_1_.substring(i2 + 1);
        }
        if ((j2 = s2.lastIndexOf(46)) >= 0) {
            s2 = s2.substring(0, j2);
        }
        s2 = CustomColormap.fixTextureName(s2, p_parseTexture_2_);
        return s2;
    }

    private static String fixTextureName(String p_fixTextureName_0_, String p_fixTextureName_1_) {
        String s2;
        if (!((p_fixTextureName_0_ = TextureUtils.fixResourcePath(p_fixTextureName_0_, p_fixTextureName_1_)).startsWith(p_fixTextureName_1_) || p_fixTextureName_0_.startsWith("textures/") || p_fixTextureName_0_.startsWith("mcpatcher/"))) {
            p_fixTextureName_0_ = p_fixTextureName_1_ + "/" + p_fixTextureName_0_;
        }
        if (p_fixTextureName_0_.endsWith(".png")) {
            p_fixTextureName_0_ = p_fixTextureName_0_.substring(0, p_fixTextureName_0_.length() - 4);
        }
        if (p_fixTextureName_0_.startsWith(s2 = "textures/blocks/")) {
            p_fixTextureName_0_ = p_fixTextureName_0_.substring(s2.length());
        }
        if (p_fixTextureName_0_.startsWith("/")) {
            p_fixTextureName_0_ = p_fixTextureName_0_.substring(1);
        }
        return p_fixTextureName_0_;
    }

    public boolean matchesBlock(BlockStateBase p_matchesBlock_1_) {
        return Matches.block(p_matchesBlock_1_, this.matchBlocks);
    }

    public int getColorRandom() {
        if (this.format == 2) {
            return this.color;
        }
        int i2 = CustomColors.random.nextInt(this.colors.length);
        return this.colors[i2];
    }

    public int getColor(int p_getColor_1_) {
        p_getColor_1_ = Config.limit(p_getColor_1_, 0, this.colors.length - 1);
        return this.colors[p_getColor_1_] & 0xFFFFFF;
    }

    public int getColor(int p_getColor_1_, int p_getColor_2_) {
        p_getColor_1_ = Config.limit(p_getColor_1_, 0, this.width - 1);
        p_getColor_2_ = Config.limit(p_getColor_2_, 0, this.height - 1);
        return this.colors[p_getColor_2_ * this.width + p_getColor_1_] & 0xFFFFFF;
    }

    public float[][] getColorsRgb() {
        if (this.colorsRgb == null) {
            this.colorsRgb = CustomColormap.toRgb(this.colors);
        }
        return this.colorsRgb;
    }

    @Override
    public int getColor(IBlockAccess p_getColor_1_, BlockPos p_getColor_2_) {
        BiomeGenBase biomegenbase = CustomColors.getColorBiome(p_getColor_1_, p_getColor_2_);
        return this.getColor(biomegenbase, p_getColor_2_);
    }

    @Override
    public boolean isColorConstant() {
        return this.format == 2;
    }

    public int getColor(BiomeGenBase p_getColor_1_, BlockPos p_getColor_2_) {
        return this.format == 0 ? this.getColorVanilla(p_getColor_1_, p_getColor_2_) : (this.format == 1 ? this.getColorGrid(p_getColor_1_, p_getColor_2_) : this.color);
    }

    public int getColorSmooth(IBlockAccess p_getColorSmooth_1_, double p_getColorSmooth_2_, double p_getColorSmooth_4_, double p_getColorSmooth_6_, int p_getColorSmooth_8_) {
        if (this.format == 2) {
            return this.color;
        }
        int i2 = MathHelper.floor_double(p_getColorSmooth_2_);
        int j2 = MathHelper.floor_double(p_getColorSmooth_4_);
        int k2 = MathHelper.floor_double(p_getColorSmooth_6_);
        int l2 = 0;
        int i1 = 0;
        int j1 = 0;
        int k1 = 0;
        BlockPosM blockposm = new BlockPosM(0, 0, 0);
        for (int l1 = i2 - p_getColorSmooth_8_; l1 <= i2 + p_getColorSmooth_8_; ++l1) {
            for (int i22 = k2 - p_getColorSmooth_8_; i22 <= k2 + p_getColorSmooth_8_; ++i22) {
                blockposm.setXyz(l1, j2, i22);
                int j22 = this.getColor(p_getColorSmooth_1_, (BlockPos)blockposm);
                l2 += j22 >> 16 & 0xFF;
                i1 += j22 >> 8 & 0xFF;
                j1 += j22 & 0xFF;
                ++k1;
            }
        }
        int k22 = l2 / k1;
        int l22 = i1 / k1;
        int i3 = j1 / k1;
        return k22 << 16 | l22 << 8 | i3;
    }

    private int getColorVanilla(BiomeGenBase p_getColorVanilla_1_, BlockPos p_getColorVanilla_2_) {
        double d0 = MathHelper.clamp_float(p_getColorVanilla_1_.getFloatTemperature(p_getColorVanilla_2_), 0.0f, 1.0f);
        double d1 = MathHelper.clamp_float(p_getColorVanilla_1_.getFloatRainfall(), 0.0f, 1.0f);
        int i2 = (int)((1.0 - d0) * (double)(this.width - 1));
        int j2 = (int)((1.0 - (d1 *= d0)) * (double)(this.height - 1));
        return this.getColor(i2, j2);
    }

    private int getColorGrid(BiomeGenBase p_getColorGrid_1_, BlockPos p_getColorGrid_2_) {
        int i2 = p_getColorGrid_1_.biomeID;
        int j2 = p_getColorGrid_2_.getY() - this.yOffset;
        if (this.yVariance > 0) {
            int k2 = p_getColorGrid_2_.getX() << 16 + p_getColorGrid_2_.getZ();
            int l2 = Config.intHash(k2);
            int i1 = this.yVariance * 2 + 1;
            int j1 = (l2 & 0xFF) % i1 - this.yVariance;
            j2 += j1;
        }
        return this.getColor(i2, j2);
    }

    public int getLength() {
        return this.format == 2 ? 1 : this.colors.length;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    private static float[][] toRgb(int[] p_toRgb_0_) {
        float[][] afloat = new float[p_toRgb_0_.length][3];
        for (int i2 = 0; i2 < p_toRgb_0_.length; ++i2) {
            int j2 = p_toRgb_0_[i2];
            float f2 = (float)(j2 >> 16 & 0xFF) / 255.0f;
            float f1 = (float)(j2 >> 8 & 0xFF) / 255.0f;
            float f22 = (float)(j2 & 0xFF) / 255.0f;
            float[] afloat1 = afloat[i2];
            afloat1[0] = f2;
            afloat1[1] = f1;
            afloat1[2] = f22;
        }
        return afloat;
    }

    public void addMatchBlock(MatchBlock p_addMatchBlock_1_) {
        if (this.matchBlocks == null) {
            this.matchBlocks = new MatchBlock[0];
        }
        this.matchBlocks = (MatchBlock[])Config.addObjectToArray(this.matchBlocks, p_addMatchBlock_1_);
    }

    public void addMatchBlock(int p_addMatchBlock_1_, int p_addMatchBlock_2_) {
        MatchBlock matchblock = this.getMatchBlock(p_addMatchBlock_1_);
        if (matchblock != null) {
            if (p_addMatchBlock_2_ >= 0) {
                matchblock.addMetadata(p_addMatchBlock_2_);
            }
        } else {
            this.addMatchBlock(new MatchBlock(p_addMatchBlock_1_, p_addMatchBlock_2_));
        }
    }

    private MatchBlock getMatchBlock(int p_getMatchBlock_1_) {
        if (this.matchBlocks == null) {
            return null;
        }
        for (int i2 = 0; i2 < this.matchBlocks.length; ++i2) {
            MatchBlock matchblock = this.matchBlocks[i2];
            if (matchblock.getBlockId() != p_getMatchBlock_1_) continue;
            return matchblock;
        }
        return null;
    }

    public int[] getMatchBlockIds() {
        if (this.matchBlocks == null) {
            return null;
        }
        HashSet<Integer> set = new HashSet<Integer>();
        for (int i2 = 0; i2 < this.matchBlocks.length; ++i2) {
            MatchBlock matchblock = this.matchBlocks[i2];
            if (matchblock.getBlockId() < 0) continue;
            set.add(matchblock.getBlockId());
        }
        Integer[] ainteger = set.toArray(new Integer[set.size()]);
        int[] aint = new int[ainteger.length];
        for (int j2 = 0; j2 < ainteger.length; ++j2) {
            aint[j2] = ainteger[j2];
        }
        return aint;
    }

    public String toString() {
        return "" + this.basePath + "/" + this.name + ", blocks: " + Config.arrayToString(this.matchBlocks) + ", source: " + this.source;
    }
}

