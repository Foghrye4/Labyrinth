package labyrinth.util;

import net.minecraft.util.math.BlockPos;

public class LevelUtil {
	public static int getLevel(BlockPos pos)
	{
		return -pos.getY()/32;
	}
}
