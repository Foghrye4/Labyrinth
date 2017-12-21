package labyrinth.init;

import labyrinth.LabyrinthMod;
import labyrinth.entity.EntityBlazeLeveled;
import labyrinth.entity.EntityCaveSpiderLeveled;
import labyrinth.entity.EntityCreeperLeveled;
import labyrinth.entity.EntityElderGuardianLeveled;
import labyrinth.entity.EntityEndermanLeveled;
import labyrinth.entity.EntityEndermiteLeveled;
import labyrinth.entity.EntityEvokerLeveled;
import labyrinth.entity.EntityGhastLeveled;
import labyrinth.entity.EntityGuardianLeveled;
import labyrinth.entity.EntityMagmaCubeLeveled;
import labyrinth.entity.EntityMiniSpiderLeveled;
import labyrinth.entity.EntityPigZombieLeveled;
import labyrinth.entity.EntitySilverfishLeveled;
import labyrinth.entity.EntitySkeletonLeveled;
import labyrinth.entity.EntitySlimeLeveled;
import labyrinth.entity.EntitySpiderLeveled;
import labyrinth.entity.EntityStrayLeveled;
import labyrinth.entity.EntityVexLeveled;
import labyrinth.entity.EntityVindicatorLeveled;
import labyrinth.entity.EntityWitherSkeletonLeveled;
import labyrinth.entity.EntityZombieLeveled;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class LabyrinthEntities {
	private static int ID = 0;
	public static Class<? extends EntityLiving> ZOMBIE = EntityZombieLeveled.class;
	public static Class<? extends EntityLiving> BLAZE = EntityBlazeLeveled.class;
	public static Class<? extends EntityLiving> CAVE_SPIDER = EntityCaveSpiderLeveled.class;
	public static Class<? extends EntityLiving> CREEPER = EntityCreeperLeveled.class;
	public static Class<? extends EntityLiving> ELDER_GUARDIAN = EntityElderGuardianLeveled.class;
	public static Class<? extends EntityLiving> ENDERMAN = EntityEndermanLeveled.class;
	public static Class<? extends EntityLiving> ENDERMITE = EntityEndermiteLeveled.class;
	public static Class<? extends EntityLiving> EVOKER = EntityEvokerLeveled.class;
	public static Class<? extends EntityLiving> GHAST = EntityGhastLeveled.class;
	public static Class<? extends EntityLiving> GUARDIAN = EntityGuardianLeveled.class;
	public static Class<? extends EntityLiving> MAGMA_CUBE = EntityMagmaCubeLeveled.class;
	public static Class<? extends EntityLiving> PIG_ZOMBIE = EntityPigZombieLeveled.class;
	public static Class<? extends EntityLiving> SILVERFISH = EntitySilverfishLeveled.class;
	public static Class<? extends EntityLiving> SKELETON = EntitySkeletonLeveled.class;
	public static Class<? extends EntityLiving> SLIME = EntitySlimeLeveled.class;
	public static Class<? extends EntityLiving> SPIDER = EntitySpiderLeveled.class;
	public static Class<? extends EntityLiving> MINI_SPIDER = EntityMiniSpiderLeveled.class;
	public static Class<? extends EntityLiving> STRAY = EntityStrayLeveled.class;
	public static Class<? extends EntityLiving> VEX = EntityVexLeveled.class;
	public static Class<? extends EntityLiving> VINDICATOR = EntityVindicatorLeveled.class;
	public static Class<? extends EntityLiving> WITHER_SKELETON = EntityWitherSkeletonLeveled.class;
	
	public static void register(Object mod) {
		registerModEntity(ZOMBIE, "Zombie", mod);
		registerModEntity(BLAZE, "Blaze", mod);
		registerModEntity(CAVE_SPIDER, "CaveSpider", mod);
		registerModEntity(CREEPER, "Creeper", mod);
		registerModEntity(ELDER_GUARDIAN, "ElderGuardian", mod);
		registerModEntity(ENDERMAN, "Enderman", mod);
		registerModEntity(ENDERMITE, "Endermite", mod);
		registerModEntity(EVOKER, "Evoker", mod);
		registerModEntity(GHAST, "Ghast", mod);
		registerModEntity(GUARDIAN, "Guardian", mod);
		registerModEntity(MAGMA_CUBE, "MagmaCube", mod);
		registerModEntity(PIG_ZOMBIE, "PigZombie", mod);
		registerModEntity(SILVERFISH, "Silverfish", mod);
		registerModEntity(SKELETON, "Skeleton", mod);
		registerModEntity(SLIME, "Slime", mod);
		registerModEntity(SPIDER, "Spider", mod);
		registerModEntity(MINI_SPIDER, "MiniSpider", mod);
		registerModEntity(STRAY, "Stray", mod);
		registerModEntity(VEX, "Vex", mod);
		registerModEntity(VINDICATOR, "Vindicator", mod);
		registerModEntity(WITHER_SKELETON, "WitherSkeleton", mod);
	}
	
	private static void registerModEntity(Class<? extends EntityLiving> classIn, String name, Object mod) {
		EntityRegistry.registerModEntity(new ResourceLocation(LabyrinthMod.MODID, name), classIn, name, ID++, mod, 80, 3, true);
	}
}
