package labyrinth.worldgen;

import java.io.IOException;
import java.util.Random;

import cubicchunks.api.CubeWatchEvent;
import cubicchunks.api.worldgen.populator.CubePopulatorEvent;
import cubicchunks.api.worldgen.populator.CubicPopulatorList;
import cubicchunks.api.worldgen.populator.ICubicPopulator;
import cubicchunks.util.Coords;
import cubicchunks.util.CubePos;
import cubicchunks.world.ICubeProvider;
import cubicchunks.world.ICubicWorld;
import cubicchunks.world.cube.Cube;
import labyrinth.LabyrinthMod;
import labyrinth.world.WorldSavedDataLabyrinth;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LabyrinthWorldGen {

	private WorldSavedDataLabyrinth config;
	public static LabyrinthWorldGen instance;
	public LevelFeaturesStorage storage;
	private final Random random = new Random();
	private final CubicPopulatorList biomeDecorators = new CubicPopulatorList();
	public final ICubeStructureGenerator basicCubeStructureGenerator = new RegularCubeStructureGenerator(this);
	public final ICubeStructureGenerator lavaCubeStructureGenerator = new LavaCubeStructureGenerator(this);
	public final VillageCubeStructureGenerator villageCubeStructureGenerator = new VillageCubeStructureGenerator(this);
	public final TunnelCubeStructureGenerator tunnelGenerator = new TunnelCubeStructureGenerator(this);
	static final int CITY_BIT_SIZE = 6; // 6
	static final int CITY_EVEN_OR_ODD_MASK = 1 << CITY_BIT_SIZE;
	static final int CITY_MASK = CITY_EVEN_OR_ODD_MASK - 1;
	static final int TUNNEL_LOCATION = CITY_MASK >> 1;
	static final int TUNNEL_MASK = (1 << CITY_BIT_SIZE + 1) - 1;
	static final int CITY_PROBABILITY = 6;

	public final ResourceLocation[] regularLoot = new ResourceLocation[]{
			new ResourceLocation(LabyrinthMod.MODID, "dungeon_loot_level_0"),
			new ResourceLocation(LabyrinthMod.MODID, "dungeon_loot_level_1"),
			new ResourceLocation(LabyrinthMod.MODID, "dungeon_loot_level_2"),
			new ResourceLocation(LabyrinthMod.MODID, "dungeon_loot_level_3"),
			new ResourceLocation(LabyrinthMod.MODID, "dungeon_loot_level_4"),
			new ResourceLocation(LabyrinthMod.MODID, "dungeon_loot_level_5"),
			new ResourceLocation(LabyrinthMod.MODID, "dungeon_loot_level_6"),
			new ResourceLocation(LabyrinthMod.MODID, "dungeon_loot_level_7"),
			new ResourceLocation(LabyrinthMod.MODID, "dungeon_loot_level_8"),
			new ResourceLocation(LabyrinthMod.MODID, "dungeon_loot_level_9"),
			new ResourceLocation(LabyrinthMod.MODID, "dungeon_loot_level_9"),
			new ResourceLocation(LabyrinthMod.MODID, "dungeon_loot_level_9"),
			new ResourceLocation(LabyrinthMod.MODID, "dungeon_loot_level_12"),
			new ResourceLocation(LabyrinthMod.MODID, "dungeon_loot_level_12"),
			new ResourceLocation(LabyrinthMod.MODID, "dungeon_loot_level_12"),
			new ResourceLocation(LabyrinthMod.MODID, "dungeon_loot_level_15")};

	public final ResourceLocation[] libraryLoot = new ResourceLocation[]{
			new ResourceLocation(LabyrinthMod.MODID, "library_loot_level_0"),
			new ResourceLocation(LabyrinthMod.MODID, "library_loot_level_1"),
			new ResourceLocation(LabyrinthMod.MODID, "library_loot_level_2"),
			new ResourceLocation(LabyrinthMod.MODID, "library_loot_level_3"),
			new ResourceLocation(LabyrinthMod.MODID, "library_loot_level_4"),
			new ResourceLocation(LabyrinthMod.MODID, "library_loot_level_5"),
			new ResourceLocation(LabyrinthMod.MODID, "library_loot_level_6"),
			new ResourceLocation(LabyrinthMod.MODID, "library_loot_level_7")};

	public LabyrinthWorldGen() throws IOException {
		instance = this;
		storage = new LevelFeaturesStorage();
		for (DungeonCube cube : DungeonCube.values()) {
			if (cube != DungeonCube.NOTHING && cube != DungeonCube.UNDEFINED) {
				cube.load();
				cube.precalculateLight();
			}
		}

	}

	public boolean shouldGenerateAtPos(CubePos pos, ICubicWorld world) {
		int level = config.getLevel(pos);
		if (level < 0)
			return false;
		float biomeHeightBase = world.getBiome(pos.getCenterBlockPos()).getBaseHeight();
		if (biomeHeightBase < config.dungeonBiomeHeightLowerBound
				|| biomeHeightBase > config.dungeonBiomeHeightUpperBound)
			return false;
		else {
			if ((pos.getX() & CITY_EVEN_OR_ODD_MASK | pos.getZ() & CITY_EVEN_OR_ODD_MASK) == 0) {
				return this.hasCityAtPos(pos.getX(), pos.getZ(), level, world);
			} else {
				if ((level & 1) == 0)
					return false;
				if ((pos.getY() & 1) == 0)
					return false;
				if ((pos.getX() & TUNNEL_MASK) == TUNNEL_LOCATION) {
					if (this.hasCityAtPos(pos.getX(), pos.getZ() - CITY_EVEN_OR_ODD_MASK, level, world) &&
							this.hasCityAtPos(pos.getX(), pos.getZ() + CITY_EVEN_OR_ODD_MASK, level, world))
						return true;
					else
						return false;
				}
				if ((pos.getZ() & TUNNEL_MASK) == TUNNEL_LOCATION) {
					if (this.hasCityAtPos(pos.getX() - CITY_EVEN_OR_ODD_MASK, pos.getZ(), level, world) &&
							this.hasCityAtPos(pos.getX() + CITY_EVEN_OR_ODD_MASK, pos.getZ(), level, world))
						return true;
					else
						return false;
				}
			}
		}
		return false;
	}

	private boolean hasCityAtPos(int x, int z, int level, ICubicWorld world) {
		long hash = 3;
		hash = 41 * hash + world.getSeed();
		hash = 41 * hash + (x >>> CITY_BIT_SIZE);
		hash = 41 * hash + level;
		long seed = 41 * hash + (z >>> CITY_BIT_SIZE);
		random.setSeed(seed);
		return random.nextInt(CITY_PROBABILITY) != 0;
	}

	@SubscribeEvent
	public void generate(CubePopulatorEvent event) {
		ICubicWorld world = event.getWorld();
		if (world.getProvider().getDimension() != 0)
			return;
		if(storage.lastSeed != world.getSeed())
			storage.generateRandom(world.getSeed());
		CubePos pos = event.getCube().getCoords();
		int level = config.getLevel(pos);
		DungeonCube is = getDungeonCubeType(pos, world);
		if (is == DungeonCube.UNDEFINED) {
			throw new IllegalStateException("Dungeon cube type selector return incorrect value.");
		}
		if (is == DungeonCube.NOTHING) {
			return;
		}
		ICubeStructureGenerator currentGenerator = this.selectGenerator(pos, world);
		random.setSeed(level << 8 ^ world.getSeed());
		IBlockState[] bl = this.storage.blockstateList[random.nextInt(this.storage.blockstateList.length)];
		if (level < this.storage.blockstateList.length) {
			if (is.isColumnTopOrMiddle) {
				bl = this.storage.blockstateList[config.getLevel(pos.below())];
			} else {
				bl = this.storage.blockstateList[level];
			}
		}
		byte[] data = is.data;
		Cube cube = world.getCubeFromCubeCoords(pos.getX(), pos.getY(), pos.getZ());
		ExtendedBlockStorage cstorage = cube.getStorage();
		if (cstorage == null) {
			cstorage = new ExtendedBlockStorage(pos.getMinBlockY(), true);
			cube.setStorage(cstorage);
		}
		currentGenerator.placeCube(level, cube, cstorage, pos, world, data, bl, is);
		this.config.spawnQuery.add(pos);
		this.config.markDirty();
		ICubeProvider cache = world.getCubeCache();
		World vWorld = (World) world;
		for (EnumFacing dir : EnumFacing.values()) {
			CubePos ncpos = pos.add(dir.getFrontOffsetX(), dir.getFrontOffsetY(), dir.getFrontOffsetZ());
			Cube loadedCube = cache.getLoadedCube(ncpos);
			if (loadedCube == null || loadedCube.getStorage() == null)
				continue;
			int fromBlockX = ncpos.getMinBlockX();
			int fromBlockY = ncpos.getMinBlockY();
			int fromBlockZ = ncpos.getMinBlockZ();
			int toBlockX = ncpos.getMaxBlockX();
			int toBlockY = ncpos.getMaxBlockY();
			int toBlockZ = ncpos.getMaxBlockZ();
			boolean extendBack = false;
			switch (dir) {
				case DOWN :
					fromBlockY = fromBlockY - 1;
					toBlockY = extendBack ? fromBlockY + 1 : fromBlockY;
					break;
				case UP :
					toBlockY = toBlockY + 1;
					fromBlockY = extendBack ? toBlockY - 1 : toBlockY;
					break;
				case NORTH :
					fromBlockZ = fromBlockY - 1;
					toBlockZ = extendBack ? fromBlockZ + 1 : fromBlockZ;
					break;
				case SOUTH :
					toBlockZ = toBlockZ + 1;
					fromBlockZ = extendBack ? toBlockZ - 1 : toBlockZ;
					break;
				case WEST :
					fromBlockX = fromBlockX - 1;
					toBlockX = extendBack ? fromBlockX + 1 : fromBlockX;
					break;
				case EAST :
					toBlockX = toBlockX + 1;
					fromBlockX = extendBack ? toBlockX - 1 : toBlockX;
					break;
			}
			assert (toBlockX >= fromBlockX);
			assert (toBlockY >= fromBlockY);
			assert (toBlockZ >= fromBlockZ);
			Iterable<MutableBlockPos> bPosC = BlockPos.MutableBlockPos.mutablesBetween(
					fromBlockX, fromBlockY, fromBlockZ, toBlockX, toBlockY, toBlockZ);
			for (MutableBlockPos bPos : bPosC) {
				IBlockState state = cube.getStorage().get(
						Coords.blockToLocal(bPos.getX()),
						Coords.blockToLocal(bPos.getY()),
						Coords.blockToLocal(bPos.getZ()));
				if (!state.getMaterial().isLiquid())
					continue;
				vWorld.scheduleBlockUpdate(bPos.toImmutable(), state.getBlock(), 20, 0);
			}

		}
		event.setCanceled(true);
	}

	@SubscribeEvent
	public void onCubeBeingWatched(CubeWatchEvent event) {
		Cube cube = event.getCube();
		if (cube == null)
			return;
		CubePos pos = cube.getCoords();
		if (this.config.spawnQuery.remove(pos)) {
			ICubeStructureGenerator currentGenerator = this.selectGenerator(pos, event.getWorld());
			currentGenerator.spawnMobs(this.config.getLevel(pos), event.getWorld(), pos, event.getCube().getStorage());
		}
	}

	public void setConfig(WorldSavedDataLabyrinth worldgenConfigIn) {
		this.config = worldgenConfigIn;
	}

	public void addBiomeDecorator(ICubicPopulator decorator) {
		this.biomeDecorators.add(decorator);
	}

	public DungeonCube getDungeonCubeType(CubePos pos, ICubicWorld world) {
		if (!this.shouldGenerateAtPos(pos, world))
			return DungeonCube.NOTHING;
		return this.selectGenerator(pos, world).getDungeonCubeType(pos, world);
	}

	private ICubeStructureGenerator selectGenerator(CubePos pos, ICubicWorld world) {
		int level = config.getLevel(pos);
		if ((pos.getX() & CITY_EVEN_OR_ODD_MASK | pos.getZ() & CITY_EVEN_OR_ODD_MASK) != 0)
			return this.tunnelGenerator;
		if (level > 12) {
			if (this.villageCubeStructureGenerator.isVillage(pos, world))
				return this.villageCubeStructureGenerator;
			else
				return this.lavaCubeStructureGenerator;
		} else
			return this.basicCubeStructureGenerator;
	}

	public WorldSavedDataLabyrinth getConfig() {
		return config;
	}
}
