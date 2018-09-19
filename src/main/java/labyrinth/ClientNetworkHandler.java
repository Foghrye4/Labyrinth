package labyrinth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import labyrinth.gui.GuiVillageMarket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value = Side.CLIENT)
public class ClientNetworkHandler extends ServerNetworkHandler {

	@SubscribeEvent
	public void onPacketFromServerToClient(FMLNetworkEvent.ClientCustomPacketEvent event) throws IOException {
		ByteBuf data = event.getPacket().payload();
		PacketBuffer byteBufInputStream = new PacketBuffer(data);
		Minecraft mc = Minecraft.getMinecraft();
		WorldClient world = mc.world;
		EntityPlayerSP player = mc.player;
		switch (ClientCommands.values()[byteBufInputStream.readByte()]) {
			case SHOW_ERASER_FRAME:
				BlockPos from = byteBufInputStream.readBlockPos();
				BlockPos to = byteBufInputStream.readBlockPos();
				ClientProxy.drawBlockHighlightHandler.setNewPos(from, to);
				ClientProxy.drawBlockHighlightHandler.drawEraserFrame=true;
				break;
			case HIDE_ERASER_FRAME:
				ClientProxy.drawBlockHighlightHandler.drawEraserFrame=false;
				break;
			case OPEN_GUI_VILLAGE_MARKET:
				int windowId = byteBufInputStream.readInt();
				int villagersListSize = byteBufInputStream.readInt();
				List<Entity> villagers = new ArrayList<Entity>(villagersListSize);
				while(villagersListSize--!=0){
					villagers.add(world.getEntityByID(byteBufInputStream.readInt()));
				}
				mc.displayGuiScreen(new GuiVillageMarket(player.inventory, villagers, world));
				player.openContainer.windowId=windowId;
				break;
		}
	}

	public void sendVilageMarketSelectedMerchant(int selectedMerchant) {
		WorldClient world = Minecraft.getMinecraft().world;
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		ByteBuf bb = Unpooled.buffer(36);
		PacketBuffer byteBufOutputStream = new PacketBuffer(bb);
		byteBufOutputStream.writeByte(ServerCommands.UPDATE_SELECTED_MERCHANT.ordinal());
		byteBufOutputStream.writeInt(player.getEntityId());
		byteBufOutputStream.writeInt(world.provider.getDimension());
		byteBufOutputStream.writeInt(selectedMerchant);
		channel.sendToServer(new FMLProxyPacket(byteBufOutputStream, LabyrinthMod.MODID));
	}
}
