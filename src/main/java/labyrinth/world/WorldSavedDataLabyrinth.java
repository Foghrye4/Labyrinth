package labyrinth.world;

import java.util.HashSet;
import java.util.Set;

import cubicchunks.util.CubePos;
import labyrinth.LabyrinthMod;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.WorldSavedData;

public class WorldSavedDataLabyrinth extends WorldSavedData {
	public int dungeonStartHeight = 0;
	public float dungeonBiomeHeightUpperBound = 2.0f;
	public float dungeonBiomeHeightLowerBound = -1.1f;
	public final Set<CubePos> spawnQuery = new HashSet<CubePos>();

	public WorldSavedDataLabyrinth(String name) {
		super(name);
		dungeonStartHeight = LabyrinthMod.config.getStartHeight();
		dungeonBiomeHeightUpperBound = LabyrinthMod.config.getBiomeHeightUpperBound();
		dungeonBiomeHeightLowerBound = LabyrinthMod.config.getBiomeHeightLowerBound();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		dungeonStartHeight = nbt.getInteger("dungeonStartHeight");
		dungeonBiomeHeightUpperBound = nbt.getFloat("dungeonBiomeHeightUpperBound");
		dungeonBiomeHeightLowerBound = nbt.getFloat("dungeonBiomeHeightLowerBound");
		NBTTagList list = nbt.getTagList("spawnQuery", 11);
		for (int i = 0; i < list.tagCount(); i++) {
			int[] coords = list.getIntArrayAt(i);
			spawnQuery.add(new CubePos(coords[0], coords[1], coords[2]));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("dungeonStartHeight", dungeonStartHeight);
		nbt.setFloat("dungeonBiomeHeightUpperBound", dungeonBiomeHeightUpperBound);
		nbt.setFloat("dungeonBiomeHeightLowerBound", dungeonBiomeHeightLowerBound);
		NBTTagList list = new NBTTagList();
		for (CubePos pos : spawnQuery) {
			NBTTagIntArray array = new NBTTagIntArray(new int[]{pos.getX(), pos.getY(), pos.getZ()});
			list.appendTag(array);
		}
		nbt.setTag("spawnQuery", list);
		return nbt;
	}

	public int getLevel(BlockPos pos) {
		return (dungeonStartHeight - pos.getY()) / 32;
	}

	public int getLevel(CubePos pos) {
		return (dungeonStartHeight - pos.getMinBlockY()) / 32;
	}
}
