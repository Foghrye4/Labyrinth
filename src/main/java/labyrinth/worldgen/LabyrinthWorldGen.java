package labyrinth.worldgen;

import java.io.IOException;
import java.util.Random;

import cubicchunks.api.worldgen.populator.CubePopulatorEvent;
import cubicchunks.api.worldgen.populator.CubicPopulatorList;
import cubicchunks.api.worldgen.populator.ICubicPopulator;
import cubicchunks.util.CubePos;
import cubicchunks.world.ICubicWorld;
import cubicchunks.world.cube.Cube;
import labyrinth.LabyrinthMod;
import labyrinth.world.WorldSavedDataLabyrinthConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LabyrinthWorldGen {

	private final static int MOB_SPAWN_RARITY = 8;
	private WorldSavedDataLabyrinthConfig config;
	public static LabyrinthWorldGen instance;
	public LevelFeaturesStorage storage;
	private final Random random = new Random();
	private final CubicPopulatorList biomeDecorators = new CubicPopulatorList();
	public final ICubeStructureGenerator basicCubeStructureGenerator = new RegularCubeStructureGenerator(this);
	public final ICubeStructureGenerator lavaCubeStructureGenerator = new LavaCubeStructureGenerator(this);
	public final VillageCubeStructureGenerator villageCubeStructureGenerator = new VillageCubeStructureGenerator();

	public final ResourceLocation[] regularLoot = new ResourceLocation[]{
			new ResourceLocation(LabyrinthMod.MODID, "dungeon_loot_level_0"),
			new ResourceLocation(LabyrinthMod.MODID, "dungeon_loot_level_1"),
			new ResourceLocation(LabyrinthMod.MODID, "dungeon_loot_level_2"),
			new ResourceLocation(LabyrinthMod.MODID, "dungeon_loot_level_3"),
			new ResourceLocation(LabyrinthMod.MODID, "dungeon_loot_level_4"),
			new ResourceLocation(LabyrinthMod.MODID, "dungeon_loot_level_5"),
			new ResourceLocation(LabyrinthMod.MODID, "dungeon_loot_level_6"),
			new ResourceLocation(LabyrinthMod.MODID, "dungeon_loot_level_7"),
			new ResourceLocation(LabyrinthMod.MODID, "dungeon_loot_level_8")};

	public final ResourceLocation[] libraryLoot = new ResourceLocation[]{
			new ResourceLocation(LabyrinthMod.MODID, "library/library_loot_level_0"),
			new ResourceLocation(LabyrinthMod.MODID, "library/library_loot_level_1"),
			new ResourceLocation(LabyrinthMod.MODID, "library/library_loot_level_2"),
			new ResourceLocation(LabyrinthMod.MODID, "library/library_loot_level_3"),
			new ResourceLocation(LabyrinthMod.MODID, "library/library_loot_level_4"),
			new ResourceLocation(LabyrinthMod.MODID, "library/library_loot_level_5"),
			new ResourceLocation(LabyrinthMod.MODID, "library/library_loot_level_6"),
			new ResourceLocation(LabyrinthMod.MODID, "library/library_loot_level_7")};

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
		else
			return true;
	}

	@SubscribeEvent
	public void generate(CubePopulatorEvent event) {
		ICubicWorld world = event.getWorld();
		CubePos pos = event.getCube().getCoords();
		if (!shouldGenerateAtPos(pos, world))
			return;
		int level = config.getLevel(pos);
		DungeonCube is = getDungeonCubeType(pos, world);
		if (is == DungeonCube.UNDEFINED) {
			throw new IllegalStateException("Dungeon cube type selector return incorrect value.");
		}
		if (is == DungeonCube.NOTHING) {
			return;
		}
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
		for (int index = 0; index < data.length; index++) {
			int dx = index >>> 8;
			int dy = (index >>> 4) & 15;
			int dz = index & 15;
			int bstate = Byte.toUnsignedInt(data[index]);
			BlockPos bpos = new BlockPos(pos.getMinBlockX() + dx, pos.getMinBlockY() + dy, pos.getMinBlockZ() + dz);
			cstorage.setBlocklightArray(new NibbleArray(is.lightData.clone()));
			cstorage.set(dx, dy, dz, bl[bstate]);
			cube.getColumn().getOpacityIndex().onOpacityChange(dx, pos.getMinBlockY() + dy, dz, bl[bstate].getLightOpacity((IBlockAccess) world, bpos));
			if (bstate >= 3 && bstate <= 6) {
				TileEntityChest chest = new TileEntityChest();
				NBTTagCompound compound = new NBTTagCompound();
				if (is.isLibrary)
					compound.setString("LootTable", libraryLoot[level >= libraryLoot.length ? libraryLoot.length - 1 : level].toString());
				else
					compound.setString("LootTable", regularLoot[level >= regularLoot.length ? regularLoot.length - 1 : level].toString());
				chest.readFromNBT(compound);
				chest.markDirty();
				chest.setPos(bpos);
				world.setTileEntity(bpos, chest);
			}
		}
		random.setSeed(pos.hashCode());
		if (random.nextInt(MOB_SPAWN_RARITY) == 0)
			this.selectGenerator(pos, world).spawnMobs(level, world, pos, cstorage);
		event.setCanceled(true);
	}

	public void setConfig(WorldSavedDataLabyrinthConfig worldgenConfigIn) {
		this.config = worldgenConfigIn;
	}

	public void addBiomeDecorator(ICubicPopulator decorator) {
		this.biomeDecorators.add(decorator);
	}

	public DungeonCube getDungeonCubeType(CubePos pos, ICubicWorld world) {
		return this.selectGenerator(pos, world).getDungeonCubeType(pos, world);
	}

	private ICubeStructureGenerator selectGenerator(CubePos pos, ICubicWorld world) {
		int level = config.getLevel(pos);
		if (level > 7) {
			if (level > 8 && this.villageCubeStructureGenerator.isVillage(pos, world))
				return this.villageCubeStructureGenerator;
			else
				return this.lavaCubeStructureGenerator;
		} else
			return this.basicCubeStructureGenerator;
	}
}
