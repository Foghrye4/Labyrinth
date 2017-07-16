package labyrinth.pathfinding;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CubicPathNavigateGround extends PathNavigateGround {

	private CubicPath path = new CubicPath();
	private boolean canBreakDoors = true;
	private boolean canEnterDoors = true;
	private boolean canSwim = true;
	private boolean shouldAvoidSun = true;

	public CubicPathNavigateGround(EntityLiving entitylivingIn, World worldIn) {
		super(entitylivingIn, worldIn);
	}

	@Override
	protected PathFinder getPathFinder() {
		// unused
		return null;
	}

	@Override
	protected Vec3d getEntityPosition() {
		// unused
		return null;
	}

	@Override
	protected boolean canNavigate() {
		// unused
		return false;
	}

	@Override
	public Path getPathToPos(BlockPos pos) {
		path.update(theEntity, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, this.speed, canBreakDoors, canEnterDoors, canSwim);
		return path;
	}

	@Nullable
	@Override
	public Path getPathToEntityLiving(Entity entityIn) {
		path.update(theEntity, entityIn.posX, entityIn.posY, entityIn.posZ, this.speed, canBreakDoors, canEnterDoors, canSwim);
		return path;
	}

	@Override
	public void onUpdateNavigation() {
		if (!path.isFinished()) {
			path.updateCurrentPathIndex(theEntity);
			this.theEntity.getMoveHelper().setMoveTo(path.nextX(), path.nextY(), path.nextZ(), this.speed);
		}
	}

	@Override
	protected void pathFollow() {
		// unused
	}
	
	@Override
    protected void removeSunnyPath()
    {
		// This is handled elsewhere
    }

	@Override
	protected boolean isDirectPathBetweenPoints(Vec3d posVec31, Vec3d posVec32, int sizeX, int sizeY, int sizeZ) {
		// unused
		return false;
	}
	
	@Override
    public void setBreakDoors(boolean canBreakDoorsIn)
    {
		this.canBreakDoors = canBreakDoorsIn;
    }

	@Override
    public void setEnterDoors(boolean enterDoors)
    {
		this.canEnterDoors = enterDoors;
    }

	@Override
    public boolean getEnterDoors()
    {
        return this.canEnterDoors;
    }

	@Override
    public void setCanSwim(boolean canSwimIn)
    {
		this.canSwim=canSwimIn;
    }

	@Override
    public boolean getCanSwim()
    {
        return this.canSwim;
    }

	@Override
    public void setAvoidSun(boolean avoidSun)
    {
        this.shouldAvoidSun = avoidSun;
    }

}
