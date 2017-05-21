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
			DungeonCube.COLUMN_CEIL, DungeonCube.STAIR_TOP,
			DungeonCube.COLUMN_FLOOR, DungeonCube.STAIR_FLOOR,
			DungeonCube.STAIR_MIDDLE,
			DungeonCube.WALL_EAST_NORTH, DungeonCube.WALL_EAST_SOUTH,
			DungeonCube.WALL_SOUTH_NORTH, DungeonCube.WALL_WEST_EAST, DungeonCube.WALL_WEST_NORTH,
			DungeonCube.WALL_WEST_SOUTH, DungeonCube.NOTHING, DungeonCube.NOTHING, DungeonCube.NOTHING,
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
		return (cpos.getX() & 1 | cpos.getZ() & 1 | cpos.getY() & 1) == 0;
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
		
		DungeonCube d_up = DungeonCube.UNDEFINED;
		DungeonCube d_down = DungeonCube.UNDEFINED;
		DungeonCube d_east = DungeonCube.UNDEFINED;
		DungeonCube d_west = DungeonCube.UNDEFINED;
		DungeonCube d_south = DungeonCube.UNDEFINED;
		DungeonCube d_north = DungeonCube.UNDEFINED;

			d_up = getDungeonCubeType(cpos.add(0, 1, 0), world, recursionDeepness);
			d_down = getDungeonCubeType(cpos.sub(0, 1, 0), world, recursionDeepness);
			d_east = getDungeonCubeType(cpos.add(1, 0, 0), world, recursionDeepness);
			d_west = getDungeonCubeType(cpos.sub(1, 0, 0), world, recursionDeepness);
			d_south = getDungeonCubeType(cpos.add(0, 0, 1), world, recursionDeepness);
			d_north = getDungeonCubeType(cpos.sub(0, 0, 1), world, recursionDeepness);

		if (d_up == DungeonCube.UNDEFINED && 
				d_down == DungeonCube.UNDEFINED && 
				d_east == DungeonCube.UNDEFINED && 
				d_west == DungeonCube.UNDEFINED && 
				d_south == DungeonCube.UNDEFINED && 
				d_north == DungeonCube.UNDEFINED)
			return DungeonCube.UNDEFINED;

		if((d_up == DungeonCube.UNDEFINED || d_down == DungeonCube.UNDEFINED) && 
				d_down != d_up)
			throw new IllegalStateException("Abonormal behaviour");

		if((d_east == DungeonCube.UNDEFINED || d_west == DungeonCube.UNDEFINED) && 
				d_east != d_west)
			throw new IllegalStateException("Abonormal behaviour");

		if((d_south == DungeonCube.UNDEFINED || d_north == DungeonCube.UNDEFINED) && 
				d_south != d_north)
			throw new IllegalStateException("Abonormal behaviour");
		
		if (d_up != DungeonCube.UNDEFINED) {
			if(d_up.isColumnTopOrMiddle && d_down.isColumnBottomOrMiddle)
				return DungeonCube.COLUMN_MIDDLE;
			if(d_up.isColumnTopOrMiddle && d_down.isStairBottom)
				return DungeonCube.STAIR_TOP_CEILINGLESS;
			if(d_up.isColumnTopOrMiddle)
				return DungeonCube.COLUMN_FLOOR;
			if (d_up.isStairTop && d_down.isColumnBottomOrMiddle)
				return DungeonCube.COLUMN_TOP_WITH_STAIR;
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
			if (d_down.isStairTop && d_down.isStairBottom)
				return DungeonCube.WALL_X;
		}
		
		if (d_east != DungeonCube.UNDEFINED) {
			if (d_east.isColumnTopOrMiddle || d_west.isColumnTopOrMiddle || d_south.isColumnTopOrMiddle
					|| d_north.isColumnTopOrMiddle) {
				if (!d_east.isColumnTopOrMiddle && !d_north.isColumnTopOrMiddle) {
					return DungeonCube.ROOM_OT_NORTH_EAST;
				}
				if (!d_west.isColumnTopOrMiddle && !d_north.isColumnTopOrMiddle) {
					return DungeonCube.ROOM_OT_NORTH_WEST;
				}
				if (!d_east.isColumnTopOrMiddle && !d_south.isColumnTopOrMiddle) {
					return DungeonCube.ROOM_OT_SOUTH_EAST;
				}
				if (!d_west.isColumnTopOrMiddle && !d_south.isColumnTopOrMiddle) {
					return DungeonCube.ROOM_OT_SOUTH_WEST;
				}
				if (!d_west.isColumnTopOrMiddle) {
					return DungeonCube.LIBRARY;
				}
				if (!d_south.isColumnTopOrMiddle) {
					return DungeonCube.WORKSHOP;
				}
			}

			// East
			if (d_east.isWestWall) {
				if (d_west.isEastWall) {
					return DungeonCube.WALL_WEST_EAST_BARS;
				} else if (d_south.isNorthWall) {
					return DungeonCube.WALL_EAST_SOUTH_BARS;
				} else if (d_north.isSouthWall) {
					return DungeonCube.WALL_EAST_NORTH_BARS;
				}
				switch (typedefiner % 6) {
					case 0 :
						return DungeonCube.WALL_EAST_NORTH;
					case 1 :
						return DungeonCube.WALL_EAST_NORTH_BARS;
					case 2 :
						return DungeonCube.WALL_EAST_SOUTH;
					case 3 :
						return DungeonCube.WALL_EAST_SOUTH_BARS;
					case 4 :
						return DungeonCube.WALL_WEST_EAST;
					case 5 :
						return DungeonCube.WALL_WEST_EAST_BARS;
				}
			}
			// West
			if (d_west == DungeonCube.COLUMN_CEIL) {
				return DungeonCube.COLUMN_WEST_BORDER;
			} else if (d_west.isEastWall) {
				if (d_east.isWestWall) {
					return DungeonCube.WALL_WEST_EAST_BARS;
				} else if (d_south.isNorthWall) {
					return DungeonCube.WALL_EAST_SOUTH_BARS;
				} else if (d_north.isSouthWall) {
					return DungeonCube.WALL_WEST_NORTH_BARS;
				}
				switch (typedefiner % 6) {
					case 0 :
						return DungeonCube.WALL_WEST_NORTH;
					case 1 :
						return DungeonCube.WALL_WEST_NORTH_BARS;
					case 2 :
						return DungeonCube.WALL_WEST_SOUTH;
					case 3 :
						return DungeonCube.WALL_WEST_SOUTH_BARS;
					case 4 :
						return DungeonCube.WALL_WEST_EAST;
					case 5 :
						return DungeonCube.WALL_WEST_EAST_BARS;
				}
			}
		}
		// South
		if (d_south != DungeonCube.UNDEFINED) {
			if (d_south == DungeonCube.COLUMN_CEIL) {
				return DungeonCube.COLUMN_SOUTH_BORDER;
			} else if (d_south.isNorthWall) {
				if (d_east.isWestWall) {
					return DungeonCube.WALL_WEST_NORTH_BARS;
				} else if (d_west.isEastWall) {
					return DungeonCube.WALL_EAST_NORTH_BARS;
				} else if (d_north.isSouthWall) {
					return DungeonCube.WALL_SOUTH_NORTH_DOOR;
				}
				switch (typedefiner % 6) {
					case 0 :
						return DungeonCube.WALL_SOUTH_NORTH;
					case 1 :
						return DungeonCube.WALL_EAST_SOUTH_BARS;
					case 2 :
						return DungeonCube.WALL_SOUTH_NORTH_DOOR;
					case 3 :
						return DungeonCube.WALL_WEST_SOUTH_BARS;
					case 4 :
						return DungeonCube.WALL_WEST_SOUTH;
					case 5 :
						return DungeonCube.WALL_EAST_SOUTH;
				}
			}
			// North
			if (d_north == DungeonCube.COLUMN_CEIL) {
				return DungeonCube.COLUMN_NORTH_BORDER;
			} else if (d_north.isSouthWall) {
				if (d_east.isWestWall) {
					return DungeonCube.WALL_WEST_SOUTH_BARS;
				} else if (d_west.isEastWall) {
					return DungeonCube.WALL_EAST_SOUTH_BARS;
				} else if (d_south.isNorthWall) {
					return DungeonCube.WALL_SOUTH_NORTH_DOOR;
				}
				switch (typedefiner % 7) {
					case 0 :
						return DungeonCube.WALL_SOUTH_NORTH;
					case 1 :
						return DungeonCube.WALL_EAST_NORTH_BARS;
					case 2 :
						return DungeonCube.WALL_SOUTH_NORTH_DOOR;
					case 3 :
						return DungeonCube.WALL_WEST_NORTH_BARS;
					case 4 :
						return DungeonCube.WALL_WEST_NORTH;
					case 5 :
						return DungeonCube.WALL_EAST_NORTH;
				}
			}
		}
		return DungeonCube.COLUMN_FLOOR_CEIL;
	}

	public void setConfig(WorldSavedDataLabyrinthConfig worldgenConfigIn) {
		this.config = worldgenConfigIn;
	}
}
