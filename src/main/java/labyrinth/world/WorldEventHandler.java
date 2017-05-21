package labyrinth.world;

import labyrinth.util.LimitedSizeHashMap;
import labyrinth.worldgen.LabyrinthWorldGen;
import labyrinth.worldgen.LabyrinthWorldGen.DungeonCube;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WorldEventHandler {
	private final static String CONFIG_DATA_IDENTIFIER = "labyrinthWorldgenConfig";
	private final static String CACHE_DATA_IDENTIFIER = "labyrinthWorldgenCache";

	@SubscribeEvent
	public void onWorldLoadEvent(net.minecraftforge.event.world.WorldEvent.Load event) {
		if(!event.getWorld().isRemote){
			WorldSavedDataLabyrinthConfig worldgenConfig = (WorldSavedDataLabyrinthConfig) event.getWorld().getMapStorage()
					.getOrLoadData(WorldSavedDataLabyrinthConfig.class, CONFIG_DATA_IDENTIFIER);
			WorldSavedDataCubeGeneratorCache worldgenCubeCache = (WorldSavedDataCubeGeneratorCache) event.getWorld().getMapStorage()
					.getOrLoadData(WorldSavedDataCubeGeneratorCache.class, CACHE_DATA_IDENTIFIER);
			if (worldgenConfig == null) {
				worldgenConfig = new WorldSavedDataLabyrinthConfig(CONFIG_DATA_IDENTIFIER);
				worldgenConfig.setDirty(true);
				event.getWorld().getMapStorage().setData(CONFIG_DATA_IDENTIFIER, worldgenConfig);
				event.getWorld().getMapStorage().saveAllData();
			}
			if(worldgenCubeCache == null){
				worldgenCubeCache = new WorldSavedDataCubeGeneratorCache(CACHE_DATA_IDENTIFIER);
				event.getWorld().getMapStorage().setData(CACHE_DATA_IDENTIFIER, worldgenCubeCache);
			}
			LabyrinthWorldGen.instance.setConfig(worldgenConfig);
			LabyrinthWorldGen.instance.setCache(worldgenCubeCache.dungeonCubeCache);
		}
	}
	
	@SubscribeEvent
	public void onWorldSaveEvent(net.minecraftforge.event.world.WorldEvent.Save event) {
		WorldSavedDataCubeGeneratorCache worldgenCubeCache = (WorldSavedDataCubeGeneratorCache) event.getWorld().getMapStorage()
				.getOrLoadData(WorldSavedDataCubeGeneratorCache.class, CACHE_DATA_IDENTIFIER);
		if(worldgenCubeCache == null){
			worldgenCubeCache = new WorldSavedDataCubeGeneratorCache(CACHE_DATA_IDENTIFIER);
			event.getWorld().getMapStorage().setData(CACHE_DATA_IDENTIFIER, worldgenCubeCache);
		}
		worldgenCubeCache.setDirty(true);
		event.getWorld().getMapStorage().saveAllData();
	}
}