package labyrinth.entity;

import java.util.function.Predicate;

import labyrinth.LabyrinthMod;
import labyrinth.util.LevelUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityVexLeveled extends EntityVex implements IMobLeveled {

	public EntityVexLeveled(World worldIn) {
		super(worldIn);
	}

	@Override
	public void setLevel(int levelIn) {
		this.experienceValue = LevelUtil.getExperienceValue(levelIn);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(0.5d);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(LevelUtil.getMovementSpeed(levelIn));
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(LevelUtil.getAttackDamage(levelIn/2));
		this.setHealth(this.getMaxHealth());
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
	protected void initEntityAI() {
		super.initEntityAI();
		this.tasks.taskEntries.removeIf(new Predicate<EntityAITasks.EntityAITaskEntry>() {
			@Override
			public boolean test(EntityAITasks.EntityAITaskEntry t) {
				if (t.priority == 4)
					return true;
				return false;
			}
		});
		this.tasks.addTask(4, new EntityVexLeveled.AIChargeAttackVexSingle());
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

	class AIChargeAttackVexSingle extends EntityAIBase {
		public AIChargeAttackVexSingle() {
			this.setMutexBits(1);
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		public boolean shouldExecute() {
			return EntityVexLeveled.this.getAttackTarget() != null
					&& !EntityVexLeveled.this.getMoveHelper().isUpdating()
					&& EntityVexLeveled.this.rand.nextInt(7) == 0
					&& EntityVexLeveled.this.posY - EntityVexLeveled.this.getAttackTarget().posY > 2.0D
					&& EntityVexLeveled.this.getDistanceSqToEntity(EntityVexLeveled.this.getAttackTarget()) > 4.0D;
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean shouldContinueExecuting() {
			return EntityVexLeveled.this.getMoveHelper().isUpdating() && EntityVexLeveled.this.isCharging() && EntityVexLeveled.this.getAttackTarget() != null && EntityVexLeveled.this.getAttackTarget().isEntityAlive();
		}

		/**
		 * Execute a one shot task or start executing a continuous task
		 */
		public void startExecuting() {
			EntityLivingBase entitylivingbase = EntityVexLeveled.this.getAttackTarget();
			Vec3d vec3d = entitylivingbase.getPositionEyes(1.0F);
			EntityVexLeveled.this.moveHelper.setMoveTo(vec3d.x, vec3d.y, vec3d.z, 1.0D);
			EntityVexLeveled.this.setIsCharging(true);
			EntityVexLeveled.this.playSound(SoundEvents.ENTITY_VEX_CHARGE, 1.0F, 1.0F);
		}

		/**
		 * Resets the task
		 */
		public void resetTask() {
			EntityVexLeveled.this.setIsCharging(false);
		}

		/**
		 * Updates the task
		 */
		public void updateTask() {
			EntityLivingBase entitylivingbase = EntityVexLeveled.this.getAttackTarget();

			if (EntityVexLeveled.this.getEntityBoundingBox().intersects(entitylivingbase.getEntityBoundingBox())) {
				EntityVexLeveled.this.attackEntityAsMob(entitylivingbase);
				EntityVexLeveled.this.setIsCharging(false);
			} else {
				double d0 = EntityVexLeveled.this.getDistanceSqToEntity(entitylivingbase);

				if (d0 < 9.0D) {
					Vec3d vec3d = entitylivingbase.getPositionEyes(1.0F);
					EntityVexLeveled.this.moveHelper.setMoveTo(vec3d.x, vec3d.y, vec3d.z, 1.0D);
				}
			}
		}
	}
}
