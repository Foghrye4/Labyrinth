package labyrinth.entity;

import labyrinth.util.LevelUtil;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.world.World;

public class EntityBlazeLeveled extends EntityBlaze implements IMobLeveled {

	public EntityBlazeLeveled(World worldIn) {
		super(worldIn);
	}
	
	@Override
	public void setLevel(int levelIn) {
		this.experienceValue=LevelUtil.getExperienceValue(levelIn);
		LevelUtil.setMobAttributes(this, levelIn);
        this.setHealth(this.getMaxHealth());
	}
}
