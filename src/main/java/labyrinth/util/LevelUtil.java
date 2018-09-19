package labyrinth.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;

public class LevelUtil {

	public static int getExperienceValue(int levelIn) {
		return 1 + levelIn * 4;
	}

	public static double getMaxHealth(int levelIn) {
		return 7.0d + levelIn * 4;
	}

	public static double getMovementSpeed(int levelIn) {
		return 0.2d + (levelIn > 40 ? 40 : levelIn) * 0.01d;
	}
	public static double getAttackDamage(int levelIn) {
		return 8.0d + levelIn;
	}
	public static double getArmor(int levelIn) {
		return levelIn;
	}

	public static int getSlimeSize(int levelIn) {
		return levelIn > 3 && levelIn <= 24 ? 2 : 1;
	}

	public static void setMobAttributes(EntityLivingBase entity, int levelIn) {
		entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(getMaxHealth(levelIn));
		entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(getMovementSpeed(levelIn));
		entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(getAttackDamage(levelIn));
		entity.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(getArmor(levelIn));
	}

	public static int getExplosionStrength(int level) {
		return level > 32 ? 32 : level;
	}
}
