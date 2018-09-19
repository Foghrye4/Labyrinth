package labyrinth.worldgen;

import java.util.Random;

import io.github.opencubicchunks.cubicchunks.api.util.CubePos;
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorld;
import labyrinth.worldgen.DungeonCube.DungeonCubeFlag;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class ClaustrophobicCubeStructureGenerator extends RegularCubeStructureGenerator {

	private final Random random = new Random();
	
	private DungeonCube[] randomDungeonsArray = new DungeonCube[]{
			DungeonCube.X_ROADS,
			DungeonCube.X_ROADS_HIDDEN_ROOM,
			DungeonCube.LAVA_ROOM,
			DungeonCube.LAVA_ROOM_WORKSHOP,
			DungeonCube.LADDER_CEIL,
			DungeonCube.LADDER_MIDDLE,
			DungeonCube.LADDER_FLOOR,
			DungeonCube.LADDER_FLOOR_TRAP,
			DungeonCube.ROAD_SOUTH_NORTH,
			DungeonCube.ROAD_SOUTH_NORTH_TRAP,
			DungeonCube.ROAD_EAST_WEST,
			DungeonCube.ROAD_EAST_WEST_TRAP,
			DungeonCube.ROAD_WEST_NORTH,
			DungeonCube.ROAD_WEST_NORTH_TRAP,
			DungeonCube.ROAD_SOUTH_WEST,
			DungeonCube.ROAD_NORTH_EAST,
			DungeonCube.ROAD_EAST_SOUTH,
			DungeonCube.ROAD_WEST_NORTH_EAST,
			DungeonCube.ROAD_SOUTH_WEST_NORTH,
			DungeonCube.ROAD_NORTH_EAST_SOUTH,
			DungeonCube.ROAD_EAST_SOUTH_WEST,
			DungeonCube.NOTHING, DungeonCube.NOTHING, DungeonCube.NOTHING,
			DungeonCube.NOTHING, DungeonCube.NOTHING, DungeonCube.NOTHING,};
	
	private LabyrinthWorldGen generator;
	
	public ClaustrophobicCubeStructureGenerator(LabyrinthWorldGen generatorIn){
		super(generatorIn);
		generator = generatorIn;
	}
	
	@Override
	public DungeonCube getDungeonCubeType(CubePos cpos, World world) {
		long hash = 3;
		hash = 41 * hash + world.getSeed();
		hash = 41 * hash + cpos.getX();
		hash = 41 * hash + cpos.getY();
		long seed = 41 * hash + cpos.getZ();
		random.setSeed(seed);
		int typedefiner = random.nextInt(this.randomDungeonsArray.length);
		if (isAnchorPoint(cpos)) {
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

			if (d_up.isStairTop && d_down.isStairBottom)
				return DungeonCube.LADDER_MIDDLE;

			if (d_up.isStairTop) {
				switch (typedefiner % 2) {
					case 0 :
						return DungeonCube.LADDER_FLOOR;
					case 1 :
						return DungeonCube.LADDER_FLOOR_TRAP;
				}
			}
			
			if (d_down.isStairBottom) {
				return DungeonCube.LADDER_CEIL;
			}
		}

		// All horizontal sides
		if (d_east != DungeonCube.UNDEFINED && d_west != DungeonCube.UNDEFINED &&
				d_south != DungeonCube.UNDEFINED && d_north != DungeonCube.UNDEFINED) {
			if (d_east.isWestWall && d_west.isEastWall && d_south.isNorthWall && d_north.isSouthWall){
				switch (typedefiner % 4) {
					case 0 :
						return DungeonCube.X_ROADS;
					case 1 :
						return DungeonCube.X_ROADS_HIDDEN_ROOM;
					case 2 :
						return DungeonCube.LAVA_ROOM;
					case 3 :
						return DungeonCube.LAVA_ROOM_WORKSHOP;
				}
			}

			if (d_west.isEastWall && d_east.isWestWall && d_north.isSouthWall)
				return DungeonCube.ROAD_WEST_NORTH_EAST;

			if (d_west.isEastWall && d_east.isWestWall && d_south.isNorthWall)
				return DungeonCube.ROAD_EAST_SOUTH_WEST;

			if (d_west.isEastWall && d_south.isNorthWall && d_north.isSouthWall)
				return DungeonCube.ROAD_SOUTH_WEST_NORTH;

			if (d_east.isWestWall && d_south.isNorthWall && d_north.isSouthWall)
				return DungeonCube.ROAD_NORTH_EAST_SOUTH;

			if (d_west.isEastWall && d_north.isSouthWall)
				return DungeonCube.ROAD_EAST_SOUTH;

			if (d_west.isEastWall && d_south.isNorthWall)
				return DungeonCube.ROAD_SOUTH_WEST;

			if (d_east.isWestWall && d_north.isSouthWall)
				return DungeonCube.ROAD_NORTH_EAST;

			if (d_east.isWestWall && d_south.isNorthWall)
				return DungeonCube.ROAD_EAST_SOUTH;
			
			if (d_east.isWestWall && d_west.isEastWall)
				if (typedefiner % 2 == 1)
					return DungeonCube.ROAD_EAST_WEST;
				else
					return DungeonCube.ROAD_EAST_WEST_TRAP;

			if (d_north.isSouthWall && d_south.isNorthWall)
				if (typedefiner % 2 == 1)
					return DungeonCube.ROAD_SOUTH_NORTH;
				else
					return DungeonCube.ROAD_SOUTH_NORTH_TRAP;
			
			return DungeonCube.NOTHING;
		}

		// East- west
		if (d_east != DungeonCube.UNDEFINED && d_west != DungeonCube.UNDEFINED) {
			if (d_east.isWestWall && d_west.isEastWall)
				switch (typedefiner % 2) {
					case 0 :
						return DungeonCube.X_ROADS;
					case 1 :
						return DungeonCube.X_ROADS_HIDDEN_ROOM;
				}

			if (d_east.isWestWall)
				return DungeonCube.ROAD_NORTH_EAST_SOUTH;

			if (d_west.isEastWall)
				return DungeonCube.ROAD_SOUTH_WEST_NORTH;
		}
		// South - North
		if (d_south != DungeonCube.UNDEFINED && d_north != DungeonCube.UNDEFINED) {
			if (d_south.isNorthWall && d_north.isSouthWall)
				switch (typedefiner % 2) {
					case 0 :
						return DungeonCube.ROAD_SOUTH_NORTH;
					case 1 :
						return DungeonCube.ROAD_SOUTH_NORTH_TRAP;
				}

			if (d_south.isNorthWall)
				return DungeonCube.ROAD_EAST_SOUTH_WEST;

			if (d_north.isSouthWall)
				return DungeonCube.ROAD_WEST_NORTH_EAST;

			return DungeonCube.NOTHING;
		}

		if (d_up != DungeonCube.UNDEFINED && d_down != DungeonCube.UNDEFINED ||
				d_east != DungeonCube.UNDEFINED && d_west != DungeonCube.UNDEFINED)
			return DungeonCube.NOTHING;

		return DungeonCube.UNDEFINED;
	}
	
	@Override
	protected int getSpawnHeight(){
		return 2;
	}
	
	@Override
	public void spawnMobs(int level, World world, CubePos pos, ExtendedBlockStorage data) {
		random.setSeed(pos.hashCode() ^ world.getSeed());
		if (random.nextInt(getMobSpawnRarity()) != 0)
			return;
		try {
		DifficultyInstance difficulty = world.getDifficultyForLocation(pos.getCenterBlockPos());
		LevelFeaturesStorage storage = generator.storage;
		IEntityLivingData livingdata = null;
		int mobRandom = level < storage.levelToMob.length ? level : random.nextInt() & (storage.levelToMob.length - 1);
		Class<? extends EntityLiving>[] mobs = storage.levelToMob[mobRandom];
		int dy = getSpawnHeight();
		for (int dx = random.nextInt(12); dx < 15; dx += random.nextInt(12) + 2)
			for (int dz = 7; dz <= 8; dz ++)
					this.spawnMobIfPossible(world, pos, mobs, dx, dy, dz, data, difficulty, livingdata, level);
		for (int dz = random.nextInt(12); dz < 15; dz += random.nextInt(12) + 2)
			for (int dx = 7; dx <= 8; dx ++)
					this.spawnMobIfPossible(world, pos, mobs, dx, dy, dz, data, difficulty, livingdata, level);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
