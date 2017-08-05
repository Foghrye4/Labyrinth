package labyrinth.event;

import cubicchunks.util.CubePos;
import cubicchunks.world.ICubicWorld;
import labyrinth.LabyrinthMod;
import labyrinth.worldgen.LabyrinthWorldGen;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityEventHandler {

	@SubscribeEvent
	public void onEntityCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
		if (LabyrinthMod.config.shouldSkipSpawnVanillaMobsInDungeons()) {
			CubePos pos = CubePos.fromBlockCoords((int) (event.getX() - 0.5f), (int) event.getY(), (int) (event.getZ() - 0.5f));
			ICubicWorld world = (ICubicWorld) event.getWorld();
			if (LabyrinthWorldGen.instance.shouldGenerateAtPos(pos, world)) {
				event.setResult(Result.DENY);
			}
		}
	}
}
