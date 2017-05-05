package labyrinth.command;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import labyrinth.LabyrinthMod;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.init.Blocks;

public abstract class LCubeEditCommandBase extends CommandBase {
	List<IBlockState> blockstateList = new ArrayList<IBlockState>();
	
	public LCubeEditCommandBase(){
		blockstateList.add(Blocks.AIR.getDefaultState());
		blockstateList.add(Blocks.STONE.getDefaultState());
		blockstateList.add(Blocks.STONEBRICK.getDefaultState());
		for(IBlockState bs:Blocks.IRON_BARS.getBlockState().getValidStates()){
			blockstateList.add(bs);
		}
		for(IBlockState bs:Blocks.STONE_STAIRS.getBlockState().getValidStates()){
			blockstateList.add(bs);
		}
		for(IBlockState bs:Blocks.COBBLESTONE_WALL.getBlockState().getValidStates()){
			blockstateList.add(bs);
		}
		for(IBlockState bs:Blocks.STICKY_PISTON.getBlockState().getValidStates()){
			blockstateList.add(bs);
		}
		for(IBlockState bs:Blocks.LEVER.getBlockState().getValidStates()){
			blockstateList.add(bs);
		}
	}

	public File getFile(String folder_name, String filename) {
		File folder = new File(LabyrinthMod.proxy.getMinecraftDir(), folder_name);
		folder.mkdirs();
		return new File(folder, filename);
	}
	
}

