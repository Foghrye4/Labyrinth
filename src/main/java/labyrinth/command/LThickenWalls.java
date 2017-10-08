package labyrinth.command;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import labyrinth.worldgen.DungeonCube;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class LThickenWalls extends LCubeEditCommandBase {

	public LThickenWalls() {
		super();
	}
	@Override
	public String getName() {
		return "l_thicken_walls";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/l_thicken_walls";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		for (DungeonCube dcube : DungeonCube.values()) {
			byte[] data = dcube.data;
			boolean isSomeKindOfWall = false;
			if (dcube.isWestWall) {
				isSomeKindOfWall = true;
				for (int ix = 0; ix < 8; ix++)
					for (int iy = 0; iy <= 16; iy++) {
						int index1 = this.indexOf(ix, iy, 7);
						int index2 = this.indexOf(ix, iy, 8);
						if (data[index1] == 1)
							data[index2] = 1;
						if (data[index2] == 1)
							data[index1] = 1;
					}
			}
			if (dcube.isEastWall) {
				isSomeKindOfWall = true;
				for (int ix = 8; ix < 16; ix++)
					for (int iy = 0; iy <= 16; iy++) {
						int index1 = this.indexOf(ix, iy, 7);
						int index2 = this.indexOf(ix, iy, 8);
						if (data[index1] == 1)
							data[index2] = 1;
						if (data[index2] == 1)
							data[index1] = 1;
					}
			}
			if (dcube.isNorthWall) {
				isSomeKindOfWall = true;
				for (int iz = 0; iz < 8; iz++)
					for (int iy = 0; iy <= 16; iy++) {
						int index1 = this.indexOf(7, iy, iz);
						int index2 = this.indexOf(8, iy, iz);
						if (data[index1] == 1)
							data[index2] = 1;
						if (data[index2] == 1)
							data[index1] = 1;
					}
			}
			if (dcube.isSouthWall) {
				isSomeKindOfWall = true;
				for (int iz = 8; iz < 16; iz++)
					for (int iy = 0; iy <= 16; iy++) {
						int index1 = this.indexOf(7, iy, iz);
						int index2 = this.indexOf(8, iy, iz);
						if (data[index1] == 1)
							data[index2] = 1;
						if (data[index2] == 1)
							data[index1] = 1;
					}
			}
			if (!isSomeKindOfWall)
				continue;
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

	private int indexOf(int x, int y, int z) {
		return x << 8 | y << 4 | z;
	}
}
