package labyrinth.entity;

import labyrinth.util.LevelUtil;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.world.World;

public class EntitySilverfishLeveled extends EntitySilverfish implements IMobLeveled {

	public EntitySilverfishLeveled(World worldIn) {
		super(worldIn);
	}

	@Override
	public void setLevel(int levelIn) {
		this.experienceValue = LevelUtil.getExperienceValue(levelIn);
		LevelUtil.setMobAttributes(this, levelIn);
		this.setHealth(this.getMaxHealth());
	}
}