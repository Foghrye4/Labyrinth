package labyrinth.entity;

import labyrinth.LabyrinthMod;
import labyrinth.util.LevelUtil;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityCaveSpiderLeveled extends EntityCaveSpider implements IMobLeveled {

	public EntityCaveSpiderLeveled(World worldIn) {
		super(worldIn);
	}
	
	/**Because cave spider venomous it is nerfed compared to regular spider**/
	@Override
	public void setLevel(int levelIn) {
		this.experienceValue=LevelUtil.getExperienceValue(levelIn);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(LevelUtil.getMaxHealth(levelIn)/4d);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(LevelUtil.getMovementSpeed(levelIn));
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(LevelUtil.getAttackDamage(levelIn)/4d);
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
