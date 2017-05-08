package labyrinth.init;

import labyrinth.LabyrinthMod;
import labyrinth.entity.*;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class LabyrinthEntities {
	private static int ID = 0;
	public static Class<? extends EntityLivingBase> ZOMBIE = EntityZombieLeveled.class;
	public static Class<? extends EntityLivingBase> BLAZE = EntityBlazeLeveled.class;
	public static Class<? extends EntityLivingBase> CAVE_SPIDER = EntityCaveSpiderLeveled.class;
	public static Class<? extends EntityLivingBase> CREEPER = EntityCreeperLeveled.class;
	public static Class<? extends EntityLivingBase> ELDER_GUARDIAN = EntityElderGuardianLeveled.class;
	public static Class<? extends EntityLivingBase> ENDERMAN = EntityEndermanLeveled.class;
	public static Class<? extends EntityLivingBase> ENDERMITE = EntityEndermiteLeveled.class;
	public static Class<? extends EntityLivingBase> EVOKER = EntityEvokerLeveled.class;
	public static Class<? extends EntityLivingBase> GHAST = EntityGhastLeveled.class;
	public static Class<? extends EntityLivingBase> GUARDIAN = EntityGuardianLeveled.class;
	public static Class<? extends EntityLivingBase> MAGMA_CUBE = EntityMagmaCubeLeveled.class;
	public static Class<? extends EntityLivingBase> PIG_ZOMBIE = EntityPigZombieLeveled.class;
	public static Class<? extends EntityLivingBase> SILVERFISH = EntitySilverfishLeveled.class;
	public static Class<? extends EntityLivingBase> SKELETON = EntitySkeletonLeveled.class;
	public static Class<? extends EntityLivingBase> SLIME = EntitySlimeLeveled.class;
	public static Class<? extends EntityLivingBase> SPIDER = EntitySpiderLeveled.class;
	public static Class<? extends EntityLivingBase> STRAY = EntityStrayLeveled.class;
	public static Class<? extends EntityLivingBase> VEX = EntityVexLeveled.class;
	public static Class<? extends EntityLivingBase> VINDICATOR = EntityVindicatorLeveled.class;
	public static Class<? extends EntityLivingBase> WITCH = EntityWitchLeveled.class;
	public static Class<? extends EntityLivingBase> WITHER_SKELETON = EntityWitherSkeletonLeveled.class;
	
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
		registerModEntity(STRAY, "Stray", mod);
		registerModEntity(VEX, "Vex", mod);
		registerModEntity(VINDICATOR, "Vindicator", mod);
		registerModEntity(WITCH, "Witch", mod);
		registerModEntity(WITHER_SKELETON, "WitherSkeleton", mod);
	}
	
	private static void registerModEntity(Class<? extends EntityLivingBase> classIn, String name, Object mod) {
		EntityRegistry.registerModEntity(new ResourceLocation(LabyrinthMod.MODID, name), classIn, name, ID++, mod, 80, 3, true);
	}
}
