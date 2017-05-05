package labyrinth.command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import cubicchunks.util.CubePos;
import labyrinth.LabyrinthMod;
import labyrinth.worldgen.LabyrinthWorldGen;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class LReplaceCubeFloorCommand extends LCubeEditCommandBase {

	public LReplaceCubeFloorCommand(){
		super();
	}
	@Override
	public String getName() {
		return "l_replace_cube_floor";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/l_replace_cube_floor";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
/*		for(String dungeon: LabyrinthWorldGen.instance.valid_dungeon_types){
			try {
				ByteBuffer bf = ByteBuffer.allocate(4096);
				int index = 0;
				InputStream is = new FileInputStream(getFile("cubes", dungeon));
				DataInputStream dis = new DataInputStream(is);
				while(dis.available()>0){
					int dx = index>>>8;
					int dy = (index>>>4)&15;
					int dz = index&15;
					index++;
					int bsid = dis.readUnsignedByte();
					if(bsid == 1 && dy==0)
						bsid++;
					bf.put((byte) bsid);
				}
				DataOutputStream osWriter = new DataOutputStream(new FileOutputStream(getFile("cubes_out",dungeon)));
				osWriter.write(bf.array());
				osWriter.close();
				dis.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}		
			sender.sendMessage(new TextComponentString("Done"));
		}*/
	}
}
