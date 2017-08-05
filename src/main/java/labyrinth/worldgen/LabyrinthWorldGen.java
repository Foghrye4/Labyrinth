package labyrinth.worldgen;

import java.io.IOException;
import java.util.Random;

import cubicchunks.api.worldgen.biome.CubicBiome;
import cubicchunks.api.worldgen.populator.CubePopulatorEvent;
import cubicchunks.api.worldgen.populator.CubicPopulatorList;
import cubicchunks.api.worldgen.populator.ICubicPopulator;
import cubicchunks.util.CubePos;
import cubicchunks.world.ICubicWorld;
import cubicchunks.world.cube.Cube;
import labyrinth.LabyrinthMod;
import labyrinth.entity.IMobLeveled;
import labyrinth.entity.ISlime;
import labyrinth.init.LabyrinthEntities;
import labyrinth.util.LevelUtil;
import labyrinth.world.WorldSavedDataLabyrinthConfig;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LabyrinthWorldGen {


	private WorldSavedDataLabyrinthConfig config;
	public static LabyrinthWorldGen instance;
	public LevelFeaturesStorage storage;
	private final Random random = new Random();
	private final CubicPopulatorList biomeDecorators = new CubicPopulatorList();
	private final ICubeStructureGenerator basicCubeStructureGenerator = new RegularCubeStructureGenerator();
	private final ICubeStructureGenerator lavaCubeStructureGenerator = new LavaCubeStructureGenerator();

	private final ResourceLocation[] regularLoot = new ResourceLocation[]{
			new ResourceLocation(LabyrinthMod.MODID, "dungeon_loot_level_0"),
			new ResourceLocation(LabyrinthMod.MODID, "dungeon_loot_level_1"),
			new ResourceLocation(LabyrinthMod.MODID, "dungeon_loot_level_2"),
			new ResourceLocation(LabyrinthMod.MODID, "dungeon_loot_level_3"),
			new ResourceLocation(LabyrinthMod.MODID, "dungeon_loot_level_4"),
			new ResourceLocation(LabyrinthMod.MODID, "dungeon_loot_level_5"),
			new ResourceLocation(LabyrinthMod.MODID, "dungeon_loot_level_6"),
			new ResourceLocation(LabyrinthMod.MODID, "dungeon_loot_level_7"),
			new ResourceLocation(LabyrinthMod.MODID, "dungeon_loot_level_8")};

	private final ResourceLocation[] libraryLoot = new ResourceLocation[]{
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
		DungeonCube is;
		if (level <= 7) {
			is = this.basicCubeStructureGenerator.getDungeonCubeType(pos, world);
		} else {
			is = this.lavaCubeStructureGenerator.getDungeonCubeType(pos, world);
		}
		boolean spawnMobs = random.nextInt(8) == 0;
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
		if (spawnMobs)
			this.spawnMobs(level, world, pos, data);
		event.setCanceled(true);
	}

	private void spawnMobs(int level, ICubicWorld world, CubePos pos, byte[] data) {
		int mobRandom = level < this.storage.levelToMob.length ? level : random.nextInt() & (this.storage.levelToMob.length - 1);
		Class<? extends EntityLivingBase>[] mobs = this.storage.levelToMob[mobRandom];
		EntityLivingBase mobEntity;
		for (int dy = 0; dy <= 2; dy += 2)
		for (int dx = random.nextInt(8); dx < 15; dx += random.nextInt(8) + 1)
		for (int dz = random.nextInt(8); dz < 15; dz += random.nextInt(8) + 1)
				try {
					Class<? extends EntityLivingBase> mob = mobs[this.random.nextInt(2)];
					int index0 = dx << 8 | dy + 0 << 4 | dz;
					int index1 = dx << 8 | dy + 1 << 4 | dz;
					int index2 = dx << 8 | dy + 2 << 4 | dz;
					if (data[index0] == 0 || data[index1] != 0 || data[index2] != 0)
						continue;
					mobEntity = mob.getDeclaredConstructor(World.class).newInstance(world);
					mobEntity.setLocationAndAngles(pos.getMinBlockX() + dx + 0.5, pos.getMinBlockY() + dy + 1,
							pos.getMinBlockZ() + dz + 0.5, random.nextFloat() * 360.0F, 0.0F);
					if (mob == LabyrinthEntities.STRAY || mob == LabyrinthEntities.SKELETON)
						mobEntity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
					else if (mob == LabyrinthEntities.SLIME || mob == LabyrinthEntities.MAGMA_CUBE)
						((ISlime) mobEntity).setSlimeSize(LevelUtil.getSlimeSize(level));
					else if (mob == LabyrinthEntities.VINDICATOR)
						mobEntity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
					((IMobLeveled) mobEntity).setLevel(level);
					((IMobLeveled) mobEntity).setLootTable(regularLoot[level >= regularLoot.length ? regularLoot.length - 1 : level]);
					world.spawnEntity(mobEntity);
				} catch (Throwable e) {
					e.printStackTrace();
				}

	}



	public void setConfig(WorldSavedDataLabyrinthConfig worldgenConfigIn) {
		this.config = worldgenConfigIn;
	}

	public void addBiomeDecorator(ICubicPopulator decorator) {
		this.biomeDecorators.add(decorator);
	}
}
