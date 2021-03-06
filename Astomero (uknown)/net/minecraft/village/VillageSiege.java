package net.minecraft.village;

import net.minecraft.entity.player.*;
import java.util.*;
import net.minecraft.util.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.*;
import net.minecraft.world.*;

public class VillageSiege
{
    private World worldObj;
    private boolean field_75535_b;
    private int field_75536_c;
    private int field_75533_d;
    private int field_75534_e;
    private Village theVillage;
    private int field_75532_g;
    private int field_75538_h;
    private int field_75539_i;
    
    public VillageSiege(final World worldIn) {
        this.field_75536_c = -1;
        this.worldObj = worldIn;
    }
    
    public void tick() {
        if (this.worldObj.isDaytime()) {
            this.field_75536_c = 0;
        }
        else if (this.field_75536_c != 2) {
            if (this.field_75536_c == 0) {
                final float f = this.worldObj.getCelestialAngle(0.0f);
                if (f < 0.5 || f > 0.501) {
                    return;
                }
                this.field_75536_c = ((this.worldObj.rand.nextInt(10) == 0) ? 1 : 2);
                this.field_75535_b = false;
                if (this.field_75536_c == 2) {
                    return;
                }
            }
            if (this.field_75536_c != -1) {
                if (!this.field_75535_b) {
                    if (!this.func_75529_b()) {
                        return;
                    }
                    this.field_75535_b = true;
                }
                if (this.field_75534_e > 0) {
                    --this.field_75534_e;
                }
                else {
                    this.field_75534_e = 2;
                    if (this.field_75533_d > 0) {
                        this.spawnZombie();
                        --this.field_75533_d;
                    }
                    else {
                        this.field_75536_c = 2;
                    }
                }
            }
        }
    }
    
    private boolean func_75529_b() {
        final List<EntityPlayer> list = this.worldObj.playerEntities;
        for (final EntityPlayer entityplayer : list) {
            if (!entityplayer.isSpectator()) {
                this.theVillage = this.worldObj.getVillageCollection().getNearestVillage(new BlockPos(entityplayer), 1);
                if (this.theVillage == null || this.theVillage.getNumVillageDoors() < 10 || this.theVillage.getTicksSinceLastDoorAdding() < 20 || this.theVillage.getNumVillagers() < 20) {
                    continue;
                }
                final BlockPos blockpos = this.theVillage.getCenter();
                final float f = (float)this.theVillage.getVillageRadius();
                boolean flag = false;
                for (int i = 0; i < 10; ++i) {
                    final float f2 = this.worldObj.rand.nextFloat() * 3.1415927f * 2.0f;
                    this.field_75532_g = blockpos.getX() + (int)(MathHelper.cos(f2) * f * 0.9);
                    this.field_75538_h = blockpos.getY();
                    this.field_75539_i = blockpos.getZ() + (int)(MathHelper.sin(f2) * f * 0.9);
                    flag = false;
                    for (final Village village : this.worldObj.getVillageCollection().getVillageList()) {
                        if (village != this.theVillage && village.func_179866_a(new BlockPos(this.field_75532_g, this.field_75538_h, this.field_75539_i))) {
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        break;
                    }
                }
                if (flag) {
                    return false;
                }
                final Vec3 vec3 = this.func_179867_a(new BlockPos(this.field_75532_g, this.field_75538_h, this.field_75539_i));
                if (vec3 != null) {
                    this.field_75534_e = 0;
                    this.field_75533_d = 20;
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    private boolean spawnZombie() {
        final Vec3 vec3 = this.func_179867_a(new BlockPos(this.field_75532_g, this.field_75538_h, this.field_75539_i));
        if (vec3 == null) {
            return false;
        }
        EntityZombie entityzombie;
        try {
            entityzombie = new EntityZombie(this.worldObj);
            entityzombie.onInitialSpawn(this.worldObj.getDifficultyForLocation(new BlockPos(entityzombie)), null);
            entityzombie.setVillager(false);
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
        entityzombie.setLocationAndAngles(vec3.xCoord, vec3.yCoord, vec3.zCoord, this.worldObj.rand.nextFloat() * 360.0f, 0.0f);
        this.worldObj.spawnEntityInWorld(entityzombie);
        final BlockPos blockpos = this.theVillage.getCenter();
        entityzombie.setHomePosAndDistance(blockpos, this.theVillage.getVillageRadius());
        return true;
    }
    
    private Vec3 func_179867_a(final BlockPos p_179867_1_) {
        for (int i = 0; i < 10; ++i) {
            final BlockPos blockpos = p_179867_1_.add(this.worldObj.rand.nextInt(16) - 8, this.worldObj.rand.nextInt(6) - 3, this.worldObj.rand.nextInt(16) - 8);
            if (this.theVillage.func_179866_a(blockpos) && SpawnerAnimals.canCreatureTypeSpawnAtLocation(EntityLiving.SpawnPlacementType.ON_GROUND, this.worldObj, blockpos)) {
                return new Vec3(blockpos.getX(), blockpos.getY(), blockpos.getZ());
            }
        }
        return null;
    }
}
