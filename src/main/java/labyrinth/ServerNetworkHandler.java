package labyrinth;

import static labyrinth.LabyrinthMod.MODID;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public class ServerNetworkHandler {

	protected static final FMLEventChannel channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(MODID);

	public ServerNetworkHandler() {
		channel.register(this);
	}

	public void showEraserFrameForPlayer(EntityPlayer player, BlockPos from, BlockPos to) {
		ByteBuf bb = Unpooled.buffer(36);
		PacketBuffer byteBufOutputStream = new PacketBuffer(bb);
		byteBufOutputStream.writeByte(0);
		byteBufOutputStream.writeBlockPos(from);
		byteBufOutputStream.writeBlockPos(to);
		channel.sendTo(new FMLProxyPacket(byteBufOutputStream, LabyrinthMod.MODID), (EntityPlayerMP) player);
	}
}
