package labyrinth.worldgen;

import io.github.opencubicchunks.cubicchunks.api.util.CubePos;
import net.minecraft.world.World;

public interface ICubeStructureGenerator {
	DungeonCube getDungeonCubeType(CubePos cpos, World world, LabyrinthWorldGen labyrinthWorldGen);
}
