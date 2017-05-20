package labyrinth.command;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import cubicchunks.util.CubePos;
import labyrinth.worldgen.LabyrinthWorldGen;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class LMixInCubeCommand extends LCubeEditCommandBase {
	
	public static String last_placed_dungeon_type = "";
	
	public LMixInCubeCommand(){
		super();
	}
	@Override
	public String getName() {
		return "l_mixin_cube";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/l_mixin_cube filename [x_offset y_offset z_offset]";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(args==null || args.length==0){
			sender.sendMessage(new TextComponentString("File name cannot be empty."));
			throw new CommandException("File name cannot be empty.", sender);
		}
		World world =sender.getEntityWorld();
		BlockPos pos = CubePos.fromBlockCoords(sender.getPosition()).getMinBlockPos();
		String cubeS = args[0];
		int cn = -1;
		try {
			cn = Integer.parseInt(cubeS);
		}
		catch(NumberFormatException e) {
			cn = -1;
		}
		if(cn!=-1){
			cubeS = LabyrinthWorldGen.DungeonCube.values()[cn].name;
		}
		try {
			int index = 0;
			InputStream is = new FileInputStream(getFile("cubes",cubeS));
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
				int bsindex = dis.readUnsignedByte();
				if(world.getBlockState(pos.east(dx).up(dy).south(dz)) == Blocks.AIR.getDefaultState())
					world.setBlockState(pos.east(dx).up(dy).south(dz), blockstateList.get(bsindex));
				index++;
			}
			dis.close();
			is.close();
			last_placed_dungeon_type = cubeS;
		} catch (IOException e) {
			e.printStackTrace();
		}
		sender.sendMessage(new TextComponentString("Done placing "+cubeS));
	}
}
