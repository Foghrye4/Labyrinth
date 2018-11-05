package labyrinth.noise;

import java.util.Random;

import io.github.opencubicchunks.cubicchunks.api.util.CubePos;
import net.minecraft.world.World;

public class ManhattanNoise implements INoise {
	
	private final Random random = new Random();
	private static final int BIT_SIZE = 4;
	private static final int CELL_SIZE = 1<<BIT_SIZE;
	long lastSeed = 0;
	
	@Override
	public boolean canGenerateAt(CubePos cpos, World world) {
		int x0 = cpos.getX();
		int y0 = cpos.getY();
		int z0 = cpos.getZ();
		return canGenerateAt(x0,y0,z0,world);
	}
	
	public boolean canGenerateAt(int x0, int y0, int z0, World world) {
		int cx = x0 >> BIT_SIZE;
		int cy = y0 >> BIT_SIZE;
		int cz = z0 >> BIT_SIZE;
		long worldSeed = world.getSeed();
		assert lastSeed==0 || lastSeed == worldSeed;
		lastSeed = worldSeed;
		Point centralPoint = new Point(random,cx,cy,cz,worldSeed);
		if(!centralPoint.hidden && centralPoint.isInside(x0, y0, z0))
			return true;
		for(int ix=cx-1;ix<=cx+1;ix++)
		for(int iy=cy-1;iy<=cy+1;iy++)
		for(int iz=cz-1;iz<=cz+1;iz++) {
			if(ix==cx && iy==cy && iz==cz)
				continue;
			Point point = new Point(random,ix,iy,iz,worldSeed);
			if(point.hidden) 
				continue;
			if(point.isInside(x0, y0, z0))
				return true;
			if(centralPoint.hidden)
				continue;
			if(centralPoint.intersects(point))
				continue;
			int x1 = centralPoint.intersectionMiddleX(point);
			int y1 = centralPoint.intersectionMiddleY(point);
			int z1 = centralPoint.intersectionMiddleZ(point);
			
			assert point.intersectionMiddleX(centralPoint) == x1;
			assert point.intersectionMiddleY(centralPoint) == y1;
			assert point.intersectionMiddleZ(centralPoint) == z1;
			
			if (centralPoint.intersectX(point) && centralPoint.intersectY(point)) {
				int minZ = Math.min(centralPoint.centerZ, point.centerZ);
				int maxZ = Math.max(centralPoint.centerZ, point.centerZ);
				if(x0==x1 && y0==y1 && minZ<=z0 && maxZ>=z0)
					return true;
				continue;
			}
			if (centralPoint.intersectZ(point) && centralPoint.intersectY(point)) {
				int minX = Math.min(centralPoint.centerX, point.centerX);
				int maxX = Math.max(centralPoint.centerX, point.centerX);
				if(z0==z1 && y0==y1 && minX<=x0 && maxX>=x0)
					return true;
				continue;
			}
			int maxY = Math.min(centralPoint.maxY(), point.maxY());
			int minY = Math.max(centralPoint.minY(), point.minY());
			if (centralPoint.intersectX(point) && minY<=y0 && maxY>=y0) {
				if(x0==x1 && (z0-z1==y0-y1||z0-z1==y0-y1+1) ) {
					return true;
				}
				continue;
			}
			if (centralPoint.intersectZ(point) && minY<=y0 && maxY>=y0) {
				if(z0==z1 && (x0-x1==y0-y1||x0-x1==y0-y1+1)) {
					return true;
				}
				continue;
			}
		}
		return false;
	}

	public static class Point {
		public final boolean hidden;
		public final int cellX;
		public final int cellY;
		public final int cellZ;
		public final int centerX;
		public final int centerY;
		public final int centerZ;
		public final int dX;
		public final int dY;
		public final int dZ;
		
		public Point(Random random, int cellXIn, int cellYIn, int cellZIn, long worldSeed) {
			cellX=cellXIn;
			cellY=cellYIn;
			cellZ=cellZIn;
			long hash = 3;
			hash = 41 * hash + worldSeed;
			hash = 41 * hash + cellY;
			hash = 41 * hash + cellX;
			hash = 41 * hash + cellZ;
			random.setSeed(hash);
			hidden = random.nextFloat()>0.8f;
			centerX = (cellX<<BIT_SIZE) + random.nextInt(CELL_SIZE);
			centerY = (cellY<<BIT_SIZE) + random.nextInt(CELL_SIZE);
			centerZ = (cellZ<<BIT_SIZE) + random.nextInt(CELL_SIZE);
			dX = random.nextInt(CELL_SIZE/2)+1;
			dY = random.nextInt(CELL_SIZE/2)+1;
			dZ = random.nextInt(CELL_SIZE/2)+1;
		}
		
		public int maxX() {
			return centerX + dX;
		}
		
		public int minX() {
			return centerX - dX;
		}
		
		public int maxY() {
			return centerY + dY;
		}
		
		public int minY() {
			return centerY - dY;
		}
		
		public int maxZ() {
			return centerZ + dZ;
		}
		
		public int minZ() {
			return centerZ - dZ;
		}

		public boolean isInside(int x, int y, int z) {
			return centerX - dX <= x && centerX + dX >= x &&
					centerY - dY <= y && centerY + dY >= y &&
					centerZ - dZ <= z && centerZ + dZ >= z;
		}
		
		public boolean intersects(Point other) {
	        return this.minX() <= other.maxX() && 
	        		this.maxX() >= other.minX() && 
	        		this.minY() <= other.maxY() && 
	        		this.maxY() >= other.minY() && 
	        		this.minZ() <= other.maxZ() && 
	        		this.maxZ() >= other.minZ();
		}
		
		public boolean intersectX(Point other) {
	        return this.minX() <= other.maxX() && 
	        		this.maxX() >= other.minX();
		}
		
		public boolean intersectY(Point other) {
	        return this.minY() <= other.maxY() && 
	        		this.maxY() >= other.minY();
		}
		
		public boolean intersectZ(Point other) {
	        return this.minZ() <= other.maxZ() && 
	        		this.maxZ() >= other.minZ();
		}
		
		public int intersectionMiddleX(Point other) {
			return (Math.min(this.maxX(), other.maxX()) + Math.max(this.minX(), other.minX())) / 2;
		}
		
		public int intersectionMiddleY(Point other) {
			return (Math.min(this.maxY(), other.maxY()) + Math.max(this.minY(), other.minY())) / 2;
		}
		
		public int intersectionMiddleZ(Point other) {
			return (Math.min(this.maxZ(), other.maxZ()) + Math.max(this.minZ(), other.minZ())) / 2;
		}
		
		public boolean isXLayBetween(int x, Point other) {
			return x <= Math.min(this.maxX(), other.maxX()) && x >= Math.max(this.minX(), other.minX());
		}
		
		public boolean isYLayBetween(int y, Point other) {
			return y <= Math.min(this.maxY(), other.maxY()) && y >= Math.max(this.minY(), other.minY());
		}

		public boolean isZLayBetween(int z, Point other) {
			return z <= Math.min(this.maxZ(), other.maxZ()) && z >= Math.max(this.minZ(), other.minZ());
		}
	}
}
