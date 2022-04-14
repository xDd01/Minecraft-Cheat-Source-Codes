/*
 * Decompiled with CFR 0.152.
 */
package optifine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import optifine.Blender;
import optifine.Config;
import optifine.CustomItemProperties;
import optifine.CustomItemsComparator;
import optifine.NbtTagValue;
import optifine.ResUtils;
import optifine.StrUtils;
import shadersmod.client.Shaders;
import shadersmod.client.ShadersRender;

public class CustomItems {
    private static CustomItemProperties[][] itemProperties = null;
    private static CustomItemProperties[][] enchantmentProperties = null;
    private static Map mapPotionIds = null;
    private static ItemModelGenerator itemModelGenerator = new ItemModelGenerator();
    private static boolean useGlint = true;
    public static final int MASK_POTION_SPLASH = 16384;
    public static final int MASK_POTION_NAME = 63;
    public static final String KEY_TEXTURE_OVERLAY = "texture.potion_overlay";
    public static final String KEY_TEXTURE_SPLASH = "texture.potion_bottle_splash";
    public static final String KEY_TEXTURE_DRINKABLE = "texture.potion_bottle_drinkable";
    public static final String DEFAULT_TEXTURE_OVERLAY = "items/potion_overlay";
    public static final String DEFAULT_TEXTURE_SPLASH = "items/potion_bottle_splash";
    public static final String DEFAULT_TEXTURE_DRINKABLE = "items/potion_bottle_drinkable";
    private static final int[] EMPTY_INT_ARRAY = new int[0];
    private static final int[][] EMPTY_INT2_ARRAY = new int[0][];

    public static void updateIcons(TextureMap p_updateIcons_0_) {
        itemProperties = null;
        enchantmentProperties = null;
        useGlint = true;
        if (Config.isCustomItems()) {
            CustomItems.readCitProperties("mcpatcher/cit.properties");
            IResourcePack[] airesourcepack = Config.getResourcePacks();
            for (int i2 = airesourcepack.length - 1; i2 >= 0; --i2) {
                IResourcePack iresourcepack = airesourcepack[i2];
                CustomItems.updateIcons(p_updateIcons_0_, iresourcepack);
            }
            CustomItems.updateIcons(p_updateIcons_0_, Config.getDefaultResourcePack());
            if (itemProperties.length <= 0) {
                itemProperties = null;
            }
            if (enchantmentProperties.length <= 0) {
                enchantmentProperties = null;
            }
        }
    }

    private static void readCitProperties(String p_readCitProperties_0_) {
        try {
            ResourceLocation resourcelocation = new ResourceLocation(p_readCitProperties_0_);
            InputStream inputstream = Config.getResourceStream(resourcelocation);
            if (inputstream == null) {
                return;
            }
            Config.dbg("CustomItems: Loading " + p_readCitProperties_0_);
            Properties properties = new Properties();
            properties.load(inputstream);
            inputstream.close();
            useGlint = Config.parseBoolean(properties.getProperty("useGlint"), true);
        }
        catch (FileNotFoundException var4) {
            return;
        }
        catch (IOException ioexception) {
            ioexception.printStackTrace();
        }
    }

    private static void updateIcons(TextureMap p_updateIcons_0_, IResourcePack p_updateIcons_1_) {
        Object[] astring = ResUtils.collectFiles(p_updateIcons_1_, "mcpatcher/cit/", ".properties", (String[])null);
        Map map = CustomItems.makeAutoImageProperties(p_updateIcons_1_);
        if (map.size() > 0) {
            Set set = map.keySet();
            Object[] astring1 = set.toArray(new String[set.size()]);
            astring = (String[])Config.addObjectsToArray(astring, astring1);
        }
        Arrays.sort(astring);
        List list = CustomItems.makePropertyList(itemProperties);
        List list1 = CustomItems.makePropertyList(enchantmentProperties);
        for (int i2 = 0; i2 < astring.length; ++i2) {
            Object s2 = astring[i2];
            Config.dbg("CustomItems: " + (String)s2);
            try {
                CustomItemProperties customitemproperties = null;
                if (map.containsKey(s2)) {
                    customitemproperties = (CustomItemProperties)map.get(s2);
                }
                if (customitemproperties == null) {
                    ResourceLocation resourcelocation = new ResourceLocation((String)s2);
                    InputStream inputstream = p_updateIcons_1_.getInputStream(resourcelocation);
                    if (inputstream == null) {
                        Config.warn("CustomItems file not found: " + (String)s2);
                        continue;
                    }
                    Properties properties = new Properties();
                    properties.load(inputstream);
                    customitemproperties = new CustomItemProperties(properties, (String)s2);
                }
                if (!customitemproperties.isValid((String)s2)) continue;
                customitemproperties.updateIcons(p_updateIcons_0_);
                CustomItems.addToItemList(customitemproperties, list);
                CustomItems.addToEnchantmentList(customitemproperties, list1);
                continue;
            }
            catch (FileNotFoundException var12) {
                Config.warn("CustomItems file not found: " + (String)s2);
                continue;
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        itemProperties = CustomItems.propertyListToArray(list);
        enchantmentProperties = CustomItems.propertyListToArray(list1);
        Comparator comparator = CustomItems.getPropertiesComparator();
        for (int j2 = 0; j2 < itemProperties.length; ++j2) {
            CustomItemProperties[] acustomitemproperties = itemProperties[j2];
            if (acustomitemproperties == null) continue;
            Arrays.sort(acustomitemproperties, comparator);
        }
        for (int k2 = 0; k2 < enchantmentProperties.length; ++k2) {
            CustomItemProperties[] acustomitemproperties1 = enchantmentProperties[k2];
            if (acustomitemproperties1 == null) continue;
            Arrays.sort(acustomitemproperties1, comparator);
        }
    }

    private static Comparator getPropertiesComparator() {
        Comparator comparator = new Comparator(){

            public int compare(Object p_compare_1_, Object p_compare_2_) {
                CustomItemProperties customitemproperties = (CustomItemProperties)p_compare_1_;
                CustomItemProperties customitemproperties1 = (CustomItemProperties)p_compare_2_;
                return customitemproperties.layer != customitemproperties1.layer ? customitemproperties.layer - customitemproperties1.layer : (customitemproperties.weight != customitemproperties1.weight ? customitemproperties1.weight - customitemproperties.weight : (!customitemproperties.basePath.equals(customitemproperties1.basePath) ? customitemproperties.basePath.compareTo(customitemproperties1.basePath) : customitemproperties.name.compareTo(customitemproperties1.name)));
            }
        };
        return comparator;
    }

    public static void updateModels() {
        if (itemProperties != null) {
            for (int i2 = 0; i2 < itemProperties.length; ++i2) {
                CustomItemProperties[] acustomitemproperties = itemProperties[i2];
                if (acustomitemproperties == null) continue;
                for (int j2 = 0; j2 < acustomitemproperties.length; ++j2) {
                    CustomItemProperties customitemproperties = acustomitemproperties[j2];
                    if (customitemproperties == null || customitemproperties.type != 1) continue;
                    TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();
                    customitemproperties.updateModel(texturemap, itemModelGenerator);
                }
            }
        }
    }

    private static Map makeAutoImageProperties(IResourcePack p_makeAutoImageProperties_0_) {
        HashMap map = new HashMap();
        map.putAll(CustomItems.makePotionImageProperties(p_makeAutoImageProperties_0_, false));
        map.putAll(CustomItems.makePotionImageProperties(p_makeAutoImageProperties_0_, true));
        return map;
    }

    private static Map makePotionImageProperties(IResourcePack p_makePotionImageProperties_0_, boolean p_makePotionImageProperties_1_) {
        HashMap<String, CustomItemProperties> map = new HashMap<String, CustomItemProperties>();
        String s2 = p_makePotionImageProperties_1_ ? "splash/" : "normal/";
        String[] astring = new String[]{"mcpatcher/cit/potion/" + s2, "mcpatcher/cit/Potion/" + s2};
        String[] astring1 = new String[]{".png"};
        String[] astring2 = ResUtils.collectFiles(p_makePotionImageProperties_0_, astring, astring1);
        for (int i2 = 0; i2 < astring2.length; ++i2) {
            String s1 = astring2[i2];
            String name = StrUtils.removePrefixSuffix(s1, astring, astring1);
            Properties properties = CustomItems.makePotionProperties(name, p_makePotionImageProperties_1_, s1);
            if (properties == null) continue;
            String s3 = StrUtils.removeSuffix(s1, astring1) + ".properties";
            CustomItemProperties customitemproperties = new CustomItemProperties(properties, s3);
            map.put(s3, customitemproperties);
        }
        return map;
    }

    private static Properties makePotionProperties(String p_makePotionProperties_0_, boolean p_makePotionProperties_1_, String p_makePotionProperties_2_) {
        if (StrUtils.endsWith(p_makePotionProperties_0_, new String[]{"_n", "_s"})) {
            return null;
        }
        if (p_makePotionProperties_0_.equals("empty") && !p_makePotionProperties_1_) {
            int l2 = Item.getIdFromItem(Items.glass_bottle);
            Properties properties = new Properties();
            properties.put("type", "item");
            properties.put("items", "" + l2);
            return properties;
        }
        int i2 = Item.getIdFromItem(Items.potionitem);
        int[] aint = (int[])CustomItems.getMapPotionIds().get(p_makePotionProperties_0_);
        if (aint == null) {
            Config.warn("Potion not found for image: " + p_makePotionProperties_2_);
            return null;
        }
        StringBuffer stringbuffer = new StringBuffer();
        for (int j2 = 0; j2 < aint.length; ++j2) {
            int k2 = aint[j2];
            if (p_makePotionProperties_1_) {
                k2 |= 0x4000;
            }
            if (j2 > 0) {
                stringbuffer.append(" ");
            }
            stringbuffer.append(k2);
        }
        int i1 = 16447;
        Properties properties1 = new Properties();
        properties1.put("type", "item");
        properties1.put("items", "" + i2);
        properties1.put("damage", "" + stringbuffer.toString());
        properties1.put("damageMask", "" + i1);
        if (p_makePotionProperties_1_) {
            properties1.put(KEY_TEXTURE_SPLASH, p_makePotionProperties_0_);
        } else {
            properties1.put(KEY_TEXTURE_DRINKABLE, p_makePotionProperties_0_);
        }
        return properties1;
    }

    private static Map getMapPotionIds() {
        if (mapPotionIds == null) {
            mapPotionIds = new LinkedHashMap();
            mapPotionIds.put("water", new int[]{0});
            mapPotionIds.put("awkward", new int[]{16});
            mapPotionIds.put("thick", new int[]{32});
            mapPotionIds.put("potent", new int[]{48});
            mapPotionIds.put("regeneration", CustomItems.getPotionIds(1));
            mapPotionIds.put("moveSpeed", CustomItems.getPotionIds(2));
            mapPotionIds.put("fireResistance", CustomItems.getPotionIds(3));
            mapPotionIds.put("poison", CustomItems.getPotionIds(4));
            mapPotionIds.put("heal", CustomItems.getPotionIds(5));
            mapPotionIds.put("nightVision", CustomItems.getPotionIds(6));
            mapPotionIds.put("clear", CustomItems.getPotionIds(7));
            mapPotionIds.put("bungling", CustomItems.getPotionIds(23));
            mapPotionIds.put("charming", CustomItems.getPotionIds(39));
            mapPotionIds.put("rank", CustomItems.getPotionIds(55));
            mapPotionIds.put("weakness", CustomItems.getPotionIds(8));
            mapPotionIds.put("damageBoost", CustomItems.getPotionIds(9));
            mapPotionIds.put("moveSlowdown", CustomItems.getPotionIds(10));
            mapPotionIds.put("diffuse", CustomItems.getPotionIds(11));
            mapPotionIds.put("smooth", CustomItems.getPotionIds(27));
            mapPotionIds.put("refined", CustomItems.getPotionIds(43));
            mapPotionIds.put("acrid", CustomItems.getPotionIds(59));
            mapPotionIds.put("harm", CustomItems.getPotionIds(12));
            mapPotionIds.put("waterBreathing", CustomItems.getPotionIds(13));
            mapPotionIds.put("invisibility", CustomItems.getPotionIds(14));
            mapPotionIds.put("thin", CustomItems.getPotionIds(15));
            mapPotionIds.put("debonair", CustomItems.getPotionIds(31));
            mapPotionIds.put("sparkling", CustomItems.getPotionIds(47));
            mapPotionIds.put("stinky", CustomItems.getPotionIds(63));
        }
        return mapPotionIds;
    }

    private static int[] getPotionIds(int p_getPotionIds_0_) {
        return new int[]{p_getPotionIds_0_, p_getPotionIds_0_ + 16, p_getPotionIds_0_ + 32, p_getPotionIds_0_ + 48};
    }

    private static int getPotionNameDamage(String p_getPotionNameDamage_0_) {
        String s2 = "potion." + p_getPotionNameDamage_0_;
        Potion[] apotion = Potion.potionTypes;
        for (int i2 = 0; i2 < apotion.length; ++i2) {
            String s1;
            Potion potion = apotion[i2];
            if (potion == null || !s2.equals(s1 = potion.getName())) continue;
            return potion.getId();
        }
        return -1;
    }

    private static List makePropertyList(CustomItemProperties[][] p_makePropertyList_0_) {
        ArrayList<ArrayList<CustomItemProperties>> list = new ArrayList<ArrayList<CustomItemProperties>>();
        if (p_makePropertyList_0_ != null) {
            for (int i2 = 0; i2 < p_makePropertyList_0_.length; ++i2) {
                CustomItemProperties[] acustomitemproperties = p_makePropertyList_0_[i2];
                ArrayList<CustomItemProperties> list1 = null;
                if (acustomitemproperties != null) {
                    list1 = new ArrayList<CustomItemProperties>(Arrays.asList(acustomitemproperties));
                }
                list.add(list1);
            }
        }
        return list;
    }

    private static CustomItemProperties[][] propertyListToArray(List p_propertyListToArray_0_) {
        CustomItemProperties[][] acustomitemproperties = new CustomItemProperties[p_propertyListToArray_0_.size()][];
        for (int i2 = 0; i2 < p_propertyListToArray_0_.size(); ++i2) {
            List list = (List)p_propertyListToArray_0_.get(i2);
            if (list == null) continue;
            CustomItemProperties[] acustomitemproperties1 = list.toArray(new CustomItemProperties[list.size()]);
            Arrays.sort(acustomitemproperties1, new CustomItemsComparator());
            acustomitemproperties[i2] = acustomitemproperties1;
        }
        return acustomitemproperties;
    }

    private static void addToItemList(CustomItemProperties p_addToItemList_0_, List p_addToItemList_1_) {
        if (p_addToItemList_0_.items != null) {
            for (int i2 = 0; i2 < p_addToItemList_0_.items.length; ++i2) {
                int j2 = p_addToItemList_0_.items[i2];
                if (j2 <= 0) {
                    Config.warn("Invalid item ID: " + j2);
                    continue;
                }
                CustomItems.addToList(p_addToItemList_0_, p_addToItemList_1_, j2);
            }
        }
    }

    private static void addToEnchantmentList(CustomItemProperties p_addToEnchantmentList_0_, List p_addToEnchantmentList_1_) {
        if (p_addToEnchantmentList_0_.type == 2 && p_addToEnchantmentList_0_.enchantmentIds != null) {
            for (int i2 = 0; i2 < 256; ++i2) {
                if (!p_addToEnchantmentList_0_.enchantmentIds.isInRange(i2)) continue;
                CustomItems.addToList(p_addToEnchantmentList_0_, p_addToEnchantmentList_1_, i2);
            }
        }
    }

    private static void addToList(CustomItemProperties p_addToList_0_, List p_addToList_1_, int p_addToList_2_) {
        while (p_addToList_2_ >= p_addToList_1_.size()) {
            p_addToList_1_.add(null);
        }
        ArrayList<CustomItemProperties> list = (ArrayList<CustomItemProperties>)p_addToList_1_.get(p_addToList_2_);
        if (list == null) {
            list = new ArrayList<CustomItemProperties>();
            p_addToList_1_.set(p_addToList_2_, list);
        }
        list.add(p_addToList_0_);
    }

    public static IBakedModel getCustomItemModel(ItemStack p_getCustomItemModel_0_, IBakedModel p_getCustomItemModel_1_, ModelResourceLocation p_getCustomItemModel_2_) {
        if (p_getCustomItemModel_1_.isGui3d()) {
            return p_getCustomItemModel_1_;
        }
        if (itemProperties == null) {
            return p_getCustomItemModel_1_;
        }
        CustomItemProperties customitemproperties = CustomItems.getCustomItemProperties(p_getCustomItemModel_0_, 1);
        return customitemproperties == null ? p_getCustomItemModel_1_ : customitemproperties.getModel(p_getCustomItemModel_2_);
    }

    public static boolean bindCustomArmorTexture(ItemStack p_bindCustomArmorTexture_0_, int p_bindCustomArmorTexture_1_, String p_bindCustomArmorTexture_2_) {
        if (itemProperties == null) {
            return false;
        }
        ResourceLocation resourcelocation = CustomItems.getCustomArmorLocation(p_bindCustomArmorTexture_0_, p_bindCustomArmorTexture_1_, p_bindCustomArmorTexture_2_);
        if (resourcelocation == null) {
            return false;
        }
        Config.getTextureManager().bindTexture(resourcelocation);
        return true;
    }

    private static ResourceLocation getCustomArmorLocation(ItemStack p_getCustomArmorLocation_0_, int p_getCustomArmorLocation_1_, String p_getCustomArmorLocation_2_) {
        CustomItemProperties customitemproperties = CustomItems.getCustomItemProperties(p_getCustomArmorLocation_0_, 3);
        if (customitemproperties == null) {
            return null;
        }
        if (customitemproperties.mapTextureLocations == null) {
            return null;
        }
        Item item = p_getCustomArmorLocation_0_.getItem();
        if (!(item instanceof ItemArmor)) {
            return null;
        }
        ItemArmor itemarmor = (ItemArmor)item;
        String s2 = itemarmor.getArmorMaterial().getName();
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("texture.");
        stringbuffer.append(s2);
        stringbuffer.append("_layer_");
        stringbuffer.append(p_getCustomArmorLocation_1_);
        if (p_getCustomArmorLocation_2_ != null) {
            stringbuffer.append("_");
            stringbuffer.append(p_getCustomArmorLocation_2_);
        }
        String s1 = stringbuffer.toString();
        ResourceLocation resourcelocation = (ResourceLocation)customitemproperties.mapTextureLocations.get(s1);
        return resourcelocation;
    }

    private static CustomItemProperties getCustomItemProperties(ItemStack p_getCustomItemProperties_0_, int p_getCustomItemProperties_1_) {
        CustomItemProperties[] acustomitemproperties;
        if (itemProperties == null) {
            return null;
        }
        if (p_getCustomItemProperties_0_ == null) {
            return null;
        }
        Item item = p_getCustomItemProperties_0_.getItem();
        int i2 = Item.getIdFromItem(item);
        if (i2 >= 0 && i2 < itemProperties.length && (acustomitemproperties = itemProperties[i2]) != null) {
            for (int j2 = 0; j2 < acustomitemproperties.length; ++j2) {
                CustomItemProperties customitemproperties = acustomitemproperties[j2];
                if (customitemproperties.type != p_getCustomItemProperties_1_ || !CustomItems.matchesProperties(customitemproperties, p_getCustomItemProperties_0_, null)) continue;
                return customitemproperties;
            }
        }
        return null;
    }

    private static boolean matchesProperties(CustomItemProperties p_matchesProperties_0_, ItemStack p_matchesProperties_1_, int[][] p_matchesProperties_2_) {
        Item item = p_matchesProperties_1_.getItem();
        if (p_matchesProperties_0_.damage != null) {
            int i2 = p_matchesProperties_1_.getItemDamage();
            if (p_matchesProperties_0_.damageMask != 0) {
                i2 &= p_matchesProperties_0_.damageMask;
            }
            if (p_matchesProperties_0_.damagePercent) {
                int j2 = item.getMaxDamage();
                i2 = (int)((double)(i2 * 100) / (double)j2);
            }
            if (!p_matchesProperties_0_.damage.isInRange(i2)) {
                return false;
            }
        }
        if (p_matchesProperties_0_.stackSize != null && !p_matchesProperties_0_.stackSize.isInRange(p_matchesProperties_1_.stackSize)) {
            return false;
        }
        int[][] aint = p_matchesProperties_2_;
        if (p_matchesProperties_0_.enchantmentIds != null) {
            if (p_matchesProperties_2_ == null) {
                aint = CustomItems.getEnchantmentIdLevels(p_matchesProperties_1_);
            }
            boolean flag = false;
            for (int k2 = 0; k2 < aint.length; ++k2) {
                int l2 = aint[k2][0];
                if (!p_matchesProperties_0_.enchantmentIds.isInRange(l2)) continue;
                flag = true;
                break;
            }
            if (!flag) {
                return false;
            }
        }
        if (p_matchesProperties_0_.enchantmentLevels != null) {
            if (aint == null) {
                aint = CustomItems.getEnchantmentIdLevels(p_matchesProperties_1_);
            }
            boolean flag1 = false;
            for (int i1 = 0; i1 < aint.length; ++i1) {
                int k1 = aint[i1][1];
                if (!p_matchesProperties_0_.enchantmentLevels.isInRange(k1)) continue;
                flag1 = true;
                break;
            }
            if (!flag1) {
                return false;
            }
        }
        if (p_matchesProperties_0_.nbtTagValues != null) {
            NBTTagCompound nbttagcompound = p_matchesProperties_1_.getTagCompound();
            for (int j1 = 0; j1 < p_matchesProperties_0_.nbtTagValues.length; ++j1) {
                NbtTagValue nbttagvalue = p_matchesProperties_0_.nbtTagValues[j1];
                if (nbttagvalue.matches(nbttagcompound)) continue;
                return false;
            }
        }
        return true;
    }

    private static int[][] getEnchantmentIdLevels(ItemStack p_getEnchantmentIdLevels_0_) {
        NBTTagList nbttaglist;
        Item item = p_getEnchantmentIdLevels_0_.getItem();
        NBTTagList nBTTagList = nbttaglist = item == Items.enchanted_book ? Items.enchanted_book.getEnchantments(p_getEnchantmentIdLevels_0_) : p_getEnchantmentIdLevels_0_.getEnchantmentTagList();
        if (nbttaglist != null && nbttaglist.tagCount() > 0) {
            int[][] aint = new int[nbttaglist.tagCount()][2];
            for (int i2 = 0; i2 < nbttaglist.tagCount(); ++i2) {
                NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i2);
                short j2 = nbttagcompound.getShort("id");
                short k2 = nbttagcompound.getShort("lvl");
                aint[i2][0] = j2;
                aint[i2][1] = k2;
            }
            return aint;
        }
        return EMPTY_INT2_ARRAY;
    }

    public static boolean renderCustomEffect(RenderItem p_renderCustomEffect_0_, ItemStack p_renderCustomEffect_1_, IBakedModel p_renderCustomEffect_2_) {
        if (enchantmentProperties == null) {
            return false;
        }
        if (p_renderCustomEffect_1_ == null) {
            return false;
        }
        int[][] aint = CustomItems.getEnchantmentIdLevels(p_renderCustomEffect_1_);
        if (aint.length <= 0) {
            return false;
        }
        HashSet<Integer> set = null;
        boolean flag = false;
        TextureManager texturemanager = Config.getTextureManager();
        for (int i2 = 0; i2 < aint.length; ++i2) {
            CustomItemProperties[] acustomitemproperties;
            int j2 = aint[i2][0];
            if (j2 < 0 || j2 >= enchantmentProperties.length || (acustomitemproperties = enchantmentProperties[j2]) == null) continue;
            for (int k2 = 0; k2 < acustomitemproperties.length; ++k2) {
                CustomItemProperties customitemproperties = acustomitemproperties[k2];
                if (set == null) {
                    set = new HashSet<Integer>();
                }
                if (!set.add(j2) || !CustomItems.matchesProperties(customitemproperties, p_renderCustomEffect_1_, aint) || customitemproperties.textureLocation == null) continue;
                texturemanager.bindTexture(customitemproperties.textureLocation);
                float f2 = customitemproperties.getTextureWidth(texturemanager);
                if (!flag) {
                    flag = true;
                    GlStateManager.depthMask(false);
                    GlStateManager.depthFunc(514);
                    GlStateManager.disableLighting();
                    GlStateManager.matrixMode(5890);
                }
                Blender.setupBlend(customitemproperties.blend, 1.0f);
                GlStateManager.pushMatrix();
                GlStateManager.scale(f2 / 2.0f, f2 / 2.0f, f2 / 2.0f);
                float f1 = customitemproperties.speed * (float)(Minecraft.getSystemTime() % 3000L) / 3000.0f / 8.0f;
                GlStateManager.translate(f1, 0.0f, 0.0f);
                GlStateManager.rotate(customitemproperties.rotation, 0.0f, 0.0f, 1.0f);
                p_renderCustomEffect_0_.renderModel(p_renderCustomEffect_2_, -1);
                GlStateManager.popMatrix();
            }
        }
        if (flag) {
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.matrixMode(5888);
            GlStateManager.enableLighting();
            GlStateManager.depthFunc(515);
            GlStateManager.depthMask(true);
            texturemanager.bindTexture(TextureMap.locationBlocksTexture);
        }
        return flag;
    }

    public static boolean renderCustomArmorEffect(EntityLivingBase p_renderCustomArmorEffect_0_, ItemStack p_renderCustomArmorEffect_1_, ModelBase p_renderCustomArmorEffect_2_, float p_renderCustomArmorEffect_3_, float p_renderCustomArmorEffect_4_, float p_renderCustomArmorEffect_5_, float p_renderCustomArmorEffect_6_, float p_renderCustomArmorEffect_7_, float p_renderCustomArmorEffect_8_, float p_renderCustomArmorEffect_9_) {
        if (enchantmentProperties == null) {
            return false;
        }
        if (Config.isShaders() && Shaders.isShadowPass) {
            return false;
        }
        if (p_renderCustomArmorEffect_1_ == null) {
            return false;
        }
        int[][] aint = CustomItems.getEnchantmentIdLevels(p_renderCustomArmorEffect_1_);
        if (aint.length <= 0) {
            return false;
        }
        HashSet<Integer> set = null;
        boolean flag = false;
        TextureManager texturemanager = Config.getTextureManager();
        for (int i2 = 0; i2 < aint.length; ++i2) {
            CustomItemProperties[] acustomitemproperties;
            int j2 = aint[i2][0];
            if (j2 < 0 || j2 >= enchantmentProperties.length || (acustomitemproperties = enchantmentProperties[j2]) == null) continue;
            for (int k2 = 0; k2 < acustomitemproperties.length; ++k2) {
                CustomItemProperties customitemproperties = acustomitemproperties[k2];
                if (set == null) {
                    set = new HashSet<Integer>();
                }
                if (!set.add(j2) || !CustomItems.matchesProperties(customitemproperties, p_renderCustomArmorEffect_1_, aint) || customitemproperties.textureLocation == null) continue;
                texturemanager.bindTexture(customitemproperties.textureLocation);
                float f2 = customitemproperties.getTextureWidth(texturemanager);
                if (!flag) {
                    flag = true;
                    if (Config.isShaders()) {
                        ShadersRender.renderEnchantedGlintBegin();
                    }
                    GlStateManager.enableBlend();
                    GlStateManager.depthFunc(514);
                    GlStateManager.depthMask(false);
                }
                Blender.setupBlend(customitemproperties.blend, 1.0f);
                GlStateManager.disableLighting();
                GlStateManager.matrixMode(5890);
                GlStateManager.loadIdentity();
                GlStateManager.rotate(customitemproperties.rotation, 0.0f, 0.0f, 1.0f);
                float f1 = f2 / 8.0f;
                GlStateManager.scale(f1, f1 / 2.0f, f1);
                float f22 = customitemproperties.speed * (float)(Minecraft.getSystemTime() % 3000L) / 3000.0f / 8.0f;
                GlStateManager.translate(0.0f, f22, 0.0f);
                GlStateManager.matrixMode(5888);
                p_renderCustomArmorEffect_2_.render(p_renderCustomArmorEffect_0_, p_renderCustomArmorEffect_3_, p_renderCustomArmorEffect_4_, p_renderCustomArmorEffect_6_, p_renderCustomArmorEffect_7_, p_renderCustomArmorEffect_8_, p_renderCustomArmorEffect_9_);
            }
        }
        if (flag) {
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(5888);
            GlStateManager.enableLighting();
            GlStateManager.depthMask(true);
            GlStateManager.depthFunc(515);
            GlStateManager.disableBlend();
            if (Config.isShaders()) {
                ShadersRender.renderEnchantedGlintEnd();
            }
        }
        return flag;
    }

    public static boolean isUseGlint() {
        return useGlint;
    }
}

