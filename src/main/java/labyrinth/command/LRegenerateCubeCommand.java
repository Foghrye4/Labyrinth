package labyrinth.command;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import cubicchunks.api.worldgen.biome.CubicBiome;
import cubicchunks.util.CubePos;
import cubicchunks.world.ICubicWorld;
import labyrinth.LabyrinthMod;
import labyrinth.worldgen.LabyrinthWorldGen;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class LRegenerateCubeCommand extends CommandBase {
	
	public File getFile(String folder_name, String filename) {
		File folder = new File(LabyrinthMod.proxy.getMinecraftDir(), folder_name);
		folder.mkdirs();
		return new File(folder, filename);
	}

	@Override
	public String getName() {
		return "l_regenerate_cube";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return getName();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		ICubicWorld world = (ICubicWorld) sender.getEntityWorld();
		LabyrinthWorldGen.instance.verbose=true;
		LabyrinthWorldGen.instance.generate(world, new Random(), CubePos.fromBlockCoords(sender.getPosition()), CubicBiome.getCubic(world.getBiome(sender.getPosition())));
		LabyrinthWorldGen.instance.verbose=false;
	}
}

