package labyrinth.noise;

import io.github.opencubicchunks.cubicchunks.api.util.CubePos;
import net.minecraft.world.World;

public interface INoise {
	public boolean canGenerateAt(CubePos pos, World world);
}
