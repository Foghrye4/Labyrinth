package labyrinth;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value = Side.CLIENT)
public class ClientNetworkHandler extends ServerNetworkHandler {

	@SubscribeEvent
	public void onPacketFromServerToClient(FMLNetworkEvent.ClientCustomPacketEvent event) throws IOException {
		ByteBuf data = event.getPacket().payload();
		ByteBufInputStream byteBufInputStream = new ByteBufInputStream(data);
		switch (byteBufInputStream.read()) {
		case 0:
			break;
		case 1:
			break;
		}
		byteBufInputStream.close();
	}
}
