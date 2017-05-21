package labyrinth.world;

import java.util.HashMap;
import java.util.Map;

import cubicchunks.util.CubePos;
import labyrinth.util.DataConversionUtil;
import labyrinth.util.LimitedSizeHashMap;
import labyrinth.worldgen.LabyrinthWorldGen;
import labyrinth.worldgen.LabyrinthWorldGen.DungeonCube;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;

public class WorldSavedDataCubeGeneratorCache extends WorldSavedData {
	
	public final Map<Integer, DungeonCube> dungeonCubeCache = new LimitedSizeHashMap<Integer, DungeonCube>(LabyrinthWorldGen.CACHE_SIZE);

	public WorldSavedDataCubeGeneratorCache(String name) {
		super(name);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		byte[] cacheData = nbt.getByteArray("dungeonCubeCache");
		DataConversionUtil.loadDungeonCubeMapFromByteArray(cacheData, dungeonCubeCache);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setByteArray("dungeonCubeCache",DataConversionUtil.dungeonCubeMapToByteArray(dungeonCubeCache));
		return nbt;
	}
}
