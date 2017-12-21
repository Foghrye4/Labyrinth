package labyrinth.command;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import labyrinth.worldgen.DungeonCube;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class LAlignPlatforms extends LCubeEditCommandBase {

	public LAlignPlatforms() {
		super();
	}
	@Override
	public String getName() {
		return "l_align_platforms";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/l_align_platforms";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		for (DungeonCube dcube : DungeonCube.values()) {
			byte[] data = dcube.data;
			boolean isSomeKindOfWall = false;
			if (dcube.isWestWall || dcube.isEastWall) {
				isSomeKindOfWall = true;
				for (int ix = 0; ix < 16; ix++) {
					int iy = 6;
					int platformIndexNorth1 = this.indexOf(ix, iy, 3);
					int platformIndexSouth1 = this.indexOf(ix, iy, 12);
					int platformIndexNorth2 = this.indexOf(ix, iy, 2);
					int platformIndexSouth2 = this.indexOf(ix, iy, 13);
					
					if (data[platformIndexNorth1] == 2)
						data[platformIndexNorth2] = 2;
					if (data[platformIndexSouth1] == 2)
						data[platformIndexSouth2] = 2;
					
					iy = 7;
					
					platformIndexNorth1 = this.indexOf(ix, iy, 3);
					platformIndexSouth1 = this.indexOf(ix, iy, 12);
					platformIndexNorth2 = this.indexOf(ix, iy, 2);
					platformIndexSouth2 = this.indexOf(ix, iy, 13);
					
					if (data[platformIndexNorth1] == 120) {
						data[platformIndexNorth2] = 120;
						data[platformIndexNorth1] = 0;
					}
					if (data[platformIndexSouth1] == 120) {
						data[platformIndexSouth2] = 120;
						data[platformIndexSouth1] = 0;
					}
				}
			}
			if (dcube.isNorthWall || dcube.isSouthWall) {
				isSomeKindOfWall = true;
				for (int iz = 0; iz < 16; iz++) {
					int iy = 6;
					int platformIndexWest1 = this.indexOf(3, iy, iz);
					int platformIndexEast1 = this.indexOf(12, iy, iz);
					int platformIndexWest2 = this.indexOf(2, iy, iz);
					int platformIndexEast2 = this.indexOf(13, iy, iz);
					
					if (data[platformIndexWest1] == 2)
						data[platformIndexWest2] = 2;
					if (data[platformIndexEast1] == 2)
						data[platformIndexEast2] = 2;
					
					iy = 7;
					
					platformIndexWest1 = this.indexOf(3, iy, iz);
					platformIndexEast1 = this.indexOf(12, iy, iz);
					platformIndexWest2 = this.indexOf(2, iy, iz);
					platformIndexEast2 = this.indexOf(13, iy, iz);
					
					if (data[platformIndexWest1] == 120) {
						data[platformIndexWest2] = 120;
						data[platformIndexWest1] = 0;
					}
					if (data[platformIndexEast1] == 120) {
						data[platformIndexEast2] = 120;
						data[platformIndexEast1] = 0;
					}
				}
			}
			
			if (dcube.isNorthWall || dcube.isSouthWall) {
				for (int ix = 0; ix < 14; ix++) {
					int iy = 7;
					int platformIndexNorth1 = this.indexOf(ix, iy, 2);
					int platformIndexSouth1 = this.indexOf(ix, iy, 13);
					int platformIndexNorth2 = this.indexOf(ix+2, iy, 2);
					int platformIndexSouth2 = this.indexOf(ix+2, iy, 13);
					int platformIndexNorth3 = this.indexOf(ix+1, iy, 2);
					int platformIndexSouth3 = this.indexOf(ix+1, iy, 13);
					
					if (data[platformIndexNorth1] == 120 && data[platformIndexNorth2] == 120)
						data[platformIndexNorth3] = 120;
					if (data[platformIndexSouth1] == 120 && data[platformIndexSouth2] == 120)
						data[platformIndexSouth3] = 120;
				}
			}
			
			if (dcube.isWestWall || dcube.isEastWall) {
				for (int iz = 0; iz < 14; iz++) {
					int iy = 7;
					int platformIndexWest1 = this.indexOf(2, iy, iz);
					int platformIndexEast1 = this.indexOf(13, iy, iz);
					int platformIndexWest2 = this.indexOf(2, iy, iz+2);
					int platformIndexEast2 = this.indexOf(13, iy, iz+2);
					int platformIndexWest3 = this.indexOf(2, iy, iz+1);
					int platformIndexEast3 = this.indexOf(13, iy, iz+1);
					
					if (data[platformIndexWest1] == 120 && data[platformIndexWest2] == 120)
						data[platformIndexWest3] = 120;
					if (data[platformIndexEast1] == 120 && data[platformIndexEast2] == 120)
						data[platformIndexEast3] = 120;
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
