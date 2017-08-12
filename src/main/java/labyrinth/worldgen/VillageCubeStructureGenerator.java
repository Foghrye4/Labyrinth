package labyrinth.worldgen;

import java.util.Random;

import cubicchunks.util.CubePos;
import cubicchunks.world.ICubicWorld;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

public class VillageCubeStructureGenerator implements ICubeStructureGenerator {

	private final Random random = new Random();

	@SuppressWarnings("unchecked")
	private final Class<? extends EntityLiving>[] VILLAGE_ENTITIES = new Class[]{
			EntityVillager.class,
			EntityChicken.class,
			EntityOcelot.class
	};

	@SuppressWarnings("unchecked")
	private final Class<? extends EntityLiving>[] VILLAGE_CORRAL_ENTITIES = new Class[]{
			EntityCow.class,
			EntityPig.class,
			EntitySheep.class
	};

	public DungeonCube[] randomDungeonsArray = new DungeonCube[]{
			DungeonCube.VILLAGE_PARK,
			DungeonCube.VILLAGE_PARK,
			DungeonCube.VILLAGE_PARK,
			DungeonCube.VILLAGE_HOME
	};

	public boolean isVillage(CubePos cpos, ICubicWorld world) {
		int x = cpos.getX() >> 3;
		int z = cpos.getZ() >> 3;
		if((x & 3 | z & 3) != 0)
			return false;
		long hash = 3;
		hash = 41 * hash + world.getSeed();
		hash = 41 * hash + cpos.getY();
		hash = 41 * hash + x;
		hash = 41 * hash + z;
		random.setSeed(hash);
		return random.nextInt(16) == 0;
	}

	@Override
	public DungeonCube getDungeonCubeType(CubePos cpos, ICubicWorld world) {
		int localX = cpos.getX() & 7;
		int localZ = cpos.getZ() & 7;
		boolean eastBorder = localX == 7;
		boolean westBorder = localX == 0;
		boolean southBorder = localZ == 7;
		boolean northBorder = localZ == 0;
		boolean centralWest = localX == 3;
		boolean centralEast = localX == 4;
		if (northBorder && westBorder)
			return DungeonCube.VILLAGE_NORTH_WEST;
		if (northBorder && eastBorder)
			return DungeonCube.VILLAGE_NORTH_EAST;
		if (southBorder && westBorder)
			return DungeonCube.VILLAGE_SOUTH_WEST;
		if (southBorder && eastBorder)
			return DungeonCube.VILLAGE_SOUTH_EAST;
		if (northBorder && centralWest)
			return DungeonCube.VILLAGE_NORTH_GATE_WEST_SIDE;
		if (northBorder && centralEast)
			return DungeonCube.VILLAGE_NORTH_GATE_EAST_SIDE;
		if (southBorder && centralWest)
			return DungeonCube.VILLAGE_SOUTH_GATE_WEST_SIDE;
		if (southBorder && centralEast)
			return DungeonCube.VILLAGE_SOUTH_GATE_EAST_SIDE;
		if (westBorder)
			return DungeonCube.VILLAGE_WEST;
		if (eastBorder)
			return DungeonCube.VILLAGE_EAST;
		if (southBorder)
			return DungeonCube.VILLAGE_SOUTH;
		if (northBorder)
			return DungeonCube.VILLAGE_NORTH;
		if (centralWest)
			return DungeonCube.VILLAGE_CENTRAL_WEST_SIDE;
		if (centralEast)
			return DungeonCube.VILLAGE_CENTRAL_EAST_SIDE;

		if((localX & 1 | localZ & 1) == 0)
			return DungeonCube.VILLAGE_HOME;
		else
			return DungeonCube.VILLAGE_PARK;
	}

	@Override
	public void spawnMobs(int level, ICubicWorld world, CubePos pos, ExtendedBlockStorage data) {
		Class<? extends EntityLiving> entityClass;
		int dy = 0;
		int space = 10;
		if (this.getDungeonCubeType(pos, world).isCorral) {
			dy = 1;
			space = 4;
			entityClass = VILLAGE_CORRAL_ENTITIES[random.nextInt(VILLAGE_CORRAL_ENTITIES.length)];
		} else {
			entityClass = VILLAGE_ENTITIES[random.nextInt(VILLAGE_ENTITIES.length)];
		}
		EntityLiving entity;
		for (int dx = random.nextInt(space); dx < 15; dx += random.nextInt(space) + 2)
			for (int dz = random.nextInt(space); dz < 15; dz += random.nextInt(space) + 2)
				try {
					if (!data.get(dx, dy, dz).isFullBlock() || data.get(dx, dy+1, dz).getMaterial() != Material.AIR || data.get(dx, dy+2, dz).getMaterial() != Material.AIR)
						continue;
					entity = entityClass.getDeclaredConstructor(World.class).newInstance(world);
					entity.setLocationAndAngles(pos.getMinBlockX() + dx + 0.5, pos.getMinBlockY() + dy + 1,
							pos.getMinBlockZ() + dz + 0.5, random.nextFloat() * 360.0F, 0.0F);
					if (entityClass == EntityVillager.class) {
						VillagerRegistry.setRandomProfession((EntityVillager) entity, random);
					}
					if (entity instanceof EntityAgeable && random.nextFloat() < 0.2f) {
						((EntityAgeable) entity).setGrowingAge(-24000);
					}
					if (entity instanceof EntityTameable) {
						((EntityTameable) entity).setTamed(true);
					}
					if(entity.isNotColliding())
						world.spawnEntity(entity);
					else
						entity.setDead();
					world.spawnEntity(entity);
				} catch (Throwable e) {
					e.printStackTrace();
				}
	}
}
