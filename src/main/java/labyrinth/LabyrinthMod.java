package labyrinth;

import java.io.IOException;

import org.apache.logging.log4j.Logger;

import labyrinth.command.LPlaceCubeCommand;
import labyrinth.command.LReplaceCubeFloorCommand;
import labyrinth.command.LWriteCubeCommand;
import labyrinth.worldgen.LabyrinthWorldGen;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = LabyrinthMod.MODID, name = LabyrinthMod.NAME, version = LabyrinthMod.VERSION, dependencies = "required-after:cubicchunks")
public class LabyrinthMod {
	public static final String MODID = "labyrinth";
	public static final String NAME = "Labyrinth";
	public static final String VERSION = "0.001";

	public static Logger log;
	@SidedProxy(clientSide = "labyrinth.ClientProxy", serverSide = "labyrinth.ServerProxy")
	public static ServerProxy proxy;
	@SidedProxy(clientSide = "labyrinth.ClientNetworkHandler", serverSide = "labyrinth.ServerNetworkHandler")
	public static ServerNetworkHandler network;
		
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) throws IOException, IllegalAccessException {
		log = event.getModLog();
		network = new ServerNetworkHandler();
		GameRegistry.registerWorldGenerator(new LabyrinthWorldGen(), 0);
	}
	
	@EventHandler
	public void init(FMLPostInitializationEvent event) throws IOException, IllegalAccessException {
		proxy.load();
	}
	
    @EventHandler
    public void serverStart(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new LWriteCubeCommand());
        event.registerServerCommand(new LPlaceCubeCommand());
        //event.registerServerCommand(new LReplaceCubeFloorCommand());
    }

}
