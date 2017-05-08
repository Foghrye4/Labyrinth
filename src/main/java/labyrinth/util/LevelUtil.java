package labyrinth.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.math.BlockPos;

public class LevelUtil {
	public static int getLevel(BlockPos pos) {
		return -pos.getY() / 32;
	}

	public static int getExperienceValue(int levelIn) {
		return 5*(1+levelIn*levelIn);
	}

	public static double getMaxHealth(int levelIn) {
		return 5.0d+levelIn;
	}

	public static double getMovementSpeed(int levelIn) {
		return 0.2d+(levelIn>40?40:levelIn)*0.02d;
	}
	public static double getAttackDamage(int levelIn) {
		return 5.0d+levelIn*levelIn;
	}
	public static double getArmor(int levelIn) {
		return levelIn;
	}
	
	public static int getSlimeSize(int levelIn) {
		return levelIn<8?levelIn+2:10;
	}

	public static void setMobAttributes(EntityLivingBase entity, int levelIn) {
		entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(getMaxHealth(levelIn));
		entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(getMovementSpeed(levelIn));
		entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(getAttackDamage(levelIn));
		entity.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(getArmor(levelIn));
	}
}
