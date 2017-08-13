package labyrinth.command;

import labyrinth.village.UndergroundVillage;
import labyrinth.worldgen.LabyrinthWorldGen;
import labyrinth.worldgen.VillageCubeStructureGenerator;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class LVillageInfo extends LCubeEditCommandBase {
	
	VillageCubeStructureGenerator cubeStructureGenerator = LabyrinthWorldGen.instance.villageCubeStructureGenerator;

	public LVillageInfo(){
		super();
	}
	@Override
	public String getName() {
		return "l_village_info";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/l_village_info";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		World world = sender.getEntityWorld();
		BlockPos pos = sender.getPosition();
		for(Village village : ((WorldServer)world).villageCollection.getVillageList()) {
			if(!(village instanceof UndergroundVillage))
				continue;
			UndergroundVillage uVillage = (UndergroundVillage)village;
			BlockPos c = uVillage.getCenter();
			sender.sendMessage(new TextComponentString("Found an underground village at: "+c.getX()+";"+c.getY()+";"+c.getZ()));
			sender.sendMessage(new TextComponentString("Village radius: "+uVillage.getVillageRadius()));
			if(uVillage.isBlockPosWithinSqVillageRadius(pos)){
				return;
			}
		}
		sender.sendMessage(new TextComponentString("Nothing is founded"));
	}
}
