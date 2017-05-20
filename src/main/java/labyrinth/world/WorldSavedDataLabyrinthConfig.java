package labyrinth.world;

import cubicchunks.util.CubePos;
import labyrinth.LabyrinthMod;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldSavedData;

public class WorldSavedDataLabyrinthConfig extends WorldSavedData {
	public int dungeonStartHeight = 0;
	public float dungeonBiomeHeightUpperBound = 2.0f;
	public float dungeonBiomeHeightLowerBound = -1.1f;
	
	public WorldSavedDataLabyrinthConfig(String name) {
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
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("dungeonStartHeight",dungeonStartHeight);
		nbt.setFloat("dungeonBiomeHeightUpperBound", dungeonBiomeHeightUpperBound);
		nbt.setFloat("dungeonBiomeHeightLowerBound", dungeonBiomeHeightLowerBound);
		return nbt;
	}
	
	public int getLevel(BlockPos pos) {
		return (dungeonStartHeight-pos.getY()) / 32;
	}

	public int getLevel(CubePos pos) {
		return (dungeonStartHeight-pos.getMinBlockY()) / 32;
	}
	
}
