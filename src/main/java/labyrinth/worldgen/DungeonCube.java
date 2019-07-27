package labyrinth.worldgen;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.github.opencubicchunks.cubicchunks.api.util.CubePos;
import io.github.opencubicchunks.cubicchunks.api.world.ICube;
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorld;
import labyrinth.LabyrinthMod;
import labyrinth.village.UndergroundVillage;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.BlockPos.PooledMutableBlockPos;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class DungeonCube {

	public static final List<DungeonCube> values = new ArrayList<DungeonCube>();
	public static DungeonCube COLUMN_CEIL = new DungeonCube("column_ceil.cube_structure", DungeonCubeFlag.COLUMN_TOP);
	public static DungeonCube COLUMN_MIDDLE = new DungeonCube("column_middle.cube_structure", DungeonCubeFlag.COLUMN_MIDDLE);
	public static DungeonCube COLUMN_FLOOR = new DungeonCube("column_floor.cube_structure", DungeonCubeFlag.COLUMN_BOTTOM);
	public static DungeonCube STAIR_TOP_WITH_STAIR = new DungeonCube("column_ceil_with_south_mini_stair.cube_structure", DungeonCubeFlag.STAIR_TOP);
	public static DungeonCube STAIR_TOP = new DungeonCube("stair_ceil.cube_structure", DungeonCubeFlag.STAIR_TOP);
	public static DungeonCube STAIR_TOP_CEILINGLESS = new DungeonCube("stair_top_ceilingless.cube_structure", DungeonCubeFlag.STAIR_TOP, DungeonCubeFlag.COLUMN_BOTTOM);
	public static DungeonCube STAIR_MIDDLE = new DungeonCube("stair_middle.cube_structure", DungeonCubeFlag.STAIR_TOP, DungeonCubeFlag.STAIR_BOTTOM);
	public static DungeonCube STAIR_FLOOR = new DungeonCube("stair_floor.cube_structure", DungeonCubeFlag.STAIR_BOTTOM);
	public static DungeonCube COLUMN_FLOOR_CEIL = new DungeonCube("column_floor_ceil.cube_structure");
	public static DungeonCube COLUMN_EAST_BORDER = new DungeonCube("column_east_border.cube_structure");
	public static DungeonCube COLUMN_NORTH_BORDER = new DungeonCube("column_north_border.cube_structure");
	public static DungeonCube COLUMN_SOUTH_BORDER = new DungeonCube("column_south_border.cube_structure");
	public static DungeonCube COLUMN_WEST_BORDER = new DungeonCube("column_west_border.cube_structure");
	public static DungeonCube EAST_BORDER_WITH_WALL_SOUTH_NORTH = new DungeonCube("east_border_with_wall_south_north.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.NORTH_WALL);
	public static DungeonCube NORTH_BORDER_WITH_WALL_EAST_WEST = new DungeonCube("north_border_with_wall_east_west.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.WEST_WALL);
	public static DungeonCube SOUTH_BORDER_WITH_WALL_EAST_WEST = new DungeonCube("south_border_with_wall_east_west.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.WEST_WALL);
	public static DungeonCube WEST_BORDER_WITH_WALL_SOUTH_NORTH = new DungeonCube("west_border_with_wall_south_north.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.NORTH_WALL);

	public static DungeonCube EAST_BORDER_WITH_WALL_SOUTH_WEST = new DungeonCube("east_border_with_wall_south_west.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.WEST_WALL);
	public static DungeonCube NORTH_BORDER_WITH_WALL_EAST_SOUTH = new DungeonCube("north_border_with_wall_east_south.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.EAST_WALL);
	public static DungeonCube SOUTH_BORDER_WITH_WALL_WEST_NORTH = new DungeonCube("south_border_with_wall_west_north.cube_structure", DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL);
	public static DungeonCube WEST_BORDER_WITH_WALL_NORTH_EAST = new DungeonCube("west_border_with_wall_north_east.cube_structure", DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.EAST_WALL);

	public static DungeonCube EAST_BORDER_WITH_WALL_WEST_NORTH = new DungeonCube("east_border_with_wall_west_north.cube_structure", DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL);
	public static DungeonCube NORTH_BORDER_WITH_WALL_SOUTH_WEST = new DungeonCube("north_border_with_wall_south_west.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.WEST_WALL);
	public static DungeonCube SOUTH_BORDER_WITH_WALL_NORTH_EAST = new DungeonCube("south_border_with_wall_north_east.cube_structure", DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.EAST_WALL);
	public static DungeonCube WEST_BORDER_WITH_WALL_EAST_SOUTH = new DungeonCube("west_border_with_wall_east_south.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.EAST_WALL);

	public static DungeonCube NORTH_EAST_BORDER = new DungeonCube("north_east_border.cube_structure");
	public static DungeonCube WEST_NORTH_BORDER = new DungeonCube("west_north_border.cube_structure");
	public static DungeonCube EAST_SOUTH_BORDER = new DungeonCube("east_south_border.cube_structure");
	public static DungeonCube SOUTH_WEST_BORDER = new DungeonCube("south_west_border.cube_structure");

	public static DungeonCube NORTH_BORDER_COLUMN_FLOOR = new DungeonCube("north_border_column_floor.cube_structure", DungeonCubeFlag.COLUMN_BOTTOM);
	public static DungeonCube SOUTH_BORDER_COLUMN_FLOOR = new DungeonCube("south_border_column_floor.cube_structure", DungeonCubeFlag.COLUMN_BOTTOM);
	public static DungeonCube WEST_BORDER_COLUMN_FLOOR = new DungeonCube("west_border_column_floor.cube_structure", DungeonCubeFlag.COLUMN_BOTTOM);
	public static DungeonCube EAST_BORDER_COLUMN_FLOOR = new DungeonCube("east_border_column_floor.cube_structure", DungeonCubeFlag.COLUMN_BOTTOM);

	public static DungeonCube WALL_EAST_NORTH_SOUTH = new DungeonCube("wall_east_north_south.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.SOUTH_WALL);
	public static DungeonCube WALL_SOUTH_EAST_WEST = new DungeonCube("wall_south_east_west.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.WEST_WALL);
	public static DungeonCube WALL_WEST_SOUTH_NORTH = new DungeonCube("wall_west_south_north.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL);
	public static DungeonCube WALL_NORTH_WEST_EAST = new DungeonCube("wall_north_west_east.cube_structure", DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.EAST_WALL);

	public static DungeonCube EAST_BORDER_WITH_WALLS = new DungeonCube("east_border_with_walls.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL);
	public static DungeonCube NORTH_BORDER_WITH_WALLS = new DungeonCube("north_border_with_walls.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.EAST_WALL);
	public static DungeonCube SOUTH_BORDER_WITH_WALLS = new DungeonCube("south_border_with_walls.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.EAST_WALL);
	public static DungeonCube WEST_BORDER_WITH_WALLS = new DungeonCube("west_border_with_walls.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.EAST_WALL);
	public static DungeonCube STAIR_FLOOR_W_ROOM_OT_NORTH_EAST = new DungeonCube("stair_floor_w_room_ot_north_east.cube_structure", DungeonCubeFlag.STAIR_BOTTOM);
	public static DungeonCube STAIR_FLOOR_W_ROOM_OT_NORTH_WEST = new DungeonCube("stair_floor_w_room_ot_north_west.cube_structure", DungeonCubeFlag.STAIR_BOTTOM);
	public static DungeonCube STAIR_FLOOR_W_ROOM_OT_SOUTH_EAST = new DungeonCube("stair_floor_w_room_ot_south_east.cube_structure", DungeonCubeFlag.STAIR_BOTTOM);
	public static DungeonCube STAIR_FLOOR_W_ROOM_OT_SOUTH_WEST = new DungeonCube("stair_floor_w_room_ot_south_west.cube_structure", DungeonCubeFlag.STAIR_BOTTOM);
	public static DungeonCube STAIR_TOP_W_ROOM_OT_NORTH_EAST = new DungeonCube("stair_ceil_w_room_ot_north_east.cube_structure", DungeonCubeFlag.STAIR_TOP);
	public static DungeonCube STAIR_TOP_W_ROOM_OT_NORTH_WEST = new DungeonCube("stair_ceil_w_room_ot_north_west.cube_structure", DungeonCubeFlag.STAIR_TOP);
	public static DungeonCube STAIR_TOP_W_ROOM_OT_SOUTH_EAST = new DungeonCube("stair_ceil_w_room_ot_south_east.cube_structure", DungeonCubeFlag.STAIR_TOP);
	public static DungeonCube STAIR_TOP_W_ROOM_OT_SOUTH_WEST = new DungeonCube("stair_ceil_w_room_ot_south_west.cube_structure", DungeonCubeFlag.STAIR_TOP);

	public static DungeonCube WORKSHOP = new DungeonCube("workshop_south_door.cube_structure");
	public static DungeonCube LIBRARY = new DungeonCube("library.cube_structure", DungeonCubeFlag.LIBRARY);
	public static DungeonCube WALL_X = new DungeonCube("wall_x.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.SOUTH_WALL);
	public static DungeonCube WALL_EAST_NORTH_BARS = new DungeonCube("wall_east_north_bars.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.NORTH_WALL);
	public static DungeonCube WALL_EAST_NORTH = new DungeonCube("wall_east_north.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.NORTH_WALL);
	public static DungeonCube WALL_EAST_SOUTH_BARS = new DungeonCube("wall_east_south_bars.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.SOUTH_WALL);
	public static DungeonCube WALL_EAST_SOUTH = new DungeonCube("wall_east_south.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.SOUTH_WALL);
	public static DungeonCube WALL_SOUTH_NORTH = new DungeonCube("wall_south_north.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.NORTH_WALL);
	public static DungeonCube WALL_SOUTH_NORTH_DOOR = new DungeonCube("wall_south_north_door.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.NORTH_WALL);
	public static DungeonCube WALL_WEST_EAST_BARS = new DungeonCube("wall_west_east_bars.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.EAST_WALL);
	public static DungeonCube WALL_WEST_EAST = new DungeonCube("wall_west_east.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.EAST_WALL);
	public static DungeonCube WALL_WEST_NORTH_BARS = new DungeonCube("wall_west_north_bars.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.NORTH_WALL);
	public static DungeonCube WALL_WEST_NORTH = new DungeonCube("wall_west_north.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.NORTH_WALL);
	public static DungeonCube WALL_WEST_SOUTH_BARS = new DungeonCube("wall_west_south_bars.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.SOUTH_WALL);
	public static DungeonCube WALL_WEST_SOUTH = new DungeonCube("wall_west_south.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.SOUTH_WALL);

	public static DungeonCube COLUMN_FLOOR_LAVA = new DungeonCube("column_floor_lava.cube_structure", DungeonCubeFlag.COLUMN_BOTTOM);
	public static DungeonCube STAIR_TOP_LAVA = new DungeonCube("stair_ceil_lava.cube_structure", DungeonCubeFlag.STAIR_TOP);
	public static DungeonCube STAIR_TOP_CEILINGLESS_LAVA = new DungeonCube("stair_top_ceilingless_lava.cube_structure", DungeonCubeFlag.STAIR_TOP, DungeonCubeFlag.COLUMN_BOTTOM);
	public static DungeonCube STAIR_MIDDLE_LAVA = new DungeonCube("stair_middle_lava.cube_structure", DungeonCubeFlag.STAIR_TOP, DungeonCubeFlag.STAIR_BOTTOM);
	public static DungeonCube STAIR_FLOOR_LAVA = new DungeonCube("stair_floor_lava.cube_structure", DungeonCubeFlag.STAIR_BOTTOM);
	public static DungeonCube COLUMN_FLOOR_CEIL_LAVA = new DungeonCube("column_floor_ceil_lava.cube_structure");
	public static DungeonCube COLUMN_EAST_BORDER_LAVA = new DungeonCube("column_east_border_lava.cube_structure");
	public static DungeonCube COLUMN_NORTH_BORDER_LAVA = new DungeonCube("column_north_border_lava.cube_structure");
	public static DungeonCube COLUMN_SOUTH_BORDER_LAVA = new DungeonCube("column_south_border_lava.cube_structure");
	public static DungeonCube COLUMN_WEST_BORDER_LAVA = new DungeonCube("column_west_border_lava.cube_structure");
	public static DungeonCube EAST_BORDER_WITH_WALL_SOUTH_NORTH_LAVA = new DungeonCube("east_border_with_wall_south_north_lava.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.NORTH_WALL);
	public static DungeonCube NORTH_BORDER_WITH_WALL_EAST_WEST_LAVA = new DungeonCube("north_border_with_wall_east_west_lava.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.WEST_WALL);
	public static DungeonCube SOUTH_BORDER_WITH_WALL_EAST_WEST_LAVA = new DungeonCube("south_border_with_wall_west_east_lava.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.WEST_WALL);
	public static DungeonCube WEST_BORDER_WITH_WALL_SOUTH_NORTH_LAVA = new DungeonCube("west_border_with_wall_north_south_lava.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.NORTH_WALL);

	public static DungeonCube EAST_BORDER_WITH_WALL_SOUTH_WEST_LAVA = new DungeonCube("east_border_with_wall_south_west_lava.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.WEST_WALL);
	public static DungeonCube NORTH_BORDER_WITH_WALL_EAST_SOUTH_LAVA = new DungeonCube("north_border_with_wall_east_south_lava.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.EAST_WALL);
	public static DungeonCube SOUTH_BORDER_WITH_WALL_WEST_NORTH_LAVA = new DungeonCube("south_border_with_wall_west_north_lava.cube_structure", DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL);
	public static DungeonCube WEST_BORDER_WITH_WALL_NORTH_EAST_LAVA = new DungeonCube("west_border_with_wall_north_east_lava.cube_structure", DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.EAST_WALL);

	public static DungeonCube EAST_BORDER_WITH_WALL_WEST_NORTH_LAVA = new DungeonCube("east_border_with_wall_west_north_lava.cube_structure", DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL);
	public static DungeonCube NORTH_BORDER_WITH_WALL_SOUTH_WEST_LAVA = new DungeonCube("north_border_with_wall_south_west_lava.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.WEST_WALL);
	public static DungeonCube SOUTH_BORDER_WITH_WALL_NORTH_EAST_LAVA = new DungeonCube("south_border_with_wall_north_east_lava.cube_structure", DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.EAST_WALL);
	public static DungeonCube WEST_BORDER_WITH_WALL_EAST_SOUTH_LAVA = new DungeonCube("west_border_with_wall_east_south_lava.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.EAST_WALL);

	public static DungeonCube NORTH_EAST_BORDER_LAVA = new DungeonCube("north_east_border_lava.cube_structure");
	public static DungeonCube WEST_NORTH_BORDER_LAVA = new DungeonCube("west_north_border_lava.cube_structure");
	public static DungeonCube EAST_SOUTH_BORDER_LAVA = new DungeonCube("east_south_border_lava.cube_structure");
	public static DungeonCube SOUTH_WEST_BORDER_LAVA = new DungeonCube("south_west_border_lava.cube_structure");

	public static DungeonCube NORTH_BORDER_COLUMN_FLOOR_LAVA = new DungeonCube("north_border_column_floor_lava.cube_structure", DungeonCubeFlag.COLUMN_BOTTOM);
	public static DungeonCube SOUTH_BORDER_COLUMN_FLOOR_LAVA = new DungeonCube("south_border_column_floor_lava.cube_structure", DungeonCubeFlag.COLUMN_BOTTOM);
	public static DungeonCube WEST_BORDER_COLUMN_FLOOR_LAVA = new DungeonCube("west_border_column_floor_lava.cube_structure", DungeonCubeFlag.COLUMN_BOTTOM);
	public static DungeonCube EAST_BORDER_COLUMN_FLOOR_LAVA = new DungeonCube("east_border_column_floor_lava.cube_structure", DungeonCubeFlag.COLUMN_BOTTOM);

	public static DungeonCube WALL_EAST_LAVA = new DungeonCube("wall_east_lava.cube_structure", DungeonCubeFlag.EAST_WALL);
	public static DungeonCube WALL_SOUTH_LAVA = new DungeonCube("wall_south_lava.cube_structure", DungeonCubeFlag.SOUTH_WALL);
	public static DungeonCube WALL_NORTH_LAVA = new DungeonCube("wall_north_lava.cube_structure", DungeonCubeFlag.NORTH_WALL);
	public static DungeonCube WALL_WEST_LAVA = new DungeonCube("wall_west_lava.cube_structure", DungeonCubeFlag.WEST_WALL);

	public static DungeonCube WALL_EAST_NORTH_SOUTH_LAVA = new DungeonCube("wall_north_south_east_lava.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.SOUTH_WALL);
	public static DungeonCube WALL_SOUTH_EAST_WEST_LAVA = new DungeonCube("wall_east_west_south_lava.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.WEST_WALL);
	public static DungeonCube WALL_WEST_SOUTH_NORTH_LAVA = new DungeonCube("wall_south_north_west_lava.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL);
	public static DungeonCube WALL_NORTH_WEST_EAST_LAVA = new DungeonCube("wall_west_east_north_lava.cube_structure", DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.EAST_WALL);

	public static DungeonCube EAST_BORDER_WITH_WALLS_LAVA = new DungeonCube("east_border_with_walls_lava.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL);
	public static DungeonCube NORTH_BORDER_WITH_WALLS_LAVA = new DungeonCube("north_border_with_walls_lava.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.EAST_WALL);
	public static DungeonCube SOUTH_BORDER_WITH_WALLS_LAVA = new DungeonCube("south_border_with_walls_lava.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.EAST_WALL);
	public static DungeonCube WEST_BORDER_WITH_WALLS_LAVA = new DungeonCube("west_border_with_walls_lava.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.EAST_WALL);
	public static DungeonCube STAIR_FLOOR_W_ROOM_OT_NORTH_EAST_LAVA = new DungeonCube("stair_floor_w_room_ot_north_east_lava.cube_structure", DungeonCubeFlag.STAIR_BOTTOM);
	public static DungeonCube STAIR_FLOOR_W_ROOM_OT_NORTH_WEST_LAVA = new DungeonCube("stair_floor_w_room_ot_north_west_lava.cube_structure", DungeonCubeFlag.STAIR_BOTTOM);
	public static DungeonCube STAIR_FLOOR_W_ROOM_OT_SOUTH_EAST_LAVA = new DungeonCube("stair_floor_w_room_ot_south_east_lava.cube_structure", DungeonCubeFlag.STAIR_BOTTOM);
	public static DungeonCube STAIR_FLOOR_W_ROOM_OT_SOUTH_WEST_LAVA = new DungeonCube("stair_floor_w_room_ot_south_west_lava.cube_structure", DungeonCubeFlag.STAIR_BOTTOM);
	public static DungeonCube STAIR_TOP_W_ROOM_OT_NORTH_EAST_LAVA = new DungeonCube("stair_ceil_w_room_ot_north_east_lava.cube_structure", DungeonCubeFlag.STAIR_TOP);
	public static DungeonCube STAIR_TOP_W_ROOM_OT_NORTH_WEST_LAVA = new DungeonCube("stair_ceil_w_room_ot_north_west_lava.cube_structure", DungeonCubeFlag.STAIR_TOP);
	public static DungeonCube STAIR_TOP_W_ROOM_OT_SOUTH_EAST_LAVA = new DungeonCube("stair_ceil_w_room_ot_south_east_lava.cube_structure", DungeonCubeFlag.STAIR_TOP);
	public static DungeonCube STAIR_TOP_W_ROOM_OT_SOUTH_WEST_LAVA = new DungeonCube("stair_ceil_w_room_ot_south_west_lava.cube_structure", DungeonCubeFlag.STAIR_TOP);
	public static DungeonCube WORKSHOP_LAVA = new DungeonCube("workshop_lava.cube_structure");
	public static DungeonCube LIBRARY_LAVA = new DungeonCube("library_lava.cube_structure", DungeonCubeFlag.LIBRARY);
	public static DungeonCube WALL_X_LAVA = new DungeonCube("wall_x_lava.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.SOUTH_WALL);
	public static DungeonCube WALL_EAST_NORTH_LAVA = new DungeonCube("wall_north_east_lava.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.NORTH_WALL);
	public static DungeonCube WALL_EAST_SOUTH_LAVA = new DungeonCube("wall_east_south_lava.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.SOUTH_WALL);
	public static DungeonCube WALL_SOUTH_NORTH_LAVA = new DungeonCube("wall_north_south_lava.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.NORTH_WALL);
	public static DungeonCube WALL_WEST_EAST_LAVA = new DungeonCube("wall_west_east_lava.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.EAST_WALL);
	public static DungeonCube WALL_WEST_NORTH_LAVA = new DungeonCube("wall_west_north_lava.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.NORTH_WALL);
	public static DungeonCube WALL_WEST_SOUTH_LAVA = new DungeonCube("wall_south_west_lava.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.SOUTH_WALL);

	public static DungeonCube X_ROADS = new DungeonCube("x_roads.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.SOUTH_WALL);
	public static DungeonCube X_ROADS_HIDDEN_ROOM = new DungeonCube("x_roads_hidden_room.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.LIBRARY);
	public static DungeonCube LAVA_ROOM = new DungeonCube("lava_room.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.SOUTH_WALL);
	public static DungeonCube LAVA_ROOM_WORKSHOP = new DungeonCube("lava_room_workshop.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.SOUTH_WALL);
	public static DungeonCube LADDER_CEIL = new DungeonCube("ladder_ceil.cube_structure", DungeonCubeFlag.STAIR_TOP, DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.SOUTH_WALL);
	public static DungeonCube LADDER_MIDDLE = new DungeonCube("ladder_middle.cube_structure", DungeonCubeFlag.STAIR_TOP, DungeonCubeFlag.STAIR_BOTTOM);
	public static DungeonCube LADDER_FLOOR = new DungeonCube("ladder_floor.cube_structure", DungeonCubeFlag.STAIR_BOTTOM, DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.SOUTH_WALL);
	public static DungeonCube LADDER_FLOOR_TRAP = new DungeonCube("ladder_floor_trap.cube_structure", DungeonCubeFlag.STAIR_BOTTOM, DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.SOUTH_WALL);
	public static DungeonCube ROAD_SOUTH_NORTH = new DungeonCube("road_south_north.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.NORTH_WALL);
	public static DungeonCube ROAD_SOUTH_NORTH_TRAP = new DungeonCube("road_south_north_trap.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.NORTH_WALL);
	public static DungeonCube ROAD_EAST_WEST = new DungeonCube("road_east_west.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.WEST_WALL);
	public static DungeonCube ROAD_EAST_WEST_TRAP = new DungeonCube("road_east_west_trap.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.WEST_WALL);
	public static DungeonCube ROAD_WEST_NORTH = new DungeonCube("road_west_north.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.NORTH_WALL);
	public static DungeonCube ROAD_WEST_NORTH_TRAP = new DungeonCube("road_west_north_trap.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.NORTH_WALL);
	public static DungeonCube ROAD_SOUTH_WEST = new DungeonCube("road_south_west.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.SOUTH_WALL);
	public static DungeonCube ROAD_NORTH_EAST = new DungeonCube("road_north_east.cube_structure", DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.EAST_WALL);
	public static DungeonCube ROAD_EAST_SOUTH = new DungeonCube("road_east_south.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.SOUTH_WALL);
	public static DungeonCube ROAD_WEST_NORTH_EAST = new DungeonCube("road_west_north_east.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.EAST_WALL);
	public static DungeonCube ROAD_SOUTH_WEST_NORTH = new DungeonCube("road_south_west_north.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.SOUTH_WALL);
	public static DungeonCube ROAD_NORTH_EAST_SOUTH = new DungeonCube("road_north_east_south.cube_structure", DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.SOUTH_WALL);
	public static DungeonCube ROAD_EAST_SOUTH_WEST = new DungeonCube("road_east_south_west.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.WEST_WALL);

	public static DungeonCube VILLAGE_SOUTH_WEST = new DungeonCube("village_south_west.cube_structure", DungeonCubeFlag.CORRAL);
	public static DungeonCube VILLAGE_SOUTH_EAST = new DungeonCube("village_south_east.cube_structure");
	public static DungeonCube VILLAGE_NORTH_WEST = new DungeonCube("village_north_west.cube_structure");
	public static DungeonCube VILLAGE_NORTH_EAST = new DungeonCube("village_north_east.cube_structure");
	public static DungeonCube VILLAGE_SOUTH = new DungeonCube("village_south.cube_structure");
	public static DungeonCube VILLAGE_EAST = new DungeonCube("village_east.cube_structure");
	public static DungeonCube VILLAGE_WEST = new DungeonCube("village_west.cube_structure");
	public static DungeonCube VILLAGE_NORTH = new DungeonCube("village_north.cube_structure");
	public static DungeonCube VILLAGE_HOME = new DungeonCube("village_home.cube_structure", DungeonCubeFlag.VILLAGE_HOME);
	public static DungeonCube VILLAGE_PARK = new DungeonCube("village_park.cube_structure");
	public static DungeonCube VILLAGE_NORTH_GATE_WEST_SIDE = new DungeonCube("village_north_gate_west_side.cube_structure");
	public static DungeonCube VILLAGE_NORTH_GATE_EAST_SIDE = new DungeonCube("village_north_gate_east_side.cube_structure");
	public static DungeonCube VILLAGE_SOUTH_GATE_WEST_SIDE = new DungeonCube("village_south_gate_west_side.cube_structure");
	public static DungeonCube VILLAGE_SOUTH_GATE_EAST_SIDE = new DungeonCube("village_south_gate_east_side.cube_structure");
	public static DungeonCube VILLAGE_CENTRAL_WEST_SIDE = new DungeonCube("village_central_west_side.cube_structure");
	public static DungeonCube VILLAGE_CENTRAL_EAST_SIDE = new DungeonCube("village_central_east_side.cube_structure");
	public static DungeonCube VILLAGE_MARKET_WEST = new DungeonCube("village_market.cube_structure", DungeonCubeFlag.MARKET);
	public static DungeonCube VILLAGE_MARKET_EAST = new DungeonCube("village_market_east.cube_structure", DungeonCubeFlag.MARKET);

	public static DungeonCube TUNNEL_EAST_WEST = new DungeonCube("tunnel_east_west.cube_structure");
	public static DungeonCube TUNNEL_SOUTH_NORTH = new DungeonCube("tunnel_south_north.cube_structure");

	public static DungeonCube MONOLITH = new DungeonCube("monolith.cube_structure");
	public static DungeonCube MONOLITH_NE = new DungeonCube("monolith_ne.cube_structure");
	public static DungeonCube MONOLITH_NW = new DungeonCube("monolith_nw.cube_structure");
	public static DungeonCube MONOLITH_SE = new DungeonCube("monolith_se.cube_structure");
	public static DungeonCube MONOLITH_SW = new DungeonCube("monolith_sw.cube_structure");

	public static DungeonCube NOTHING = new DungeonCube("");
	public static DungeonCube UNDEFINED = new DungeonCube("");

	public final String name;
	public boolean isEastWall = false;
	public boolean isWestWall = false;
	public boolean isSouthWall = false;
	public boolean isNorthWall = false;
	public boolean isColumnTop = false;
	public boolean isColumnMiddle = false;
	public boolean isColumnTopOrMiddle = false;
	public boolean isColumnBottomOrMiddle = false;
	public boolean isColumnBottom = false;
	public boolean isStairTop = false;
	public boolean isStairBottom = false;
	public boolean isLibrary = false;
	public boolean isCorral = false;
	public boolean isVillageHome = false;
	public boolean isMarket = false;
	public final byte[] data = new byte[4096];

	DungeonCube(String nameIn, DungeonCubeFlag... flagIn) {
		values.add(this);
		name = nameIn;
		for (DungeonCubeFlag flag : flagIn) {
			switch (flag) {
				case EAST_WALL :
					isEastWall = true;
					break;
				case WEST_WALL :
					isWestWall = true;
					break;
				case SOUTH_WALL :
					isSouthWall = true;
					break;
				case NORTH_WALL :
					isNorthWall = true;
					break;
				case COLUMN_TOP :
					isColumnTop = true;
					isColumnTopOrMiddle = true;
					break;
				case COLUMN_MIDDLE :
					isColumnMiddle = true;
					isColumnTopOrMiddle = true;
					isColumnBottomOrMiddle = true;
					break;
				case COLUMN_BOTTOM :
					isColumnBottom = true;
					isColumnBottomOrMiddle = true;
					break;
				case STAIR_TOP :
					isStairTop = true;
					break;
				case STAIR_BOTTOM :
					isStairBottom = true;
					break;
				case LIBRARY :
					isLibrary = true;
					break;
				case CORRAL :
					isCorral = true;
					break;
				case MARKET :
					isMarket = true;
					break;
				case VILLAGE_HOME :
					isVillageHome = true;
					break;
			}
		}
	}

	public void load(World world) throws IOException {
		InputStream stream = LabyrinthMod.getResourceInputStream(world, new ResourceLocation("labyrinth", "cubes/" + name));
		stream.read(data);
		stream.close();
	}

	public void placeCube(ICube cube, WorldServer world, DungeonLayer layer) {
		placeCube(cube, data, layer.mapping, world, this.isLibrary ? layer.libraryLootTable : layer.regularLootTable, false, this.isVillageHome);
		layer.decorate(cube, this);
		world.addScheduledTask(() -> {
			layer.doInitialBlockLighting((ICubicWorld) world, cube, this);
		});
	}

	public static void placeCube(ICube cube, byte[] data, IBlockState[] mapping, WorldServer world, String lootTable, boolean use255, boolean isVillage) {
		Village currentVillage = null;
		if (isVillage)
			currentVillage = getOrCreateVillage(cube.getCoords(), world);

		CubePos pos = cube.getCoords();
		ExtendedBlockStorage cstorage = cube.getStorage();
		if (cstorage == null) {
			cube.setBlockState(pos.getCenterBlockPos(), Blocks.STONE.getDefaultState());
			cstorage = cube.getStorage();
		}
		List<BlockPos> lightUpdateQueue = new ArrayList<BlockPos>();
		MutableBlockPos bpos = new BlockPos.MutableBlockPos();
		for (int index = 0; index < data.length; index++) {
			int dx = index >>> 8;
			int dy = (index >>> 4) & 15;
			int dz = index & 15;
			int bstate = Byte.toUnsignedInt(data[index]);
			if (!use255 && bstate == 255)
				continue;
			bpos.setPos(pos.getMinBlockX() + dx, pos.getMinBlockY() + dy, pos.getMinBlockZ() + dz);
			IBlockState oldState = cstorage.get(dx, dy, dz);
			IBlockState newState = mapping[bstate];
			cstorage.set(dx, dy, dz, newState);
			if (oldState.getLightValue(world, bpos) != newState.getLightValue(world, bpos)) {
				lightUpdateQueue.add(bpos.toImmutable());
			}
			int newOpacity = newState.getLightOpacity(world, bpos);
			if (oldState.getLightOpacity(world, bpos) != newOpacity)
				cube.getColumn().getOpacityIndex().onOpacityChange(dx, pos.getMinBlockY() + dy, dz, newOpacity);
			if (newState.getBlock() instanceof BlockChest) {
				TileEntityChest chest = new TileEntityChest();
				NBTTagCompound compound = new NBTTagCompound();
				compound.setString("LootTable", lootTable);
				chest.readFromNBT(compound);
				chest.markDirty();
				chest.setPos(bpos);
				world.setTileEntity(bpos, chest);
			} else if (isVillage && newState.getBlock() instanceof BlockDoor && newState.getMaterial() == Material.WOOD) {
				currentVillage.addVillageDoorInfo(new VillageDoorInfo(bpos.toImmutable(), 8 - dx, 8 - dz, 0));
			}
		}
		/*
		 * world.addScheduledTask(() -> { for(BlockPos luPos:lightUpdateQueue)
		 * world.checkLight(luPos); });
		 */
	}

	public static Village getOrCreateVillage(CubePos pos, WorldServer world) {
		Village currentVillage = null;
		for (Village village : world.villageCollection.getVillageList()) {
			if (village.isBlockPosWithinSqVillageRadius(pos.getCenterBlockPos())) {
				currentVillage = village;
				break;
			}
		}
		if (currentVillage == null) {
			currentVillage = new UndergroundVillage(world);
			world.villageCollection.getVillageList().add(currentVillage);
		}
		return currentVillage;
	}

	public enum DungeonCubeFlag {
		EAST_WALL,
		WEST_WALL,
		SOUTH_WALL,
		NORTH_WALL,
		COLUMN_TOP,
		COLUMN_MIDDLE,
		COLUMN_BOTTOM,
		STAIR_TOP,
		STAIR_BOTTOM,
		LIBRARY,
		CORRAL,
		VILLAGE_HOME,
		MARKET;
	}

	public static int getIndex(int x, int y, int z) {
		return x << 8 | y << 4 | z;
	}

	public static List<DungeonCube> values() {
		return values;
	}
}
