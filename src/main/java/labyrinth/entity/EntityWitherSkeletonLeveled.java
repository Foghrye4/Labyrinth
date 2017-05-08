package labyrinth.entity;

import labyrinth.util.LevelUtil;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.world.World;

public class EntityWitherSkeletonLeveled extends EntityWitherSkeleton implements IMobLeveled {

	public EntityWitherSkeletonLeveled(World worldIn) {
		super(worldIn);
	}
	
	@Override
	public void setLevel(int levelIn) {
		this.experienceValue=LevelUtil.getExperienceValue(levelIn);
		LevelUtil.setMobAttributes(this, levelIn);
        this.setHealth(this.getMaxHealth());
	}
}
