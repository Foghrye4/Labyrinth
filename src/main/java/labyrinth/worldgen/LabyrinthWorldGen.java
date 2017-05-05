package labyrinth.worldgen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cubicchunks.api.ICubicWorldGenerator;
import cubicchunks.util.CubePos;
import cubicchunks.world.ICubicWorld;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
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

	@SuppressWarnings("unchecked")
	List<IBlockState>[] blockstateList = new List[] {
			new ArrayList<IBlockState>(),
			new ArrayList<IBlockState>(),
			new ArrayList<IBlockState>()};
	
	private static final int CACHE_SIZE=8192;
	Map<CubePos, DungeonCube> dtype_cache = new HashMap<CubePos, DungeonCube>(CACHE_SIZE+1);
	private final Random random = new Random();
	public static LabyrinthWorldGen instance;
	
	public LabyrinthWorldGen() {
		instance=this;

		blockstateList[0].add(Blocks.AIR.getDefaultState());
		blockstateList[0].add(Blocks.QUARTZ_BLOCK.getDefaultState());
		blockstateList[0].add(Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.CHISELED));
		for (IBlockState bs : Blocks.STAINED_GLASS_PANE.getBlockState().getValidStates()) {
			if(bs.getProperties().get(BlockStainedGlassPane.COLOR).equals(EnumDyeColor.RED))
				blockstateList[0].add(bs);
		}
		for (IBlockState bs : Blocks.QUARTZ_STAIRS.getBlockState().getValidStates()) {
			blockstateList[0].add(bs);
		}
		for (IBlockState bs : Blocks.ACACIA_FENCE.getBlockState().getValidStates()) {
			blockstateList[0].add(bs);
		}
		for (IBlockState bs : Blocks.STICKY_PISTON.getBlockState().getValidStates()) {
			blockstateList[0].add(bs);
		}
		for (IBlockState bs : Blocks.LEVER.getBlockState().getValidStates()) {
			blockstateList[0].add(bs);
		}

		blockstateList[2].add(Blocks.AIR.getDefaultState());
		blockstateList[2].add(Blocks.COBBLESTONE.getDefaultState());
		blockstateList[2].add(Blocks.STONE.getDefaultState());
		for (IBlockState bs : Blocks.IRON_BARS.getBlockState().getValidStates()) {
			blockstateList[2].add(bs);
		}
		for (IBlockState bs : Blocks.STONE_STAIRS.getBlockState().getValidStates()) {
			blockstateList[2].add(bs);
		}
		for (IBlockState bs : Blocks.COBBLESTONE_WALL.getBlockState().getValidStates()) {
			blockstateList[2].add(bs);
		}
		for (IBlockState bs : Blocks.STICKY_PISTON.getBlockState().getValidStates()) {
			blockstateList[2].add(bs);
		}
		for (IBlockState bs : Blocks.LEVER.getBlockState().getValidStates()) {
			blockstateList[2].add(bs);
		}
		
		blockstateList[1].add(Blocks.AIR.getDefaultState());
		blockstateList[1].add(Blocks.STONEBRICK.getDefaultState());
		blockstateList[1].add(Blocks.STONE.getDefaultState());
		for (IBlockState bs : Blocks.IRON_BARS.getBlockState().getValidStates()) {
			blockstateList[1].add(bs);
		}
		for (IBlockState bs : Blocks.STONE_BRICK_STAIRS.getBlockState().getValidStates()) {
			blockstateList[1].add(bs);
		}
		for (IBlockState bs : Blocks.COBBLESTONE_WALL.getBlockState().getValidStates()) {
			blockstateList[1].add(bs);
		}
		for (IBlockState bs : Blocks.STICKY_PISTON.getBlockState().getValidStates()) {
			blockstateList[1].add(bs);
		}
		for (IBlockState bs : Blocks.LEVER.getBlockState().getValidStates()) {
			blockstateList[1].add(bs);
		}
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator,
			IChunkProvider chunkProvider) {
	}

	@Override
	public void generate(Random random, BlockPos pos, World world) {
		if (pos.getY() < -0) {
			int level = -pos.getY()/32;
			List<IBlockState> bl = this.blockstateList[this.blockstateList.length-1];
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
				world.setBlockState(pos.east(dx).up(dy).south(dz), bl.get(Byte.toUnsignedInt(is[index])));
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
