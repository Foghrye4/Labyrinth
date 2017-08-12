package labyrinth.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;

public class LevelUtil {

	public static int getExperienceValue(int levelIn) {
		return 1 + levelIn * 4;
	}

	public static double getMaxHealth(int levelIn) {
		return 7.0d + levelIn * 2;
	}

	public static double getMovementSpeed(int levelIn) {
		return 0.2d + (levelIn > 20 ? 20 : levelIn) * 0.01d;
	}
	public static double getAttackDamage(int levelIn) {
		return 10.0d + levelIn;
	}
	public static double getArmor(int levelIn) {
		return levelIn;
	}

	public static int getSlimeSize(int levelIn) {
		return 4;
	}

	public static void setMobAttributes(EntityLivingBase entity, int levelIn) {
		entity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16.0D*12);
		entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(getMaxHealth(levelIn));
		entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(getMovementSpeed(levelIn));
		entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(getAttackDamage(levelIn));
		entity.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(getArmor(levelIn));
	}

	public static int getExplosionStrength(int level) {
		return level > 32 ? 32 : level;
	}
}
