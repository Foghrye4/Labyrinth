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

public enum DungeonCube {
		COLUMN_CEIL("column_ceil.cube_structure", DungeonCubeFlag.COLUMN_TOP), 
		COLUMN_MIDDLE("column_middle.cube_structure", DungeonCubeFlag.COLUMN_MIDDLE), 
		COLUMN_FLOOR("column_floor.cube_structure", DungeonCubeFlag.COLUMN_BOTTOM), 
		STAIR_TOP_WITH_STAIR("column_ceil_with_south_mini_stair.cube_structure", DungeonCubeFlag.STAIR_TOP), 
		STAIR_TOP("stair_ceil.cube_structure",	DungeonCubeFlag.STAIR_TOP), 
		STAIR_TOP_CEILINGLESS("stair_top_ceilingless.cube_structure", DungeonCubeFlag.STAIR_TOP, DungeonCubeFlag.COLUMN_BOTTOM), 
		STAIR_MIDDLE("stair_middle.cube_structure", DungeonCubeFlag.STAIR_TOP, DungeonCubeFlag.STAIR_BOTTOM), 
		STAIR_FLOOR("stair_floor.cube_structure", DungeonCubeFlag.STAIR_BOTTOM),
		COLUMN_FLOOR_CEIL("column_floor_ceil.cube_structure"), 
		COLUMN_EAST_BORDER("column_east_border.cube_structure"),
		COLUMN_NORTH_BORDER("column_north_border.cube_structure"), 
		COLUMN_SOUTH_BORDER("column_south_border.cube_structure"), 
		COLUMN_WEST_BORDER(	"column_west_border.cube_structure"), 
		EAST_BORDER_WITH_WALL_SOUTH_NORTH("east_border_with_wall_south_north.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.NORTH_WALL),
		NORTH_BORDER_WITH_WALL_EAST_WEST("north_border_with_wall_east_west.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.WEST_WALL), 
		SOUTH_BORDER_WITH_WALL_EAST_WEST("south_border_with_wall_east_west.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.WEST_WALL), 
		WEST_BORDER_WITH_WALL_SOUTH_NORTH("west_border_with_wall_south_north.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.NORTH_WALL), 
		
		EAST_BORDER_WITH_WALL_SOUTH_WEST("east_border_with_wall_south_west.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.WEST_WALL),
		NORTH_BORDER_WITH_WALL_EAST_SOUTH("north_border_with_wall_east_south.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.EAST_WALL), 
		SOUTH_BORDER_WITH_WALL_WEST_NORTH("south_border_with_wall_west_north.cube_structure", DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL), 
		WEST_BORDER_WITH_WALL_NORTH_EAST("west_border_with_wall_north_east.cube_structure", DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.EAST_WALL), 

		EAST_BORDER_WITH_WALL_WEST_NORTH("east_border_with_wall_west_north.cube_structure", DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL),
		NORTH_BORDER_WITH_WALL_SOUTH_WEST("north_border_with_wall_south_west.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.WEST_WALL), 
		SOUTH_BORDER_WITH_WALL_NORTH_EAST("south_border_with_wall_north_east.cube_structure", DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.EAST_WALL), 
		WEST_BORDER_WITH_WALL_EAST_SOUTH("west_border_with_wall_east_south.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.EAST_WALL), 

		NORTH_EAST_BORDER("north_east_border.cube_structure"),
		WEST_NORTH_BORDER("west_north_border.cube_structure"), 
		EAST_SOUTH_BORDER("east_south_border.cube_structure"), 
		SOUTH_WEST_BORDER("south_west_border.cube_structure"), 

		NORTH_BORDER_COLUMN_FLOOR("north_border_column_floor.cube_structure", DungeonCubeFlag.COLUMN_BOTTOM), 
		SOUTH_BORDER_COLUMN_FLOOR("south_border_column_floor.cube_structure", DungeonCubeFlag.COLUMN_BOTTOM), 
		WEST_BORDER_COLUMN_FLOOR("west_border_column_floor.cube_structure", DungeonCubeFlag.COLUMN_BOTTOM), 
		EAST_BORDER_COLUMN_FLOOR("east_border_column_floor.cube_structure", DungeonCubeFlag.COLUMN_BOTTOM), 
		
		WALL_EAST_NORTH_SOUTH("wall_east_north_south.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.SOUTH_WALL),
		WALL_SOUTH_EAST_WEST("wall_south_east_west.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.WEST_WALL),
		WALL_WEST_SOUTH_NORTH("wall_west_south_north.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL),
		WALL_NORTH_WEST_EAST("wall_north_west_east.cube_structure", DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.EAST_WALL),
		
		EAST_BORDER_WITH_WALLS("east_border_with_walls.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL),
		NORTH_BORDER_WITH_WALLS("north_border_with_walls.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.EAST_WALL), 
		SOUTH_BORDER_WITH_WALLS("south_border_with_walls.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.EAST_WALL), 
		WEST_BORDER_WITH_WALLS("west_border_with_walls.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.EAST_WALL), 
		STAIR_FLOOR_W_ROOM_OT_NORTH_EAST("stair_floor_w_room_ot_north_east.cube_structure",DungeonCubeFlag.STAIR_BOTTOM), 
		STAIR_FLOOR_W_ROOM_OT_NORTH_WEST("stair_floor_w_room_ot_north_west.cube_structure",DungeonCubeFlag.STAIR_BOTTOM), 
		STAIR_FLOOR_W_ROOM_OT_SOUTH_EAST("stair_floor_w_room_ot_south_east.cube_structure",DungeonCubeFlag.STAIR_BOTTOM), 
		STAIR_FLOOR_W_ROOM_OT_SOUTH_WEST("stair_floor_w_room_ot_south_west.cube_structure",DungeonCubeFlag.STAIR_BOTTOM), 
		STAIR_TOP_W_ROOM_OT_NORTH_EAST("stair_ceil_w_room_ot_north_east.cube_structure",DungeonCubeFlag.STAIR_TOP), 
		STAIR_TOP_W_ROOM_OT_NORTH_WEST("stair_ceil_w_room_ot_north_west.cube_structure",DungeonCubeFlag.STAIR_TOP), 
		STAIR_TOP_W_ROOM_OT_SOUTH_EAST("stair_ceil_w_room_ot_south_east.cube_structure",DungeonCubeFlag.STAIR_TOP),
		STAIR_TOP_W_ROOM_OT_SOUTH_WEST("stair_ceil_w_room_ot_south_west.cube_structure", DungeonCubeFlag.STAIR_TOP),		
		WORKSHOP("workshop_south_door.cube_structure"),
		LIBRARY("library.cube_structure", DungeonCubeFlag.LIBRARY),
		WALL_X("wall_x.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.SOUTH_WALL),
		WALL_EAST_NORTH_BARS("wall_east_north_bars.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.NORTH_WALL),
		WALL_EAST_NORTH("wall_east_north.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.NORTH_WALL),
		WALL_EAST_SOUTH_BARS("wall_east_south_bars.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.SOUTH_WALL),
		WALL_EAST_SOUTH("wall_east_south.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.SOUTH_WALL),
		WALL_SOUTH_NORTH("wall_south_north.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.NORTH_WALL),
		WALL_SOUTH_NORTH_DOOR("wall_south_north_door.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.NORTH_WALL),
		WALL_WEST_EAST_BARS("wall_west_east_bars.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.EAST_WALL),
		WALL_WEST_EAST("wall_west_east.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.EAST_WALL),
		WALL_WEST_NORTH_BARS("wall_west_north_bars.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.NORTH_WALL),
		WALL_WEST_NORTH("wall_west_north.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.NORTH_WALL),
		WALL_WEST_SOUTH_BARS("wall_west_south_bars.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.SOUTH_WALL),
		WALL_WEST_SOUTH("wall_west_south.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.SOUTH_WALL),

		//Lava variants
		COLUMN_FLOOR_LAVA("column_floor_lava.cube_structure", DungeonCubeFlag.COLUMN_BOTTOM), 
		STAIR_TOP_LAVA("stair_ceil_lava.cube_structure",	DungeonCubeFlag.STAIR_TOP), 
		STAIR_TOP_CEILINGLESS_LAVA("stair_top_ceilingless_lava.cube_structure", DungeonCubeFlag.STAIR_TOP, DungeonCubeFlag.COLUMN_BOTTOM), 
		STAIR_MIDDLE_LAVA("stair_middle_lava.cube_structure", DungeonCubeFlag.STAIR_TOP, DungeonCubeFlag.STAIR_BOTTOM), 
		STAIR_FLOOR_LAVA("stair_floor_lava.cube_structure", DungeonCubeFlag.STAIR_BOTTOM),
		COLUMN_FLOOR_CEIL_LAVA("column_floor_ceil_lava.cube_structure"), 
		COLUMN_EAST_BORDER_LAVA("column_east_border_lava.cube_structure"),
		COLUMN_NORTH_BORDER_LAVA("column_north_border_lava.cube_structure"), 
		COLUMN_SOUTH_BORDER_LAVA("column_south_border_lava.cube_structure"), 
		COLUMN_WEST_BORDER_LAVA("column_west_border_lava.cube_structure"), 
		EAST_BORDER_WITH_WALL_SOUTH_NORTH_LAVA("east_border_with_wall_south_north_lava.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.NORTH_WALL),
		NORTH_BORDER_WITH_WALL_EAST_WEST_LAVA("north_border_with_wall_east_west_lava.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.WEST_WALL), 
		SOUTH_BORDER_WITH_WALL_EAST_WEST_LAVA("south_border_with_wall_west_east_lava.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.WEST_WALL), 
		WEST_BORDER_WITH_WALL_SOUTH_NORTH_LAVA("west_border_with_wall_north_south_lava.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.NORTH_WALL), 
		
		EAST_BORDER_WITH_WALL_SOUTH_WEST_LAVA("east_border_with_wall_south_west_lava.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.WEST_WALL),
		NORTH_BORDER_WITH_WALL_EAST_SOUTH_LAVA("north_border_with_wall_east_south_lava.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.EAST_WALL), 
		SOUTH_BORDER_WITH_WALL_WEST_NORTH_LAVA("south_border_with_wall_west_north_lava.cube_structure", DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL), 
		WEST_BORDER_WITH_WALL_NORTH_EAST_LAVA("west_border_with_wall_north_east_lava.cube_structure", DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.EAST_WALL), 

		EAST_BORDER_WITH_WALL_WEST_NORTH_LAVA("east_border_with_wall_west_north_lava.cube_structure", DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL),
		NORTH_BORDER_WITH_WALL_SOUTH_WEST_LAVA("north_border_with_wall_south_west_lava.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.WEST_WALL), 
		SOUTH_BORDER_WITH_WALL_NORTH_EAST_LAVA("south_border_with_wall_north_east_lava.cube_structure", DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.EAST_WALL), 
		WEST_BORDER_WITH_WALL_EAST_SOUTH_LAVA("west_border_with_wall_east_south_lava.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.EAST_WALL), 

		NORTH_EAST_BORDER_LAVA("north_east_border_lava.cube_structure"),
		WEST_NORTH_BORDER_LAVA("west_north_border_lava.cube_structure"), 
		EAST_SOUTH_BORDER_LAVA("east_south_border_lava.cube_structure"), 
		SOUTH_WEST_BORDER_LAVA("south_west_border_lava.cube_structure"), 

		NORTH_BORDER_COLUMN_FLOOR_LAVA("north_border_column_floor_lava.cube_structure", DungeonCubeFlag.COLUMN_BOTTOM), 
		SOUTH_BORDER_COLUMN_FLOOR_LAVA("south_border_column_floor_lava.cube_structure", DungeonCubeFlag.COLUMN_BOTTOM), 
		WEST_BORDER_COLUMN_FLOOR_LAVA("west_border_column_floor_lava.cube_structure", DungeonCubeFlag.COLUMN_BOTTOM), 
		EAST_BORDER_COLUMN_FLOOR_LAVA("east_border_column_floor_lava.cube_structure", DungeonCubeFlag.COLUMN_BOTTOM), 
		
		WALL_EAST_LAVA("wall_east_lava.cube_structure", DungeonCubeFlag.EAST_WALL),
		WALL_SOUTH_LAVA("wall_south_lava.cube_structure", DungeonCubeFlag.SOUTH_WALL),
		WALL_NORTH_LAVA("wall_north_lava.cube_structure", DungeonCubeFlag.NORTH_WALL),
		WALL_WEST_LAVA("wall_west_lava.cube_structure", DungeonCubeFlag.WEST_WALL),
		
		WALL_EAST_NORTH_SOUTH_LAVA("wall_north_south_east_lava.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.SOUTH_WALL),
		WALL_SOUTH_EAST_WEST_LAVA("wall_east_west_south_lava.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.WEST_WALL),
		WALL_WEST_SOUTH_NORTH_LAVA("wall_south_north_west_lava.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL),
		WALL_NORTH_WEST_EAST_LAVA("wall_west_east_north_lava.cube_structure", DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.EAST_WALL),
		
		EAST_BORDER_WITH_WALLS_LAVA("east_border_with_walls_lava.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL),
		NORTH_BORDER_WITH_WALLS_LAVA("north_border_with_walls_lava.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.EAST_WALL), 
		SOUTH_BORDER_WITH_WALLS_LAVA("south_border_with_walls_lava.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.EAST_WALL), 
		WEST_BORDER_WITH_WALLS_LAVA("west_border_with_walls_lava.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.EAST_WALL), 
		STAIR_FLOOR_W_ROOM_OT_NORTH_EAST_LAVA("stair_floor_w_room_ot_north_east_lava.cube_structure",DungeonCubeFlag.STAIR_BOTTOM), 
		STAIR_FLOOR_W_ROOM_OT_NORTH_WEST_LAVA("stair_floor_w_room_ot_north_west_lava.cube_structure",DungeonCubeFlag.STAIR_BOTTOM), 
		STAIR_FLOOR_W_ROOM_OT_SOUTH_EAST_LAVA("stair_floor_w_room_ot_south_east_lava.cube_structure",DungeonCubeFlag.STAIR_BOTTOM), 
		STAIR_FLOOR_W_ROOM_OT_SOUTH_WEST_LAVA("stair_floor_w_room_ot_south_west_lava.cube_structure",DungeonCubeFlag.STAIR_BOTTOM), 
		STAIR_TOP_W_ROOM_OT_NORTH_EAST_LAVA("stair_ceil_w_room_ot_north_east_lava.cube_structure",DungeonCubeFlag.STAIR_TOP), 
		STAIR_TOP_W_ROOM_OT_NORTH_WEST_LAVA("stair_ceil_w_room_ot_north_west_lava.cube_structure",DungeonCubeFlag.STAIR_TOP), 
		STAIR_TOP_W_ROOM_OT_SOUTH_EAST_LAVA("stair_ceil_w_room_ot_south_east_lava.cube_structure",DungeonCubeFlag.STAIR_TOP),
		STAIR_TOP_W_ROOM_OT_SOUTH_WEST_LAVA("stair_ceil_w_room_ot_south_west_lava.cube_structure", DungeonCubeFlag.STAIR_TOP),
		WORKSHOP_LAVA("workshop_lava.cube_structure"),
		LIBRARY_LAVA("library_lava.cube_structure", DungeonCubeFlag.LIBRARY),
		WALL_X_LAVA("wall_x_lava.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.SOUTH_WALL),
		WALL_EAST_NORTH_LAVA("wall_north_east_lava.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.NORTH_WALL),
		WALL_EAST_SOUTH_LAVA("wall_east_south_lava.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.SOUTH_WALL),
		WALL_SOUTH_NORTH_LAVA("wall_north_south_lava.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.NORTH_WALL),
		WALL_WEST_EAST_LAVA("wall_west_east_lava.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.EAST_WALL),
		WALL_WEST_NORTH_LAVA("wall_west_north_lava.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.NORTH_WALL),
		WALL_WEST_SOUTH_LAVA("wall_south_west_lava.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.SOUTH_WALL),

		//Claustrophobic
		X_ROADS("x_roads.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.SOUTH_WALL),
		X_ROADS_HIDDEN_ROOM("x_roads_hidden_room.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.LIBRARY),
		LAVA_ROOM("lava_room.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.SOUTH_WALL),
		LAVA_ROOM_WORKSHOP("lava_room_workshop.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.SOUTH_WALL),
		LADDER_CEIL("ladder_ceil.cube_structure", DungeonCubeFlag.STAIR_TOP, DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.SOUTH_WALL),
		LADDER_MIDDLE("ladder_middle.cube_structure", DungeonCubeFlag.STAIR_TOP, DungeonCubeFlag.STAIR_BOTTOM),
		LADDER_FLOOR("ladder_floor.cube_structure", DungeonCubeFlag.STAIR_BOTTOM, DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.SOUTH_WALL),
		LADDER_FLOOR_TRAP("ladder_floor_trap.cube_structure", DungeonCubeFlag.STAIR_BOTTOM, DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.SOUTH_WALL),
		ROAD_SOUTH_NORTH("road_south_north.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.NORTH_WALL),
		ROAD_SOUTH_NORTH_TRAP("road_south_north_trap.cube_structure", DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.NORTH_WALL),
		ROAD_EAST_WEST("road_east_west.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.WEST_WALL),
		ROAD_EAST_WEST_TRAP("road_east_west_trap.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.WEST_WALL),
		ROAD_WEST_NORTH("road_west_north.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.NORTH_WALL),
		ROAD_WEST_NORTH_TRAP("road_west_north_trap.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.NORTH_WALL),
		ROAD_SOUTH_WEST("road_south_west.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.SOUTH_WALL),
		ROAD_NORTH_EAST("road_north_east.cube_structure", DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.EAST_WALL),
		ROAD_EAST_SOUTH("road_east_south.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.SOUTH_WALL),
		ROAD_WEST_NORTH_EAST("road_west_north_east.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.EAST_WALL),
		ROAD_SOUTH_WEST_NORTH("road_south_west_north.cube_structure", DungeonCubeFlag.WEST_WALL, DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.SOUTH_WALL),
		ROAD_NORTH_EAST_SOUTH("road_north_east_south.cube_structure", DungeonCubeFlag.NORTH_WALL, DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.SOUTH_WALL),
		ROAD_EAST_SOUTH_WEST("road_east_south_west.cube_structure", DungeonCubeFlag.EAST_WALL, DungeonCubeFlag.SOUTH_WALL, DungeonCubeFlag.WEST_WALL),
		
		//Village
		VILLAGE_SOUTH_WEST("village_south_west.cube_structure", DungeonCubeFlag.CORRAL),
		VILLAGE_SOUTH_EAST("village_south_east.cube_structure"),
		VILLAGE_NORTH_WEST("village_north_west.cube_structure"),
		VILLAGE_NORTH_EAST("village_north_east.cube_structure"),
		VILLAGE_SOUTH("village_south.cube_structure"),
		VILLAGE_EAST("village_east.cube_structure"),
		VILLAGE_WEST("village_west.cube_structure"),
		VILLAGE_NORTH("village_north.cube_structure"),
		VILLAGE_HOME("village_home.cube_structure", DungeonCubeFlag.VILLAGE_HOME),
		VILLAGE_PARK("village_park.cube_structure"),
		VILLAGE_NORTH_GATE_WEST_SIDE("village_north_gate_west_side.cube_structure"),
		VILLAGE_NORTH_GATE_EAST_SIDE("village_north_gate_east_side.cube_structure"),
		VILLAGE_SOUTH_GATE_WEST_SIDE("village_south_gate_west_side.cube_structure"),
		VILLAGE_SOUTH_GATE_EAST_SIDE("village_south_gate_east_side.cube_structure"),
		VILLAGE_CENTRAL_WEST_SIDE("village_central_west_side.cube_structure"),
		VILLAGE_CENTRAL_EAST_SIDE("village_central_east_side.cube_structure"),
		VILLAGE_MARKET_WEST("village_market.cube_structure", DungeonCubeFlag.MARKET),
		VILLAGE_MARKET_EAST("village_market_east.cube_structure", DungeonCubeFlag.MARKET),
		
		//Tunnel
		TUNNEL_EAST_WEST("tunnel_east_west.cube_structure"),
		TUNNEL_SOUTH_NORTH("tunnel_south_north.cube_structure"),
		
		NOTHING(""),
		UNDEFINED("");

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
			name = nameIn;
			for (DungeonCubeFlag flag : flagIn) {
				switch (flag) {
				case EAST_WALL:
					isEastWall = true;
					break;
				case WEST_WALL:
					isWestWall = true;
					break;
				case SOUTH_WALL:
					isSouthWall = true;
					break;
				case NORTH_WALL:
					isNorthWall = true;
					break;
				case COLUMN_TOP:
					isColumnTop = true;
					isColumnTopOrMiddle = true;
					break;
				case COLUMN_MIDDLE:
					isColumnMiddle = true;
					isColumnTopOrMiddle = true;
					isColumnBottomOrMiddle = true;
					break;
				case COLUMN_BOTTOM:
					isColumnBottom = true;
					isColumnBottomOrMiddle = true;
					break;
				case STAIR_TOP:
					isStairTop = true;
					break;
				case STAIR_BOTTOM:
					isStairBottom = true;
					break;
				case LIBRARY:
					isLibrary = true;
					break;
				case CORRAL:
					isCorral = true;
					break;
				case MARKET:
					isMarket = true;
					break;
				case VILLAGE_HOME:
					isVillageHome = true;
					break;
				}
			}
		}

	public void load(World world) throws IOException {
		InputStream stream  = LabyrinthMod.getResourceInputStream(world, new ResourceLocation("labyrinth", "cubes/" + name));
		stream.read(data);
		stream.close();
	}
	
	public void placeCube(ICube cube, WorldServer world, DungeonLayer layer) {
		placeCube(cube, data, layer.mapping, world, this.isLibrary ? layer.libraryLootTable : layer.regularLootTable, false, this.isVillageHome);
		world.addScheduledTask(() -> {
			layer.doInitialBlockLighting((ICubicWorld) world, cube, this);
		});
	}
	
	public static void placeCube(ICube cube, byte[] data, IBlockState[] mapping,  WorldServer world, String lootTable, boolean use255, boolean isVillage) {
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
			if(oldState.getLightValue(world, bpos)!= newState.getLightValue(world, bpos)) {
				lightUpdateQueue.add(bpos.toImmutable());
			}
			int newOpacity = newState.getLightOpacity(world, bpos);
			if(oldState.getLightOpacity(world, bpos)!=newOpacity)
				cube.getColumn().getOpacityIndex().onOpacityChange(dx, pos.getMinBlockY() + dy, dz, newOpacity);
			if (newState.getBlock() instanceof BlockChest) {
				TileEntityChest chest = new TileEntityChest();
				NBTTagCompound compound = new NBTTagCompound();
				compound.setString("LootTable", lootTable);
				chest.readFromNBT(compound);
				chest.markDirty();
				chest.setPos(bpos);
				world.setTileEntity(bpos, chest);
			}
			else if(isVillage && newState.getBlock() instanceof BlockDoor && newState.getMaterial()  == Material.WOOD) {
				currentVillage.addVillageDoorInfo(new VillageDoorInfo(bpos.toImmutable(), 8 - dx, 8 - dz, 0));
			}
		}
/*		world.addScheduledTask(() -> {
			for(BlockPos luPos:lightUpdateQueue)
				world.checkLight(luPos);
		});*/
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
}
