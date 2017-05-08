package labyrinth.entity;

import labyrinth.util.LevelUtil;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.world.World;

public class EntityVexLeveled extends EntityVex implements IMobLeveled {

	public EntityVexLeveled(World worldIn) {
		super(worldIn);
	}
	
	@Override
	public void setLevel(int levelIn) {
		this.experienceValue=LevelUtil.getExperienceValue(levelIn);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(0.5d);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(LevelUtil.getMovementSpeed(levelIn));
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(LevelUtil.getAttackDamage(levelIn));
        this.setHealth(this.getMaxHealth());
	}
}
