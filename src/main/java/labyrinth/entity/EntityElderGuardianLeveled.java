package labyrinth.entity;

import labyrinth.util.LevelUtil;
import net.minecraft.entity.monster.EntityElderGuardian;
import net.minecraft.world.World;

public class EntityElderGuardianLeveled extends EntityElderGuardian implements IMobLeveled {
	
	public EntityElderGuardianLeveled(World worldIn) {
		super(worldIn);
	}

	@Override
	public void setLevel(int levelIn) {
		this.experienceValue=LevelUtil.getExperienceValue(levelIn);
		LevelUtil.setMobAttributes(this, levelIn);
        this.setHealth(this.getMaxHealth());
	}

}
