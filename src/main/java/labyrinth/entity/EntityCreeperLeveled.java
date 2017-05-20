package labyrinth.entity;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import cubicchunks.world.ICubicWorld;
import labyrinth.LabyrinthMod;
import labyrinth.util.LevelUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityCreeperLeveled extends EntityCreeper implements IMobLeveled {

	public EntityCreeperLeveled(World worldIn) {
		super(worldIn);
	}

	private static final Predicate<Entity> EXPLOSION_TARGETS = new Predicate<Entity>() {
				public boolean apply(@Nullable Entity entity) {
					return (entity instanceof EntityLivingBase) && entity.isEntityAlive();
				};
	};
	
	@Override
	public void setLevel(int levelIn) {
		this.experienceValue = LevelUtil.getExperienceValue(levelIn);
		LevelUtil.setMobAttributes(this, levelIn);
		this.setHealth(this.getMaxHealth());
	}

	int level = 0;
	ResourceLocation lootTable = new ResourceLocation(LabyrinthMod.MODID+":dungeon_loot_level_0");
	
	@Override
	public void setDead() {
		if (this.dead && 
				!this.world.isRemote && 
				this.getHealth() > 0.0F && 
				this.getCreeperState() > 0 && 
				((ICubicWorld)world).isAreaLoaded(new BlockPos(this.getPosition().add(-16, -16, -16)), new BlockPos(this.getPosition().add(16, 16, 16)))) {
			List<Entity> elist = this.world.getEntitiesInAABBexcluding(this,
					this.getEntityBoundingBox().expandXyz(LevelUtil.getExplosionStrength(level)), EXPLOSION_TARGETS);
			Vec3d traceFrom = this.getEntityBoundingBox().getCenter();
			for (Entity target : elist) {
				Vec3d traceTo = target.getEntityBoundingBox().getCenter();
				if (world.rayTraceBlocks(traceFrom, traceTo).entityHit == target)
					target.attackEntityFrom(DamageSource.causeExplosionDamage(this),
							(float) LevelUtil.getAttackDamage(level));
			}
		}
		super.setDead();
	}
	
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
	/**
	 * Do not update entities too far from player (to avoid lag).
	 */
	Entity nearestPlayer = null;
	
	@Override
	public void onUpdate() {
		if (!world.isRemote) {
			if (nearestPlayer != null) {
				int dx = (int) (nearestPlayer.posX - this.posX);
				int dy = (int) (nearestPlayer.posY - this.posY);
				int dz = (int) (nearestPlayer.posZ - this.posZ);
				if (dy * dy * 16 + dx * dx + dz * dz > 6144) {
					nearestPlayer = null;
					return;
				}
			} else {
				for (EntityPlayer player:this.getEntityWorld().playerEntities) {
					int dx = (int) (player.posX - this.posX);
					int dy = (int) (player.posY - this.posY);
					int dz = (int) (player.posZ - this.posZ);
					if (dy * dy * 16 + dx * dx + dz * dz < 4096) {
						nearestPlayer = player;
					}
				}
				if (nearestPlayer == null) {
					return;
				}
			}
		}
		super.onUpdate();
	}
}