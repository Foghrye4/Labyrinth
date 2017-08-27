package labyrinth.command;

import cubicchunks.util.CubePos;
import cubicchunks.world.ICubicWorld;
import labyrinth.worldgen.LabyrinthWorldGen;
import labyrinth.worldgen.VillageCubeStructureGenerator;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class LFindAVillage extends LCubeEditCommandBase {

	VillageCubeStructureGenerator cubeStructureGenerator = LabyrinthWorldGen.instance.villageCubeStructureGenerator;

	public LFindAVillage() {
		super();
	}
	@Override
	public String getName() {
		return "l_find_village";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/l_find_village [range]";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		World world = sender.getEntityWorld();
		BlockPos pos = sender.getPosition();
		CubePos cpos = CubePos.fromBlockCoords(pos);
		int range = 32;
		if (args.length > 0)
			range = Integer.parseInt(args[0]);
		cpos.forEachWithinRange(range, c -> {
			if (cubeStructureGenerator.isVillage(c, (ICubicWorld) world)) {
				sender.sendMessage(new TextComponentString("Found a village at: " + c.getXCenter() + ";" + c.getYCenter() + ";" + c.getZCenter()));
			}
		});
		sender.sendMessage(new TextComponentString("Done searching."));
	}
}
