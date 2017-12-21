package labyrinth;

import java.io.IOException;

import org.apache.logging.log4j.Logger;

import labyrinth.command.*;
import labyrinth.config.LabyrinthConfig;
import labyrinth.entity.EntityEraserFrame;
import labyrinth.event.EntityEventHandler;
import labyrinth.init.LabyrinthBlocks;
import labyrinth.init.LabyrinthEntities;
import labyrinth.init.LabyrinthItems;
import labyrinth.init.RegistryEventHandler;
import labyrinth.tileentity.TileEntityVillageMarket;
import labyrinth.world.WorldEventHandler;
import labyrinth.worldgen.LabyrinthWorldGen;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = LabyrinthMod.MODID, name = LabyrinthMod.NAME, version = LabyrinthMod.VERSION, guiFactory = LabyrinthMod.GUI_FACTORY, dependencies = "required-after:cubicchunks")
public class LabyrinthMod {
	public static final String MODID = "labyrinth";
	public static final String NAME = "Labyrinth";
	public static final String VERSION = "0.2.6";
	public static final String GUI_FACTORY = "labyrinth.gui.LabyrinthGuiFactory";

	public static Logger log;
	@SidedProxy(clientSide = "labyrinth.ClientProxy", serverSide = "labyrinth.ServerProxy")
	public static ServerProxy proxy;
	public static LabyrinthConfig config;

	public static boolean DEBUG_STOP_ENTITY_TICK = false;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) throws IOException, IllegalAccessException {
		log = event.getModLog();
		config = new LabyrinthConfig(new Configuration(event.getSuggestedConfigurationFile()));
		MinecraftForge.EVENT_BUS.register(config);
		MinecraftForge.EVENT_BUS.register(new WorldEventHandler());
		LabyrinthBlocks.init();
		LabyrinthItems.init();
		LabyrinthBlocks.register();
		LabyrinthItems.register();
		proxy.preInit();
		LabyrinthEntities.register(this);
		MinecraftForge.EVENT_BUS.register(new LabyrinthWorldGen());
		MinecraftForge.EVENT_BUS.register(new EntityEventHandler());
		MinecraftForge.EVENT_BUS.register(new RegistryEventHandler());
		if (config.shouldRemoveMobSpawn())
			for (Biome biome : Biome.REGISTRY) {
				biome.getSpawnableList(EnumCreatureType.MONSTER).clear();
			}
		EntityRegistry.registerModEntity(new ResourceLocation(MODID, "eraser_frame"), EntityEraserFrame.class, "EraserFrame", 255, this, 64, 1, true);
		GameRegistry.registerTileEntity(TileEntityVillageMarket.class, MODID+":counter");
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.registerRenders();
	}


	@EventHandler
	public void serverStart(FMLServerStartingEvent event) {
		event.registerServerCommand(new LWriteCubeCommand());
		event.registerServerCommand(new LPlaceCubeCommand());
		event.registerServerCommand(new LMixInCubeCommand());
		event.registerServerCommand(new LGetStructureInfo());
		event.registerServerCommand(new LPlaceStructureBlock());
		event.registerServerCommand(new LWriteWithRotationsCommand());
		event.registerServerCommand(new LStopEntityTick());
		event.registerServerCommand(new LFindAVillage());
		event.registerServerCommand(new LVillageInfo());
		event.registerServerCommand(new LThickenWalls());
		event.registerServerCommand(new LAlignPlatforms());
		event.registerServerCommand(new LRemoveFence());
	}
}
