package labyrinth;

import static labyrinth.LabyrinthMod.MODID;

import java.io.IOException;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import labyrinth.inventory.ContainerVillageMarket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.server.FMLServerHandler;

public class ServerNetworkHandler {

	protected static final FMLEventChannel channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(MODID);

	public enum ServerCommands {
		UPDATE_SELECTED_MERCHANT;
	}
	
	public enum ClientCommands {
		SHOW_ERASER_FRAME,
		OPEN_GUI_VILLAGE_MARKET;
	}

	public ServerNetworkHandler() {
		channel.register(this);
	}
	
	@SubscribeEvent
	public void onPacketFromClientToServer(FMLNetworkEvent.ServerCustomPacketEvent event) throws IOException {
		ByteBuf data = event.getPacket().payload();
		ByteBufInputStream byteBufInputStream = new ByteBufInputStream(data);
		int playerEntityId;
		int worldDimensionId;
		switch (ServerCommands.values()[byteBufInputStream.read()]) {
		case UPDATE_SELECTED_MERCHANT:
			playerEntityId = byteBufInputStream.readInt();
			worldDimensionId = byteBufInputStream.readInt();
			int merchantId = byteBufInputStream.readInt();
			if(FMLServerHandler.instance().getServer()==null){
				byteBufInputStream.close();
				throw new NullPointerException("Server is null");
			}
			World world = FMLServerHandler.instance().getServer().getWorld(worldDimensionId);
			EntityPlayerMP player = (EntityPlayerMP) world.getEntityByID(playerEntityId);
			((ContainerVillageMarket)player.openContainer).setCurrentMerchant(merchantId);
			break;
		}
		byteBufInputStream.close();
	}

	public void showEraserFrameForPlayer(EntityPlayer player, BlockPos from, BlockPos to) {
		ByteBuf bb = Unpooled.buffer(36);
		PacketBuffer byteBufOutputStream = new PacketBuffer(bb);
		byteBufOutputStream.writeByte(ClientCommands.SHOW_ERASER_FRAME.ordinal());
		byteBufOutputStream.writeBlockPos(from);
		byteBufOutputStream.writeBlockPos(to);
		channel.sendTo(new FMLProxyPacket(byteBufOutputStream, LabyrinthMod.MODID), (EntityPlayerMP) player);
	}

	@SuppressWarnings("rawtypes")
	public void sendOpenGuiVillageMarket(List vl, EntityPlayer playerIn, World worldIn) {
	}

	public void openGuiVillageMarket(List<IMerchant> vl, EntityPlayer playerIn, World worldIn) {
		EntityPlayerMP playerMP = (EntityPlayerMP) playerIn;
		playerMP.getNextWindowId();
		playerMP.openContainer = new ContainerVillageMarket(playerMP.inventory, vl, worldIn);
		playerMP.openContainer.windowId = playerMP.currentWindowId;
		playerMP.openContainer.addListener(playerMP);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerContainerEvent.Open(playerMP, playerMP.openContainer));
		ByteBuf bb = Unpooled.buffer(36);
		PacketBuffer byteBufOutputStream = new PacketBuffer(bb);
		byteBufOutputStream.writeByte(ClientCommands.OPEN_GUI_VILLAGE_MARKET.ordinal());
		byteBufOutputStream.writeInt(playerIn.openContainer.windowId);
		byteBufOutputStream.writeInt(vl.size());
		for(Object villager:vl){
			byteBufOutputStream.writeInt(((Entity)villager).getEntityId());
		}
		channel.sendTo(new FMLProxyPacket(byteBufOutputStream, LabyrinthMod.MODID), playerMP);
	}

	public void sendVilageMarketSelectedMerchant(int selectedMerchant) {
	}
}
