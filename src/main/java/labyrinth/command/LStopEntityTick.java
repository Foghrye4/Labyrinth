package labyrinth.command;

import labyrinth.LabyrinthMod;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class LStopEntityTick extends LCubeEditCommandBase {

	public LStopEntityTick(){
		super();
	}
	@Override
	public String getName() {
		return "l_toggle_tick";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/l_toggle_tick";
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		LabyrinthMod.DEBUG_STOP_ENTITY_TICK=!LabyrinthMod.DEBUG_STOP_ENTITY_TICK;
		sender.sendMessage(new TextComponentString("DEBUG_STOP_ENTITY_TICK="+LabyrinthMod.DEBUG_STOP_ENTITY_TICK));
	}
}
