package labyrinth.pathfinding;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public class MutableAxisAlignedBB extends AxisAlignedBB {

	/**
	 * The minimum X coordinate of this bounding box. Guaranteed to always be
	 * less than or equal to {@link #maxX}.
	 */
	public double minX;
	/**
	 * The minimum Y coordinate of this bounding box. Guaranteed to always be
	 * less than or equal to {@link #maxY}.
	 */
	public double minY;
	/**
	 * The minimum Y coordinate of this bounding box. Guaranteed to always be
	 * less than or equal to {@link #maxZ}.
	 */
	public double minZ;
	/**
	 * The maximum X coordinate of this bounding box. Guaranteed to always be
	 * greater than or equal to {@link #minX}.
	 */
	public double maxX;
	/**
	 * The maximum Y coordinate of this bounding box. Guaranteed to always be
	 * greater than or equal to {@link #minY}.
	 */
	public double maxY;
	/**
	 * The maximum Z coordinate of this bounding box. Guaranteed to always be
	 * greater than or equal to {@link #minZ}.
	 */
	public double maxZ;

	public MutableAxisAlignedBB(double x1, double y1, double z1, double x2, double y2, double z2) {
		super(x1, y1, z1, x2, y2, z2);
		this.minX = Math.min(x1, x2);
		this.minY = Math.min(y1, y2);
		this.minZ = Math.min(z1, z2);
		this.maxX = Math.max(x1, x2);
		this.maxY = Math.max(y1, y2);
		this.maxZ = Math.max(z1, z2);
	}

	public MutableAxisAlignedBB(AxisAlignedBB aabbIn) {
		super(aabbIn.minX, aabbIn.minY, aabbIn.minZ, aabbIn.maxX, aabbIn.maxY, aabbIn.maxZ);
		this.minX = aabbIn.minX;
		this.minY = aabbIn.minY;
		this.minZ = aabbIn.minZ;
		this.maxX = aabbIn.maxX;
		this.maxY = aabbIn.maxY;
		this.maxZ = aabbIn.maxZ;
	}

	public void setTo(double x1, double y1, double z1, double x2, double y2, double z2) {
		this.minX = Math.min(x1, x2);
		this.minY = Math.min(y1, y2);
		this.minZ = Math.min(z1, z2);
		this.maxX = Math.max(x1, x2);
		this.maxY = Math.max(y1, y2);
		this.maxZ = Math.max(z1, z2);
	}

	@Override
	public AxisAlignedBB intersect(AxisAlignedBB aabbAnother) {
		double d0 = Math.max(this.minX, aabbAnother.minX);
		double d1 = Math.max(this.minY, aabbAnother.minY);
		double d2 = Math.max(this.minZ, aabbAnother.minZ);
		double d3 = Math.min(this.maxX, aabbAnother.maxX);
		double d4 = Math.min(this.maxY, aabbAnother.maxY);
		double d5 = Math.min(this.maxZ, aabbAnother.maxZ);
		this.setTo(d0, d1, d2, d3, d4, d5);
		return this;
	}

	@Override
	public boolean intersectsWith(AxisAlignedBB other) {
		return this.intersects(other.minX, other.minY, other.minZ, other.maxX, other.maxY, other.maxZ);
	}

	@Override
	public boolean intersects(double x1, double y1, double z1, double x2, double y2, double z2) {
		return this.minX < x2 && this.maxX > x1 && this.minY < y2 && this.maxY > y1 && this.minZ < z2 && this.maxZ > z1;
	}

	@Override
	public boolean isVecInside(Vec3d vec) {
		return vec.xCoord > this.minX && vec.xCoord < this.maxX ? (vec.yCoord > this.minY && vec.yCoord < this.maxY ? vec.zCoord > this.minZ && vec.zCoord < this.maxZ : false) : false;
	}

	@Override
	public String toString() {
		return "box[" + this.minX + ", " + this.minY + ", " + this.minZ + " -> " + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
	}

}
