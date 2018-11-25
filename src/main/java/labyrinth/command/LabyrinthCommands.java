package labyrinth.command;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.opencubicchunks.cubicchunks.api.util.CubePos;
import io.github.opencubicchunks.cubicchunks.api.world.ICube;
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorld;
import io.github.opencubicchunks.cubicchunks.core.network.PacketCubes;
import io.github.opencubicchunks.cubicchunks.core.network.PacketDispatcher;
import io.github.opencubicchunks.cubicchunks.core.world.cube.Cube;
import labyrinth.LabyrinthMod;
import labyrinth.worldgen.DefaultMapping;
import labyrinth.worldgen.DungeonCube;
import labyrinth.worldgen.LevelsStorage;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class LabyrinthCommands extends CommandBase {
	
	private final Map<CubePos, String> loadedCubes = new HashMap<CubePos, String>();
	
	private boolean load(MinecraftServer server, String filename, byte[] bf) {
		try {
			InputStream stream = LabyrinthMod.getResourceInputStream(server.getWorld(0), new ResourceLocation("labyrinth", "cubes/" + filename));
			if (stream == null)
				return false;
			stream.read(bf);
			stream.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean save(World world, String filename, ByteBuffer bf) {
		try {
			DataOutputStream osWriter = new DataOutputStream(LabyrinthMod.getOutputStream(world, new ResourceLocation("labyrinth",  "cubes/" + filename)));
			osWriter.write(bf.array());
			osWriter.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private void save(CubePos cpos, ICommandSender sender, MinecraftServer server, String cubeName) {
		WorldServer world = server.getWorld(0);
		ByteBuffer bf = ByteBuffer.allocate(4096);
		DefaultMapping defaultMapping = LevelsStorage.defaultMapping;
		for (int x = cpos.getMinBlockX(); x <= cpos.getMaxBlockX(); x++)
		for (int y = cpos.getMinBlockY(); y <= cpos.getMaxBlockY(); y++)
		for (int z = cpos.getMinBlockZ(); z <= cpos.getMaxBlockZ(); z++) {
			int bsid = 0;
			IBlockState bs = world.getBlockState(new BlockPos(x, y, z));
			bsid = defaultMapping.getId(bs);
			if (bsid!=-1) {
				bf.put((byte) bsid);
			}
		}
		if(this.save(world, cubeName, bf))
			sender.sendMessage(new TextComponentString("Successfully saved cube: " + cubeName));
		else
			sender.sendMessage(new TextComponentString("Save failed: " + cubeName));
	}
	
	private String getCubeName(CubePos cpos, String[] args) {
		String cubeName = loadedCubes.get(cpos);
		if (args.length >= 2)
			cubeName = args[1];
		if(cubeName == null) {
			return cubeName;
		}
		if(!cubeName.contains(".cube_structure")) {
			cubeName+=".cube_structure";
		}
		return cubeName;
	}

	private void place(WorldServer world, CubePos cpos, byte[] bf) {
		ICubicWorld cworld = (ICubicWorld) world;
		ICube cube = cworld.getCubeCache().getCube(cpos);
		DungeonCube.placeCube(cube, bf, LevelsStorage.defaultMapping.mapping, world, "", true, false);
		List<Cube> cubes = new ArrayList<Cube>();
		cubes.add((Cube) cube);
		PacketCubes pc = new PacketCubes(cubes);
		PacketDispatcher.sendToAllAround(pc, 0, cpos.getXCenter(), cpos.getYCenter(), cpos.getZCenter(), 64);
	}
	
	private String getRotatedSideName(String sideIn){
		if(sideIn.contains("north"))
			return sideIn.replace("north", "east");
		if(sideIn.contains("east"))
			return sideIn.replace("east","south");
		if(sideIn.contains("south"))
			return sideIn.replace("south","west");
		if(sideIn.contains("west"))
			return sideIn.replace("west","north");
		return sideIn;
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 4;
	}

	@Override
	public String getName() {
		return "labyrinth";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/"+getName()+" {load|save} [cubename]";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 1)
			throw new WrongUsageException("Usage:"+getUsage(sender));
		WorldServer world = server.getWorld(0);
		CubePos cpos = CubePos.fromBlockCoords(sender.getPosition());
		if(args[0].equalsIgnoreCase("load")) {
			if (args.length < 2)
				throw new WrongUsageException("Usage:"+getUsage(sender));
			byte[] bf = new byte[4096];
			String cubeName = args[1];
			if(!cubeName.contains(".cube_structure")) {
				cubeName+=".cube_structure";
			}
			if (this.load(server, cubeName, bf)) {
				this.place(world, cpos, bf);
				loadedCubes.put(cpos, args[1]);
			}
			else {
				sender.sendMessage(new TextComponentString("No such cube: " + cubeName));
			}
		}
		else if(args[0].equalsIgnoreCase("save")) {
			String cubeName = getCubeName(cpos, args);
			this.save(cpos, sender, server, cubeName);
		}
		else if(args[0].equalsIgnoreCase("shift")) {
			byte[] bf = new byte[4096];
			DefaultMapping defaultMapping = LevelsStorage.defaultMapping;
			for (int x = cpos.getMinBlockX(); x <= cpos.getMaxBlockX(); x++)
			for (int y = cpos.getMinBlockY(); y <= cpos.getMaxBlockY(); y++)
			for (int z = cpos.getMinBlockZ(); z <= cpos.getMaxBlockZ(); z++) {
				int bsid = 0;
				IBlockState bs = world.getBlockState(new BlockPos(x, y, z));
				bsid = defaultMapping.getId(bs);
				if (bsid!=-1) {
					int x1 = x - cpos.getMinBlockX();
					int y1 = y - cpos.getMinBlockY();
					int z1 = z - cpos.getMinBlockZ();
					x1 = (x1 + ICube.SIZE/2) % ICube.SIZE;
					z1 = (z1 + ICube.SIZE/2) % ICube.SIZE;
					bf[x1<<8|y1<<4|z1] = (byte) bsid;
				}
			}
			this.place(world, cpos, bf);
		}
		else if(args[0].equalsIgnoreCase("mirrorx")) {
			byte[] bf = new byte[4096];
			DefaultMapping defaultMapping = LevelsStorage.defaultMapping;
			for (int x = cpos.getMinBlockX(); x <= cpos.getMaxBlockX(); x++)
			for (int y = cpos.getMinBlockY(); y <= cpos.getMaxBlockY(); y++)
			for (int z = cpos.getMinBlockZ(); z <= cpos.getMaxBlockZ(); z++) {
				int x1 = x - cpos.getMinBlockX();
				int y1 = y - cpos.getMinBlockY();
				int z1 = z - cpos.getMinBlockZ();
				IBlockState bs = world.getBlockState(new BlockPos(x, y, z));
				if(x1 > ICube.SIZE/2) {
					bs = world.getBlockState(new BlockPos(cpos.getMinBlockX()+ICube.SIZE - x1, y, z)).withMirror(Mirror.FRONT_BACK);
				}
				int bsid = defaultMapping.getId(bs);
				if (bsid!=-1) {
					bf[x1<<8|y1<<4|z1] = (byte) bsid;
				}
			}
			this.place(world, cpos, bf);
		}
		else if(args[0].equalsIgnoreCase("mirrorz")) {
			byte[] bf = new byte[4096];
			DefaultMapping defaultMapping = LevelsStorage.defaultMapping;
			for (int x = cpos.getMinBlockX(); x <= cpos.getMaxBlockX(); x++)
			for (int y = cpos.getMinBlockY(); y <= cpos.getMaxBlockY(); y++)
			for (int z = cpos.getMinBlockZ(); z <= cpos.getMaxBlockZ(); z++) {
				int x1 = x - cpos.getMinBlockX();
				int y1 = y - cpos.getMinBlockY();
				int z1 = z - cpos.getMinBlockZ();
				IBlockState bs = world.getBlockState(new BlockPos(x, y, z));
				if(z1 > ICube.SIZE/2) {
					bs = world.getBlockState(new BlockPos(x, y, cpos.getMinBlockZ()+ICube.SIZE - z1)).withMirror(Mirror.LEFT_RIGHT);
				}
				int bsid = defaultMapping.getId(bs);
				if (bsid!=-1) {
					bf[x1<<8|y1<<4|z1] = (byte) bsid;
				}
			}
			this.place(world, cpos, bf);
		}
		else if(args[0].equalsIgnoreCase("rotate")) {
			byte[] bf = new byte[4096];
			DefaultMapping defaultMapping = LevelsStorage.defaultMapping;
			for (int x = cpos.getMinBlockX(); x <= cpos.getMaxBlockX(); x++)
			for (int y = cpos.getMinBlockY(); y <= cpos.getMaxBlockY(); y++)
			for (int z = cpos.getMinBlockZ(); z <= cpos.getMaxBlockZ(); z++) {
				int x1 = x - cpos.getMinBlockX();
				int y1 = y - cpos.getMinBlockY();
				int z1 = z - cpos.getMinBlockZ();
				IBlockState bs = world.getBlockState(new BlockPos(x, y, z));
				bs = world.getBlockState(new BlockPos(cpos.getMinBlockX() + z1, y, cpos.getMinBlockZ() + ICube.SIZE - 1 - x1)).withRotation(Rotation.CLOCKWISE_90);
				int bsid = defaultMapping.getId(bs);
				if (bsid!=-1) {
					bf[x1<<8|y1<<4|z1] = (byte) bsid;
				}
			}
			this.place(world, cpos, bf);
		}
	}
}