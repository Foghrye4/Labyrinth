package labyrinth.entity;

import java.util.ArrayList;
import java.util.List;

import labyrinth.LabyrinthMod;
import labyrinth.util.LevelUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityMiniSpiderLeveled extends EntitySpider implements IMobLeveled {

	public float bodyPitch = 0;
	public float prevBodyPitch = 0;
	public float bodyRoll = 0;
	public float prevBodyRoll = 0;
	private int level = 0;
	private static final DataParameter<Float> ROLL = EntityDataManager.<Float>createKey(EntityMiniSpiderLeveled.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> PITCH = EntityDataManager.<Float>createKey(EntityMiniSpiderLeveled.class, DataSerializers.FLOAT);

	public EntityMiniSpiderLeveled(World worldIn) {
		super(worldIn);
		this.setSize(0.9F, 0.3F);
		this.dataManager.register(ROLL, 0f);
		this.dataManager.register(PITCH, 0f);
	}

	@Override
	public float getEyeHeight() {
		return 0.15F;
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();
		this.tasks.addTask(12, new AIWeaveWeb());
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!world.isRemote)
			return;
		// Client-side only fields.
		boolean resetBB = false;
		prevBodyPitch = bodyPitch;
		prevBodyRoll = bodyRoll;
		if (bodyRoll < this.getBodyRoll()) {
			bodyRoll += 10f;
			resetBB = true;
		}
		if (bodyRoll > this.getBodyRoll()) {
			bodyRoll -= 10f;
			resetBB = true;
		}
		if (bodyPitch < this.getBodyPitch()) {
			bodyPitch += 10f;
			resetBB = true;
		}
		if (bodyPitch > this.getBodyPitch()) {
			bodyPitch -= 10f;
			resetBB = true;
		}
		if (resetBB)
			this.resetCollisionBB();
	}

	@Override
	public void move(MoverType type, double x, double y, double z) {
		super.move(type, x, y, z);
		AxisAlignedBB bb = this.getEntityBoundingBox().grow(0.2d);
		double minSqdx = 1;
		double minSqdy = 1;
		double minSqdz = 1;
		double dx = 0, dy = 0, dz = 0;
		if (this.isCollidedVertically) {
			this.setBodyPitch(0);
			this.setBodyRoll(0);
			return;
		}
		List<AxisAlignedBB> collisionBB = world.getCollisionBoxes(this, bb);
		for (AxisAlignedBB cbb : collisionBB) {
			if (bb.intersects(cbb)) {
				// What side?
				double bbCenterPosX = (cbb.minX + cbb.maxX) * 0.5D;
				double bbCenterPosY = (cbb.minY + cbb.maxY) * 0.5D;
				double bbCenterPosZ = (cbb.minZ + cbb.maxZ) * 0.5D;
				dx = bbCenterPosX - posX;
				dy = bbCenterPosY - posY - this.height * 0.5;
				dz = bbCenterPosZ - posZ;
				double sqdx = dx * dx;
				double sqdy = dy * dy;
				double sqdz = dz * dz;
				if (sqdx < minSqdx)
					minSqdx = sqdx;
				if (sqdy < minSqdy)
					minSqdy = sqdy;
				if (sqdz < minSqdz)
					minSqdz = sqdz;
			}
		}
		if (collisionBB.isEmpty()) {
			this.setBodyPitch(0);
			this.setBodyRoll(0);
		} else {
			if (minSqdx < minSqdy && minSqdx < minSqdz) {
				this.setBodyRoll(0);
				if (dz < 0)
					this.setBodyPitch(90);
				else
					this.setBodyPitch(-90);
			} else if (minSqdz < minSqdx && minSqdz < minSqdy) {
				this.setBodyPitch(0);
				if (dx < 0)
					this.setBodyRoll(90);
				else
					this.setBodyRoll(-90);
			} else {
				this.setBodyPitch(0);
				this.setBodyRoll(0);
			}
		}
	}

	public float getBodyPitch() {
		return this.dataManager.get(PITCH).floatValue();
	}

	public void setBodyPitch(float pitch) {
		if (this.bodyPitch != pitch) {
			this.bodyPitch = pitch;
			this.resetCollisionBB();
			this.dataManager.set(PITCH, pitch);
		}
	}

	public float getBodyRoll() {
		return this.dataManager.get(ROLL).floatValue();
	}

	public void setBodyRoll(float roll) {
		if (this.bodyRoll != roll) {
			this.bodyRoll = roll;
			this.resetCollisionBB();
			this.dataManager.set(ROLL, roll);
		}
	}

	public void resetCollisionBB() {
		double d0 = (double) width / 2.0D;
		double d1 = (double) width / 2.0D;
		if (this.bodyPitch > 45f) {
			d0 = (double) height / 2.0D;
			d1 = (double) width / 2.0D;
		} else if (this.bodyRoll > 45f) {
			d1 = (double) height / 2.0D;
			d0 = (double) width / 2.0D;
		}
		this.setEntityBoundingBox(new AxisAlignedBB(this.posX - d1, this.posY, this.posZ - d0, this.posX + d1, this.posY + (double) this.height, this.posZ + d0));
	}

	// Leveled entity part
	@Override
	public void setLevel(int levelIn) {
		this.experienceValue = LevelUtil.getExperienceValue(levelIn);
		LevelUtil.setMobAttributes(this, levelIn);
		this.setHealth(this.getMaxHealth());
	}

	ResourceLocation lootTable = new ResourceLocation(LabyrinthMod.MODID + ":dungeon_loot_level_0");

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		compound.setInteger("level", this.level);
		compound.setString("lootTable", lootTable.toString());
	}

	@Override
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
	@Override
	protected boolean canDespawn() {
		return false;
	}

	/**
	 * Remove despawn.
	 */
	@Override
	protected void despawnEntity() {
	}

	class AIWeaveWeb extends EntityAIBase {
		public AIWeaveWeb() {
			this.setMutexBits(4);
		}

		/**
		 * Returns whether the EntityAIBase should begin execution.
		 */
		public boolean shouldExecute() {
			return !EntityMiniSpiderLeveled.this.getMoveHelper().isUpdating() && EntityMiniSpiderLeveled.this.rand.nextInt(200) == 0;
		}

		/**
		 * Returns whether an in-progress EntityAIBase should continue executing
		 */
		public boolean shouldContinueExecuting() {
			return false;
		}

		/**
		 * Keep ticking a continuous task that has already been started
		 */
		public void updateTask() {
			BlockPos blockpos = new BlockPos(EntityMiniSpiderLeveled.this);
			if (EntityMiniSpiderLeveled.this.world.isAirBlock(blockpos)) {
				EntityMiniSpiderLeveled.this.world.setBlockState(blockpos, Blocks.WEB.getDefaultState());
			}
		}
	}
}
