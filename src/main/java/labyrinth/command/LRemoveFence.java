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
		return "l_fill255";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/l_fill255";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		for (DungeonCube dcube : DungeonCube.values()) {
			if (dcube.name.equals(""))
				continue;
			sender.sendMessage(new TextComponentString("Making " + dcube.name));
			byte[] data = dcube.data;
			for (int ix = 0; ix < 16; ix++) {
				for (int iy = 0; iy < 16; iy++) {
					for (int iz = 0; iz < 16; iz++) {
						if (!this.isReplaceable(Byte.toUnsignedInt(data[indexOf(ix, iy, iz)]))) {
							continue;
						}
						if (ix > 0 && !this.isReplaceable(Byte.toUnsignedInt(data[indexOf(ix - 1, iy, iz)]))) {
							continue;
						}
						if (iy > 0 && !this.isReplaceable(Byte.toUnsignedInt(data[indexOf(ix, iy - 1, iz)]))) {
							continue;
						}
						if (iz > 0 && !this.isReplaceable(Byte.toUnsignedInt(data[indexOf(ix, iy, iz - 1)]))) {
							continue;
						}
						if (ix < 15 && !this.isReplaceable(Byte.toUnsignedInt(data[indexOf(ix + 1, iy, iz)]))) {
							continue;
						}
						if (iy < 15 && !this.isReplaceable(Byte.toUnsignedInt(data[indexOf(ix, iy + 1, iz)]))) {
							continue;
						}
						if (iz < 15 && !this.isReplaceable(Byte.toUnsignedInt(data[indexOf(ix, iy, iz + 1)]))) {
							continue;
						}
						data[indexOf(ix, iy, iz)] = (byte) 255;
					}
				}
			}
			DataOutputStream osWriter = null;
			try {
				osWriter = new DataOutputStream(new FileOutputStream(getFile(server.worlds[0],"cubes", dcube.name)));
				osWriter.write(data);
				osWriter.close();
				sender.sendMessage(new TextComponentString("Writing data for cube " + dcube.name));
			} catch (IOException e) {
				throw new CommandException("I/O error while writing cube " + dcube.name + " " + e.getMessage(), sender);
			}
		}
		sender.sendMessage(new TextComponentString("Done"));
	}
	private boolean isReplaceable(int value) {
		return value == 1 || value == 2 || value == 255;
	}
	private int indexOf(int x, int y, int z) {
		return x << 8 | y << 4 | z;
	}
}
