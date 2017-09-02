package labyrinth.worldgen;

import java.util.Random;

import cubicchunks.util.CubePos;
import cubicchunks.world.ICubicWorld;
import cubicchunks.world.cube.Cube;
import labyrinth.tileentity.TileEntityVillageMarket;
import labyrinth.village.UndergroundVillage;

import static labyrinth.village.UndergroundVillage.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

public class VillageCubeStructureGenerator implements ICubeStructureGenerator {

	private final Random random = new Random();

	private LabyrinthWorldGen generator;
	public VillageCubeStructureGenerator(LabyrinthWorldGen generatorIn) {
		generator = generatorIn;
	}

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
		int x = cpos.getX() >> BIT_SIZE;
		int z = cpos.getZ() >> BIT_SIZE;
		if ((x & 3 | z & 3) != 0)
			return false;
		long hash = 3;
		hash = 41 * hash + world.getSeed();
		hash = 41 * hash + cpos.getY();
		hash = 41 * hash + x;
		hash = 41 * hash + z;
		random.setSeed(hash);
		boolean canPlaceVillageHere = random.nextInt(16) == 0;
		if (!canPlaceVillageHere)
			return false;
		int cy = cpos.getY();
		int cx1 = cpos.getX() & INV_BIT_MASK;
		int cz1 = cpos.getZ() & INV_BIT_MASK;
		int cx2 = cx1 | LOCAL_BIT_MASK;
		int cz2 = cz1 | LOCAL_BIT_MASK;
		int cxmax = Math.max(cx1, cx2);
		int czmax = Math.max(cz1, cz2);
		for (int cx = Math.min(cx1, cx2); cx <= cxmax; cx++)
			for (int cz = Math.min(cz1, cz2); cz <= czmax; cz++)
				if (!this.generator.shouldGenerateAtPos(new CubePos(cx, cy, cz), world))
					return false;
		return true;
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
		boolean center = (localZ == 3 || localZ == 4) && (localX == 3 || localX == 4);
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
		if (center && centralWest)
			return DungeonCube.VILLAGE_MARKET_WEST;
		if (center && centralEast)
			return DungeonCube.VILLAGE_MARKET_EAST;
		if (centralWest)
			return DungeonCube.VILLAGE_CENTRAL_WEST_SIDE;
		if (centralEast)
			return DungeonCube.VILLAGE_CENTRAL_EAST_SIDE;

		if ((localX & 1 | localZ & 1) == 0)
			return DungeonCube.VILLAGE_HOME;
		else
			return DungeonCube.VILLAGE_PARK;
	}

	@Override
	public void spawnMobs(int level, ICubicWorld world, CubePos pos, ExtendedBlockStorage data) {
		Class<? extends EntityLiving> entityClass;
		int space = 10;
		DungeonCube ct = this.getDungeonCubeType(pos, world);
		if (ct.isCorral || ct.isMarket)
			space = 6;
		EntityLiving entity;
		for (int dy = 0; dy <= 1; dy++)
		for (int dx = random.nextInt(space); dx < 15; dx += random.nextInt(space) + 2)
		for (int dz = random.nextInt(space); dz < 15; dz += random.nextInt(space) + 2)
				try {
					if (ct.isCorral)
						entityClass = VILLAGE_CORRAL_ENTITIES[random.nextInt(VILLAGE_CORRAL_ENTITIES.length)];
					else
						entityClass = VILLAGE_ENTITIES[random.nextInt(VILLAGE_ENTITIES.length)];
					if (!data.get(dx, dy, dz).isFullBlock() || data.get(dx, dy + 1, dz).getMaterial() != Material.AIR || data.get(dx, dy + 2, dz).getMaterial() != Material.AIR)
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
					if (entity.isNotColliding())
						world.spawnEntity(entity);
					else
						entity.setDead();
				} catch (Throwable e) {
					e.printStackTrace();
				}
	}

	@Override
	public void placeCube(int level, Cube cube, ExtendedBlockStorage cstorage, CubePos pos, ICubicWorld world, byte[] data, IBlockState[] bl, DungeonCube is) {
		UndergroundVillage currentVillage = null;
		if (is.isVillageHome) {
			for (Village village : ((WorldServer) world).villageCollection.getVillageList()) {
				if (!(village instanceof UndergroundVillage))
					continue;
				UndergroundVillage uVillage = (UndergroundVillage) village;
				if (uVillage.isBlockPosWithinSqVillageRadius(pos)) {
					currentVillage = uVillage;
					break;
				}
			}
			if (currentVillage == null) {
				currentVillage = new UndergroundVillage((World) world);
				((WorldServer) world).villageCollection.getVillageList().add(currentVillage);
			}
		}
		for (int index = 0; index < data.length; index++) {
			int dx = index >>> 8;
			int dy = (index >>> 4) & 15;
			int dz = index & 15;
			int bstate = Byte.toUnsignedInt(data[index]);
			BlockPos bpos = new BlockPos(pos.getMinBlockX() + dx, pos.getMinBlockY() + dy, pos.getMinBlockZ() + dz);
			cstorage.setBlockLight(new NibbleArray(is.lightData.clone()));
			IBlockState blockState = bl[bstate];
			cstorage.set(dx, dy, dz, blockState);
			cube.getColumn().getOpacityIndex().onOpacityChange(dx, pos.getMinBlockY() + dy, dz, bl[bstate].getLightOpacity((IBlockAccess) world, bpos));
			if (bstate >= 3 && bstate <= 6) {
				TileEntityChest chest = new TileEntityChest();
				NBTTagCompound compound = new NBTTagCompound();
				compound.setString("LootTable", generator.regularLoot[level >= generator.regularLoot.length ? generator.regularLoot.length - 1 : level].toString());
				chest.readFromNBT(compound);
				chest.markDirty();
				chest.setPos(bpos);
				world.setTileEntity(bpos, chest);
				chest.validate();
			} else if (bstate == 121) {
				TileEntityVillageMarket counter = new TileEntityVillageMarket();
				counter.setPos(bpos);
				counter.markDirty();
				world.setTileEntity(bpos, counter);
				counter.validate();
			} else if (is.isVillageHome && bstate >= 58 && bstate <= 65 || bstate >= 78 && bstate <= 85) {
				currentVillage.addVillageDoorInfo(new VillageDoorInfo(bpos, 8 - dx, 8 - dz, 0));
			}
		}
	}
}
