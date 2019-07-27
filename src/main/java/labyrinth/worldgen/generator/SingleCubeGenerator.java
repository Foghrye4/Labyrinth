package labyrinth.worldgen.generator;

import io.github.opencubicchunks.cubicchunks.api.util.CubePos;
import labyrinth.worldgen.DungeonCube;
import labyrinth.worldgen.ICubeStructureGenerator;
import labyrinth.worldgen.LabyrinthWorldGen;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class SingleCubeGenerator  implements ICubeStructureGenerator {

	DungeonCube cube = DungeonCube.MONOLITH;
	
	@Override
	public DungeonCube getDungeonCubeType(CubePos cpos, World world, LabyrinthWorldGen labyrinthWorldGen) {
		return cube;
	}
	
	public void setCube(DungeonCube cubeIn) {
		cube = cubeIn;
	}
}
