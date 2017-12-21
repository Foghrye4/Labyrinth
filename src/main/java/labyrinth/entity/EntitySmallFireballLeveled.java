package labyrinth.entity;

import labyrinth.util.LevelUtil;
import labyrinth.worldgen.LabyrinthWorldGen;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntitySmallFireballLeveled extends EntitySmallFireball {

	public EntitySmallFireballLeveled(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ) {
		super(worldIn, shooter, accelX, accelY, accelZ);
	}

	protected void onImpact(RayTraceResult result) {
		if (!this.world.isRemote) {
			if (result.entityHit != null) {
				if (!result.entityHit.isImmuneToFire()) {
					double damage = LevelUtil.getAttackDamage(LabyrinthWorldGen.instance.getConfig().getLevel(this.shootingEntity.getPosition()));
					boolean flag = result.entityHit.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), (float) damage);

					if (flag) {
						this.applyEnchantments(this.shootingEntity, result.entityHit);
						result.entityHit.setFire(5);
					}
				}
			} else {
				boolean flag1 = true;

				if (this.shootingEntity != null && this.shootingEntity instanceof EntityLiving) {
					flag1 = this.world.getGameRules().getBoolean("mobGriefing");
				}

				if (flag1) {
					BlockPos blockpos = result.getBlockPos().offset(result.sideHit);

					if (this.world.isAirBlock(blockpos)) {
						this.world.setBlockState(blockpos, Blocks.FIRE.getDefaultState());
					}
				}
			}

			this.setDead();
		}
	}

}
