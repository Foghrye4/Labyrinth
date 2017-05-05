package labyrinth.command;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import labyrinth.LabyrinthMod;
import labyrinth.worldgen.LabyrinthWorldGen;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.init.Blocks;
import scala.actors.threadpool.Arrays;

public abstract class LCubeEditCommandBase extends CommandBase {
	
	List<IBlockState> blockstateList = Arrays.asList(LabyrinthWorldGen.instance.blockstateList[0]);
	
	public File getFile(String folder_name, String filename) {
		File folder = new File(LabyrinthMod.proxy.getMinecraftDir(), folder_name);
		folder.mkdirs();
		return new File(folder, filename);
	}
	
}

