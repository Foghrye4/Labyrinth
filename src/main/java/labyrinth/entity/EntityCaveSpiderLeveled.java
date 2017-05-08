package labyrinth.entity;

import labyrinth.util.LevelUtil;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.world.World;

public class EntityCaveSpiderLeveled extends EntityCaveSpider implements IMobLeveled {

	public EntityCaveSpiderLeveled(World worldIn) {
		super(worldIn);
	}
	
	@Override
	public void setLevel(int levelIn) {
		this.experienceValue=LevelUtil.getExperienceValue(levelIn);
		LevelUtil.setMobAttributes(this, levelIn);
        this.setHealth(this.getMaxHealth());
	}
}
