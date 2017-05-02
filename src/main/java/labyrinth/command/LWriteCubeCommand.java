package labyrinth.command;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

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

public class LWriteCubeCommand extends CommandBase {

	Map<IBlockState, Integer> blockstateMap = new HashMap<IBlockState, Integer>();
	public LWriteCubeCommand(){
		int index = 0;
		blockstateMap.put(Blocks.AIR.getDefaultState(), index++);
		blockstateMap.put(Blocks.STONE.getDefaultState(), index++);
		blockstateMap.put(Blocks.IRON_BARS.getDefaultState(), index++);
		for(IBlockState bs:Blocks.IRON_BARS.getBlockState().getValidStates()){
			blockstateMap.put(bs, index++);
		}
		for(IBlockState bs:Blocks.STONE_STAIRS.getBlockState().getValidStates()){
			blockstateMap.put(bs, index++);
		}
		for(IBlockState bs:Blocks.COBBLESTONE_WALL.getBlockState().getValidStates()){
			blockstateMap.put(bs, index++);
		}
		for(IBlockState bs:Blocks.STICKY_PISTON.getBlockState().getValidStates()){
			blockstateMap.put(bs, index++);
		}
		for(IBlockState bs:Blocks.LEVER.getBlockState().getValidStates()){
			blockstateMap.put(bs, index++);
		}
	}
	@Override
	public String getName() {
		return "l_write_cube";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/l_write_cube filename";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(args==null || args.length==0){
			sender.sendMessage(new TextComponentString("File name cannot be empty."));
			throw new CommandException("File name cannot be empty.", sender);
		}
		ByteBuffer bf = ByteBuffer.allocate(4096);
		String filename = args[0];
		BlockPos pos = sender.getPosition();
		World world = sender.getEntityWorld();
		CubePos cpos = CubePos.fromBlockCoords(pos);
		for (int x = cpos.getMinBlockX(); x <= cpos.getMaxBlockX(); x++)
			for (int y = cpos.getMinBlockY(); y <= cpos.getMaxBlockY(); y++)
				for (int z = cpos.getMinBlockZ(); z <= cpos.getMaxBlockZ(); z++) {
					int bsid = 0;
					IBlockState bs = world.getBlockState(new BlockPos(x, y, z));
					if (blockstateMap.containsKey(bs)) {
						bsid = blockstateMap.get(bs);
					}
					bf.put((byte) bsid);
				}
		DataOutputStream osWriter = null;
		try {
			osWriter = new DataOutputStream(new FileOutputStream(getFile(filename)));
			osWriter.write(bf.array());
			osWriter.close();
		} catch (IOException e) {
			throw new CommandException("I/O error", sender);
		}
		sender.sendMessage(new TextComponentString("Done"));
	}

	private static File getFile(String filename) {
		File folder = new File(LabyrinthMod.proxy.getMinecraftDir(), "cubes");
		folder.mkdirs();
		return new File(folder, filename);
	}

}
