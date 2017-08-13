package labyrinth.village;

import cubicchunks.util.Coords;
import cubicchunks.util.CubePos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;
import net.minecraft.world.World;

public class UndergroundVillage extends Village {

	public final static int BIT_SIZE = 3;
	public final static int LOCAL_BIT_MASK = (1 << BIT_SIZE) - 1;
	public final static int INV_BIT_MASK = 0xFFFFFFFF ^ LOCAL_BIT_MASK;

	public UndergroundVillage(World worldIn) {
		super(worldIn);
	}
	
	@Override
	public void tick(int tickCounterIn) {
		for (VillageDoorInfo doorInfo : this.getVillageDoorInfoList())
			doorInfo.setLastActivityTimestamp(tickCounterIn);
		super.tick(tickCounterIn);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof UndergroundVillage))
			return false;
		BlockPos centerPosOther = ((UndergroundVillage) o).getCenter();
		return this.isBlockPosWithinSqVillageRadius(centerPosOther);
	}

	@Override
	public boolean isBlockPosWithinSqVillageRadius(BlockPos pos) {
		CubePos cposOther = CubePos.fromBlockCoords(pos);
		return this.isBlockPosWithinSqVillageRadius(cposOther);
	}

	public boolean isBlockPosWithinSqVillageRadius(CubePos cposOther) {
		CubePos cposThis = CubePos.fromBlockCoords(super.getCenter());
		int x1 = cposOther.getX() >> BIT_SIZE;
		int z1 = cposOther.getZ() >> BIT_SIZE;
		int x2 = cposThis.getX() >> BIT_SIZE;
		int z2 = cposThis.getZ() >> BIT_SIZE;
		return cposOther.getY() == cposThis.getY() && x1 == x2 && z1 == z2;
	}

	@Override
	public BlockPos getCenter() {
		BlockPos center = super.getCenter();
		CubePos cpos = CubePos.fromBlockCoords(center);
		int cx = cpos.getX() & INV_BIT_MASK;
		int cz = cpos.getZ() & INV_BIT_MASK;
		int bx = Coords.cubeToMinBlock(cx);
		int bz = Coords.cubeToMinBlock(cz);
		bx += Coords.cubeToMaxBlock(cx | LOCAL_BIT_MASK);
		bz += Coords.cubeToMaxBlock(cz | LOCAL_BIT_MASK);
		return new BlockPos(bx / 2, center.getY(), bz / 2);
	}

	@Override
	public int getVillageRadius() {
		return Coords.cubeToMaxBlock(LOCAL_BIT_MASK)/2;
	}
}