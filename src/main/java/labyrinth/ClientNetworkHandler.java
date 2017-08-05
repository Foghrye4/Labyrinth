package labyrinth;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import labyrinth.entity.EntityEraserFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value = Side.CLIENT)
public class ClientNetworkHandler extends ServerNetworkHandler {


	@SubscribeEvent
	public void onPacketFromServerToClient(FMLNetworkEvent.ClientCustomPacketEvent event) throws IOException {
		ByteBuf data = event.getPacket().payload();
		PacketBuffer byteBufInputStream = new PacketBuffer(data);
		switch (byteBufInputStream.readByte()) {
			case 0 :
				Minecraft mc = Minecraft.getMinecraft();
				WorldClient world = mc.world;
				for (Entity entity : world.loadedEntityList) {
					if (entity instanceof EntityEraserFrame)
						entity.setDead();
				}
				BlockPos from = byteBufInputStream.readBlockPos();
				BlockPos to = byteBufInputStream.readBlockPos();
				Entity entity = new EntityEraserFrame(world);
				entity.setPosition(to.getX(), to.getY(), to.getZ());
				int x1 = from.getX();
				int y1 = from.getY();
				int z1 = from.getZ();
				int x2 = to.getX();
				int y2 = to.getY();
				int z2 = to.getZ();
				x1+=x1>x2?1:0;
				y1+=y1>y2?1:0;
				z1+=z1>z2?1:0;
				x2+=x2>x1?1:0;
				y2+=y2>y1?1:0;
				z2+=z2>z1?1:0;
				entity.setEntityBoundingBox(new AxisAlignedBB(x1,y1,z1,x2,y2,z2));
				world.spawnEntity(entity);
				break;
			case 1 :
				break;
		}
	}

	public void syncEntityBoundingBox(Entity entity) {
	}

}
