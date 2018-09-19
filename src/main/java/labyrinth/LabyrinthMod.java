package labyrinth;

import java.io.IOException;

import org.apache.logging.log4j.Logger;

import labyrinth.command.*;
import labyrinth.config.LabyrinthConfig;
import labyrinth.event.EntityEventHandler;
import labyrinth.init.LabyrinthBlocks;
import labyrinth.init.LabyrinthEntities;
import labyrinth.init.LabyrinthItems;
import labyrinth.init.RegistryEventHandler;
import labyrinth.item.LabyrinthCreativeTab;
import labyrinth.tileentity.TileEntityVillageMarket;
import labyrinth.world.WorldEventHandler;
import labyrinth.worldgen.LabyrinthWorldGen;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = LabyrinthMod.MODID, name = LabyrinthMod.NAME, version = LabyrinthMod.VERSION, guiFactory = LabyrinthMod.GUI_FACTORY, dependencies = "required-after:cubicchunks")
public class LabyrinthMod {
	public static final String MODID = "labyrinth";
	public static final String NAME = "Labyrinth";
	public static final String VERSION = "0.3.0";
	public static final String GUI_FACTORY = "labyrinth.gui.LabyrinthGuiFactory";

	public static Logger log;
	@SidedProxy(clientSide = "labyrinth.ClientProxy", serverSide = "labyrinth.ServerProxy")
	public static ServerProxy proxy;
	public static LabyrinthConfig config;

	public static boolean DEBUG_STOP_ENTITY_TICK = false;
	public static LabyrinthCreativeTab creativeTab;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) throws IOException, IllegalAccessException {
		log = event.getModLog();
		config = new LabyrinthConfig(new Configuration(event.getSuggestedConfigurationFile()));
		MinecraftForge.EVENT_BUS.register(config);
		MinecraftForge.EVENT_BUS.register(new WorldEventHandler());
		creativeTab = new LabyrinthCreativeTab("labyrinth.tab");
		LabyrinthBlocks.init();
		LabyrinthItems.init();
		LabyrinthBlocks.register();
		LabyrinthItems.register();
		proxy.preInit();
		LabyrinthEntities.register(this);
		MinecraftForge.EVENT_BUS.register(new EntityEventHandler());
		MinecraftForge.EVENT_BUS.register(new RegistryEventHandler());
		MinecraftForge.EVENT_BUS.register(new LabyrinthWorldGen());
		if (config.shouldRemoveMobSpawn())
			for (Biome biome : Biome.REGISTRY) {
				biome.getSpawnableList(EnumCreatureType.MONSTER).clear();
			}
		TileEntity.register(MODID+":counter", TileEntityVillageMarket.class);
	}
	
	
	@EventHandler
	public void init(FMLInitializationEvent event) throws IOException {
		LabyrinthWorldGen.instance.storage.defineCandidates();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		LabyrinthMod.proxy.registerRenders();
	}


	@EventHandler
	public void serverStart(FMLServerStartingEvent event) {
		event.registerServerCommand(new LWriteCubeCommand());
		event.registerServerCommand(new LPlaceCubeCommand());
		event.registerServerCommand(new LMixInCubeCommand());
//		event.registerServerCommand(new LGetStructureInfo());
//		event.registerServerCommand(new LPlaceStructureBlock());
		event.registerServerCommand(new LWriteWithRotationsCommand());
//		event.registerServerCommand(new LVillageInfo());
//		event.registerServerCommand(new LThickenWalls());
//		event.registerServerCommand(new LAlignPlatforms());
//		event.registerServerCommand(new LRemoveFence());
	}
}
