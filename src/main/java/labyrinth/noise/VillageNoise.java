package labyrinth.noise;

import static labyrinth.village.UndergroundVillage.BIT_SIZE;

import java.util.Random;

import io.github.opencubicchunks.cubicchunks.api.util.CubePos;
import net.minecraft.world.World;

public class VillageNoise implements INoise {
	private final Random random = new Random();
	
	@Override
	public boolean canGenerateAt(CubePos cpos, World world) {
		int x = cpos.getX() >> BIT_SIZE;
		int z = cpos.getZ() >> BIT_SIZE;
		if ((x & 3 | z & 3) != 0)
			return false;
		long hash = 3;
		hash = 41 * hash + world.getSeed();
		hash = 41 * hash + cpos.getY();
		hash = 41 * hash + x;
		hash = 41 * hash + z;
		random.setSeed(hash);
		boolean canPlaceVillageHere = random.nextInt(16) == 0;
		if (!canPlaceVillageHere)
			return false;
		return true;
	}
}
