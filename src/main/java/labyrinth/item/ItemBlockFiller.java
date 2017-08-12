package labyrinth.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBlockFiller extends Item {

	BlockPos from;
	BlockPos to;

	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			if (from == null) {
				from = pos;
			} else if (to == null) {
				to = pos;
			} else {
				if (to.equals(pos)) {
					IBlockState bstate = worldIn.getBlockState(from);
					BlockPos.getAllInBox(from, to).forEach(b -> {
						worldIn.setBlockState(b, bstate);
					});
				}
				from = null;
				to = null;
			}
		}
		return EnumActionResult.SUCCESS;
	}

}
