package labyrinth.pathfinding;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PathNavigateGroundFixed extends PathNavigateGround {

	public PathNavigateGroundFixed(EntityLiving entitylivingIn, World worldIn) {
		super(entitylivingIn, worldIn);
	}
	
	@Override
	protected void checkForStuck(Vec3d entityPositionVec3) {
        if (this.currentPath != null && !this.currentPath.isFinished())
        {
    		Vec3d vec3d = this.currentPath.getCurrentPos();
    		if (vec3d.y - entityPositionVec3.y > 1.6d) {
    			this.clearPathEntity();
    			return;
    		}
        }
		super.checkForStuck(entityPositionVec3);
	}
}
