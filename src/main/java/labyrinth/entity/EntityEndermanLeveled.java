package labyrinth.entity;

import java.util.List;

import com.google.common.base.Predicate;

import labyrinth.LabyrinthMod;
import labyrinth.util.LevelUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityEndermanLeveled extends EntityEnderman implements IMobLeveled {

	public EntityEndermanLeveled(World worldIn) {
		super(worldIn);
	}
	
	@Override
	protected void initEntityAI() {
		super.initEntityAI();
		this.targetTasks.addTask(4, new EntityAINearestAttackableTarget<EntityPlayer>(this, EntityPlayer.class, true));
	}

	@Override
	public void setLevel(int levelIn) {
		this.experienceValue = LevelUtil.getExperienceValue(levelIn);
		LevelUtil.setMobAttributes(this, levelIn);
		this.setHealth(this.getMaxHealth());
	}

	public boolean attackEntityFrom(DamageSource source, float amount) {
		boolean r = super.attackEntityFrom(source, amount);
		Entity entity1 = source.getTrueSource();
		if (entity1 != null && this.getRevengeTarget() != null && entity1 instanceof EntityLivingBase) {
			List<EntityEndermanLeveled> comrads = world.getEntitiesWithinAABB(this.getClass(), this.getEntityBoundingBox().grow(16, 4, 16), new Predicate<EntityEndermanLeveled>() {
				@Override
				public boolean apply(EntityEndermanLeveled input) {
					return !input.isDead && input != EntityEndermanLeveled.this && input.getRevengeTarget() == null;
				}
			});
			for (EntityEndermanLeveled comrad : comrads) {
				comrad.setRevengeTarget((EntityLivingBase) entity1);
			}
		}
		return r;
	}

	int level = 0;
	ResourceLocation lootTable = new ResourceLocation(LabyrinthMod.MODID + ":dungeon_loot_level_0");

	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger("level", this.level);
		compound.setString("lootTable", lootTable.toString());
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
	protected ResourceLocation getLootTable() {
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
	protected void despawnEntity() {
	}
}
