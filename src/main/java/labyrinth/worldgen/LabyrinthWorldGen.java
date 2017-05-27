package labyrinth.worldgen;

import java.io.IOException;
import java.util.Random;

import cubicchunks.api.worldgen.biome.CubicBiome;
import cubicchunks.api.worldgen.populator.ICubicPopulator;
import cubicchunks.util.CubePos;
import cubicchunks.world.ICubicWorld;
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
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;

public class LabyrinthWorldGen implements ICubicPopulator {

	public DungeonCube[] randomDungeonsArray = new DungeonCube[] { 
			DungeonCube.COLUMN_CEIL,
			DungeonCube.COLUMN_FLOOR, 
			DungeonCube.STAIR_FLOOR,
			DungeonCube.STAIR_MIDDLE,
			DungeonCube.STAIR_MIDDLE,
			DungeonCube.LIBRARY,
			DungeonCube.WORKSHOP,
			DungeonCube.WALL_EAST_NORTH, 
			DungeonCube.WALL_EAST_SOUTH,
			DungeonCube.WALL_SOUTH_NORTH, 
			DungeonCube.WALL_WEST_EAST, 
			DungeonCube.WALL_WEST_NORTH,
			DungeonCube.WALL_WEST_SOUTH, 
			DungeonCube.NOTHING, DungeonCube.NOTHING, DungeonCube.NOTHING,
			DungeonCube.NOTHING, DungeonCube.NOTHING, DungeonCube.NOTHING, };

	private WorldSavedDataLabyrinthConfig config;
	public static LabyrinthWorldGen instance;
	private int max_loot_level = 7;
	public LevelFeaturesStorage storage;
	private final Random random = new Random();
	private final IBlockState AIR = Blocks.AIR.getDefaultState();
	public boolean verbose = false;
	
	public LabyrinthWorldGen() throws IOException {
		instance = this;
		storage = new LevelFeaturesStorage();
		for (DungeonCube cube : DungeonCube.values()) {
			if (cube != DungeonCube.NOTHING && cube != DungeonCube.UNDEFINED) {
				cube.load();
			}
		}
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator,
			IChunkProvider chunkProvider) {
	}

	@Override
	public void generate(ICubicWorld world, Random random, CubePos pos, CubicBiome biome) {
		if(verbose)
			LabyrinthMod.log.info("Generating cube at pos "+pos.toString());
		float biomeHeightBase = biome.getBiome().getBaseHeight();
		if (biomeHeightBase < config.dungeonBiomeHeightLowerBound
				|| biomeHeightBase > config.dungeonBiomeHeightUpperBound)
			return;
		int level = config.getLevel(pos);
		if (level >= 0) {
			DungeonCube is = getDungeonCubeType(pos, world, 0);
			if (is == DungeonCube.UNDEFINED) {
				throw new IllegalStateException("Dungeon cube type selector return incorrect value.");
			}
			if (is == DungeonCube.NOTHING) {
				return;
			}
			if(verbose) {
				if(isAnchorPoint(pos))
					LabyrinthMod.log.info("Pos is anchor point");
				LabyrinthMod.log.info("Selected type: "+is.name());
				LabyrinthMod.log.info("Type below: "+getDungeonCubeType(pos.below(), world, 1));
				LabyrinthMod.log.info("Type above: "+getDungeonCubeType(pos.above(), world, 1));
				DungeonCube d_east = getDungeonCubeType(pos.add(1, 0, 0), world, 1);
				DungeonCube d_west = getDungeonCubeType(pos.sub(1, 0, 0), world, 1);
				DungeonCube d_south = getDungeonCubeType(pos.add(0, 0, 1), world, 1);
				DungeonCube d_north = getDungeonCubeType(pos.sub(0, 0, 1), world, 1);
				LabyrinthMod.log.info("Type at north: "+d_north);
				LabyrinthMod.log.info("Type at south: "+d_south);
				LabyrinthMod.log.info("Type at east: "+d_east);
				LabyrinthMod.log.info("Type at west: "+d_west);
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
			int mobRandom = level < this.storage.levelToMob.length ? level : random.nextInt() & (this.storage.levelToMob.length - 1);
			byte[] data = is.data;
			Class<? extends EntityLivingBase>[] mobs = this.storage.levelToMob[mobRandom];
			for (int index = 0; index < data.length; index++) {
				int dx = index >>> 8;
				int dy = (index >>> 4) & 15;
				int dz = index & 15;
				dx += 8;
				dy += 8;
				dz += 8;
				int bstate = Byte.toUnsignedInt(data[index]);
				BlockPos bpos = new BlockPos(pos.getMinBlockX() + dx, pos.getMinBlockY() + dy, pos.getMinBlockZ() + dz);
				if (bstate == 255) {
					world.setBlockState(bpos, AIR, 3);
					int mobRandom2 = this.random.nextFloat() > 0.7f ? 1 : 0;
					Class<? extends EntityLivingBase> mob = mobs[mobRandom2];
					EntityLivingBase mobEntity;
					try {
						mobEntity = mob.getDeclaredConstructor(World.class).newInstance(world);
						mobEntity.setPosition(pos.getMinBlockX() + dx + 0.5, pos.getMinBlockY() + dy,
								pos.getMinBlockZ() + dz + 0.5);
						if (mob == LabyrinthEntities.STRAY || mob == LabyrinthEntities.SKELETON)
							mobEntity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
						else if (mob == LabyrinthEntities.SLIME || mob == LabyrinthEntities.MAGMA_CUBE)
							((ISlime) mobEntity).setSlimeSize(LevelUtil.getSlimeSize(level));
						else if (mob == LabyrinthEntities.VINDICATOR)
							mobEntity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
						((IMobLeveled) mobEntity).setLevel(level);
						((IMobLeveled) mobEntity).setLootTable(new ResourceLocation(LabyrinthMod.MODID
								+ ":dungeon_loot_level_" + (level > max_loot_level ? max_loot_level : level)));
						world.spawnEntity(mobEntity);
					} catch (Throwable e) {
						e.printStackTrace();
					}
				} else {
					world.setBlockState(bpos, bl[bstate], 3);
					if (bstate >= 3 && bstate <= 6) {
						TileEntityChest chest = new TileEntityChest();
						NBTTagCompound compound = new NBTTagCompound();
						compound.setString("LootTable", LabyrinthMod.MODID + ":dungeon_loot_level_"
								+ (level > max_loot_level ? max_loot_level : level));
						chest.readFromNBT(compound);
						chest.markDirty();
						chest.setPos(bpos);
						world.setTileEntity(bpos, chest);
					}
				}
			}
		}
	}

	private boolean isAnchorPoint(CubePos cpos){
		return (cpos.getX() & 1 | cpos.getZ() & 1 | cpos.getY()+cpos.getX()+cpos.getZ() & 1) == 0;
	}
	
	private DungeonCube getDungeonCubeType(CubePos cpos, ICubicWorld world, int recursionDeepness) {
		long hash = 3;
        hash = 41 * hash + world.getSeed();
        hash = 41 * hash + cpos.getX();
        hash = 41 * hash + cpos.getY();
        long seed = 41 * hash + cpos.getZ();
		random.setSeed(seed);
		int typedefiner = random.nextInt(this.randomDungeonsArray.length);
		if (isAnchorPoint(cpos))
			return randomDungeonsArray[typedefiner];
		if (recursionDeepness++ > 2)
			return DungeonCube.UNDEFINED;
		
		DungeonCube d_up = getDungeonCubeType(cpos.add(0, 1, 0), world, recursionDeepness);
		DungeonCube d_down = getDungeonCubeType(cpos.sub(0, 1, 0), world, recursionDeepness);
		DungeonCube d_east = getDungeonCubeType(cpos.add(1, 0, 0), world, recursionDeepness);
		DungeonCube d_west = getDungeonCubeType(cpos.sub(1, 0, 0), world, recursionDeepness);
		DungeonCube d_south = getDungeonCubeType(cpos.add(0, 0, 1), world, recursionDeepness);
		DungeonCube d_north = getDungeonCubeType(cpos.sub(0, 0, 1), world, recursionDeepness);

		// Up - Down
		if (d_up != DungeonCube.UNDEFINED && d_down != DungeonCube.UNDEFINED) {
			
			if(d_up.isColumnTopOrMiddle && d_down.isColumnBottomOrMiddle)
				return DungeonCube.COLUMN_MIDDLE;
			
			if(d_up.isColumnTopOrMiddle && d_down.isStairBottom)
				return DungeonCube.STAIR_TOP_CEILINGLESS;
			
			if(d_up.isColumnTopOrMiddle)
				return DungeonCube.COLUMN_FLOOR;
			
			if (d_up.isStairTop && d_down.isColumnBottomOrMiddle)
				return DungeonCube.NODE; // Should be impossible
			
			if (d_up.isStairTop && d_down.isStairBottom)
				return DungeonCube.STAIR_MIDDLE;
			
			if (d_up.isStairTop) {
				if (d_east.isColumnTopOrMiddle 	|| 
						d_west.isColumnTopOrMiddle ||
						d_south.isColumnTopOrMiddle ||
						d_north.isColumnTopOrMiddle ||
						d_east.isWestWall ||
						d_west.isEastWall ||
						d_south.isNorthWall ||
						d_north.isSouthWall) {
					if (!d_east.isColumnTopOrMiddle && !d_north.isColumnTopOrMiddle) {
						return DungeonCube.STAIR_FLOOR_W_ROOM_OT_NORTH_EAST;
					}
					if (!d_west.isColumnTopOrMiddle && !d_north.isColumnTopOrMiddle) {
						return DungeonCube.STAIR_FLOOR_W_ROOM_OT_NORTH_WEST;
					}
					if (!d_east.isColumnTopOrMiddle && !d_south.isColumnTopOrMiddle) {
						return DungeonCube.STAIR_FLOOR_W_ROOM_OT_SOUTH_EAST;
					}
					if (!d_west.isColumnTopOrMiddle && !d_south.isColumnTopOrMiddle) {
						return DungeonCube.STAIR_FLOOR_W_ROOM_OT_SOUTH_WEST;
					}
					switch (typedefiner % 4) { // Unhandled case
						case 0 :
							return DungeonCube.STAIR_FLOOR_W_ROOM_OT_NORTH_WEST;
						case 1 :
							return DungeonCube.STAIR_FLOOR_W_ROOM_OT_NORTH_EAST;
						case 2 :
							return DungeonCube.STAIR_FLOOR_W_ROOM_OT_SOUTH_WEST;
						case 3 :
							return DungeonCube.STAIR_FLOOR_W_ROOM_OT_SOUTH_EAST;
					}
				}
				return DungeonCube.STAIR_FLOOR;
			}
			if (d_down.isColumnBottomOrMiddle)
				return DungeonCube.COLUMN_CEIL;
			
			if (d_down.isStairBottom) {
				if (d_east.isColumnTopOrMiddle || 
						d_west.isColumnTopOrMiddle || 
						d_south.isColumnTopOrMiddle ||
						d_north.isColumnTopOrMiddle) {
					if (!d_east.isColumnTopOrMiddle && !d_north.isColumnTopOrMiddle) {
						return DungeonCube.STAIR_TOP_W_ROOM_OT_NORTH_EAST;
					}
					if (!d_west.isColumnTopOrMiddle && !d_north.isColumnTopOrMiddle) {
						return DungeonCube.STAIR_TOP_W_ROOM_OT_NORTH_WEST;
					}
					if (!d_east.isColumnTopOrMiddle && !d_south.isColumnTopOrMiddle) {
						return DungeonCube.STAIR_TOP_W_ROOM_OT_SOUTH_EAST;
					}
					if (!d_west.isColumnTopOrMiddle && !d_south.isColumnTopOrMiddle) {
						return DungeonCube.STAIR_TOP_W_ROOM_OT_SOUTH_WEST;
					}
					switch (typedefiner % 4) { // Unhandled case
						case 0 :
							return DungeonCube.STAIR_TOP_W_ROOM_OT_NORTH_WEST;
						case 1 :
							return DungeonCube.STAIR_TOP_W_ROOM_OT_NORTH_EAST;
						case 2 :
							return DungeonCube.STAIR_TOP_W_ROOM_OT_SOUTH_WEST;
						case 3 :
							return DungeonCube.STAIR_TOP_W_ROOM_OT_SOUTH_EAST;
					}
				}
				return DungeonCube.STAIR_TOP;
			}
			return DungeonCube.COLUMN_FLOOR_CEIL;
		}
		
		// East- west
		if (d_east != DungeonCube.UNDEFINED && d_west != DungeonCube.UNDEFINED) {
			if(d_east.isColumnMiddle && d_west.isColumnMiddle)
				return DungeonCube.COLUMN_MIDDLE;
			
			if (d_east.isColumnTopOrMiddle && d_west.isColumnTopOrMiddle)
				return DungeonCube.COLUMN_CEIL;
			
			if (d_east.isWestWall && d_west.isEastWall && d_south.isNorthWall && d_north.isSouthWall)
				return DungeonCube.WALL_X;
			
			if (d_east.isColumnTopOrMiddle && d_west.isEastWall && d_south.isNorthWall && d_north.isSouthWall)
				return DungeonCube.EAST_BORDER_WITH_WALLS;

			if (d_west.isColumnTopOrMiddle && d_east.isWestWall && d_south.isNorthWall && d_north.isSouthWall)
				return DungeonCube.WEST_BORDER_WITH_WALLS;

			if (d_east.isColumnTopOrMiddle && d_south.isNorthWall && d_north.isSouthWall)
				return DungeonCube.EAST_BORDER_WITH_WALL_SOUTH_NORTH;

			if (d_west.isColumnTopOrMiddle && d_south.isNorthWall && d_north.isSouthWall)
				return DungeonCube.WEST_BORDER_WITH_WALL_SOUTH_NORTH;

			if (d_east.isColumnTopOrMiddle && d_west.isEastWall && d_north.isSouthWall)
				return DungeonCube.EAST_BORDER_WITH_WALL_WEST_NORTH;

			if (d_west.isColumnTopOrMiddle && d_east.isWestWall && d_north.isSouthWall)
				return DungeonCube.WEST_BORDER_WITH_WALL_NORTH_EAST;
			
			if (d_east.isColumnTopOrMiddle && d_west.isEastWall && d_south.isNorthWall)
				return DungeonCube.EAST_BORDER_WITH_WALL_SOUTH_WEST;

			if (d_west.isColumnTopOrMiddle && d_east.isWestWall && d_south.isNorthWall)
				return DungeonCube.WEST_BORDER_WITH_WALL_EAST_SOUTH;
			
			if (d_east.isWestWall || d_west.isEastWall)
				return DungeonCube.WALL_WEST_EAST;
			
			return DungeonCube.COLUMN_FLOOR_CEIL;
		}
		// South - North
		if (d_south != DungeonCube.UNDEFINED && d_north != DungeonCube.UNDEFINED) {
			if(d_south.isColumnMiddle && d_north.isColumnMiddle)
				return DungeonCube.COLUMN_MIDDLE;
			
			if (d_south.isColumnTopOrMiddle && d_north.isColumnTopOrMiddle)
				return DungeonCube.COLUMN_CEIL;
			
			if (d_east.isWestWall && d_west.isEastWall && d_south.isNorthWall && d_north.isSouthWall)
				return DungeonCube.WALL_X;
			
			if (d_south.isColumnTopOrMiddle && d_west.isEastWall && d_east.isWestWall && d_north.isSouthWall)
				return DungeonCube.SOUTH_BORDER_WITH_WALLS;

			if (d_north.isColumnTopOrMiddle && d_west.isEastWall && d_east.isWestWall && d_south.isNorthWall)
				return DungeonCube.NORTH_BORDER_WITH_WALLS;

			if (d_north.isColumnTopOrMiddle && d_west.isEastWall && d_east.isWestWall)
				return DungeonCube.NORTH_BORDER_WITH_WALL_EAST_WEST;
				
			if (d_south.isColumnTopOrMiddle && d_west.isEastWall && d_east.isWestWall)
				return DungeonCube.SOUTH_BORDER_WITH_WALL_EAST_WEST;

			if (d_north.isColumnTopOrMiddle && d_west.isEastWall && d_south.isNorthWall)
				return DungeonCube.NORTH_BORDER_WITH_WALL_SOUTH_WEST;
				
			if (d_south.isColumnTopOrMiddle && d_west.isEastWall && d_north.isSouthWall)
				return DungeonCube.SOUTH_BORDER_WITH_WALL_WEST_NORTH;

			if (d_north.isColumnTopOrMiddle && d_east.isWestWall && d_south.isNorthWall)
				return DungeonCube.NORTH_BORDER_WITH_WALL_EAST_SOUTH;
				
			if (d_south.isColumnTopOrMiddle && d_east.isWestWall && d_north.isSouthWall)
				return DungeonCube.SOUTH_BORDER_WITH_WALL_NORTH_EAST;
			
			if (d_south.isNorthWall || d_north.isSouthWall)
				return DungeonCube.WALL_SOUTH_NORTH;
			
			return DungeonCube.COLUMN_FLOOR_CEIL;
		}
		return DungeonCube.UNDEFINED;
	}

	public void setConfig(WorldSavedDataLabyrinthConfig worldgenConfigIn) {
		this.config = worldgenConfigIn;
	}
}
