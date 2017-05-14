package labyrinth.world;

import labyrinth.worldgen.LabyrinthWorldGen;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WorldEventHandler {
	private final static String DATA_IDENTIFIER = "labyrinthWorldgenConfig";

	@SubscribeEvent
	public void onWorldLoadEvent(net.minecraftforge.event.world.WorldEvent.Load event) {
		if(!event.getWorld().isRemote){
			WorldSavedDataLabyrinthConfig worldgenConfig = (WorldSavedDataLabyrinthConfig) event.getWorld().getMapStorage()
					.getOrLoadData(WorldSavedDataLabyrinthConfig.class, DATA_IDENTIFIER);
			if (worldgenConfig == null) {
				worldgenConfig = new WorldSavedDataLabyrinthConfig(DATA_IDENTIFIER);
				worldgenConfig.setDirty(true);
				event.getWorld().getMapStorage().setData(DATA_IDENTIFIER, worldgenConfig);
				event.getWorld().getMapStorage().saveAllData();
			}
			LabyrinthWorldGen.instance.setConfig(worldgenConfig);
		}
	}
}