package labyrinth.command;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import labyrinth.worldgen.DungeonCube;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import static labyrinth.worldgen.DungeonCube.*;

public class LRemoveFence extends LCubeEditCommandBase {

	private DungeonCube[] lavaDungeonsArray = new DungeonCube[]{
			EAST_BORDER_WITH_WALL_SOUTH_NORTH_LAVA,
			NORTH_BORDER_WITH_WALL_EAST_WEST_LAVA, 
			SOUTH_BORDER_WITH_WALL_EAST_WEST_LAVA, 
			WEST_BORDER_WITH_WALL_SOUTH_NORTH_LAVA, 
			
			EAST_BORDER_WITH_WALL_SOUTH_WEST_LAVA,
			NORTH_BORDER_WITH_WALL_EAST_SOUTH_LAVA, 
			SOUTH_BORDER_WITH_WALL_WEST_NORTH_LAVA, 
			WEST_BORDER_WITH_WALL_NORTH_EAST_LAVA, 

			EAST_BORDER_WITH_WALL_WEST_NORTH_LAVA,
			NORTH_BORDER_WITH_WALL_SOUTH_WEST_LAVA, 
			SOUTH_BORDER_WITH_WALL_NORTH_EAST_LAVA, 
			WEST_BORDER_WITH_WALL_EAST_SOUTH_LAVA, 

			NORTH_EAST_BORDER_LAVA,
			WEST_NORTH_BORDER_LAVA, 
			EAST_SOUTH_BORDER_LAVA, 
			SOUTH_WEST_BORDER_LAVA, 

			NORTH_BORDER_COLUMN_FLOOR_LAVA, 
			SOUTH_BORDER_COLUMN_FLOOR_LAVA, 
			WEST_BORDER_COLUMN_FLOOR_LAVA, 
			EAST_BORDER_COLUMN_FLOOR_LAVA, 
			
			WALL_EAST_LAVA,
			WALL_SOUTH_LAVA,
			WALL_NORTH_LAVA,
			WALL_WEST_LAVA,
			
			WALL_EAST_NORTH_SOUTH_LAVA,
			WALL_SOUTH_EAST_WEST_LAVA,
			WALL_WEST_SOUTH_NORTH_LAVA,
			WALL_NORTH_WEST_EAST_LAVA,
			
			EAST_BORDER_WITH_WALLS_LAVA,
			NORTH_BORDER_WITH_WALLS_LAVA, 
			SOUTH_BORDER_WITH_WALLS_LAVA, 
			WEST_BORDER_WITH_WALLS_LAVA, 
			STAIR_FLOOR_W_ROOM_OT_NORTH_EAST_LAVA, 
			STAIR_FLOOR_W_ROOM_OT_NORTH_WEST_LAVA, 
			STAIR_FLOOR_W_ROOM_OT_SOUTH_EAST_LAVA, 
			STAIR_FLOOR_W_ROOM_OT_SOUTH_WEST_LAVA, 
			STAIR_TOP_W_ROOM_OT_NORTH_EAST_LAVA, 
			STAIR_TOP_W_ROOM_OT_NORTH_WEST_LAVA, 
			STAIR_TOP_W_ROOM_OT_SOUTH_EAST_LAVA,
			STAIR_TOP_W_ROOM_OT_SOUTH_WEST_LAVA,
			WALL_X_LAVA,
			WALL_EAST_NORTH_LAVA,
			WALL_EAST_SOUTH_LAVA,
			WALL_SOUTH_NORTH_LAVA,
			WALL_WEST_EAST_LAVA,
			WALL_WEST_NORTH_LAVA,
			WALL_WEST_SOUTH_LAVA};

	
	public LRemoveFence() {
		super();
	}
	@Override
	public String getName() {
		return "l_remove_fence";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/l_remove_fence";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		for (DungeonCube dcube : this.lavaDungeonsArray) {
			byte[] data = dcube.data;
			for(int index=0;index<data.length;index++){
				int dx = index>>>8;
				int dz = index&15;
				if(dx==0 && !dcube.isWestWall)
					continue;
				if(dx==15 && !dcube.isEastWall)
					continue;
				if(dz==0 && !dcube.isNorthWall)
					continue;
				if(dz==15 && !dcube.isSouthWall)
					continue;
				if(data[index] == 120) {
					data[index] = 0;
				}
			}
			DataOutputStream osWriter = null;
			try {
				osWriter = new DataOutputStream(new FileOutputStream(getFile("cubes", dcube.name)));
				osWriter.write(data);
				osWriter.close();
			} catch (IOException e) {
				throw new CommandException("I/O error", sender);
			}
		}
		sender.sendMessage(new TextComponentString("Done"));
	}
}
