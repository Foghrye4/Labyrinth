package labyrinth.worldgen;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cubicchunks.api.ICubicWorldGenerator;
import cubicchunks.util.CubePos;
import cubicchunks.world.ICubicWorld;
import labyrinth.LabyrinthMod;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.MinecraftForge;

public class LabyrinthWorldGen implements ICubicWorldGenerator {

	private final String column_ceil = "column_ceil.cube_structure";
	private final String column_east_border = "column_east_border.cube_structure";
	private final String column_floor_ceil = "column_floor_ceil.cube_structure";
	private final String column_floor = "column_floor.cube_structure";
	private final String column_north_border = "column_north_border.cube_structure";
	private final String column_south_border = "column_south_border.cube_structure";
	private final String column_west_border = "column_west_border.cube_structure";
	private final String stair_ceil = "stair_ceil.cube_structure";
	private final String stair_floor = "stair_floor.cube_structure";
	private final String wall_east_north_bars = "wall_east_north_bars.cube_structure";
	private final String wall_east_north = "wall_east_north.cube_structure";
	private final String wall_east_south_bars = "wall_east_south_bars.cube_structure";
	private final String wall_east_south = "wall_east_south.cube_structure";
	private final String wall_south_north_bars = "wall_south_north_bars.cube_structure";
	private final String wall_south_north = "wall_south_north.cube_structure";
	private final String wall_south_north_door = "wall_south_north_door.cube_structure";
	private final String wall_west_east_bars = "wall_west_east_bars.cube_structure";
	private final String wall_west_east = "wall_west_east.cube_structure";
	private final String wall_west_north_bars = "wall_west_north_bars.cube_structure";
	private final String wall_west_north = "wall_west_north.cube_structure";
	private final String wall_west_south_bars = "wall_west_south_bars.cube_structure";
	private final String wall_west_south = "wall_west_south.cube_structure";
	private final String[] valid_dungeon_types = new String[] { column_ceil, column_east_border, column_floor_ceil,
			column_floor, column_north_border, column_south_border, column_west_border, stair_ceil, stair_floor,
			wall_east_north_bars, wall_east_north, wall_east_south_bars, wall_east_south, wall_south_north_bars,
			wall_south_north, wall_south_north_door, wall_west_east_bars, wall_west_east, wall_west_north_bars,
			wall_west_north, wall_west_south_bars, wall_west_south };

	List<IBlockState> blockstateList = new ArrayList<IBlockState>();
	private static final int CACHE_SIZE=8192;
	Map<CubePos, String> dtype_cache = new HashMap<CubePos, String>(CACHE_SIZE+1);
	
	public LabyrinthWorldGen() {
		blockstateList.add(Blocks.AIR.getDefaultState());
		blockstateList.add(Blocks.STONEBRICK.getDefaultState());
		blockstateList.add(Blocks.IRON_BARS.getDefaultState());
		for (IBlockState bs : Blocks.IRON_BARS.getBlockState().getValidStates()) {
			blockstateList.add(bs);
		}
		for (IBlockState bs : Blocks.STONE_BRICK_STAIRS.getBlockState().getValidStates()) {
			blockstateList.add(bs);
		}
		for (IBlockState bs : Blocks.COBBLESTONE_WALL.getBlockState().getValidStates()) {
			blockstateList.add(bs);
		}
		for (IBlockState bs : Blocks.STICKY_PISTON.getBlockState().getValidStates()) {
			blockstateList.add(bs);
		}
		for (IBlockState bs : Blocks.LEVER.getBlockState().getValidStates()) {
			blockstateList.add(bs);
		}
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator,
			IChunkProvider chunkProvider) {
	}

	@Override
	public void generate(Random random, BlockPos pos, World world) {
		if (pos.getY() < -0) {
			try {
				int index = 0;
				InputStream is = getFile(pos, (ICubicWorld) world);
				if(is==null){
					return;
				}
				DataInputStream dis = new DataInputStream(is);
				while (dis.available() > 0) {
					int dx = index >>> 8;
					int dy = (index >>> 4) & 15;
					int dz = index & 15;
					world.setBlockState(pos.east(dx).up(dy).south(dz), this.blockstateList.get(dis.readUnsignedByte()));
					index++;
				}
				dis.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private InputStream getFile(BlockPos pos, ICubicWorld world) throws IOException {
		CubePos cpos = CubePos.fromBlockCoords(pos);
		String d_type = null;
		System.out.println("dtype_cache size="+dtype_cache.size());
		if (dtype_cache.containsKey(cpos)) {
			d_type = dtype_cache.get(cpos);
		} else {
			d_type = getDungeonCubeType(cpos, world, 0);
		}
		if (d_type == null)
			return null;
		return Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("labyrinth","cubes/"+d_type)).getInputStream();
//		File folder = new File(LabyrinthMod.proxy.getMinecraftDir(), "cubes");
//		return new File(folder, d_type);
	}

	private String getDungeonCubeType(CubePos cpos, ICubicWorld world, int deep) {
		if(deep++ > 8)
			return null;
		int typedefiner = (cpos.getX()^cpos.getY()^cpos.getZ()^(int)world.getSeed())&31;
		if(dtype_cache.size()>CACHE_SIZE){
			dtype_cache.clear();
		}
		String d_down = getDungeonCubeType(cpos.sub(0, 1, 0), world, deep);
		String d_up = getDungeonCubeType(cpos.add(0, 1, 0), world, deep);
		String d_east = getDungeonCubeType(cpos.add(1, 0, 0), world, deep);
		String d_west = getDungeonCubeType(cpos.sub(1, 0, 0), world, deep);
		String d_south = getDungeonCubeType(cpos.add(0, 0, 1), world, deep);
		String d_north = getDungeonCubeType(cpos.sub(0, 0, 1), world, deep);
		if (d_down == this.column_floor) {
			dtype_cache.put(cpos, this.column_ceil);
			return this.column_ceil;
		} else if (d_down == this.stair_floor) {
			dtype_cache.put(cpos, this.stair_ceil);
			return this.stair_ceil;
		}
		
		//Up
		if (d_up == this.column_ceil) {
			dtype_cache.put(cpos, this.column_floor);
			return this.column_floor;
		} else if (d_up == this.stair_ceil) {
			dtype_cache.put(cpos, this.stair_floor);
			return this.stair_floor;
		}
		//East
		if (d_east == this.column_ceil) {
			dtype_cache.put(cpos, this.column_east_border);
			return this.column_east_border;
		} else if (isWestWall(d_east)) {
			if(isEastWall(d_west)){
				dtype_cache.put(cpos, this.wall_west_east_bars);
				return this.wall_west_east_bars;
			}
			else if(isNorthWall(d_south)){
				dtype_cache.put(cpos, this.wall_east_south_bars);
				return this.wall_east_south_bars;
			}
			else if(isSouthWall(d_north)){
				dtype_cache.put(cpos, this.wall_east_north_bars);
				return this.wall_east_north_bars;
			}
			switch (typedefiner % 6) {
			case 0:
				dtype_cache.put(cpos, this.wall_east_north);
				return this.wall_east_north;
			case 1:
				dtype_cache.put(cpos, this.wall_east_north_bars);
				return this.wall_east_north_bars;
			case 2:
				dtype_cache.put(cpos, this.wall_east_south);
				return this.wall_east_south;
			case 3:
				dtype_cache.put(cpos, this.wall_east_south_bars);
				return this.wall_east_south_bars;
			case 4:
				dtype_cache.put(cpos, this.wall_west_east);
				return this.wall_west_east;
			case 5:
				dtype_cache.put(cpos, this.wall_west_east_bars);
				return this.wall_west_east_bars;
			}
		}
		//West
		if (d_west == this.column_ceil) {
			dtype_cache.put(cpos, this.column_west_border);
			return this.column_west_border;
		} else if (isEastWall(d_west)) {
			if(isWestWall(d_east)){
				dtype_cache.put(cpos, this.wall_west_east_bars);
				return this.wall_west_east_bars;
			}
			else if(isNorthWall(d_south)){
				dtype_cache.put(cpos, this.wall_west_south_bars);
				return this.wall_east_south_bars;
			}
			else if(isSouthWall(d_north)){
				dtype_cache.put(cpos, this.wall_west_north_bars);
				return this.wall_west_north_bars;
			}
			switch (typedefiner % 6) {
			case 0:
				dtype_cache.put(cpos, this.wall_west_north);
				return this.wall_west_north;
			case 1:
				dtype_cache.put(cpos, this.wall_west_north_bars);
				return this.wall_west_north_bars;
			case 2:
				dtype_cache.put(cpos, this.wall_west_south);
				return this.wall_west_south;
			case 3:
				dtype_cache.put(cpos, this.wall_west_south_bars);
				return this.wall_west_south_bars;
			case 4:
				dtype_cache.put(cpos, this.wall_west_east);
				return this.wall_west_east;
			case 5:
				dtype_cache.put(cpos, this.wall_west_east_bars);
				return this.wall_west_east_bars;
			}
		}
		//South
		if (d_south == this.column_ceil) {
			dtype_cache.put(cpos, this.column_south_border);
			return this.column_south_border;
		} else if (isNorthWall(d_south)) {
			if(isWestWall(d_east)){
				dtype_cache.put(cpos, this.wall_west_north_bars);
				return this.wall_west_north_bars;
			}
			else if(isEastWall(d_west)){
				dtype_cache.put(cpos, this.wall_east_north_bars);
				return this.wall_east_north_bars;
			}
			else if(isSouthWall(d_north)){
				dtype_cache.put(cpos, this.wall_south_north_bars);
				return this.wall_south_north_bars;
			}
			switch (typedefiner % 7) {
			case 0:
				dtype_cache.put(cpos, this.wall_south_north);
				return this.wall_south_north;
			case 1:
				dtype_cache.put(cpos, this.wall_south_north_bars);
				return this.wall_south_north_bars;
			case 2:
				dtype_cache.put(cpos, this.wall_south_north_door);
				return this.wall_south_north_door;
			case 3:
				dtype_cache.put(cpos, this.wall_west_south_bars);
				return this.wall_west_south_bars;
			case 4:
				dtype_cache.put(cpos, this.wall_west_south);
				return this.wall_west_south;
			case 5:
				dtype_cache.put(cpos, this.wall_east_south);
				return this.wall_east_south;
			case 6:
				dtype_cache.put(cpos, this.wall_east_south_bars);
				return this.wall_east_south_bars;
			}
		}
		//North
		if (d_north == this.column_ceil) {
			dtype_cache.put(cpos, this.column_north_border);
			return this.column_north_border;
		} else if (isSouthWall(d_north)) {
			if(isWestWall(d_east)){
				dtype_cache.put(cpos, this.wall_west_south_bars);
				return this.wall_west_south_bars;
			}
			else if(isEastWall(d_west)){
				dtype_cache.put(cpos, this.wall_east_south_bars);
				return this.wall_east_south_bars;
			}
			else if(isNorthWall(d_south)){
				dtype_cache.put(cpos, this.wall_south_north_bars);
				return this.wall_south_north_bars;
			}
			switch (typedefiner % 7) {
			case 0:
				dtype_cache.put(cpos, this.wall_south_north);
				return this.wall_south_north;
			case 1:
				dtype_cache.put(cpos, this.wall_south_north_bars);
				return this.wall_south_north_bars;
			case 2:
				dtype_cache.put(cpos, this.wall_south_north_door);
				return this.wall_south_north_door;
			case 3:
				dtype_cache.put(cpos, this.wall_west_north_bars);
				return this.wall_west_north_bars;
			case 4:
				dtype_cache.put(cpos, this.wall_west_north);
				return this.wall_west_north;
			case 5:
				dtype_cache.put(cpos, this.wall_east_north);
				return this.wall_east_north;
			case 6:
				dtype_cache.put(cpos, this.wall_east_north_bars);
				return this.wall_east_north_bars;
			}
		}
		if(typedefiner < this.valid_dungeon_types.length) {
			return this.valid_dungeon_types[typedefiner];
		}
		return null;
	}
	
	private boolean isWestWall(String d_east){
		return d_east == this.wall_west_east || 
				d_east == this.wall_west_east_bars || 
				d_east == this.wall_west_north || 
				d_east == this.wall_west_north_bars || 
				d_east == this.wall_west_south || 
				d_east == this.wall_west_south_bars;
	}
	private boolean isEastWall(String d_west){
		return d_west == this.wall_west_east || 
				d_west == this.wall_west_east_bars || 
				d_west == this.wall_east_north ||
				d_west == this.wall_east_north_bars || 
				d_west == this.wall_east_south || 
				d_west == this.wall_east_south_bars;
	}
	private boolean isNorthWall(String d_south){
		return d_south == this.wall_south_north || 
				d_south == this.wall_south_north_bars || 
				d_south == this.wall_south_north_door || 
				d_south == this.wall_east_north
				|| d_south == this.wall_east_north_bars || 
				d_south == this.wall_west_north || 
				d_south == this.wall_west_north_bars;
	}
	private boolean isSouthWall(String d_north){
		return d_north == this.wall_south_north || 
				d_north == this.wall_south_north_bars || 
				d_north == this.wall_south_north_door || 
				d_north == this.wall_east_south || 
				d_north == this.wall_east_south_bars || 
				d_north == this.wall_west_south || 
				d_north == this.wall_west_south_bars;
	}
}
