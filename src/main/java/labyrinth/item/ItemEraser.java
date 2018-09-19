package labyrinth.item;

import labyrinth.LabyrinthMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemEraser extends Item {

	BlockPos from;
	BlockPos to;

	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			if (from == null) {
				from = pos;
			} else if (to == null) {
				to = pos;
				LabyrinthMod.proxy.getNetwork().showEraserFrameForPlayer(player, from, to);
			} else {
				if (to.equals(pos)) {
					BlockPos.getAllInBox(from, to).forEach(b -> {
						worldIn.setBlockToAir(b);
					});
					LabyrinthMod.proxy.getNetwork().hideEraserFrameForPlayer(player);
				}
				from = null;
				to = null;
			}
		}
		return EnumActionResult.SUCCESS;
	}

}
