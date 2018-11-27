package labyrinth.worldgen.generator;

import java.util.Random;

import io.github.opencubicchunks.cubicchunks.api.util.CubePos;
import io.github.opencubicchunks.cubicchunks.api.world.ICube;
import labyrinth.worldgen.DungeonCube;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class Decorator {
	private static Random rand = new Random();

	public float chance = 1.0f;
	public IBlockState state = Blocks.GLOWSTONE.getDefaultState();
	public NBTTagCompound nbt = new NBTTagCompound();
	public boolean attachToFloor = false;
	public boolean attachToCeiling = false;
	public boolean attachToNorthWall = false;
	public boolean attachToSouthWall = false;
	public boolean attachToWestWall = false;
	public boolean attachToEastWall = false;
	public boolean canAttachToItself = false;
	public int replaceBlock = 0;
	public int clusterSize = 1;

	public void decorateWithChance(ICube cube, DungeonCube is) {
		if (rand.nextFloat() > chance)
			return;
		if (chance <= 1.0f)
			this.decorate(cube, is);
		if (chance > 15.0f) {
			for (int x = 0; x < 16; x++)
				for (int y = 0; y < 16; y++)
					for (int z = 0; z < 16; z++) {
						if (canPlaceHere(cube, is, x, y, z))
							this.set(cube, is, x, y, z, clusterSize);
					}
			return;
		}
		int x = rand.nextInt((int) (16 / chance));
		int y = rand.nextInt((int) (16 / chance));
		int z = rand.nextInt((int) (16 / chance));
		int i=0;
		while (!this.outOfBorders(x, y, z)) {
			if (canPlaceHere(cube, is, x, y, z))
				this.set(cube, is, x, y, z, clusterSize);
			int dx = rand.nextInt(MathHelper.ceil(16 / chance));
			int dy = rand.nextInt(MathHelper.ceil(16 / chance));
			int dz = rand.nextInt(MathHelper.ceil(16 / chance));
			if (dx + dy + dz == 0) {
				++i;
				x += i % 3 == 0 ? 1 : 0;
				y += i % 3 == 1 ? 1 : 0;
				z += i % 3 == 2 ? 1 : 0;
			} else {
				x += dx;
				y += dy;
				z += dz;
			}
		}
	}

	public void decorate(ICube cube, DungeonCube is) {
		int x = rand.nextInt(16);
		int y = rand.nextInt(16);
		int z = rand.nextInt(16);
		if (!canPlaceHere(cube, is, x, y, z))
			return;
		this.set(cube, is, x, y, z, clusterSize);
	}

	private void set(ICube cube, DungeonCube is, int x, int y, int z, int recursionDeepness) {
		if (recursionDeepness <= 0)
			return;
		cube.getStorage().set(x, y, z, state);
		if (state.getBlock() instanceof ITileEntityProvider) {
			CubePos cpos = cube.getCoords();
			BlockPos bpos = cpos.localToBlock(x, y, z);
			ITileEntityProvider teProvider = (ITileEntityProvider) state.getBlock();
			TileEntity te = teProvider.createNewTileEntity(cube.getWorld(), state.getBlock().getMetaFromState(state));
			te.readFromNBT(nbt);
			te.setPos(bpos);
			te.markDirty();
			cube.getWorld().setTileEntity(bpos, te);
		}
		int[] xyz = {0, 0, -1, 0, 0, 1, 0, 0};
		int startI = rand.nextInt(xyz.length) + 2;
		for (int i = startI; i < startI + xyz.length; i++) {
			int x1 = x + xyz[i % xyz.length];
			int y1 = y + xyz[(i + 1) % xyz.length];
			int z1 = z + xyz[(i + 2) % xyz.length];
			if (canPlaceHere(cube, is, x1, y1, z1) && cube.getStorage().get(x1, y1, z1) != state) {
				this.set(cube, is, x1, y1, z1, --recursionDeepness);
			}
		}
	}

	private boolean canPlaceHere(ICube cube, DungeonCube is, int x, int y, int z) {
		return stateMatchCriteria(cube, x, y, z) && replaceBlock == is.data[DungeonCube.getIndex(x, y, z)];
	}

	private boolean stateMatchCriteria(ICube cube, int x, int y, int z) {
		if (this.outOfBorders(x, y, z))
			return false;
		if (attachToFloor) {
			if (y == 0)
				return false;
			if (!this.isShapeSolid(cube, x, y - 1, z, EnumFacing.UP) 
					&& (!canAttachToItself || !this.isSame(cube, x, y - 1, z)))
				return false;
		}
		if (attachToCeiling) {
			if (y == 15)
				return false;
			if (!this.isShapeSolid(cube, x, y + 1, z, EnumFacing.DOWN)
				&& (!canAttachToItself || !this.isSame(cube, x, y + 1, z)))
				return false;
		}
		if (attachToNorthWall) {
			if (z == 0)
				return false;
			if (!this.isShapeSolid(cube, x, y, z - 1, EnumFacing.SOUTH)
					&& (!canAttachToItself || !this.isSame(cube, x, y, z - 1)))
				return false;
		}
		if (attachToSouthWall && z == 15) {
			if (z == 15)
				return false;
			if (!this.isShapeSolid(cube, x, y, z + 1, EnumFacing.NORTH)
					&& (!canAttachToItself || !this.isSame(cube, x, y, z + 1)))
				return false;
		}
		if (attachToWestWall && x == 0) {
			if (x == 0)
				return false;
			if (!this.isShapeSolid(cube, x - 1, y, z, EnumFacing.EAST)
					&& (!canAttachToItself || !this.isSame(cube, x - 1, y, z)))
				return false;
		}
		if (attachToEastWall && x == 15) {
			if (x == 15)
				return false;
			if (!this.isShapeSolid(cube, x + 1, y, z, EnumFacing.WEST)
					&& (!canAttachToItself || !this.isSame(cube, x + 1, y, z)))
				return false;
		}
		return true;
	}

	private boolean isShapeSolid(ICube cube, int x, int y, int z, EnumFacing facing) {
		CubePos cpos = cube.getCoords();
		BlockPos bpos = cpos.localToBlock(x, y, z);
		ExtendedBlockStorage storage = cube.getStorage();
		return storage.get(x, y, z).getBlockFaceShape(cube.getWorld(), bpos, facing) == BlockFaceShape.SOLID;
	}
	
	private boolean isSame(ICube cube, int x, int y, int z) {
		ExtendedBlockStorage storage = cube.getStorage();
		return storage.get(x, y, z).equals(state);
	}

	private boolean outOfBorders(int x, int y, int z) {
		return x < 0 || x > 15 || y < 0 || y > 15 || z < 0 || z > 15;
	}
}
