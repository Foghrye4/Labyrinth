package labyrinth.worldgen;

import cubicchunks.util.CubePos;
import cubicchunks.world.ICubicWorld;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public interface ICubeStructureGenerator {
	DungeonCube getDungeonCubeType(CubePos cpos, ICubicWorld world);

	void spawnMobs(int level, ICubicWorld world, CubePos pos, ExtendedBlockStorage cstorage);
}
