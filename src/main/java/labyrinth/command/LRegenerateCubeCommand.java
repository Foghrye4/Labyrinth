package labyrinth.command;

import java.io.File;

import cubicchunks.util.CubePos;
import cubicchunks.world.ICubicWorld;
import labyrinth.LabyrinthMod;
import labyrinth.worldgen.DungeonCube;
import labyrinth.worldgen.LabyrinthWorldGen;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class LRegenerateCubeCommand extends CommandBase {
	
	public File getFile(String folder_name, String filename) {
		File folder = new File(LabyrinthMod.proxy.getMinecraftDir(), folder_name);
		folder.mkdirs();
		return new File(folder, filename);
	}

	@Override
	public String getName() {
		return "l_check_cube";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return getName()+" [recursion_level]";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		ICubicWorld world = (ICubicWorld) sender.getEntityWorld();
		CubePos cpos = CubePos.fromBlockCoords(sender.getPosition());
		
		int recursionDeepness = 0;
		if(args.length>0)
			recursionDeepness = Integer.parseInt(args[0]);
		DungeonCube origin = LabyrinthWorldGen.instance.getDungeonCubeType(cpos, world);
		recursionDeepness++;
		DungeonCube d_up = LabyrinthWorldGen.instance.getDungeonCubeType(cpos.add(0, 1, 0), world);
		DungeonCube d_down = LabyrinthWorldGen.instance.getDungeonCubeType(cpos.sub(0, 1, 0), world);
		DungeonCube d_east = LabyrinthWorldGen.instance.getDungeonCubeType(cpos.add(1, 0, 0), world);
		DungeonCube d_west = LabyrinthWorldGen.instance.getDungeonCubeType(cpos.sub(1, 0, 0), world);
		DungeonCube d_south = LabyrinthWorldGen.instance.getDungeonCubeType(cpos.add(0, 0, 1), world);
		DungeonCube d_north = LabyrinthWorldGen.instance.getDungeonCubeType(cpos.sub(0, 0, 1), world);
		
		if(LabyrinthWorldGen.instance.isAnchorPoint(cpos))
			sender.sendMessage(new TextComponentString("Pos is anchor point"));
		sender.sendMessage(new TextComponentString("Selected type: "+origin.name()));
		sender.sendMessage(new TextComponentString("Type below: "+d_down));
		sender.sendMessage(new TextComponentString("Type above: "+d_up));
		sender.sendMessage(new TextComponentString("Type at north: "+d_north));
		sender.sendMessage(new TextComponentString("Type at south: "+d_south));
		sender.sendMessage(new TextComponentString("Type at east: "+d_east));
		sender.sendMessage(new TextComponentString("Type at west: "+d_west));
	}
}

