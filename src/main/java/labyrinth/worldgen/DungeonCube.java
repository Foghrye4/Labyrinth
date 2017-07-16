package labyrinth.worldgen;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import labyrinth.LabyrinthMod;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.NibbleArray;

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
		public final byte[] lightData = new byte[2048];

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
			LabyrinthMod.proxy.getResourceInputStream(new ResourceLocation("labyrinth", "cubes/" + name)).read(data);
		}
		
		void precalculateLight() {
			NibbleArray lightNibbleArray = new NibbleArray(lightData);
			Set<BlockPos> pointsOfInterest = new HashSet<BlockPos>();
			for(int index=0;index<data.length;index++) {
				if((Byte.toUnsignedInt(data[index])>=153 && Byte.toUnsignedInt(data[index])<=157) ||Byte.toUnsignedInt(data[index])==16	){
					int dx = index >>> 8;
					int dy = (index >>> 4) & 15;
					int dz = index & 15;
					pointsOfInterest.add(new BlockPos(dx,dy,dz));
				}
			}
			for(BlockPos lightPos:pointsOfInterest){
				setLight(lightPos, lightNibbleArray, 14);
			}
		}

		private void setLight(BlockPos lightPos, NibbleArray lightNibbleArray, int lightValue) {
			int index = lightPos.getX()<<8|lightPos.getY()<<4|lightPos.getZ();
			if(index<0 || 
					index>=4096 || 
					lightValue<=0 ||
					lightPos.getX() < 0 ||
					lightPos.getY() < 0 ||
					lightPos.getZ() < 0 ||
					lightPos.getX() > 15 ||
					lightPos.getY() > 15 ||
					lightPos.getZ() > 15){
				return;
			}
			int blockStateNum = Byte.toUnsignedInt(data[index]);
			if((blockStateNum>=1 && blockStateNum<=11) || (blockStateNum>=19 && blockStateNum<=54)){
				return;
			}
			if(lightNibbleArray.get(lightPos.getX(), lightPos.getY(), lightPos.getZ()) < lightValue){
				lightNibbleArray.set(lightPos.getX(), lightPos.getY(), lightPos.getZ(),lightValue);
				setLight(lightPos.up(), lightNibbleArray, lightValue-1);
				setLight(lightPos.down(), lightNibbleArray, lightValue-1);
				setLight(lightPos.north(), lightNibbleArray, lightValue-1);
				setLight(lightPos.south(), lightNibbleArray, lightValue-1);
				setLight(lightPos.west(), lightNibbleArray, lightValue-1);
				setLight(lightPos.east(), lightNibbleArray, lightValue-1);
			}
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
