package labyrinth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import labyrinth.command.LabyrinthCommands;
import labyrinth.worldgen.LabyrinthWorldGen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = LabyrinthMod.MODID, name = LabyrinthMod.NAME, version = LabyrinthMod.VERSION, dependencies = "required-after:cubicchunks")
public class LabyrinthMod {
	public static final String MODID = "labyrinth";
	public static final String NAME = "Labyrinth";
	public static final String VERSION = "0.4.0";

	public static Logger log;

	public static boolean DEBUG_STOP_ENTITY_TICK = false;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) throws IOException, IllegalAccessException {
		log = event.getModLog();
		LabyrinthWorldGen worldgen = new LabyrinthWorldGen();
		MinecraftForge.TERRAIN_GEN_BUS.register(worldgen);
		MinecraftForge.EVENT_BUS.register(worldgen);
		MinecraftForge.EVENT_BUS.register(worldgen.storage);
	}
	
	@EventHandler
	public void serverStart(FMLServerStartingEvent event) {
		event.registerServerCommand(new LabyrinthCommands());
	}
	
	public static InputStream getResourceInputStream(World world, ResourceLocation resource) {
		File resourceFile = new File(world.getSaveHandler().getWorldDirectory(), "/data/" + resource.getResourceDomain() + "/" + resource.getResourcePath());
		if(resourceFile.exists()) {
			try {
				return new FileInputStream(resourceFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		String resourceURLPath = "/assets/" + resource.getResourceDomain() + "/" + resource.getResourcePath();
		return LabyrinthMod.class.getResourceAsStream(resourceURLPath);
	}
	
	public static OutputStream getOutputStream(World world, ResourceLocation resource) throws IOException {
		File folder = new File(world.getSaveHandler().getWorldDirectory(), "/data/" + resource.getResourceDomain() + "/");
		if (!folder.exists() && !folder.mkdirs())
			throw new IOException("Cannot create new directory at " + folder.getAbsolutePath());
		File resourceFile = new File(folder, resource.getResourcePath());
		resourceFile.createNewFile();
		return new FileOutputStream(resourceFile);
	}	
	

	@NetworkCheckHandler
	public boolean checkModLists(Map<String, String> modList, Side sideIn) {
		return true;
	}
}
