package labyrinth.command;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cubicchunks.util.CubePos;
import labyrinth.LabyrinthMod;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class LPlaceCubeCommand extends CommandBase {

	List<IBlockState> blockstateList = new ArrayList<IBlockState>();
	public LPlaceCubeCommand(){
		blockstateList.add(Blocks.AIR.getDefaultState());
		blockstateList.add(Blocks.STONE.getDefaultState());
		blockstateList.add(Blocks.IRON_BARS.getDefaultState());
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
	@Override
	public String getName() {
		return "l_place_cube";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/l_place_cube filename [x_offset y_offset z_offset]";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(args==null || args.length==0){
			sender.sendMessage(new TextComponentString("File name cannot be empty."));
			throw new CommandException("File name cannot be empty.", sender);
		}
		World world =sender.getEntityWorld();
		BlockPos pos = CubePos.fromBlockCoords(sender.getPosition()).getMinBlockPos();
		try {
			int index = 0;
			InputStream is = new FileInputStream(getFile(args[0]));
			DataInputStream dis = new DataInputStream(is);
			while(dis.available()>0){
				int dx = index>>>8;
				int dy = (index>>>4)&15;
				int dz = index&15;
				if(args.length == 4){
					dx+=Integer.parseInt(args[1]);
					dy+=Integer.parseInt(args[2]);
					dz+=Integer.parseInt(args[3]);
				}
				world.setBlockState(pos.east(dx).up(dy).south(dz), this.blockstateList.get(dis.readUnsignedByte()));
				index++;
			}
			dis.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}		sender.sendMessage(new TextComponentString("Done"));
	}

	private static File getFile(String filename) {
		File folder = new File(LabyrinthMod.proxy.getMinecraftDir(), "cubes");
		folder.mkdirs();
		return new File(folder, filename);
	}

}
