package labyrinth.entity;

import labyrinth.LabyrinthMod;
import labyrinth.pathfinding.PathNavigateGroundFixed;
import labyrinth.util.LevelUtil;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntitySlimeLeveled extends EntitySlime implements IMobLeveled, ISlime {

	int level = 0;

	public EntitySlimeLeveled(World worldIn) {
		super(worldIn);
	}
	
	@Override
	protected PathNavigate createNavigator(World worldIn) {
		return new PathNavigateGroundFixed(this, worldIn);
	}

	@Override
	public void setLevel(int levelIn) {
		level = levelIn;
		this.experienceValue = LevelUtil.getExperienceValue(levelIn);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(LevelUtil.getMaxHealth(levelIn));
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(2*LevelUtil.getMovementSpeed(levelIn));
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(LevelUtil.getArmor(levelIn));
		this.setHealth(this.getMaxHealth());
	}

	protected int getAttackStrength() {
		return MathHelper.ceil(LevelUtil.getAttackDamage(level));
	}

	public void setSlimeSize(int size, boolean setHealth) {
		super.setSlimeSize(size, setHealth);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(LevelUtil.getMaxHealth(level));
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(2*LevelUtil.getMovementSpeed(level));
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(LevelUtil.getArmor(level));
		this.experienceValue = LevelUtil.getExperienceValue(level);
	}
	
	ResourceLocation lootTable = new ResourceLocation(LabyrinthMod.MODID+":dungeon_loot_level_0");
	
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger("level", this.level);
		compound.setString("lootTable",lootTable.toString());
	}

	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		this.level = compound.getInteger("level");
		this.experienceValue = LevelUtil.getExperienceValue(level);
		this.lootTable = new ResourceLocation(compound.getString("lootTable"));
	}

	@Override
	public void setLootTable(ResourceLocation lootTableIn) {
		lootTable = lootTableIn;
	}
	
	@Override
    protected EntitySlime createInstance()
    {
		EntitySlimeLeveled esl = new EntitySlimeLeveled(this.world);
		esl.setLevel(level);
        return esl;
    }
	
	@Override
    protected int getJumpDelay()
    {
        return this.rand.nextInt(100) + 40;
    }
	
	@Override
    protected ResourceLocation getLootTable()
    {
        return lootTable;
    }

	@Override
	public void setSlimeSize(int size) {
		this.setSlimeSize(size, true);
	}
	/**
	 * Leveled creatures shall not despawn.
	 */
	protected boolean canDespawn() {
		return false;
	}

	/**
	 * Remove despawn.
	 */
	protected void despawnEntity() {}
}
