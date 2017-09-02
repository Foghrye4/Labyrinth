package labyrinth.block;

import java.util.List;

import javax.annotation.Nullable;

import labyrinth.tileentity.TileEntityVillageMarket;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockVillageMarket extends Block implements ITileEntityProvider {

	public static final AxisAlignedBB BOARD_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1D / 16D, 1.0D);
	public static final AxisAlignedBB PLATE_AABB = new AxisAlignedBB(7/16D, 6/16D, 1/16D, 1.0D, 15D / 16D, 2/16D);
	public static final AxisAlignedBB SELECTION_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 2D / 16D, 1.0D);
	
	public BlockVillageMarket(Material materialIn) {
		super(materialIn);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote)
			return true;
		TileEntity te = worldIn.getTileEntity(pos);
		if (te instanceof TileEntityVillageMarket)
			((TileEntityVillageMarket) te).tryTrade(playerIn);
		return true;
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		IBlockState state = worldIn.getBlockState(pos.down());
		return state.isFullCube();
	}

	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if (this.canPlaceBlockAt(worldIn, pos))
			return;
		if (worldIn.getBlockState(pos).getBlock() == this) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
		}
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {
		addCollisionBoxToList(pos, entityBox, collidingBoxes, BOARD_AABB);
		addCollisionBoxToList(pos, entityBox, collidingBoxes, PLATE_AABB);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return SELECTION_AABB;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityVillageMarket();
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
}
