package labyrinth.entity;

import labyrinth.util.LevelUtil;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.world.World;

public class EntityVindicatorLeveled extends EntityVindicator implements IMobLeveled {
	
	public EntityVindicatorLeveled(World worldIn) {
		super(worldIn);
	}

	@Override
	public void setLevel(int levelIn) {
		this.experienceValue=LevelUtil.getExperienceValue(levelIn);
		LevelUtil.setMobAttributes(this, levelIn);
        this.setHealth(this.getMaxHealth());
	}

}
