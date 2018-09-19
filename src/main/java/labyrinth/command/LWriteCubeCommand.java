package labyrinth.command;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import io.github.opencubicchunks.cubicchunks.api.util.CubePos;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class LWriteCubeCommand extends LCubeEditCommandBase {

	public LWriteCubeCommand(){
		super();
	}
	@Override
	public String getName() {
		return "l_write_cube";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/l_write_cube [filename]";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		String filename;
		if(args==null || args.length==0){
			filename = LCubeEditCommandBase.lastPlacedDungeonType;
		}
		else {
			filename = args[0];
		}
		ByteBuffer bf = ByteBuffer.allocate(4096);
		BlockPos pos = sender.getPosition();
		World world = sender.getEntityWorld();
		CubePos cpos = CubePos.fromBlockCoords(pos);
		for (int x = cpos.getMinBlockX(); x <= cpos.getMaxBlockX(); x++)
			for (int y = cpos.getMinBlockY(); y <= cpos.getMaxBlockY(); y++)
				for (int z = cpos.getMinBlockZ(); z <= cpos.getMaxBlockZ(); z++) {
					int bsid = 0;
					IBlockState bs = world.getBlockState(new BlockPos(x, y, z));
					if (blockstateList.contains(bs)) {
						bsid = blockstateList.indexOf(bs);
					}
					bf.put((byte) bsid);
				}
		DataOutputStream osWriter = null;
		try {
			osWriter = new DataOutputStream(new FileOutputStream(getFile(server.worlds[0], "cubes",filename)));
			osWriter.write(bf.array());
			osWriter.close();
		} catch (IOException e) {
			throw new CommandException("I/O error", sender);
		}
		sender.sendMessage(new TextComponentString("Done writing "+filename));
	}
}
