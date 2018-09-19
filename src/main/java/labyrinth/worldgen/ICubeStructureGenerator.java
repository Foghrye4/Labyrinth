package labyrinth.worldgen;

import io.github.opencubicchunks.cubicchunks.api.util.CubePos;
import io.github.opencubicchunks.cubicchunks.api.world.ICube;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public interface ICubeStructureGenerator {
	DungeonCube getDungeonCubeType(CubePos cpos, World world);

	void spawnMobs(int level, World world, CubePos pos, ExtendedBlockStorage cstorage);

	void placeCube(int level, ICube cube, ExtendedBlockStorage cstorage, CubePos pos, World world, byte[] data, IBlockState[] bl, DungeonCube is);
}
