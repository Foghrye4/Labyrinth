package labyrinth.entity;

import labyrinth.util.LevelUtil;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.world.World;

public class EntitySpiderLeveled extends EntitySpider implements IMobLeveled {

	public EntitySpiderLeveled(World worldIn) {
		super(worldIn);
	}
	
	@Override
	public void setLevel(int levelIn) {
		this.experienceValue=LevelUtil.getExperienceValue(levelIn);
		LevelUtil.setMobAttributes(this, levelIn);
        this.setHealth(this.getMaxHealth());
	}
}
