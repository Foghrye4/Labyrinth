package labyrinth.command;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import cubicchunks.util.CubePos;
import labyrinth.worldgen.DungeonCube;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class LPlaceCubeCommand extends LCubeEditCommandBase {
	
	public static String last_placed_dungeon_type = "";
	
	public LPlaceCubeCommand(){
		super();
	}
	@Override
	public String getName() {
		return "l_place_cube";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/l_place_cube filename [x_offset y_offset z_offset boolean_rotatecv]/[mirrorX mirrorY mirrorZ]";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(args==null || args.length==0){
			sender.sendMessage(new TextComponentString("File name cannot be empty."));
			throw new CommandException("File name cannot be empty.", sender);
		}
		World world =sender.getEntityWorld();
		BlockPos pos = CubePos.fromBlockCoords(sender.getPosition()).getMinBlockPos();
		String cubeS = args[0];
		if (!cubeS.contains(".cube_structure")) {
			cubeS = cubeS + ".cube_structure";
		}
		int cn = -1;
		try {
			cn = Integer.parseInt(cubeS);
		}
		catch(NumberFormatException e) {
			cn = -1;
		}
		if(cn!=-1){
			cubeS = DungeonCube.values()[cn].name;
		}
		try {
			int index = 0;
			InputStream is = new FileInputStream(getFile("cubes",cubeS));
			DataInputStream dis = new DataInputStream(is);
			boolean rotateCV = args.length==5 && Boolean.valueOf(args[4]);
			boolean mirrorX = args.length==4 && Boolean.valueOf(args[1]);
			boolean mirrorY = args.length==4 && Boolean.valueOf(args[2]);
			boolean mirrorZ = args.length==4 && Boolean.valueOf(args[3]);
			this.writeStructureToEBS(world, pos, args, index, dis, rotateCV, mirrorX, mirrorY, mirrorZ);
			dis.close();
			is.close();
			is = new FileInputStream(getFile("cubes",cubeS));
			dis = new DataInputStream(is);
			this.writeStructureToEBS(world, pos, args, index, dis, rotateCV, mirrorX, mirrorY, mirrorZ);
			dis.close();
			is.close();
			last_placed_dungeon_type = cubeS;
		} catch (IOException e) {
			e.printStackTrace();
		}
		sender.sendMessage(new TextComponentString("Done placing "+cubeS));
	}
	
	private void writeStructureToEBS(World world,BlockPos pos, String[] args, int index, DataInputStream dis, boolean rotateCV, boolean mirrorX, boolean mirrorY, boolean mirrorZ){
		try {
			while(dis.available()>0){
				int dx = index>>>8;
				int dy = (index>>>4)&15;
				int dz = index&15;
				IBlockState blockstate = blockstateList.get(dis.readUnsignedByte());
				if(mirrorX){
					dx=15-dx;
					blockstate = blockstate.withMirror(Mirror.FRONT_BACK);
				}
				if(mirrorY){
					dy=15-dy;
				}
				if(mirrorZ){
					dz=15-dz;
					blockstate = blockstate.withMirror(Mirror.LEFT_RIGHT);
				}
				if(rotateCV) {
					dx = 15-(index&15);
					dz = index>>>8;
					blockstate = blockstate.withRotation(Rotation.CLOCKWISE_90);
				}
				if(args.length == 5){
					dx+=Integer.parseInt(args[1]);
					dy+=Integer.parseInt(args[2]);
					dz+=Integer.parseInt(args[3]);
				}
				world.setBlockState(pos.east(dx).up(dy).south(dz), blockstate);
				index++;
			}
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}

	}
}
