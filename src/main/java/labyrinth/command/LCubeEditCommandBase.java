package labyrinth.command;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import labyrinth.LabyrinthMod;
import labyrinth.worldgen.LabyrinthWorldGen;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;

public abstract class LCubeEditCommandBase extends CommandBase {
	
	public static String lastPlacedDungeonType;
	List<IBlockState> blockstateList = Arrays.asList(LabyrinthWorldGen.instance.storage.blockstateList[0]);
	
	public File getFile(WorldServer world, String folder_name, String filename) {
		File folder = new File(world.getSaveHandler().getWorldDirectory(), folder_name);
		folder.mkdirs();
		File file = new File(folder, filename);
		if(!file.exists()){
			try {
				byte[] bf = new byte[4096];
				InputStream stream = LabyrinthMod.proxy.getResourceInputStream(new ResourceLocation("labyrinth", folder_name + "/" + filename));
				stream.read(bf);
				stream.close();
				DataOutputStream osWriter = null;
				osWriter = new DataOutputStream(new FileOutputStream(file));
				osWriter.write(bf);
				osWriter.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}
}

