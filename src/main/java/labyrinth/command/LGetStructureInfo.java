package labyrinth.command;

import io.github.opencubicchunks.cubicchunks.api.util.CubePos;
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorld;
import labyrinth.worldgen.DungeonCube;
import labyrinth.worldgen.LabyrinthWorldGen;
import labyrinth.worldgen.LavaCubeStructureGenerator;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class LGetStructureInfo extends LCubeEditCommandBase {
	
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
		DungeonCube dc = LabyrinthWorldGen.instance.getDungeonCubeType(CubePos.fromBlockCoords(pos), (World) world);
		sender.sendMessage(new TextComponentString("Dungeon cube is "+dc.name));
	}
}
