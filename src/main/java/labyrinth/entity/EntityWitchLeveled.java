package labyrinth.entity;

import labyrinth.util.LevelUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityWitchLeveled extends EntityWitch implements IMobLeveled {

	int level = 0;

	public EntityWitchLeveled(World worldIn) {
		super(worldIn);
	}

	@Override
	public void setLevel(int levelIn) {
		this.experienceValue = LevelUtil.getExperienceValue(levelIn);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(LevelUtil.getMaxHealth(levelIn));
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
				.setBaseValue(LevelUtil.getMovementSpeed(levelIn));
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(LevelUtil.getArmor(levelIn));
		this.setHealth(this.getMaxHealth());
	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
		if (!this.isDrinkingPotion()) {
			double d0 = target.posY + (double) target.getEyeHeight() - 1.1D;
			double d1 = target.posX + target.motionX - this.posX;
			double d2 = d0 - this.posY;
			double d3 = target.posZ + target.motionZ - this.posZ;
			float f = MathHelper.sqrt(d1 * d1 + d3 * d3);
			PotionType potiontype = PotionTypes.HARMING;

			if (f >= 8.0F && !target.isPotionActive(MobEffects.SLOWNESS)) {
				potiontype = PotionTypes.SLOWNESS;
			} else if (target.getHealth() >= 8.0F && !target.isPotionActive(MobEffects.POISON)) {
				potiontype = PotionTypes.POISON;
			} else if (f <= 3.0F && !target.isPotionActive(MobEffects.WEAKNESS) && this.rand.nextFloat() < 0.25F) {
				potiontype = PotionTypes.WEAKNESS;
			}

			EntityPotionLeveled entitypotion = new EntityPotionLeveled(this.world, this,
					PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), potiontype), level);

			entitypotion.rotationPitch -= -20.0F;
			entitypotion.setThrowableHeading(d1, d2 + (double) (f * 0.2F), d3, 0.75F, 8.0F);
			this.world.playSound((EntityPlayer) null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_WITCH_THROW,
					this.getSoundCategory(), 1.0F, 0.8F + this.rand.nextFloat() * 0.4F);
			this.world.spawnEntity(entitypotion);
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
