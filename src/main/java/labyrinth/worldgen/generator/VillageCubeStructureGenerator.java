package labyrinth.worldgen.generator;

import io.github.opencubicchunks.cubicchunks.api.util.CubePos;
import labyrinth.village.UndergroundVillage;
import labyrinth.worldgen.DungeonCube;
import labyrinth.worldgen.DungeonLayer;
import labyrinth.worldgen.ICubeStructureGenerator;
import labyrinth.worldgen.LabyrinthWorldGen;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class VillageCubeStructureGenerator implements ICubeStructureGenerator {

	public DungeonCube[] randomDungeonsArray = new DungeonCube[]{
			DungeonCube.VILLAGE_PARK,
			DungeonCube.VILLAGE_PARK,
			DungeonCube.VILLAGE_PARK,
			DungeonCube.VILLAGE_HOME
	};

	public VillageCubeStructureGenerator(LabyrinthWorldGen labyrinthWorldGen) {
	}

	@Override
	public DungeonCube getDungeonCubeType(CubePos cpos, World world, LabyrinthWorldGen level) {
		int localX = cpos.getX() & 7;
		int localZ = cpos.getZ() & 7;
		boolean eastBorder = localX == 7;
		boolean westBorder = localX == 0;
		boolean southBorder = localZ == 7;
		boolean northBorder = localZ == 0;
		boolean centralWest = localX == 3;
		boolean centralEast = localX == 4;
		boolean center = (localZ == 3 || localZ == 4) && (localX == 3 || localX == 4);
		if (northBorder && westBorder)
			return DungeonCube.VILLAGE_NORTH_WEST;
		if (northBorder && eastBorder)
			return DungeonCube.VILLAGE_NORTH_EAST;
		if (southBorder && westBorder)
			return DungeonCube.VILLAGE_SOUTH_WEST;
		if (southBorder && eastBorder)
			return DungeonCube.VILLAGE_SOUTH_EAST;
		if (northBorder && centralWest)
			return DungeonCube.VILLAGE_NORTH_GATE_WEST_SIDE;
		if (northBorder && centralEast)
			return DungeonCube.VILLAGE_NORTH_GATE_EAST_SIDE;
		if (southBorder && centralWest)
			return DungeonCube.VILLAGE_SOUTH_GATE_WEST_SIDE;
		if (southBorder && centralEast)
			return DungeonCube.VILLAGE_SOUTH_GATE_EAST_SIDE;
		if (westBorder)
			return DungeonCube.VILLAGE_WEST;
		if (eastBorder)
			return DungeonCube.VILLAGE_EAST;
		if (southBorder)
			return DungeonCube.VILLAGE_SOUTH;
		if (northBorder)
			return DungeonCube.VILLAGE_NORTH;
		if (center && centralWest)
			return DungeonCube.VILLAGE_MARKET_WEST;
		if (center && centralEast)
			return DungeonCube.VILLAGE_MARKET_EAST;
		if (centralWest)
			return DungeonCube.VILLAGE_CENTRAL_WEST_SIDE;
		if (centralEast)
			return DungeonCube.VILLAGE_CENTRAL_EAST_SIDE;

		if ((localX & 1 | localZ & 1) == 0)
			return DungeonCube.VILLAGE_HOME;
		else
			return DungeonCube.VILLAGE_PARK;
	}
}
