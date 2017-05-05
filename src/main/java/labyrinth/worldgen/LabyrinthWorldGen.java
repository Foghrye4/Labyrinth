package labyrinth.worldgen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cubicchunks.api.ICubicWorldGenerator;
import cubicchunks.util.CubePos;
import cubicchunks.world.ICubicWorld;
import labyrinth.entity.EntityZombieLeveled;
import labyrinth.util.LevelUtil;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockHardenedClay;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockLever.EnumOrientation;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.BlockStairs.EnumShape;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;

public class LabyrinthWorldGen implements ICubicWorldGenerator {

	public enum DungeonCube {
		
		COLUMN_CEIL("column_ceil.cube_structure",false,false,false,false),
		COLUMN_FLOOR("column_floor.cube_structure",false,false,false,false),
		COLUMN_EAST_BORDER("column_east_border.cube_structure",false,false,false,false),
		COLUMN_FLOOR_CEIL("column_floor_ceil.cube_structure",false,false,false,false),
		COLUMN_NORTH_BORDER("column_north_border.cube_structure",false,false,false,false),
		COLUMN_SOUTH_BORDER("column_south_border.cube_structure",false,false,false,false),
		COLUMN_WEST_BORDER("column_west_border.cube_structure",false,false,false,false),
		STAIR_CEIL("stair_ceil.cube_structure",false,false,false,false),
		STAIR_FLOOR("stair_floor.cube_structure",false,false,false,false),
		NODE("node.cube_structure",false,false,false,false),
		WALL_EAST_NORTH_bars("wall_east_north_bars.cube_structure",true,false,false,true),
		WALL_EAST_NORTH("wall_east_north.cube_structure",true,false,false,true),
		WALL_EAST_SOUTH_bars("wall_east_south_bars.cube_structure",true,false,true,false),
		WALL_EAST_SOUTH("wall_east_south.cube_structure",true,false,true,false),
		WALL_SOUTH_NORTH_bars("wall_south_north_bars.cube_structure",false,false,true,true),
		WALL_SOUTH_NORTH("wall_south_north.cube_structure",false,false,true,true),
		WALL_SOUTH_NORTH_door("wall_south_north_door.cube_structure",false,false,true,true),
		WALL_WEST_EAST_bars("wall_west_east_bars.cube_structure",true,true,false,false),
		WALL_WEST_EAST("wall_west_east.cube_structure",true,true,false,false),
		WALL_WEST_NORTH_bars("wall_west_north_bars.cube_structure",false,true,false,true),
		WALL_WEST_NORTH("wall_west_north.cube_structure",false,true,false,true),
		WALL_WEST_SOUTH_bars("wall_west_south_bars.cube_structure",false,true,true,false),
		WALL_WEST_SOUTH("wall_west_south.cube_structure",false,true,true,false),
		NOTHING("",false,false,false,false);
		
		public final String name;
		public final boolean isEastWall;
		public final boolean isWestWall;
		public final boolean isSouthWall;
		public final boolean isNorthWall;
		public final byte[] data = new byte[4096];
		
		DungeonCube(String nameIn, boolean isEastWallIn, boolean isWestWallIn, boolean isSouthWallIn, boolean isNorthWallIn){
			name=nameIn;
			isEastWall=isEastWallIn;
			isWestWall=isWestWallIn;
			isSouthWall=isSouthWallIn;
			isNorthWall=isNorthWallIn;
			if(!name.equals("")){
				try {
					Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("labyrinth","cubes/"+name)).getInputStream().read(data);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	public IBlockState[][] blockstateList = new IBlockState[8][256];
	
	private static final int CACHE_SIZE=256;
	Map<CubePos, DungeonCube> dtype_cache = new HashMap<CubePos, DungeonCube>(CACHE_SIZE+1);
	private final Random random = new Random();
	private final IBlockState AIR = Blocks.AIR.getDefaultState();
	public static LabyrinthWorldGen instance;
	
	public LabyrinthWorldGen() {
		instance=this;
		Arrays.fill(blockstateList[0], Blocks.AIR.getDefaultState());
		blockstateList[0][1] = Blocks.QUARTZ_BLOCK.getDefaultState();
		blockstateList[0][2] = Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.CHISELED);
		blockstateList[0][18] = Blocks.STAINED_GLASS_PANE.getBlockState().getBaseState().withProperty(BlockStainedGlassPane.COLOR, EnumDyeColor.RED);
		IBlockState stair = Blocks.QUARTZ_STAIRS.getBlockState().getBaseState().withProperty(BlockStairs.SHAPE, EnumShape.STRAIGHT);
		blockstateList[0][19] = stair.withProperty(BlockStairs.FACING, EnumFacing.NORTH).withProperty(BlockStairs.HALF, EnumHalf.TOP);
		blockstateList[0][24] = stair.withProperty(BlockStairs.FACING, EnumFacing.NORTH).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);
		blockstateList[0][29] = stair.withProperty(BlockStairs.FACING, EnumFacing.SOUTH).withProperty(BlockStairs.HALF, EnumHalf.TOP);
		blockstateList[0][34] = stair.withProperty(BlockStairs.FACING, EnumFacing.SOUTH).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);
		blockstateList[0][39] = stair.withProperty(BlockStairs.FACING, EnumFacing.WEST).withProperty(BlockStairs.HALF, EnumHalf.TOP);
		blockstateList[0][44] = stair.withProperty(BlockStairs.FACING, EnumFacing.WEST).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);
		blockstateList[0][49] = stair.withProperty(BlockStairs.FACING, EnumFacing.EAST).withProperty(BlockStairs.HALF, EnumHalf.TOP);
		blockstateList[0][54] = stair.withProperty(BlockStairs.FACING, EnumFacing.EAST).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);
		blockstateList[0][120] = Blocks.ACACIA_FENCE.getDefaultState();
		blockstateList[0][129] = Blocks.STICKY_PISTON.getBlockState().getBaseState().withProperty(BlockPistonBase.EXTENDED, Boolean.valueOf(true)).withProperty(BlockPistonBase.FACING, EnumFacing.NORTH);
		blockstateList[0][137] = Blocks.LEVER.getBlockState().getBaseState().withProperty(BlockLever.POWERED, Boolean.valueOf(true)).withProperty(BlockLever.FACING, EnumOrientation.EAST);
		blockstateList[0][255] = Blocks.SKULL.getBlockState().getBaseState().withProperty(BlockSkull.FACING, EnumFacing.UP).withProperty(BlockSkull.NODROP, Boolean.valueOf(false));

		for(int i=1;i<blockstateList.length;i++)
			blockstateList[i]=Arrays.copyOf(blockstateList[0], 256);
		
		blockstateList[1][1] = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE_SMOOTH);
		blockstateList[1][2] = Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.CHISELED);
		blockstateList[1][18] = Blocks.OAK_FENCE.getDefaultState();
		blockstateList[1][120] = Blocks.OAK_FENCE.getDefaultState();
		
		blockstateList[2][1] = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE_SMOOTH);
		blockstateList[2][2] = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE);
		blockstateList[2][18] = Blocks.IRON_BARS.getDefaultState();
		blockstateList[2][120] = Blocks.DARK_OAK_FENCE.getDefaultState();
		stair = Blocks.RED_SANDSTONE_STAIRS.getBlockState().getBaseState().withProperty(BlockStairs.SHAPE, EnumShape.STRAIGHT);
		blockstateList[2][19] = stair.withProperty(BlockStairs.FACING, EnumFacing.NORTH).withProperty(BlockStairs.HALF, EnumHalf.TOP);
		blockstateList[2][24] = stair.withProperty(BlockStairs.FACING, EnumFacing.NORTH).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);
		blockstateList[2][29] = stair.withProperty(BlockStairs.FACING, EnumFacing.SOUTH).withProperty(BlockStairs.HALF, EnumHalf.TOP);
		blockstateList[2][34] = stair.withProperty(BlockStairs.FACING, EnumFacing.SOUTH).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);
		blockstateList[2][39] = stair.withProperty(BlockStairs.FACING, EnumFacing.WEST).withProperty(BlockStairs.HALF, EnumHalf.TOP);
		blockstateList[2][44] = stair.withProperty(BlockStairs.FACING, EnumFacing.WEST).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);
		blockstateList[2][49] = stair.withProperty(BlockStairs.FACING, EnumFacing.EAST).withProperty(BlockStairs.HALF, EnumHalf.TOP);
		blockstateList[2][54] = stair.withProperty(BlockStairs.FACING, EnumFacing.EAST).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);

		
		blockstateList[3][1] = Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.LIGHT_BLUE);
		blockstateList[3][2] = Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.WHITE);
		blockstateList[3][18] = Blocks.IRON_BARS.getDefaultState();
		blockstateList[3][120] = Blocks.DARK_OAK_FENCE.getDefaultState();

		blockstateList[4][1] = Blocks.STONEBRICK.getDefaultState();
		blockstateList[4][2] = Blocks.STONE.getDefaultState();
		blockstateList[4][18] = Blocks.IRON_BARS.getDefaultState();
		blockstateList[4][120] = Blocks.COBBLESTONE_WALL.getDefaultState();
		stair = Blocks.STONE_BRICK_STAIRS.getBlockState().getBaseState().withProperty(BlockStairs.SHAPE, EnumShape.STRAIGHT);
		blockstateList[4][19] = stair.withProperty(BlockStairs.FACING, EnumFacing.NORTH).withProperty(BlockStairs.HALF, EnumHalf.TOP);
		blockstateList[4][24] = stair.withProperty(BlockStairs.FACING, EnumFacing.NORTH).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);
		blockstateList[4][29] = stair.withProperty(BlockStairs.FACING, EnumFacing.SOUTH).withProperty(BlockStairs.HALF, EnumHalf.TOP);
		blockstateList[4][34] = stair.withProperty(BlockStairs.FACING, EnumFacing.SOUTH).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);
		blockstateList[4][39] = stair.withProperty(BlockStairs.FACING, EnumFacing.WEST).withProperty(BlockStairs.HALF, EnumHalf.TOP);
		blockstateList[4][44] = stair.withProperty(BlockStairs.FACING, EnumFacing.WEST).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);
		blockstateList[4][49] = stair.withProperty(BlockStairs.FACING, EnumFacing.EAST).withProperty(BlockStairs.HALF, EnumHalf.TOP);
		blockstateList[4][54] = stair.withProperty(BlockStairs.FACING, EnumFacing.EAST).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);

		blockstateList[5][1] = Blocks.COBBLESTONE.getDefaultState();
		blockstateList[5][2] = Blocks.STONE.getDefaultState();
		blockstateList[5][18] = Blocks.IRON_BARS.getDefaultState();
		blockstateList[5][120] = Blocks.COBBLESTONE_WALL.getDefaultState();
		stair = Blocks.STONE_STAIRS.getBlockState().getBaseState().withProperty(BlockStairs.SHAPE, EnumShape.STRAIGHT);
		blockstateList[5][19] = stair.withProperty(BlockStairs.FACING, EnumFacing.NORTH).withProperty(BlockStairs.HALF, EnumHalf.TOP);
		blockstateList[5][24] = stair.withProperty(BlockStairs.FACING, EnumFacing.NORTH).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);
		blockstateList[5][29] = stair.withProperty(BlockStairs.FACING, EnumFacing.SOUTH).withProperty(BlockStairs.HALF, EnumHalf.TOP);
		blockstateList[5][34] = stair.withProperty(BlockStairs.FACING, EnumFacing.SOUTH).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);
		blockstateList[5][39] = stair.withProperty(BlockStairs.FACING, EnumFacing.WEST).withProperty(BlockStairs.HALF, EnumHalf.TOP);
		blockstateList[5][44] = stair.withProperty(BlockStairs.FACING, EnumFacing.WEST).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);
		blockstateList[5][49] = stair.withProperty(BlockStairs.FACING, EnumFacing.EAST).withProperty(BlockStairs.HALF, EnumHalf.TOP);
		blockstateList[5][54] = stair.withProperty(BlockStairs.FACING, EnumFacing.EAST).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);

		blockstateList[6][1] = Blocks.NETHER_BRICK.getDefaultState();
		blockstateList[6][2] = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE_SMOOTH);
		blockstateList[6][18] = Blocks.IRON_BARS.getDefaultState();
		blockstateList[6][120] = Blocks.NETHER_BRICK_FENCE.getDefaultState();
		stair = Blocks.NETHER_BRICK_STAIRS.getBlockState().getBaseState().withProperty(BlockStairs.SHAPE, EnumShape.STRAIGHT);
		blockstateList[6][19] = stair.withProperty(BlockStairs.FACING, EnumFacing.NORTH).withProperty(BlockStairs.HALF, EnumHalf.TOP);
		blockstateList[6][24] = stair.withProperty(BlockStairs.FACING, EnumFacing.NORTH).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);
		blockstateList[6][29] = stair.withProperty(BlockStairs.FACING, EnumFacing.SOUTH).withProperty(BlockStairs.HALF, EnumHalf.TOP);
		blockstateList[6][34] = stair.withProperty(BlockStairs.FACING, EnumFacing.SOUTH).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);
		blockstateList[6][39] = stair.withProperty(BlockStairs.FACING, EnumFacing.WEST).withProperty(BlockStairs.HALF, EnumHalf.TOP);
		blockstateList[6][44] = stair.withProperty(BlockStairs.FACING, EnumFacing.WEST).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);
		blockstateList[6][49] = stair.withProperty(BlockStairs.FACING, EnumFacing.EAST).withProperty(BlockStairs.HALF, EnumHalf.TOP);
		blockstateList[6][54] = stair.withProperty(BlockStairs.FACING, EnumFacing.EAST).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);

		blockstateList[7][1] = Blocks.RED_NETHER_BRICK.getDefaultState();
		blockstateList[7][2] = Blocks.MAGMA.getDefaultState();
		blockstateList[7][18] = Blocks.NETHER_BRICK_FENCE.getDefaultState();
		blockstateList[7][120] = Blocks.NETHER_BRICK_FENCE.getDefaultState();
		stair = Blocks.NETHER_BRICK_STAIRS.getBlockState().getBaseState().withProperty(BlockStairs.SHAPE, EnumShape.STRAIGHT);
		blockstateList[7][19] = stair.withProperty(BlockStairs.FACING, EnumFacing.NORTH).withProperty(BlockStairs.HALF, EnumHalf.TOP);
		blockstateList[7][24] = stair.withProperty(BlockStairs.FACING, EnumFacing.NORTH).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);
		blockstateList[7][29] = stair.withProperty(BlockStairs.FACING, EnumFacing.SOUTH).withProperty(BlockStairs.HALF, EnumHalf.TOP);
		blockstateList[7][34] = stair.withProperty(BlockStairs.FACING, EnumFacing.SOUTH).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);
		blockstateList[7][39] = stair.withProperty(BlockStairs.FACING, EnumFacing.WEST).withProperty(BlockStairs.HALF, EnumHalf.TOP);
		blockstateList[7][44] = stair.withProperty(BlockStairs.FACING, EnumFacing.WEST).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);
		blockstateList[7][49] = stair.withProperty(BlockStairs.FACING, EnumFacing.EAST).withProperty(BlockStairs.HALF, EnumHalf.TOP);
		blockstateList[7][54] = stair.withProperty(BlockStairs.FACING, EnumFacing.EAST).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);
}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator,
			IChunkProvider chunkProvider) {
	}

	@Override
	public void generate(Random random, BlockPos pos, World world) {
		int level = LevelUtil.getLevel(pos);
		if (level>=0) {
			IBlockState[] bl = this.blockstateList[this.blockstateList.length-1];
			if(level < this.blockstateList.length) {
				bl = this.blockstateList[level];
			}
			byte[] is = getCubeData(pos, (ICubicWorld) world);
			if(is==null){
				return;
			}
			for(int index=0;index<is.length;index++) {
				int dx = index >>> 8;
				int dy = (index >>> 4) & 15;
				int dz = index & 15;
				int bstate = Byte.toUnsignedInt(is[index]);
				BlockPos bpos = pos.east(dx).up(dy).south(dz);
				if (bstate == 255) {
					world.setBlockState(bpos, AIR);
					EntityZombieLeveled zombie = new EntityZombieLeveled(world);
					zombie.setPosition(bpos.getX(), bpos.getY(), bpos.getZ());
					zombie.setLevel(level);
					world.spawnEntity(zombie);
				} else {
					world.setBlockState(bpos, bl[bstate]);
				}
			}
		}
	}

	private byte[] getCubeData(BlockPos pos, ICubicWorld world) {
		CubePos cpos = CubePos.fromBlockCoords(pos);
		DungeonCube d_type = null;
		if (dtype_cache.containsKey(cpos)) {
			d_type = dtype_cache.get(cpos);
		} else {
			d_type = getDungeonCubeType(cpos, world, 0);
		}
		if (d_type == DungeonCube.NOTHING)
			return null;
		
		return d_type.data;
	}

	private DungeonCube getDungeonCubeType(CubePos cpos, ICubicWorld world, int deep) {
		if(deep++ > 8)
			return DungeonCube.NOTHING;
		random.setSeed(world.getSeed()^cpos.getX()<<8^cpos.getY()<<4^cpos.getZ());
		int typedefiner = random.nextInt()&31;
		DungeonCube cached_value = dtype_cache.get(cpos);
		if(cached_value!=null) {
			return cached_value;
		}
		if(dtype_cache.size()>CACHE_SIZE){
			dtype_cache.clear();
		}
		DungeonCube d_down = getDungeonCubeType(cpos.sub(0, 1, 0), world, deep);
		DungeonCube d_up = getDungeonCubeType(cpos.add(0, 1, 0), world, deep);
		DungeonCube d_east = getDungeonCubeType(cpos.add(1, 0, 0), world, deep);
		DungeonCube d_west = getDungeonCubeType(cpos.sub(1, 0, 0), world, deep);
		DungeonCube d_south = getDungeonCubeType(cpos.add(0, 0, 1), world, deep);
		DungeonCube d_north = getDungeonCubeType(cpos.sub(0, 0, 1), world, deep);
		
		//Down
		if (d_down == DungeonCube.COLUMN_FLOOR) {
			dtype_cache.put(cpos, DungeonCube.COLUMN_CEIL);
			return DungeonCube.COLUMN_CEIL;
		} else if (d_down == DungeonCube.STAIR_FLOOR) {
			dtype_cache.put(cpos, DungeonCube.STAIR_CEIL);
			return DungeonCube.STAIR_CEIL;
		}
		
		//Up
		if (d_up == DungeonCube.COLUMN_CEIL) {
			dtype_cache.put(cpos, DungeonCube.COLUMN_FLOOR);
			return DungeonCube.COLUMN_FLOOR;
		} else if (d_up == DungeonCube.STAIR_CEIL) {
			dtype_cache.put(cpos, DungeonCube.STAIR_FLOOR);
			return DungeonCube.STAIR_FLOOR;
		}
		
		int pn = 0;
		if(d_east == DungeonCube.COLUMN_CEIL)
			pn+=2;
		if(d_west == DungeonCube.COLUMN_CEIL)
			pn+=2;
		if(d_south == DungeonCube.COLUMN_CEIL)
			pn+=2;
		if(d_north == DungeonCube.COLUMN_CEIL)
			pn+=2;
		if(d_east.isWestWall)
			pn++;
		if(d_west.isEastWall)
			pn++;
		if(d_south.isNorthWall)
			pn++;
		if(d_north.isSouthWall)
			pn++;
		if(pn>2){
			dtype_cache.put(cpos, DungeonCube.NODE);
			return DungeonCube.NODE;
		}
			
		//East
		if (d_east == DungeonCube.COLUMN_CEIL) {
			dtype_cache.put(cpos, DungeonCube.COLUMN_EAST_BORDER);
			return DungeonCube.COLUMN_EAST_BORDER;
		} else if (d_east.isWestWall) {
			if(d_west.isEastWall){
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_EAST_bars);
				return DungeonCube.WALL_WEST_EAST_bars;
			}
			else if(d_south.isNorthWall){
				dtype_cache.put(cpos, DungeonCube.WALL_EAST_SOUTH_bars);
				return DungeonCube.WALL_EAST_SOUTH_bars;
			}
			else if(d_north.isSouthWall){
				dtype_cache.put(cpos, DungeonCube.WALL_EAST_NORTH_bars);
				return DungeonCube.WALL_EAST_NORTH_bars;
			}
			switch (typedefiner % 6) {
			case 0:
				dtype_cache.put(cpos, DungeonCube.WALL_EAST_NORTH);
				return DungeonCube.WALL_EAST_SOUTH;
			case 1:
				dtype_cache.put(cpos, DungeonCube.WALL_EAST_NORTH_bars);
				return DungeonCube.WALL_EAST_NORTH_bars;
			case 2:
				dtype_cache.put(cpos, DungeonCube.WALL_EAST_SOUTH);
				return DungeonCube.WALL_EAST_SOUTH;
			case 3:
				dtype_cache.put(cpos, DungeonCube.WALL_EAST_SOUTH_bars);
				return DungeonCube.WALL_EAST_SOUTH_bars;
			case 4:
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_EAST);
				return DungeonCube.WALL_WEST_EAST;
			case 5:
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_EAST_bars);
				return DungeonCube.WALL_WEST_EAST_bars;
			}
		}
		//West
		if (d_west == DungeonCube.COLUMN_CEIL) {
			dtype_cache.put(cpos, DungeonCube.COLUMN_WEST_BORDER);
			return DungeonCube.COLUMN_WEST_BORDER;
		} else if (d_west.isEastWall) {
			if(d_east.isWestWall){
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_EAST_bars);
				return DungeonCube.WALL_WEST_EAST_bars;
			}
			else if(d_south.isNorthWall){
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_SOUTH_bars);
				return DungeonCube.WALL_EAST_SOUTH_bars;
			}
			else if(d_north.isSouthWall){
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_NORTH_bars);
				return DungeonCube.WALL_WEST_NORTH_bars;
			}
			switch (typedefiner % 6) {
			case 0:
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_NORTH);
				return DungeonCube.WALL_WEST_NORTH;
			case 1:
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_NORTH_bars);
				return DungeonCube.WALL_WEST_NORTH_bars;
			case 2:
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_SOUTH);
				return DungeonCube.WALL_WEST_SOUTH;
			case 3:
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_SOUTH_bars);
				return DungeonCube.WALL_WEST_SOUTH_bars;
			case 4:
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_EAST);
				return DungeonCube.WALL_WEST_EAST;
			case 5:
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_EAST_bars);
				return DungeonCube.WALL_WEST_EAST_bars;
			}
		}
		//South
		if (d_south == DungeonCube.COLUMN_CEIL) {
			dtype_cache.put(cpos, DungeonCube.COLUMN_SOUTH_BORDER);
			return DungeonCube.COLUMN_SOUTH_BORDER;
		} else if (d_south.isNorthWall) {
			if(d_east.isWestWall){
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_NORTH_bars);
				return DungeonCube.WALL_WEST_NORTH_bars;
			}
			else if(d_west.isEastWall){
				dtype_cache.put(cpos, DungeonCube.WALL_EAST_NORTH_bars);
				return DungeonCube.WALL_EAST_NORTH_bars;
			}
			else if(d_north.isSouthWall){
				dtype_cache.put(cpos, DungeonCube.WALL_SOUTH_NORTH_bars);
				return DungeonCube.WALL_SOUTH_NORTH_bars;
			}
			switch (typedefiner % 7) {
			case 0:
				dtype_cache.put(cpos, DungeonCube.WALL_SOUTH_NORTH);
				return DungeonCube.WALL_SOUTH_NORTH;
			case 1:
				dtype_cache.put(cpos, DungeonCube.WALL_SOUTH_NORTH_bars);
				return DungeonCube.WALL_SOUTH_NORTH_bars;
			case 2:
				dtype_cache.put(cpos, DungeonCube.WALL_SOUTH_NORTH_door);
				return DungeonCube.WALL_SOUTH_NORTH_door;
			case 3:
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_SOUTH_bars);
				return DungeonCube.WALL_WEST_SOUTH_bars;
			case 4:
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_SOUTH);
				return DungeonCube.WALL_WEST_SOUTH;
			case 5:
				dtype_cache.put(cpos, DungeonCube.WALL_EAST_SOUTH);
				return DungeonCube.WALL_EAST_SOUTH;
			case 6:
				dtype_cache.put(cpos, DungeonCube.WALL_EAST_SOUTH_bars);
				return DungeonCube.WALL_EAST_SOUTH_bars;
			}
		}
		//North
		if (d_north == DungeonCube.COLUMN_CEIL) {
			dtype_cache.put(cpos, DungeonCube.COLUMN_NORTH_BORDER);
			return DungeonCube.COLUMN_NORTH_BORDER;
		} else if (d_north.isSouthWall) {
			if(d_east.isWestWall){
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_SOUTH_bars);
				return DungeonCube.WALL_WEST_SOUTH_bars;
			}
			else if(d_west.isEastWall){
				dtype_cache.put(cpos, DungeonCube.WALL_EAST_SOUTH_bars);
				return DungeonCube.WALL_EAST_SOUTH_bars;
			}
			else if(d_south.isNorthWall){
				dtype_cache.put(cpos, DungeonCube.WALL_SOUTH_NORTH_bars);
				return DungeonCube.WALL_SOUTH_NORTH_bars;
			}
			switch (typedefiner % 7) {
			case 0:
				dtype_cache.put(cpos, DungeonCube.WALL_SOUTH_NORTH);
				return DungeonCube.WALL_SOUTH_NORTH;
			case 1:
				dtype_cache.put(cpos, DungeonCube.WALL_SOUTH_NORTH_bars);
				return DungeonCube.WALL_SOUTH_NORTH_bars;
			case 2:
				dtype_cache.put(cpos, DungeonCube.WALL_SOUTH_NORTH_door);
				return DungeonCube.WALL_SOUTH_NORTH_door;
			case 3:
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_NORTH_bars);
				return DungeonCube.WALL_WEST_NORTH_bars;
			case 4:
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_NORTH);
				return DungeonCube.WALL_WEST_NORTH;
			case 5:
				dtype_cache.put(cpos, DungeonCube.WALL_EAST_NORTH);
				return DungeonCube.WALL_EAST_NORTH;
			case 6:
				dtype_cache.put(cpos, DungeonCube.WALL_EAST_NORTH_bars);
				return DungeonCube.WALL_EAST_NORTH_bars;
			}
		}
		if(typedefiner < DungeonCube.values().length) {
			DungeonCube r_value = DungeonCube.values()[typedefiner];
			if((cpos.getY()&1)==0 && r_value == DungeonCube.COLUMN_CEIL){
				return DungeonCube.NOTHING;
			}
			if((cpos.getY()&1)==1 && r_value == DungeonCube.COLUMN_FLOOR){
				return DungeonCube.NOTHING;
			}
			return r_value;
		}
		return DungeonCube.NOTHING;
	}
	
}
