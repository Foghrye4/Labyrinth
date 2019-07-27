package labyrinth.worldgen.generator;

import io.github.opencubicchunks.cubicchunks.api.util.CubePos;
import labyrinth.worldgen.DungeonCube;
import labyrinth.worldgen.ICubeStructureGenerator;
import labyrinth.worldgen.LabyrinthWorldGen;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class MonolithGenerator  implements ICubeStructureGenerator {

	@Override
	public DungeonCube getDungeonCubeType(CubePos cpos, World world, LabyrinthWorldGen labyrinthWorldGen) {
		if(!labyrinthWorldGen.canGenerateAt(cpos.sub(0, 0, 1), world) && !labyrinthWorldGen.canGenerateAt(cpos.sub(1, 0, 0), world)) {
			return DungeonCube.MONOLITH_NW;
		}
		else if(!labyrinthWorldGen.canGenerateAt(cpos.sub(0, 0, 1), world) && !labyrinthWorldGen.canGenerateAt(cpos.add(1, 0, 0), world)) {
			return DungeonCube.MONOLITH_NE;
		}
		else if(!labyrinthWorldGen.canGenerateAt(cpos.add(0, 0, 1), world) && !labyrinthWorldGen.canGenerateAt(cpos.sub(1, 0, 0), world)) {
			return DungeonCube.MONOLITH_SW;
		}
		else if(!labyrinthWorldGen.canGenerateAt(cpos.add(0, 0, 1), world) && !labyrinthWorldGen.canGenerateAt(cpos.add(1, 0, 0), world)) {
			return DungeonCube.MONOLITH_SE;
		}
		return DungeonCube.MONOLITH;
	}
}
