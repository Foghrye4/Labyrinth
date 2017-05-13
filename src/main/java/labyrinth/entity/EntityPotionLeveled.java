package labyrinth.entity;

import java.util.List;

import labyrinth.util.LevelUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityPotionLeveled extends EntityPotion {

	public int level = 0;

	public EntityPotionLeveled(World worldIn, EntityWitchLeveled entityWitchLeveled, ItemStack itemStack, int levelIn) {
		super(worldIn, entityWitchLeveled, itemStack);
		level=levelIn;
	}

	protected void onImpact(RayTraceResult result) {
		if (!this.world.isRemote) {
			ItemStack itemstack = this.getPotion();
			PotionType potiontype = PotionUtils.getPotionFromItem(itemstack);
			List<PotionEffect> list = PotionUtils.getEffectsFromStack(itemstack);
			if (!list.isEmpty()) {
				this.applySplash(result, list);
			}
			int i = potiontype.hasInstantEffect() ? 2007 : 2002;
			this.world.playEvent(i, new BlockPos(this), PotionUtils.getColor(itemstack));
			this.setDead();
		}
	}

	private void applySplash(RayTraceResult rayTraceResult, List<PotionEffect> potionEffectList) {
		if (rayTraceResult.entityHit instanceof EntityLivingBase) {
			EntityLivingBase entitylivingbase = (EntityLivingBase) rayTraceResult.entityHit;
			if (entitylivingbase.canBeHitWithPotion()) {
				for (PotionEffect potioneffect : potionEffectList) {
					Potion potion = potioneffect.getPotion();

					if (potion.isInstant()) {
						potion.affectEntity(this, this.getThrower(), entitylivingbase, potioneffect.getAmplifier(),
								LevelUtil.getAttackDamage(level));
					} else {
						entitylivingbase.addPotionEffect(
								new PotionEffect(potion, 10 + level * level, potioneffect.getAmplifier(),
										potioneffect.getIsAmbient(), potioneffect.doesShowParticles()));
					}
				}
			}
		}
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
