package labyrinth.worldgen;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public enum DungeonCube {
		COLUMN_CEIL("column_ceil.cube_structure", DungeonCubeFlag.COLUMN_TOP), 
		COLUMN_MIDDLE("column_middle.cube_structure", DungeonCubeFlag.COLUMN_MIDDLE), 
		COLUMN_FLOOR("column_floor.cube_structure", DungeonCubeFlag.COLUMN_BOTTOM), 
		COLUMN_TOP_WITH_STAIR("column_ceil_with_south_mini_stair.cube_structure", DungeonCubeFlag.COLUMN_TOP), 
		STAIR_TOP("stair_ceil.cube_structure",	DungeonCubeFlag.STAIR_TOP), 
		STAIR_TOP_CEILINGLESS("stair_top_ceilingless.cube_structure", DungeonCubeFlag.STAIR_TOP), 
		STAIR_MIDDLE("stair_middle.cube_structure", DungeonCubeFlag.STAIR_TOP, DungeonCubeFlag.STAIR_BOTTOM), 
		STAIR_FLOOR("stair_floor.cube_structure", DungeonCubeFlag.STAIR_BOTTOM),
		COLUMN_FLOOR_CEIL("column_floor_ceil.cube_structure"), 
		COLUMN_EAST_BORDER("column_east_border.cube_structure"),
		COLUMN_NORTH_BORDER("column_north_border.cube_structure"), 
		COLUMN_SOUTH_BORDER("column_south_border.cube_structure"), 
		COLUMN_WEST_BORDER(	"column_west_border.cube_structure"), 
		ROOM_OT_NORTH_EAST(	"room_ot_north_east.cube_structure"), // 9
		ROOM_OT_NORTH_WEST("room_ot_north_west.cube_structure"),
		ROOM_OT_SOUTH_EAST("room_ot_south_east.cube_structure"),
		ROOM_OT_SOUTH_WEST("room_ot_south_west.cube_structure"),
		STAIR_FLOOR_W_ROOM_OT_NORTH_EAST("stair_floor_w_room_ot_north_east.cube_structure",DungeonCubeFlag.STAIR_BOTTOM), 
		STAIR_FLOOR_W_ROOM_OT_NORTH_WEST("stair_floor_w_room_ot_north_west.cube_structure",DungeonCubeFlag.STAIR_BOTTOM), 
		STAIR_FLOOR_W_ROOM_OT_SOUTH_EAST("stair_floor_w_room_ot_south_east.cube_structure",DungeonCubeFlag.STAIR_BOTTOM), 
		STAIR_FLOOR_W_ROOM_OT_SOUTH_WEST("stair_floor_w_room_ot_south_west.cube_structure",DungeonCubeFlag.STAIR_BOTTOM), 
		STAIR_TOP_W_ROOM_OT_NORTH_EAST("stair_ceil_w_room_ot_north_east.cube_structure",DungeonCubeFlag.STAIR_TOP), 
		STAIR_TOP_W_ROOM_OT_NORTH_WEST("stair_ceil_w_room_ot_north_west.cube_structure",DungeonCubeFlag.STAIR_TOP), 
		STAIR_TOP_W_ROOM_OT_SOUTH_EAST("stair_ceil_w_room_ot_south_east.cube_structure",DungeonCubeFlag.STAIR_TOP),
		STAIR_TOP_W_ROOM_OT_SOUTH_WEST("stair_ceil_w_room_ot_south_west.cube_structure", DungeonCubeFlag.STAIR_TOP),		
		NODE("node.cube_structure"),
		WORKSHOP("workshop_south_door.cube_structure"),
		LIBRARY("library.cube_structure"),
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
		NOTHING(""),
		UNDEFINED("");

		public final String name;
		boolean isEastWall = false;
		boolean isWestWall = false;
		boolean isSouthWall = false;
		boolean isNorthWall = false;
		boolean isColumnTop = false;
		boolean isColumnMiddle = false;
		boolean isColumnTopOrMiddle = false;
		boolean isColumnBottomOrMiddle = false;
		boolean isColumnBottom = false;
		boolean isStairTop = false;
		boolean isStairBottom = false;
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
				}
			}
		}

		void load() throws IOException {
			Minecraft.getMinecraft().getResourceManager()
					.getResource(new ResourceLocation("labyrinth", "cubes/" + name)).getInputStream().read(data);
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
			STAIR_BOTTOM;
		}
}
