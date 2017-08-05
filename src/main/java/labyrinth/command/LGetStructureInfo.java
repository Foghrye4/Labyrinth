package labyrinth.command;

import cubicchunks.util.CubePos;
import cubicchunks.world.ICubicWorld;
import labyrinth.worldgen.DungeonCube;
import labyrinth.worldgen.LavaCubeStructureGenerator;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class LGetStructureInfo extends LCubeEditCommandBase {
	
	LavaCubeStructureGenerator cubeStructureGenerator = new LavaCubeStructureGenerator();

	public LGetStructureInfo(){
		super();
	}
	@Override
	public String getName() {
		return "l_get_info";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/l_get_info";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		World world = sender.getEntityWorld();
		BlockPos pos = sender.getPosition();
		DungeonCube dc = cubeStructureGenerator.getDungeonCubeType(CubePos.fromBlockCoords(pos), (ICubicWorld) world);
		if(cubeStructureGenerator.isAnchorPoint(CubePos.fromBlockCoords(pos)))
			sender.sendMessage(new TextComponentString("Dungeon cube is "+dc.name() +" and it is anchor point"));
		else
			sender.sendMessage(new TextComponentString("Dungeon cube is "+dc.name() +" and it is not an anchor point"));
	}
}
