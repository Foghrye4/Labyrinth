package labyrinth.entity;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

import labyrinth.LabyrinthMod;
import labyrinth.util.LevelUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityEndermanLeveled extends EntityEnderman implements IMobLeveled {

	public EntityEndermanLeveled(World worldIn) {
		super(worldIn);
	}

	@Override
	public void setLevel(int levelIn) {
		this.experienceValue = LevelUtil.getExperienceValue(levelIn);
		LevelUtil.setMobAttributes(this, levelIn);
		this.setHealth(this.getMaxHealth());
	}

	int level = 0;
	ResourceLocation lootTable = new ResourceLocation(LabyrinthMod.MODID+":dungeon_loot_level_0");

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

	protected void updateAITasks() {
		super.updateAITasks();
		if (this.getAttackTarget() == null) {
			EntityPlayer player = this.world.getNearestAttackablePlayer(this.posX, this.posY, this.posZ, 16d, 16d,
					(Function<EntityPlayer, Double>) null, new Predicate<EntityPlayer>() {
						public boolean apply(@Nullable EntityPlayer player) {
							return player != null;
						}
					});
			if (player != null)
				this.setAttackTarget(player);
		}
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
	int playerListIndex = 0;
	int maxPlayerListIndex = 10;
	Entity nearestPlayer = null;
	/**
	 * Do not update entities too far from player (to avoid lag).
	 */
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
				List<EntityPlayer> pList = this.getEntityWorld().playerEntities;
				if (this.playerListIndex < pList.size()) {
					EntityPlayer player = pList.get(this.playerListIndex);
					int dx = (int) (player.posX - this.posX);
					int dy = (int) (player.posY - this.posY);
					int dz = (int) (player.posZ - this.posZ);
					if (++this.playerListIndex >= this.maxPlayerListIndex) {
						this.maxPlayerListIndex *= 2;
					}
					if (dy * dy * 16 + dx * dx + dz * dz < 4096) {
						nearestPlayer = player;
					} else {
						return;
					}
				}
				if (this.playerListIndex >= this.maxPlayerListIndex) {
					this.playerListIndex = 0;
				}
				return;
			}
		}
		super.onUpdate();
	}
	
    public boolean attackEntityFrom(DamageSource source, float amount) {
    	if(source instanceof EntityDamageSource) {
    		EntityDamageSource eds = (EntityDamageSource) source;
    		nearestPlayer = eds.getEntity();
    	}
   		return super.attackEntityFrom(source, amount);
    }
}
