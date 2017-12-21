package labyrinth.entity;

import labyrinth.LabyrinthMod;
import labyrinth.pathfinding.PathNavigateGroundFixed;
import labyrinth.util.LevelUtil;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityEvokerLeveled extends EntityEvoker implements IMobLeveled {

	public EntityEvokerLeveled(World worldIn) {
		super(worldIn);
	}
	
	@Override
	protected PathNavigate createNavigator(World worldIn) {
		return new PathNavigateGroundFixed(this, worldIn);
	}

	@Override
	public void setLevel(int levelIn) {
		this.experienceValue=LevelUtil.getExperienceValue(levelIn);
		LevelUtil.setMobAttributes(this, levelIn);
        this.setHealth(this.getMaxHealth());
	}
	
	int level = 0;
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
    protected ResourceLocation getLootTable()
    {
        return lootTable;
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
