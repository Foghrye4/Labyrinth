package labyrinth.noise;

import io.github.opencubicchunks.cubicchunks.api.util.CubePos;
import net.minecraft.world.World;

public class SolidNoNoise implements INoise {
	
	@Override
	public boolean canGenerateAt(CubePos cpos, World world) {
		return true;
	}
}
