package labyrinth.client;

import labyrinth.client.model.ModelMiniSpider;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientEventHandler {
	/*
	@SubscribeEvent
	public void onChatMesage(ClientChatEvent event) {
		String text = event.getMessage();
		if (text.startsWith("/leg")) {
			String[] args = text.split(" ");
			if (args[1].equalsIgnoreCase("ps")) {
				if(args.length<8)
					return;
				float x = Float.parseFloat(args[2]);
				float y = Float.parseFloat(args[3]);
				float z = Float.parseFloat(args[4]);
				int dx = Integer.parseInt(args[5]);
				int dy = Integer.parseInt(args[6]);
				int dz = Integer.parseInt(args[7]);
				ModelMiniSpider.setLegPosAndScale(x, y, z, dx, dy, dz);
			}
			if (args[1].equalsIgnoreCase("rp")) {
				if(args.length<5)
					return;
				float x = Float.parseFloat(args[2]);
				float y = Float.parseFloat(args[3]);
				float z = Float.parseFloat(args[4]);
				ModelMiniSpider.setLegRotationPoint(x, y, z);
			}
			if (args[1].equalsIgnoreCase("fr")) {
				if (args[2].equalsIgnoreCase("y"))
					ModelMiniSpider.flipRotateY();
				if (args[2].equalsIgnoreCase("z"))
					ModelMiniSpider.flipRotateZ();

			}
		}
		if (text.startsWith("/body")) {
			String[] args = text.split(" ");
			if (args[1].equalsIgnoreCase("ps")) {
				float x = Float.parseFloat(args[2]);
				float y = Float.parseFloat(args[3]);
				float z = Float.parseFloat(args[4]);
				int dx = Integer.parseInt(args[5]);
				int dy = Integer.parseInt(args[6]);
				int dz = Integer.parseInt(args[7]);
				ModelMiniSpider.setBodyPosAndScale(x, y, z, dx, dy, dz);
			}
		}
	}*/
}