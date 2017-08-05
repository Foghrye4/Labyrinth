package labyrinth.worldgen;

import cubicchunks.util.CubePos;
import cubicchunks.world.ICubicWorld;

public interface ICubeStructureGenerator {
	DungeonCube getDungeonCubeType(CubePos cpos, ICubicWorld world);
}
