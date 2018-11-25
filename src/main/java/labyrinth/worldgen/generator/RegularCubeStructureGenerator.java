package labyrinth.worldgen.generator;

import java.util.Random;

import io.github.opencubicchunks.cubicchunks.api.util.CubePos;
import labyrinth.worldgen.DungeonCube;
import labyrinth.worldgen.ICubeStructureGenerator;
import labyrinth.worldgen.LabyrinthWorldGen;
import net.minecraft.world.World;

public class RegularCubeStructureGenerator implements ICubeStructureGenerator {
	
	private LabyrinthWorldGen generator;
	protected final Random random = new Random();
	private DungeonCube[] randomDungeonsArray = new DungeonCube[]{
			DungeonCube.COLUMN_CEIL,
			DungeonCube.COLUMN_MIDDLE,
			DungeonCube.COLUMN_FLOOR,
			DungeonCube.STAIR_FLOOR,
			DungeonCube.STAIR_MIDDLE,
			DungeonCube.STAIR_MIDDLE,
			DungeonCube.LIBRARY,
			DungeonCube.WORKSHOP,
			DungeonCube.WALL_EAST_NORTH_BARS,
			DungeonCube.WALL_EAST_SOUTH_BARS,
			DungeonCube.WALL_SOUTH_NORTH_DOOR,
			DungeonCube.WALL_WEST_EAST_BARS,
			DungeonCube.WALL_WEST_NORTH_BARS,
			DungeonCube.WALL_WEST_SOUTH_BARS,
			DungeonCube.NOTHING, DungeonCube.NOTHING, DungeonCube.NOTHING,
			DungeonCube.NOTHING, DungeonCube.NOTHING, DungeonCube.NOTHING,};
	
	private DungeonCube[] randomDungeonsSafeUp = new DungeonCube[]{
			DungeonCube.COLUMN_FLOOR,
			DungeonCube.STAIR_FLOOR,
			DungeonCube.LIBRARY,
			DungeonCube.WORKSHOP,
			DungeonCube.WALL_EAST_NORTH_BARS,
			DungeonCube.WALL_EAST_SOUTH_BARS,
			DungeonCube.WALL_SOUTH_NORTH_DOOR,
			DungeonCube.WALL_WEST_EAST_BARS,
			DungeonCube.WALL_WEST_NORTH_BARS,
			DungeonCube.WALL_WEST_SOUTH_BARS,
			DungeonCube.NOTHING, DungeonCube.NOTHING, DungeonCube.NOTHING,
			DungeonCube.NOTHING, DungeonCube.NOTHING, DungeonCube.NOTHING,};
	
	private DungeonCube[] randomDungeonsSafeDown = new DungeonCube[]{
			DungeonCube.COLUMN_CEIL,
			DungeonCube.LIBRARY,
			DungeonCube.WORKSHOP,
			DungeonCube.WALL_EAST_NORTH_BARS,
			DungeonCube.WALL_EAST_SOUTH_BARS,
			DungeonCube.WALL_SOUTH_NORTH_DOOR,
			DungeonCube.WALL_WEST_EAST_BARS,
			DungeonCube.WALL_WEST_NORTH_BARS,
			DungeonCube.WALL_WEST_SOUTH_BARS,
			DungeonCube.NOTHING, DungeonCube.NOTHING, DungeonCube.NOTHING,
			DungeonCube.NOTHING, DungeonCube.NOTHING, DungeonCube.NOTHING,};

	
	public RegularCubeStructureGenerator(LabyrinthWorldGen generatorIn){
		generator = generatorIn;
	}
	
	public boolean isAnchorPoint(CubePos cpos) {
		return (cpos.getX() & 1 | cpos.getZ() & 1 | cpos.getY() + cpos.getX() / 2 + cpos.getZ() / 2 & 1) == 0;
	}

	@Override
	public DungeonCube getDungeonCubeType(CubePos cpos, World world, LabyrinthWorldGen level) {
		long hash = 3;
		hash = 41 * hash + world.getSeed();
		hash = 41 * hash + cpos.getX();
		hash = 41 * hash + cpos.getY();
		long seed = 41 * hash + cpos.getZ();
		random.setSeed(seed);
		int typedefiner = random.nextInt(this.randomDungeonsArray.length);
		if(!level.canGenerateAt(cpos.above(), world) && !level.canGenerateAt(cpos.below(), world)) {
			if(!level.canGenerateAt(cpos.add(1, 0, 0), world)&&!level.canGenerateAt(cpos.sub(1, 0, 0), world)) {
				return DungeonCube.TUNNEL_SOUTH_NORTH;
			}
			if(!level.canGenerateAt(cpos.add(0, 0, 1), world)&&!level.canGenerateAt(cpos.sub(0, 0, 1), world)) {
				return DungeonCube.TUNNEL_EAST_WEST;
			}
		}
		if (isAnchorPoint(cpos)) {
			if(!level.canGenerateAt(cpos.below(), world))
				return randomDungeonsSafeUp[typedefiner % randomDungeonsSafeUp.length];
			if(!level.canGenerateAt(cpos.above(), world))
				return randomDungeonsSafeDown[typedefiner % randomDungeonsSafeDown.length];
			return randomDungeonsArray[typedefiner];
		}

		DungeonCube d_up = DungeonCube.UNDEFINED;
		DungeonCube d_down = DungeonCube.UNDEFINED;
		DungeonCube d_east = DungeonCube.UNDEFINED;
		DungeonCube d_west = DungeonCube.UNDEFINED;
		DungeonCube d_south = DungeonCube.UNDEFINED;
		DungeonCube d_north = DungeonCube.UNDEFINED;

		if ((cpos.getX() & 1 | cpos.getZ() & 1) == 0) {
			d_up = generator.getDungeonCubeType(cpos.add(0, 1, 0), world);
			d_down = generator.getDungeonCubeType(cpos.sub(0, 1, 0), world);
		}
		if ((cpos.getX() & 1) == 1) {
			d_east = generator.getDungeonCubeType(cpos.add(1, 0, 0), world);
			d_west = generator.getDungeonCubeType(cpos.sub(1, 0, 0), world);
		}
		if ((cpos.getZ() & 1) == 1) {
			d_south = generator.getDungeonCubeType(cpos.add(0, 0, 1), world);
			d_north = generator.getDungeonCubeType(cpos.sub(0, 0, 1), world);
		}
		// Up - Down
		if (d_up != DungeonCube.UNDEFINED && d_down != DungeonCube.UNDEFINED) {

			if (d_up.isColumnTopOrMiddle && d_down.isColumnBottomOrMiddle)
				return DungeonCube.COLUMN_MIDDLE;

			if (d_up.isColumnTopOrMiddle && d_down.isStairBottom)
				return DungeonCube.STAIR_TOP_CEILINGLESS;

			if (d_up.isColumnTopOrMiddle && d_north.isColumnTopOrMiddle)
				return DungeonCube.NORTH_BORDER_COLUMN_FLOOR;

			if (d_up.isColumnTopOrMiddle && d_south.isColumnTopOrMiddle)
				return DungeonCube.SOUTH_BORDER_COLUMN_FLOOR;

			if (d_up.isColumnTopOrMiddle && d_west.isColumnTopOrMiddle)
				return DungeonCube.WEST_BORDER_COLUMN_FLOOR;

			if (d_up.isColumnTopOrMiddle && d_east.isColumnTopOrMiddle)
				return DungeonCube.EAST_BORDER_COLUMN_FLOOR;

			if (d_up.isColumnTopOrMiddle)
				return DungeonCube.COLUMN_FLOOR;

			if (d_up.isStairTop && d_down.isColumnBottomOrMiddle)
				return DungeonCube.STAIR_TOP_WITH_STAIR; // Should be impossible

			if (d_up.isStairTop && d_down.isStairBottom)
				return DungeonCube.STAIR_MIDDLE;

			if (d_up.isStairTop) {
				if (d_east.isColumnTopOrMiddle ||
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
		}

		// All horizontal sides
		if (d_east != DungeonCube.UNDEFINED && d_west != DungeonCube.UNDEFINED &&
				d_south != DungeonCube.UNDEFINED && d_north != DungeonCube.UNDEFINED) {
			if (d_east.isWestWall && d_west.isEastWall && d_south.isNorthWall && d_north.isSouthWall)
				return DungeonCube.WALL_X;

			if (d_south.isColumnTopOrMiddle && d_west.isEastWall && d_east.isWestWall && d_north.isSouthWall)
				return DungeonCube.SOUTH_BORDER_WITH_WALLS;

			if (d_north.isColumnTopOrMiddle && d_west.isEastWall && d_east.isWestWall && d_south.isNorthWall)
				return DungeonCube.NORTH_BORDER_WITH_WALLS;

			if (d_east.isColumnTopOrMiddle && d_west.isEastWall && d_south.isNorthWall && d_north.isSouthWall)
				return DungeonCube.EAST_BORDER_WITH_WALLS;

			if (d_west.isColumnTopOrMiddle && d_east.isWestWall && d_south.isNorthWall && d_north.isSouthWall)
				return DungeonCube.WEST_BORDER_WITH_WALLS;

			if (d_west.isEastWall && d_east.isWestWall && d_north.isSouthWall)
				return DungeonCube.WALL_NORTH_WEST_EAST;

			if (d_west.isEastWall && d_east.isWestWall && d_south.isNorthWall)
				return DungeonCube.WALL_SOUTH_EAST_WEST;

			if (d_west.isEastWall && d_south.isNorthWall && d_north.isSouthWall)
				return DungeonCube.WALL_WEST_SOUTH_NORTH;

			if (d_east.isWestWall && d_south.isNorthWall && d_north.isSouthWall)
				return DungeonCube.WALL_EAST_NORTH_SOUTH;

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

			if (d_west.isColumnTopOrMiddle && d_north.isColumnTopOrMiddle)
				return DungeonCube.WEST_NORTH_BORDER;

			if (d_west.isColumnTopOrMiddle && d_south.isColumnTopOrMiddle)
				return DungeonCube.SOUTH_WEST_BORDER;

			if (d_east.isColumnTopOrMiddle && d_north.isColumnTopOrMiddle)
				return DungeonCube.NORTH_EAST_BORDER;

			if (d_east.isColumnTopOrMiddle && d_south.isColumnTopOrMiddle)
				return DungeonCube.EAST_SOUTH_BORDER;

			if (d_west.isEastWall && d_north.isSouthWall)
				return DungeonCube.WALL_WEST_NORTH;

			if (d_west.isEastWall && d_south.isNorthWall)
				return DungeonCube.WALL_WEST_SOUTH;

			if (d_east.isWestWall && d_north.isSouthWall)
				return DungeonCube.WALL_EAST_NORTH;

			if (d_east.isWestWall && d_south.isNorthWall)
				return DungeonCube.WALL_EAST_SOUTH;
		}

		// East- west
		if (d_east != DungeonCube.UNDEFINED && d_west != DungeonCube.UNDEFINED) {
			if (d_east.isColumnMiddle && d_west.isColumnMiddle)
				return DungeonCube.COLUMN_MIDDLE;

			if (d_east.isColumnTop && d_west.isColumnTopOrMiddle || d_west.isColumnTop && d_east.isColumnTopOrMiddle)
				return DungeonCube.COLUMN_CEIL;

			if (d_east.isColumnBottom && d_west.isColumnBottom)
				return DungeonCube.COLUMN_FLOOR;

			if (d_east.isColumnMiddle && d_west.isColumnBottom)
				return DungeonCube.EAST_BORDER_COLUMN_FLOOR;

			if (d_west.isColumnMiddle && d_east.isColumnBottom)
				return DungeonCube.WEST_BORDER_COLUMN_FLOOR;

			if (d_east.isColumnTopOrMiddle && d_west.isEastWall)
				return DungeonCube.EAST_BORDER_WITH_WALLS;
			
			if (d_east.isColumnTopOrMiddle)
				return DungeonCube.EAST_BORDER_WITH_WALL_SOUTH_NORTH;

			if (d_west.isColumnTopOrMiddle && d_east.isWestWall )
				return DungeonCube.WEST_BORDER_WITH_WALLS;
			
			if (d_west.isColumnTopOrMiddle)
				return DungeonCube.WEST_BORDER_WITH_WALL_SOUTH_NORTH;

			if (d_east.isWestWall && d_west.isEastWall)
				return DungeonCube.WALL_X;

			if (d_east.isWestWall)
				return DungeonCube.WALL_EAST_NORTH_SOUTH;

			if (d_west.isEastWall)
				return DungeonCube.WALL_WEST_SOUTH_NORTH;
		}
		// South - North
		if (d_south != DungeonCube.UNDEFINED && d_north != DungeonCube.UNDEFINED) {
			if (d_south.isColumnMiddle && d_north.isColumnMiddle)
				return DungeonCube.COLUMN_MIDDLE;

			if (d_south.isColumnTop && d_north.isColumnTopOrMiddle || d_north.isColumnTop && d_south.isColumnTopOrMiddle)
				return DungeonCube.COLUMN_CEIL;

			if (d_south.isColumnBottom && d_north.isColumnBottom)
				return DungeonCube.COLUMN_FLOOR;

			if (d_south.isColumnMiddle && d_north.isColumnBottom)
				return DungeonCube.SOUTH_BORDER_COLUMN_FLOOR;

			if (d_north.isColumnMiddle && d_south.isColumnBottom)
				return DungeonCube.NORTH_BORDER_COLUMN_FLOOR;

			if (d_south.isColumnTopOrMiddle && d_north.isSouthWall)
				return DungeonCube.SOUTH_BORDER_WITH_WALLS;
			
			if (d_south.isColumnTopOrMiddle)
				return DungeonCube.SOUTH_BORDER_WITH_WALL_EAST_WEST;

			if (d_north.isColumnTopOrMiddle && d_south.isNorthWall)
				return DungeonCube.NORTH_BORDER_WITH_WALLS;
			
			if (d_north.isColumnTopOrMiddle)
				return DungeonCube.NORTH_BORDER_WITH_WALL_EAST_WEST;

			if (d_south.isNorthWall && d_north.isSouthWall)
				return DungeonCube.WALL_SOUTH_NORTH;

			if (d_south.isNorthWall)
				return DungeonCube.WALL_SOUTH_EAST_WEST;

			if (d_north.isSouthWall)
				return DungeonCube.WALL_NORTH_WEST_EAST;

			return DungeonCube.COLUMN_FLOOR_CEIL;
		}

		if (d_up != DungeonCube.UNDEFINED && d_down != DungeonCube.UNDEFINED ||
				d_east != DungeonCube.UNDEFINED && d_west != DungeonCube.UNDEFINED)
			return DungeonCube.COLUMN_FLOOR_CEIL;

		return DungeonCube.UNDEFINED;
	}
}
