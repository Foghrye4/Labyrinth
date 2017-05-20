package labyrinth;

import static labyrinth.LabyrinthMod.MODID;

import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class ServerNetworkHandler {

	protected static final FMLEventChannel channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(MODID);

	public ServerNetworkHandler() {
		channel.register(this);
	}
}
