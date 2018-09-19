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
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class LWriteWithRotationsCommand extends LCubeEditCommandBase {

	public LWriteWithRotationsCommand(){
		super();
	}
	@Override
	public String getName() {
		return "l_write_rotations";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/l_write_rotations filename";
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
		ByteBuffer bf_straight = ByteBuffer.allocate(4096);
		ByteBuffer bf_rotated1 = ByteBuffer.allocate(4096);
		ByteBuffer bf_rotated2 = ByteBuffer.allocate(4096);
		ByteBuffer bf_rotated3 = ByteBuffer.allocate(4096);
		BlockPos pos = sender.getPosition();
		World world = sender.getEntityWorld();
		CubePos cpos = CubePos.fromBlockCoords(pos);
		for (int x = cpos.getMinBlockX(); x <= cpos.getMaxBlockX(); x++)
			for (int y = cpos.getMinBlockY(); y <= cpos.getMaxBlockY(); y++)
				for (int z = cpos.getMinBlockZ(); z <= cpos.getMaxBlockZ(); z++) {
					int dx = x - cpos.getMinBlockX();
					int dy = y - cpos.getMinBlockY();
					int dz = z - cpos.getMinBlockZ();
					int index = dx<<8|dy<<4|dz;
					int bsid = 0;
					IBlockState bs = world.getBlockState(new BlockPos(x, y, z));
					if (blockstateList.contains(bs)) {
						bsid = blockstateList.indexOf(bs);
					}
					bf_straight.put(index, (byte) bsid);
					index = rotateIndex(index);
					bs = bs.withRotation(Rotation.CLOCKWISE_90);
					if (blockstateList.contains(bs)) {
						bsid = blockstateList.indexOf(bs);
					}
					bf_rotated1.put(index, (byte) bsid);
					index = rotateIndex(index);
					bs = bs.withRotation(Rotation.CLOCKWISE_90);
					if (blockstateList.contains(bs)) {
						bsid = blockstateList.indexOf(bs);
					}
					bf_rotated2.put(index, (byte) bsid);
					index = rotateIndex(index);
					bs = bs.withRotation(Rotation.CLOCKWISE_90);
					if (blockstateList.contains(bs)) {
						bsid = blockstateList.indexOf(bs);
					}
					bf_rotated3.put(index, (byte) bsid);
				}
		DataOutputStream osWriter = null;
		try {
			osWriter = new DataOutputStream(new FileOutputStream(getFile(server.worlds[0],"cubes",filename)));
			sender.sendMessage(new TextComponentString("Done writing "+filename));
			osWriter.write(bf_straight.array());
			osWriter.close();
			
			filename = getRotatedFilename(filename);
			osWriter = new DataOutputStream(new FileOutputStream(getFile(server.worlds[0],"cubes",filename)));
			sender.sendMessage(new TextComponentString("Done writing "+filename));
			osWriter.write(bf_rotated1.array());
			osWriter.close();

			filename = getRotatedFilename(filename);
			osWriter = new DataOutputStream(new FileOutputStream(getFile(server.worlds[0],"cubes",filename)));
			sender.sendMessage(new TextComponentString("Done writing "+filename));
			osWriter.write(bf_rotated2.array());
			osWriter.close();
			
			filename = getRotatedFilename(filename);
			osWriter = new DataOutputStream(new FileOutputStream(getFile(server.worlds[0],"cubes",filename)));
			sender.sendMessage(new TextComponentString("Done writing "+filename));
			osWriter.write(bf_rotated3.array());
			osWriter.close();
		} catch (IOException e) {
			sender.sendMessage(new TextComponentString("IO error. Filename: "+filename));
			throw new CommandException("I/O error", sender);
		}
	}
	
	private int rotateIndex(int indexIn){
		int dx = 15-(indexIn&15);
		int dy = (indexIn>>>4)&15;
		int dz = indexIn>>>8;
		return dx<<8|dy<<4|dz;
	}
	
	private String getRotatedFilename(String nameIn){
		nameIn = nameIn.replace(".cube_structure", "");
		String[] nameParts = nameIn.split("_");
		StringBuffer buffer = new StringBuffer();
		for(int i = 0 ;i<nameParts.length;i++){
			buffer.append(getRotatedSideName(nameParts[i]));
			if(i!=nameParts.length-1)
				buffer.append("_");
		}
		buffer.append(".cube_structure");
		return buffer.toString();
	}
	
	private String getRotatedSideName(String sideIn){
		if(sideIn.contains("north"))
			return sideIn.replace("north", "east");
		if(sideIn.contains("east"))
			return sideIn.replace("east","south");
		if(sideIn.contains("south"))
			return sideIn.replace("south","west");
		if(sideIn.contains("west"))
			return sideIn.replace("west","north");
		return sideIn;
	}
}
