package labyrinth.event;

import cubicchunks.util.CubePos;
import cubicchunks.world.ICubicWorld;
import labyrinth.LabyrinthMod;
import labyrinth.tileentity.TileEntityVillageMarket;
import labyrinth.worldgen.LabyrinthWorldGen;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityEventHandler {

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if(event.getWorld().isRemote)
			return;
		if(event.getEntity() instanceof EntityVillager){
			for(TileEntityVillageMarket market:TileEntityVillageMarket.eventListeners)
				market.updateMarket();
		}
		if(event.getEntity() instanceof EntityPlayerMP){
			for(TileEntityVillageMarket market:TileEntityVillageMarket.eventListeners){
				((EntityPlayerMP)event.getEntity()).connection.sendPacket(market.getUpdatePacket());
			}
		}
	}
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
