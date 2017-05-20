package labyrinth.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LPlaceStructureBlock extends LCubeEditCommandBase {
	
	public static String last_placed_dungeon_type = "";
	
	public LPlaceStructureBlock(){
		super();
	}
	@Override
	public String getName() {
		return "l_place_sb";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/l_place_sb block_index";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		World world = sender.getEntityWorld();
		BlockPos pos = sender.getPosition();
		world.setBlockState(pos, blockstateList.get(Integer.parseInt(args[0])));
	}
}
