package labyrinth;

import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import static labyrinth.LabyrinthMod.MODID;

public class ServerNetworkHandler {

	protected static final FMLEventChannel channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(MODID);

	public ServerNetworkHandler() {
		channel.register(this);
	}
}
