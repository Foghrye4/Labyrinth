package labyrinth.entity;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class EntityZombieLeveled extends EntityZombie {

	public int level = 0;
	public EntityZombieLeveled(World worldIn) {
		super(worldIn);
	}
	
	@Override
    protected void initEntityAI() {
		super.initEntityAI();
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 16.0F));
    }

	public void setLevel(int levelIn) {
		level = levelIn;
		int sql = levelIn*levelIn;
		this.experienceValue=5*(1+sql);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0d+levelIn);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2d+levelIn*0.02d);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0d+sql);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(levelIn);
        this.setHealth(this.getMaxHealth());
	}
}
