package net.minecraft.entity;

import net.minecraft.world.*;
import net.minecraft.nbt.*;
import java.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.effect.*;
import com.google.common.collect.*;
import org.apache.logging.log4j.*;
import net.minecraft.entity.projectile.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.boss.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.item.*;
import net.minecraft.stats.*;

public class EntityList
{
    public static final Map entityEggs;
    private static final Logger logger;
    private static final Map stringToClassMapping;
    private static final Map classToStringMapping;
    private static final Map idToClassMapping;
    private static final Map classToIDMapping;
    private static final Map field_180126_g;
    
    private static void addMapping(final Class p_75618_0_, final String p_75618_1_, final int p_75618_2_) {
        if (EntityList.stringToClassMapping.containsKey(p_75618_1_)) {
            throw new IllegalArgumentException("ID is already registered: " + p_75618_1_);
        }
        if (EntityList.idToClassMapping.containsKey(p_75618_2_)) {
            throw new IllegalArgumentException("ID is already registered: " + p_75618_2_);
        }
        if (p_75618_2_ == 0) {
            throw new IllegalArgumentException("Cannot register to reserved id: " + p_75618_2_);
        }
        if (p_75618_0_ == null) {
            throw new IllegalArgumentException("Cannot register null clazz for id: " + p_75618_2_);
        }
        EntityList.stringToClassMapping.put(p_75618_1_, p_75618_0_);
        EntityList.classToStringMapping.put(p_75618_0_, p_75618_1_);
        EntityList.idToClassMapping.put(p_75618_2_, p_75618_0_);
        EntityList.classToIDMapping.put(p_75618_0_, p_75618_2_);
        EntityList.field_180126_g.put(p_75618_1_, p_75618_2_);
    }
    
    private static void addMapping(final Class p_75614_0_, final String p_75614_1_, final int p_75614_2_, final int p_75614_3_, final int p_75614_4_) {
        addMapping(p_75614_0_, p_75614_1_, p_75614_2_);
        EntityList.entityEggs.put(p_75614_2_, new EntityEggInfo(p_75614_2_, p_75614_3_, p_75614_4_));
    }
    
    public static Entity createEntityByName(final String p_75620_0_, final World worldIn) {
        Entity var2 = null;
        try {
            final Class var3 = EntityList.stringToClassMapping.get(p_75620_0_);
            if (var3 != null) {
                var2 = var3.getConstructor(World.class).newInstance(worldIn);
            }
        }
        catch (Exception var4) {
            var4.printStackTrace();
        }
        return var2;
    }
    
    public static Entity createEntityFromNBT(final NBTTagCompound p_75615_0_, final World worldIn) {
        Entity var2 = null;
        if ("Minecart".equals(p_75615_0_.getString("id"))) {
            p_75615_0_.setString("id", EntityMinecart.EnumMinecartType.func_180038_a(p_75615_0_.getInteger("Type")).func_180040_b());
            p_75615_0_.removeTag("Type");
        }
        try {
            final Class var3 = EntityList.stringToClassMapping.get(p_75615_0_.getString("id"));
            if (var3 != null) {
                var2 = var3.getConstructor(World.class).newInstance(worldIn);
            }
        }
        catch (Exception var4) {
            var4.printStackTrace();
        }
        if (var2 != null) {
            var2.readFromNBT(p_75615_0_);
        }
        else {
            EntityList.logger.warn("Skipping Entity with id " + p_75615_0_.getString("id"));
        }
        return var2;
    }
    
    public static Entity createEntityByID(final int p_75616_0_, final World worldIn) {
        Entity var2 = null;
        try {
            final Class var3 = getClassFromID(p_75616_0_);
            if (var3 != null) {
                var2 = var3.getConstructor(World.class).newInstance(worldIn);
            }
        }
        catch (Exception var4) {
            var4.printStackTrace();
        }
        if (var2 == null) {
            EntityList.logger.warn("Skipping Entity with id " + p_75616_0_);
        }
        return var2;
    }
    
    public static int getEntityID(final Entity p_75619_0_) {
        final Integer var1 = EntityList.classToIDMapping.get(p_75619_0_.getClass());
        return (var1 == null) ? 0 : var1;
    }
    
    public static Class getClassFromID(final int p_90035_0_) {
        return EntityList.idToClassMapping.get(p_90035_0_);
    }
    
    public static String getEntityString(final Entity p_75621_0_) {
        return EntityList.classToStringMapping.get(p_75621_0_.getClass());
    }
    
    public static int func_180122_a(final String p_180122_0_) {
        final Integer var1 = EntityList.field_180126_g.get(p_180122_0_);
        return (var1 == null) ? 90 : var1;
    }
    
    public static String getStringFromID(final int p_75617_0_) {
        return EntityList.classToStringMapping.get(getClassFromID(p_75617_0_));
    }
    
    public static void func_151514_a() {
    }
    
    public static List func_180124_b() {
        final Set var0 = EntityList.stringToClassMapping.keySet();
        final ArrayList var2 = Lists.newArrayList();
        for (final String var4 : var0) {
            final Class var5 = EntityList.stringToClassMapping.get(var4);
            if ((var5.getModifiers() & 0x400) != 0x400) {
                var2.add(var4);
            }
        }
        var2.add("LightningBolt");
        return var2;
    }
    
    public static boolean func_180123_a(final Entity p_180123_0_, final String p_180123_1_) {
        String var2 = getEntityString(p_180123_0_);
        if (var2 == null && p_180123_0_ instanceof EntityPlayer) {
            var2 = "Player";
        }
        else if (var2 == null && p_180123_0_ instanceof EntityLightningBolt) {
            var2 = "LightningBolt";
        }
        return p_180123_1_.equals(var2);
    }
    
    public static boolean func_180125_b(final String p_180125_0_) {
        return "Player".equals(p_180125_0_) || func_180124_b().contains(p_180125_0_);
    }
    
    static {
        entityEggs = Maps.newLinkedHashMap();
        logger = LogManager.getLogger();
        stringToClassMapping = Maps.newHashMap();
        classToStringMapping = Maps.newHashMap();
        idToClassMapping = Maps.newHashMap();
        classToIDMapping = Maps.newHashMap();
        field_180126_g = Maps.newHashMap();
        addMapping(EntityItem.class, "Item", 1);
        addMapping(EntityXPOrb.class, "XPOrb", 2);
        addMapping(EntityLeashKnot.class, "LeashKnot", 8);
        addMapping(EntityPainting.class, "Painting", 9);
        addMapping(EntityArrow.class, "Arrow", 10);
        addMapping(EntitySnowball.class, "Snowball", 11);
        addMapping(EntityLargeFireball.class, "Fireball", 12);
        addMapping(EntitySmallFireball.class, "SmallFireball", 13);
        addMapping(EntityEnderPearl.class, "ThrownEnderpearl", 14);
        addMapping(EntityEnderEye.class, "EyeOfEnderSignal", 15);
        addMapping(EntityPotion.class, "ThrownPotion", 16);
        addMapping(EntityExpBottle.class, "ThrownExpBottle", 17);
        addMapping(EntityItemFrame.class, "ItemFrame", 18);
        addMapping(EntityWitherSkull.class, "WitherSkull", 19);
        addMapping(EntityTNTPrimed.class, "PrimedTnt", 20);
        addMapping(EntityFallingBlock.class, "FallingSand", 21);
        addMapping(EntityFireworkRocket.class, "FireworksRocketEntity", 22);
        addMapping(EntityArmorStand.class, "ArmorStand", 30);
        addMapping(EntityBoat.class, "Boat", 41);
        addMapping(EntityMinecartEmpty.class, EntityMinecart.EnumMinecartType.RIDEABLE.func_180040_b(), 42);
        addMapping(EntityMinecartChest.class, EntityMinecart.EnumMinecartType.CHEST.func_180040_b(), 43);
        addMapping(EntityMinecartFurnace.class, EntityMinecart.EnumMinecartType.FURNACE.func_180040_b(), 44);
        addMapping(EntityMinecartTNT.class, EntityMinecart.EnumMinecartType.TNT.func_180040_b(), 45);
        addMapping(EntityMinecartHopper.class, EntityMinecart.EnumMinecartType.HOPPER.func_180040_b(), 46);
        addMapping(EntityMinecartMobSpawner.class, EntityMinecart.EnumMinecartType.SPAWNER.func_180040_b(), 47);
        addMapping(EntityMinecartCommandBlock.class, EntityMinecart.EnumMinecartType.COMMAND_BLOCK.func_180040_b(), 40);
        addMapping(EntityLiving.class, "Mob", 48);
        addMapping(EntityMob.class, "Monster", 49);
        addMapping(EntityCreeper.class, "Creeper", 50, 894731, 0);
        addMapping(EntitySkeleton.class, "Skeleton", 51, 12698049, 4802889);
        addMapping(EntitySpider.class, "Spider", 52, 3419431, 11013646);
        addMapping(EntityGiantZombie.class, "Giant", 53);
        addMapping(EntityZombie.class, "Zombie", 54, 44975, 7969893);
        addMapping(EntitySlime.class, "Slime", 55, 5349438, 8306542);
        addMapping(EntityGhast.class, "Ghast", 56, 16382457, 12369084);
        addMapping(EntityPigZombie.class, "PigZombie", 57, 15373203, 5009705);
        addMapping(EntityEnderman.class, "Enderman", 58, 1447446, 0);
        addMapping(EntityCaveSpider.class, "CaveSpider", 59, 803406, 11013646);
        addMapping(EntitySilverfish.class, "Silverfish", 60, 7237230, 3158064);
        addMapping(EntityBlaze.class, "Blaze", 61, 16167425, 16775294);
        addMapping(EntityMagmaCube.class, "LavaSlime", 62, 3407872, 16579584);
        addMapping(EntityDragon.class, "EnderDragon", 63);
        addMapping(EntityWither.class, "WitherBoss", 64);
        addMapping(EntityBat.class, "Bat", 65, 4996656, 986895);
        addMapping(EntityWitch.class, "Witch", 66, 3407872, 5349438);
        addMapping(EntityEndermite.class, "Endermite", 67, 1447446, 7237230);
        addMapping(EntityGuardian.class, "Guardian", 68, 5931634, 15826224);
        addMapping(EntityPig.class, "Pig", 90, 15771042, 14377823);
        addMapping(EntitySheep.class, "Sheep", 91, 15198183, 16758197);
        addMapping(EntityCow.class, "Cow", 92, 4470310, 10592673);
        addMapping(EntityChicken.class, "Chicken", 93, 10592673, 16711680);
        addMapping(EntitySquid.class, "Squid", 94, 2243405, 7375001);
        addMapping(EntityWolf.class, "Wolf", 95, 14144467, 13545366);
        addMapping(EntityMooshroom.class, "MushroomCow", 96, 10489616, 12040119);
        addMapping(EntitySnowman.class, "SnowMan", 97);
        addMapping(EntityOcelot.class, "Ozelot", 98, 15720061, 5653556);
        addMapping(EntityIronGolem.class, "VillagerGolem", 99);
        addMapping(EntityHorse.class, "EntityHorse", 100, 12623485, 15656192);
        addMapping(EntityRabbit.class, "Rabbit", 101, 10051392, 7555121);
        addMapping(EntityVillager.class, "Villager", 120, 5651507, 12422002);
        addMapping(EntityEnderCrystal.class, "EnderCrystal", 200);
    }
    
    public static class EntityEggInfo
    {
        public final int spawnedID;
        public final int primaryColor;
        public final int secondaryColor;
        public final StatBase field_151512_d;
        public final StatBase field_151513_e;
        
        public EntityEggInfo(final int p_i1583_1_, final int p_i1583_2_, final int p_i1583_3_) {
            this.spawnedID = p_i1583_1_;
            this.primaryColor = p_i1583_2_;
            this.secondaryColor = p_i1583_3_;
            this.field_151512_d = StatList.func_151182_a(this);
            this.field_151513_e = StatList.func_151176_b(this);
        }
    }
}
