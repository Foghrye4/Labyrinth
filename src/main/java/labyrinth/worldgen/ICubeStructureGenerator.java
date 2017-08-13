package labyrinth.worldgen;

import cubicchunks.util.CubePos;
import cubicchunks.world.ICubicWorld;
import cubicchunks.world.cube.Cube;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public interface ICubeStructureGenerator {
	DungeonCube getDungeonCubeType(CubePos cpos, ICubicWorld world);

	void spawnMobs(int level, ICubicWorld world, CubePos pos, ExtendedBlockStorage cstorage);

	void placeCube(int level, Cube cube, ExtendedBlockStorage cstorage, CubePos pos, ICubicWorld world, byte[] data, IBlockState[] bl, DungeonCube is);
}
