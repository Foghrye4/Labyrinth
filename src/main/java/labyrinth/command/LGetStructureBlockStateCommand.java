package labyrinth.command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import cubicchunks.util.CubePos;
import labyrinth.LabyrinthMod;
import labyrinth.worldgen.LabyrinthWorldGen;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class LGetStructureBlockStateCommand extends LCubeEditCommandBase {

	public LGetStructureBlockStateCommand(){
		super();
	}
	@Override
	public String getName() {
		return "l_get_blockstate";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/l_get_blockstate";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		Entity command_sender = sender.getCommandSenderEntity();
		if(command_sender instanceof EntityPlayerMP){
			World world = sender.getEntityWorld();
			EntityPlayerMP player = (EntityPlayerMP) command_sender;
			Vec3d look = player.getLookVec();
	        Vec3d eye_pos = new Vec3d(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ);
	        Vec3d vec3d2 = eye_pos.addVector(look.xCoord * 32, look.yCoord * 32, look.zCoord * 32);
			BlockPos pos = world.rayTraceBlocks(eye_pos, vec3d2, false, false, true).getBlockPos();
			IBlockState bs = world.getBlockState(pos);
			sender.sendMessage(new TextComponentString(bs.toString()));
			CubePos cpos = CubePos.fromBlockCoords(pos);
			int dx = pos.getX()-cpos.getMinBlockX();
			int dy = pos.getY()-cpos.getMinBlockY();
			int dz = pos.getZ()-cpos.getMinBlockZ();
			int index = dx<<8|dy<<4|dz;
			String name = LPlaceCubeCommand.last_placed_dungeon_type;
			byte[] data = new byte[4096];
			try {
				Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("labyrinth","cubes/"+name)).getInputStream().read(data);
				sender.sendMessage(new TextComponentString("Byte place = "+Byte.toUnsignedInt(data[index])));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
