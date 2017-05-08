package labyrinth.entity;

import labyrinth.util.LevelUtil;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntitySkeletonLeveled extends EntitySkeleton implements IMobLeveled {

	public EntitySkeletonLeveled(World worldIn) {
		super(worldIn);
	}
	
	int level = 0;

	@Override
	public void setLevel(int levelIn) {
		level = levelIn;
		this.experienceValue = LevelUtil.getExperienceValue(levelIn);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(LevelUtil.getMaxHealth(levelIn));
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(LevelUtil.getMovementSpeed(levelIn));
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(LevelUtil.getArmor(levelIn));
		this.setHealth(this.getMaxHealth());
	}
	
	@Override
    protected EntityArrow getArrow(float distanceFactor)
    {
        return super.getArrow(distanceFactor/4f*((float)LevelUtil.getAttackDamage(level)-2f));
    }
	
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger("level", this.level);
	}
	
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		this.level = compound.getInteger("level");
	}

}
