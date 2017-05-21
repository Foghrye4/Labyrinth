package labyrinth.world;

import labyrinth.worldgen.LabyrinthWorldGen;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WorldEventHandler {
	private final static String CONFIG_DATA_IDENTIFIER = "labyrinthWorldgenConfig";

	@SubscribeEvent
	public void onWorldLoadEvent(net.minecraftforge.event.world.WorldEvent.Load event) {
		if(!event.getWorld().isRemote){
			WorldSavedDataLabyrinthConfig worldgenConfig = (WorldSavedDataLabyrinthConfig) event.getWorld().getMapStorage()
					.getOrLoadData(WorldSavedDataLabyrinthConfig.class, CONFIG_DATA_IDENTIFIER);
			if (worldgenConfig == null) {
				worldgenConfig = new WorldSavedDataLabyrinthConfig(CONFIG_DATA_IDENTIFIER);
				worldgenConfig.setDirty(true);
				event.getWorld().getMapStorage().setData(CONFIG_DATA_IDENTIFIER, worldgenConfig);
				event.getWorld().getMapStorage().saveAllData();
			}
			LabyrinthWorldGen.instance.setConfig(worldgenConfig);
		}
	}
}