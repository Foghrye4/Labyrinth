package labyrinth.entity;

import labyrinth.util.LevelUtil;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntitySlimeLeveled extends EntitySlime implements IMobLeveled, ISlime {

	int level = 0;

	public EntitySlimeLeveled(World worldIn) {
		super(worldIn);
	}

	@Override
	public void setLevel(int levelIn) {
		level = levelIn;
		this.experienceValue = LevelUtil.getExperienceValue(levelIn);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(LevelUtil.getMaxHealth(levelIn));
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(LevelUtil.getMovementSpeed(levelIn));
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(LevelUtil.getArmor(levelIn));
		this.setHealth(this.getMaxHealth());
	}

	protected int getAttackStrength() {
		return this.getSlimeSize() * (1 + level * level);
	}

	public void setSlimeSize(int size, boolean setHealth) {
		super.setSlimeSize(size, setHealth);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(LevelUtil.getMaxHealth(level));
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(LevelUtil.getMovementSpeed(level));
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(LevelUtil.getArmor(level));
		this.experienceValue = LevelUtil.getExperienceValue(level);
	}

	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger("level", this.level);
	}
	
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		this.level = compound.getInteger("level");
	}

	@Override
	public void setSlimeSize(int size) {
		this.setSlimeSize(size, true);
	}
}
