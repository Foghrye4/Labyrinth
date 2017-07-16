package labyrinth.pathfinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import cubicchunks.world.ICubicWorld;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CubicPath extends Path {

    private static final int POINTS_ARRAY_SIZE = 64;
    private static final int STEPS_PER_UPDATE = 4;
    private static final int EXPIRE_PATH_AFTER = 40;
    private static final double SENSITIVITY = 0.4d;
    private static final int DIRECTIONS_RESOLUTION = 32;
    private final static Vec3d[] DIRECTIONS = new Vec3d[DIRECTIONS_RESOLUTION];
    static {
        for(int i=0;i<DIRECTIONS_RESOLUTION;i++) {
        	DIRECTIONS[i] = Vec3d.fromPitchYaw(0, 360f*i/DIRECTIONS_RESOLUTION);
        }
    }
    
    private int currentPathIndex=0;
    private int currentPathDirection=0;
    private int pathLength=0;
    private int pathExpirationTimer=EXPIRE_PATH_AFTER;
    private final double[] xPoints = new double[POINTS_ARRAY_SIZE];
    private final double[] yPoints = new double[POINTS_ARRAY_SIZE];
    private final double[] zPoints = new double[POINTS_ARRAY_SIZE];
    
    public double lastTargetX = 0d;
    public double lastTargetY = 0d;
    public double lastTargetZ = 0d;
    
    
    public CubicPath() {
        super(new PathPoint[0]);
    }
    
    public void incrementPathIndex()
    {
    	pathExpirationTimer=EXPIRE_PATH_AFTER;
    	--this.pathLength;
        ++this.currentPathIndex;
    }

    /**
     * Returns true if this path has reached the end
     */
    public boolean isFinished()
    {
        return this.pathLength<=1;
    }

    /**
     * returns the last PathPoint of the Array
     */
    @Nullable
    public PathPoint getFinalPathPoint()
    {
        return new PathPoint((int)xPoints(this.currentPathIndex+this.pathLength-1),(int)yPoints(this.currentPathIndex+this.pathLength-1),(int)zPoints(this.currentPathIndex+this.pathLength-1));
    }

    /**
     * return the PathPoint located at the specified PathIndex, usually the current one
     */
    @Override
    public PathPoint getPathPointFromIndex(int index)
    {
        return null;
    }

    @Override
    public void setPoint(int index, PathPoint point)
    {
    }

    public int getCurrentPathLength()
    {
        return this.pathLength;
    }

    public void setCurrentPathLength(int length)
    {
        this.pathLength = length;
    }

    public int getCurrentPathIndex()
    {
        return this.currentPathIndex;
    }

    public void setCurrentPathIndex(int currentPathIndexIn)
    {
        this.currentPathIndex = currentPathIndexIn;
    }

    /**
     * Gets the vector of the PathPoint associated with the given index.
     */
    public Vec3d getVectorFromIndex(Entity entityIn, int index)
    {
        return this.getVectorFromIndex(index);
    }

    public Vec3d getVectorFromIndex(int index)
    {
        return new Vec3d(this.xPoints[index], this.yPoints[index], this.zPoints[index]);
    }

    /**
     * returns the current PathEntity target node as Vec3D
     */
    public Vec3d getPosition(Entity entityIn)
    {
        return this.getVectorFromIndex(entityIn, this.currentPathIndex);
    }

    public Vec3d getCurrentPos()
    {
        return this.getVectorFromIndex(this.currentPathIndex);
    }

    /**
     * Returns true if the EntityPath are the same. Non instance related equals.
     */
    public boolean isSamePath(Path pathentityIn)
    {
        return pathentityIn == this;
    }

    public void update(EntityLiving entityFrom, double posX, double posY, double posZ, double speed, boolean canBreakDoors, boolean canEnterDoors, boolean canSwim) {
        this.updateCurrentPathIndex(entityFrom);
        int dtpx = (int) (posX-this.lastTargetX);
        int dtpy = (int) (posY-this.lastTargetY);
        int dtpz = (int) (posZ-this.lastTargetZ);
        if(this.pathLength>0){
            int expirePathElements = dtpx*dtpx + dtpy*dtpy + dtpz*dtpz;
            if(expirePathElements>0) {
        		this.pathLength=0;
        		this.currentPathIndex=0;
            }
        }
        this.lastTargetX=posX;
        this.lastTargetY=posY;
        this.lastTargetZ=posZ;
    	if(this.pathLength>=POINTS_ARRAY_SIZE-STEPS_PER_UPDATE)
    		return;
        int primaryDirection = getPrimaryDirection(entityFrom, posX, posY, posZ);
        MutableAxisAlignedBB ebb = new MutableAxisAlignedBB(entityFrom.getEntityBoundingBox());
        double minX = ebb.minX;
        double minY = ebb.minY;
        double minZ = ebb.minZ;
        double maxX = ebb.maxX;
        double maxY = ebb.maxY;
        double maxZ = ebb.maxZ;
        double cposX = entityFrom.posX;
        double cposY = entityFrom.posY;
        double cposZ = entityFrom.posZ;
        
        if(this.pathLength>0) {
        	int lastIndex = this.currentPathIndex+this.pathLength-1;
            cposX = xPoints(lastIndex);
            cposY = yPoints(lastIndex);
            cposZ = zPoints(lastIndex);
        	double dx = cposX-entityFrom.posX;
        	double dy = cposY-entityFrom.posY;
        	double dz = cposZ-entityFrom.posZ;
        	minX+=dx;
        	maxX+=dx;
        	minY+=dy;
        	maxY+=dy;
        	minZ+=dz;
        	maxZ+=dz;
        }
        else {
        	xPoints(this.currentPathIndex,cposX);
        	yPoints(this.currentPathIndex,cposY);
        	zPoints(this.currentPathIndex,cposZ);
        	this.pathLength++;
        	this.currentPathDirection = primaryDirection;
        }
		for (int i = 0; i < STEPS_PER_UPDATE; i++) {
	    	int dirMax = primaryDirection + DIRECTIONS.length-1;
			a: for (int diri = primaryDirection; diri <= dirMax; diri++) {
		    	int[] heights = new int[] {0,-1,1};
				for (int iy:heights) {
					Vec3d dir = direction(diri);
					double dx = dir.xCoord * speed * SENSITIVITY;
					double dy = dir.yCoord * speed * SENSITIVITY + iy;
					double dz = dir.zCoord * speed * SENSITIVITY;
					double minX1 = minX + dx;
					double minY1 = minY + dy;
					double minZ1 = minZ + dz;
					double maxX1 = maxX + dx;
					double maxY1 = maxY + dy;
					double maxZ1 = maxZ + dz;
					if (checkCollisions(entityFrom.world, ebb, minX1, minY1, minZ1, maxX1, maxY1, maxZ1, canBreakDoors, canEnterDoors, canSwim)) {
						minX = minX1;
						maxX = maxX1;
						minY = minY1;
						maxY = maxY1;
						minZ = minZ1;
						maxZ = maxZ1;
						cposX += dx;
						cposY += dy;
						cposZ += dz;
						xPoints(this.currentPathIndex + this.pathLength, cposX);
						yPoints(this.currentPathIndex + this.pathLength, cposY);
						zPoints(this.currentPathIndex + this.pathLength, cposZ);
						this.pathLength++;
						this.currentPathDirection = diri;
						break a;
					}
				}
			}
		}
    }
    
    private boolean checkCollisions(World world, MutableAxisAlignedBB entityBox, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, boolean canBreakDoors, boolean canEnterDoors, boolean canSwim) {
		if(!((ICubicWorld)world).testForCubes((int)minX,(int)minY-1,(int)minZ, (int)maxX,(int)maxY,(int)maxZ, Objects::nonNull)){
			return false;
		}
		entityBox.setTo(minX, minY, minZ, maxX, maxY, maxZ);
      	List<AxisAlignedBB> cbb = new ArrayList<AxisAlignedBB>();
		for(BlockPos bpos : BlockPos.func_191531_b((int)minX-1,(int)minY,(int)minZ-1, (int)maxX,(int)maxY,(int)maxZ)){
          	IBlockState bstate = world.getBlockState(bpos);
          	bstate = bstate.getActualState(world, bpos);
          	bstate.addCollisionBoxToList(world, bpos, entityBox, cbb, null, false);
          	if(!cbb.isEmpty()){
          		return false;
          	}
     	}
		minY-=0.5d;
		entityBox.setTo(minX, minY, minZ, maxX, maxY, maxZ);
		for(BlockPos bpos : BlockPos.func_191531_b((int)minX-1,(int)minY,(int)minZ-1, (int)maxX,(int)minY,(int)maxZ)){
	          	IBlockState bstate = world.getBlockState(bpos);
	          	bstate = bstate.getActualState(world, bpos);
	          	bstate.addCollisionBoxToList(world, bpos, entityBox, cbb, null, false);
	          	if(!cbb.isEmpty()){
	        		return true;
	          	}
	    }
		for(BlockPos bpos : BlockPos.func_191531_b((int)minX-1,(int)minY,(int)minZ-1, (int)maxX,(int)minY,(int)maxZ)){
          	IBlockState bstate = world.getBlockState(bpos);
          	bstate = bstate.getActualState(world, bpos);
		}
		return false;
	}

	private static Vec3d direction(int diri) {
		if(diri<0)
			return DIRECTIONS[DIRECTIONS.length+diri];
		return DIRECTIONS[diri%DIRECTIONS.length];
	}

	private void xPoints(int i, double x) {
    	this.xPoints[i%POINTS_ARRAY_SIZE] = x;
	}
    private void yPoints(int i, double y) {
    	this.yPoints[i%POINTS_ARRAY_SIZE] = y;
	}
    private void zPoints(int i, double z) {
    	this.zPoints[i%POINTS_ARRAY_SIZE] = z;
	}

	private double xPoints(int i) {
		return this.xPoints[i%POINTS_ARRAY_SIZE];
	}
    private double yPoints(int i) {
		return this.yPoints[i%POINTS_ARRAY_SIZE];
	}
    private double zPoints(int i) {
		return this.zPoints[i%POINTS_ARRAY_SIZE];
	}

	public double nextX() {
		return this.xPoints(this.currentPathIndex+1);
	}
    public double nextY() {
		return this.yPoints(this.currentPathIndex+1);
	}
    public double nextZ() {
		return this.zPoints(this.currentPathIndex+1);
	}
    
	/** Ensure that entity on path **/
    public void updateCurrentPathIndex(EntityLiving entityFrom){
        if(--pathExpirationTimer==0){
        	this.currentPathIndex=0;
        	this.pathLength=0;
        	pathExpirationTimer=EXPIRE_PATH_AFTER;
        }
    	if(!this.isFinished()) {
    		double dx = Math.abs(this.nextX()-entityFrom.posX);
    		double dy = Math.abs(this.nextY()-entityFrom.posY);
    		double dz = Math.abs(this.nextZ()-entityFrom.posZ);
    		if(dx<SENSITIVITY && dy<SENSITIVITY && dz<SENSITIVITY)
        		incrementPathIndex();
    	}
    }
    
	private int getPrimaryDirection(EntityLiving entityFrom, double posX, double posY, double posZ) {
		double dx = posX - entityFrom.posX;
		double dz = posZ - entityFrom.posZ;
		double maxD = dx * DIRECTIONS[0].xCoord + dz * DIRECTIONS[0].zCoord;
		int primaryDirection = 0;
		for (int i = 1; i < DIRECTIONS.length; i++) {
			double maxD1 = dx * DIRECTIONS[i].xCoord + dz * DIRECTIONS[i].zCoord;
			if (maxD1 > maxD) {
				maxD = maxD1;
				primaryDirection = i;
			}
		}
		return primaryDirection;
	}
}
