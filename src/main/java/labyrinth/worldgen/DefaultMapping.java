package labyrinth.worldgen;

import java.util.Arrays;

import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockBeetroot;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStem;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockTripWire;
import net.minecraft.block.BlockTripWireHook;
import net.minecraft.block.BlockWoodSlab;
import net.minecraft.block.BlockBed.EnumPartType;
import net.minecraft.block.BlockFlowerPot.EnumFlowerType;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.BlockStairs.EnumShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;

public class DefaultMapping {
	public final IBlockState mapping[] = new IBlockState[256];
	
	public DefaultMapping() {
		Arrays.fill(mapping, Blocks.AIR.getDefaultState());
		mapping[1] = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT,
				BlockStone.EnumType.DIORITE);
		mapping[2] = Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT,
				BlockStone.EnumType.DIORITE_SMOOTH);
		mapping[3] = Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.NORTH);
		mapping[4] = Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.SOUTH);
		mapping[5] = Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.WEST);
		mapping[6] = Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.EAST);
		mapping[7] = Blocks.FURNACE.getDefaultState().withProperty(BlockFurnace.FACING, EnumFacing.NORTH);
		mapping[8] = Blocks.FURNACE.getDefaultState().withProperty(BlockFurnace.FACING, EnumFacing.SOUTH);
		mapping[9] = Blocks.FURNACE.getDefaultState().withProperty(BlockFurnace.FACING, EnumFacing.WEST);
		mapping[10] = Blocks.FURNACE.getDefaultState().withProperty(BlockFurnace.FACING, EnumFacing.EAST);
		mapping[11] = Blocks.CRAFTING_TABLE.getDefaultState();
		mapping[12] = Blocks.CAULDRON.getBlockState().getBaseState().withProperty(BlockCauldron.LEVEL, Integer.valueOf(3));
		mapping[13] = Blocks.BREWING_STAND.getDefaultState();
		mapping[14] = Blocks.COAL_BLOCK.getDefaultState();
		mapping[15] = Blocks.WOODEN_SLAB.getDefaultState()
				.withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP)
				.withProperty(BlockWoodSlab.VARIANT, BlockPlanks.EnumType.OAK);
		mapping[16] = Blocks.LAVA.getDefaultState();
		mapping[17] = Blocks.WATER.getDefaultState();
		mapping[18] = Blocks.IRON_BARS.getDefaultState();
		IBlockState stair = Blocks.QUARTZ_STAIRS.getDefaultState().withProperty(BlockStairs.SHAPE, EnumShape.STRAIGHT);
		addStairs(mapping, stair);
		mapping[57] = Blocks.WOODEN_SLAB.getDefaultState()
				.withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.BOTTOM)
				.withProperty(BlockWoodSlab.VARIANT, BlockPlanks.EnumType.OAK);

		mapping[58] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		mapping[59] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		mapping[60] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		mapping[61] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		mapping[62] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		mapping[63] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		mapping[64] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		mapping[65] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		mapping[66] = Blocks.DIRT.getDefaultState();
		mapping[67] = Blocks.MELON_BLOCK.getDefaultState();
		mapping[68] = Blocks.PUMPKIN.getDefaultState().withProperty(BlockHorizontal.FACING, EnumFacing.SOUTH);
		mapping[69] = Blocks.MELON_STEM.getDefaultState().withProperty(BlockStem.AGE, 7);
		mapping[70] = Blocks.PUMPKIN_STEM.getDefaultState().withProperty(BlockStem.AGE, 7);
		mapping[71] = Blocks.SAND.getDefaultState();
		mapping[72] = Blocks.REEDS.getDefaultState();
		mapping[73] = Blocks.POTATOES.getDefaultState().withProperty(BlockCrops.AGE, 7);
		mapping[74] = Blocks.CARROTS.getDefaultState().withProperty(BlockCrops.AGE, 7);
		mapping[75] = Blocks.LEAVES.getBlockState().getBaseState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.OAK).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false)).withProperty(BlockLeaves.DECAYABLE, Boolean.valueOf(true));
		mapping[76] = Blocks.CARPET.getBlockState().getBaseState().withProperty(BlockCarpet.COLOR, EnumDyeColor.RED);
		mapping[77] = Blocks.FLOWER_POT.getDefaultState().withProperty(BlockFlowerPot.CONTENTS, EnumFlowerType.BLUE_ORCHID);
		
		mapping[78] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		mapping[79] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		mapping[80] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		mapping[81] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		mapping[82] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		mapping[83] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		mapping[84] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		mapping[85] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		mapping[86] = Blocks.BED.getDefaultState().withProperty(BlockHorizontal.FACING, EnumFacing.NORTH).withProperty(BlockBed.PART, EnumPartType.FOOT);
		mapping[87] = Blocks.BED.getDefaultState().withProperty(BlockHorizontal.FACING, EnumFacing.NORTH).withProperty(BlockBed.PART, EnumPartType.HEAD);
		mapping[88] = Blocks.LEAVES.getBlockState().getBaseState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.OAK).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(true)).withProperty(BlockLeaves.DECAYABLE, Boolean.valueOf(true));
		mapping[89] = Blocks.STICKY_PISTON.getDefaultState()
				.withProperty(BlockPistonBase.EXTENDED, Boolean.valueOf(true))
				.withProperty(BlockDirectional.FACING, EnumFacing.EAST);
		mapping[90] = Blocks.PISTON_HEAD.getDefaultState()
				.withProperty(BlockPistonExtension.TYPE, BlockPistonExtension.EnumPistonType.STICKY)
				.withProperty(BlockDirectional.FACING, EnumFacing.EAST)
				.withProperty(BlockPistonExtension.SHORT, Boolean.valueOf(false));
		mapping[91] = Blocks.LEVER.getDefaultState()
				.withProperty(BlockLever.FACING, BlockLever.EnumOrientation.WEST)
				.withProperty(BlockLever.POWERED, Boolean.valueOf(true));
		mapping[92] = Blocks.STICKY_PISTON.getDefaultState()
				.withProperty(BlockPistonBase.EXTENDED, Boolean.valueOf(true))
				.withProperty(BlockDirectional.FACING, EnumFacing.WEST);
		mapping[93] = Blocks.PISTON_HEAD.getDefaultState()
				.withProperty(BlockPistonExtension.TYPE, BlockPistonExtension.EnumPistonType.STICKY)
				.withProperty(BlockDirectional.FACING, EnumFacing.WEST)
				.withProperty(BlockPistonExtension.SHORT, Boolean.valueOf(false));
		mapping[94] = Blocks.PISTON_HEAD.getDefaultState()
				.withProperty(BlockPistonExtension.TYPE, BlockPistonExtension.EnumPistonType.STICKY)
				.withProperty(BlockDirectional.FACING, EnumFacing.SOUTH)
				.withProperty(BlockPistonExtension.SHORT, Boolean.valueOf(false));
		mapping[95] = Blocks.POWERED_REPEATER.getDefaultState()
				.withProperty(BlockRedstoneRepeater.DELAY, 1)
				.withProperty(BlockHorizontal.FACING, EnumFacing.WEST)
				.withProperty(BlockRedstoneRepeater.LOCKED, Boolean.valueOf(false));
		mapping[96] = Blocks.POWERED_REPEATER.getDefaultState()
				.withProperty(BlockRedstoneRepeater.DELAY, 2)
				.withProperty(BlockHorizontal.FACING, EnumFacing.WEST)
				.withProperty(BlockRedstoneRepeater.LOCKED, Boolean.valueOf(false));
		mapping[97] = Blocks.POWERED_REPEATER.getDefaultState()
				.withProperty(BlockRedstoneRepeater.DELAY, 3)
				.withProperty(BlockHorizontal.FACING, EnumFacing.WEST)
				.withProperty(BlockRedstoneRepeater.LOCKED, Boolean.valueOf(false));
		mapping[98] = Blocks.POWERED_REPEATER.getDefaultState()
				.withProperty(BlockRedstoneRepeater.DELAY, 4)
				.withProperty(BlockHorizontal.FACING, EnumFacing.WEST)
				.withProperty(BlockRedstoneRepeater.LOCKED, Boolean.valueOf(false));
		mapping[99] = Blocks.POWERED_REPEATER.getDefaultState()
				.withProperty(BlockRedstoneRepeater.DELAY, 1)
				.withProperty(BlockHorizontal.FACING, EnumFacing.NORTH)
				.withProperty(BlockRedstoneRepeater.LOCKED, Boolean.valueOf(false));
		mapping[100] = Blocks.POWERED_REPEATER.getDefaultState()
				.withProperty(BlockRedstoneRepeater.DELAY, 2)
				.withProperty(BlockHorizontal.FACING, EnumFacing.NORTH)
				.withProperty(BlockRedstoneRepeater.LOCKED, Boolean.valueOf(false));
		mapping[101] = Blocks.POWERED_REPEATER.getDefaultState()
				.withProperty(BlockRedstoneRepeater.DELAY, 3)
				.withProperty(BlockHorizontal.FACING, EnumFacing.NORTH)
				.withProperty(BlockRedstoneRepeater.LOCKED, Boolean.valueOf(false));
		mapping[102] = Blocks.POWERED_REPEATER.getDefaultState()
				.withProperty(BlockRedstoneRepeater.DELAY, 4)
				.withProperty(BlockHorizontal.FACING, EnumFacing.NORTH)
				.withProperty(BlockRedstoneRepeater.LOCKED, Boolean.valueOf(false));
		mapping[103] = Blocks.UNLIT_REDSTONE_TORCH.getDefaultState();
		mapping[104] = Blocks.REDSTONE_WIRE.getDefaultState()
				.withProperty(BlockRedstoneWire.POWER, 15);
		mapping[105] = Blocks.REDSTONE_WIRE.getDefaultState()
				.withProperty(BlockRedstoneWire.POWER, 14);
		mapping[106] = Blocks.REDSTONE_WIRE.getDefaultState()
				.withProperty(BlockRedstoneWire.POWER, 13);
		mapping[107] = Blocks.REDSTONE_WIRE.getDefaultState()
				.withProperty(BlockRedstoneWire.POWER, 12);
		mapping[108] = Blocks.REDSTONE_WIRE.getDefaultState()
				.withProperty(BlockRedstoneWire.POWER, 11);
		mapping[109] = Blocks.REDSTONE_WIRE.getDefaultState()
				.withProperty(BlockRedstoneWire.POWER, 10);
		mapping[110] = Blocks.REDSTONE_WIRE.getDefaultState()
				.withProperty(BlockRedstoneWire.POWER, 9);
		mapping[111] = Blocks.REDSTONE_WIRE.getDefaultState()
				.withProperty(BlockRedstoneWire.POWER, 8);
		mapping[112] = Blocks.REDSTONE_WIRE.getDefaultState()
				.withProperty(BlockRedstoneWire.POWER, 7);
		mapping[113] = Blocks.REDSTONE_WIRE.getDefaultState()
				.withProperty(BlockRedstoneWire.POWER, 6);
		mapping[114] = Blocks.REDSTONE_WIRE.getDefaultState()
				.withProperty(BlockRedstoneWire.POWER, 5);
		mapping[115] = Blocks.REDSTONE_WIRE.getDefaultState()
				.withProperty(BlockRedstoneWire.POWER, 0);
		mapping[116] = Blocks.ANVIL.getDefaultState().withProperty(BlockAnvil.FACING, EnumFacing.NORTH)
				.withProperty(BlockAnvil.DAMAGE, Integer.valueOf(0));
		mapping[117] = Blocks.ANVIL.getDefaultState().withProperty(BlockAnvil.FACING, EnumFacing.SOUTH)
				.withProperty(BlockAnvil.DAMAGE, Integer.valueOf(0));
		mapping[118] = Blocks.ANVIL.getDefaultState().withProperty(BlockAnvil.FACING, EnumFacing.WEST)
				.withProperty(BlockAnvil.DAMAGE, Integer.valueOf(0));
		mapping[119] = Blocks.ANVIL.getDefaultState().withProperty(BlockAnvil.FACING, EnumFacing.EAST)
				.withProperty(BlockAnvil.DAMAGE, Integer.valueOf(0));
		mapping[120] = Blocks.DARK_OAK_FENCE.getDefaultState();
		mapping[129] = Blocks.STICKY_PISTON.getDefaultState()
				.withProperty(BlockPistonBase.EXTENDED, Boolean.valueOf(true))
				.withProperty(BlockDirectional.FACING, EnumFacing.NORTH);
		mapping[130] = Blocks.PISTON_HEAD.getDefaultState()
				.withProperty(BlockPistonExtension.TYPE, BlockPistonExtension.EnumPistonType.STICKY)
				.withProperty(BlockDirectional.FACING, EnumFacing.NORTH)
				.withProperty(BlockPistonExtension.SHORT, Boolean.valueOf(false));
		mapping[131] = Blocks.REDSTONE_TORCH.getDefaultState();
		mapping[137] = Blocks.LEVER.getDefaultState()
				.withProperty(BlockLever.FACING, BlockLever.EnumOrientation.EAST)
				.withProperty(BlockLever.POWERED, Boolean.valueOf(true));
		mapping[138] = Blocks.POWERED_REPEATER.getDefaultState()
				.withProperty(BlockRedstoneRepeater.DELAY, 1)
				.withProperty(BlockHorizontal.FACING, EnumFacing.EAST)
				.withProperty(BlockRedstoneRepeater.LOCKED, Boolean.valueOf(false));
		mapping[139] = Blocks.POWERED_REPEATER.getDefaultState()
				.withProperty(BlockRedstoneRepeater.DELAY, 2)
				.withProperty(BlockHorizontal.FACING, EnumFacing.EAST)
				.withProperty(BlockRedstoneRepeater.LOCKED, Boolean.valueOf(false));
		mapping[140] = Blocks.POWERED_REPEATER.getDefaultState()
				.withProperty(BlockRedstoneRepeater.DELAY, 3)
				.withProperty(BlockHorizontal.FACING, EnumFacing.EAST)
				.withProperty(BlockRedstoneRepeater.LOCKED, Boolean.valueOf(false));
		mapping[141] = Blocks.POWERED_REPEATER.getDefaultState()
				.withProperty(BlockRedstoneRepeater.DELAY, 4)
				.withProperty(BlockHorizontal.FACING, EnumFacing.EAST)
				.withProperty(BlockRedstoneRepeater.LOCKED, Boolean.valueOf(false));
		mapping[142] = Blocks.POWERED_REPEATER.getDefaultState()
				.withProperty(BlockRedstoneRepeater.DELAY, 1)
				.withProperty(BlockHorizontal.FACING, EnumFacing.SOUTH)
				.withProperty(BlockRedstoneRepeater.LOCKED, Boolean.valueOf(false));
		mapping[143] = Blocks.POWERED_REPEATER.getDefaultState()
				.withProperty(BlockRedstoneRepeater.DELAY, 2)
				.withProperty(BlockHorizontal.FACING, EnumFacing.SOUTH)
				.withProperty(BlockRedstoneRepeater.LOCKED, Boolean.valueOf(false));
		mapping[144] = Blocks.POWERED_REPEATER.getDefaultState()
				.withProperty(BlockRedstoneRepeater.DELAY, 3)
				.withProperty(BlockHorizontal.FACING, EnumFacing.SOUTH)
				.withProperty(BlockRedstoneRepeater.LOCKED, Boolean.valueOf(false));
		mapping[145] = Blocks.POWERED_REPEATER.getDefaultState()
				.withProperty(BlockRedstoneRepeater.DELAY, 4)
				.withProperty(BlockHorizontal.FACING, EnumFacing.SOUTH)
				.withProperty(BlockRedstoneRepeater.LOCKED, Boolean.valueOf(false));

		mapping[153] = Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.UP);
		mapping[154] = Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.NORTH);
		mapping[155] = Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.SOUTH);
		mapping[156] = Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.WEST);
		mapping[157] = Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.EAST);
		mapping[158] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.NORTH)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		mapping[159] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.NORTH)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		mapping[160] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.NORTH)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		mapping[161] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.NORTH)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		mapping[162] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.SOUTH)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		mapping[163] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.SOUTH)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		mapping[164] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.SOUTH)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		mapping[165] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.SOUTH)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		mapping[166] = Blocks.BOOKSHELF.getDefaultState();
		mapping[167] = Blocks.LADDER.getDefaultState().withProperty(BlockLadder.FACING, EnumFacing.SOUTH);
		mapping[171] = Blocks.ENCHANTING_TABLE.getDefaultState();
		mapping[172] = Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, EnumType.OAK);
		mapping[173] = Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH)
				.withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);
		mapping[174] = Blocks.LOG.getDefaultState().withProperty(BlockLog.LOG_AXIS, EnumAxis.X);
		mapping[175] = Blocks.LOG.getDefaultState().withProperty(BlockLog.LOG_AXIS, EnumAxis.Y);
		mapping[176] = Blocks.LOG.getDefaultState().withProperty(BlockLog.LOG_AXIS, EnumAxis.Z);
		mapping[177] = Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH)
				.withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);
		mapping[178] = Blocks.FARMLAND.getDefaultState().withProperty(BlockFarmland.MOISTURE, Integer.valueOf(7));
		mapping[179] = Blocks.REDSTONE_BLOCK.getDefaultState();
		mapping[180] = Blocks.LIT_REDSTONE_LAMP.getDefaultState();
		mapping[181] = Blocks.WHEAT.getDefaultState().withProperty(BlockCrops.AGE, 7);
		mapping[182] = Blocks.BEETROOTS.getDefaultState().withProperty(BlockBeetroot.BEETROOT_AGE, 3);
		mapping[183] = Blocks.HAY_BLOCK.getDefaultState().withProperty(BlockRotatedPillar.AXIS, EnumFacing.Axis.X);
		mapping[184] = Blocks.HAY_BLOCK.getDefaultState().withProperty(BlockRotatedPillar.AXIS, EnumFacing.Axis.Y);
		mapping[185] = Blocks.HAY_BLOCK.getDefaultState().withProperty(BlockRotatedPillar.AXIS, EnumFacing.Axis.Z);
		mapping[186] = Blocks.GRASS.getDefaultState();
		mapping[187] = Blocks.ACACIA_FENCE_GATE.getDefaultState().withProperty(BlockHorizontal.FACING, EnumFacing.WEST);
		mapping[188] = Blocks.ACACIA_FENCE_GATE.getDefaultState().withProperty(BlockHorizontal.FACING, EnumFacing.SOUTH);
		mapping[189] = Blocks.ACACIA_FENCE_GATE.getDefaultState().withProperty(BlockHorizontal.FACING, EnumFacing.NORTH);
		mapping[190] = Blocks.ACACIA_FENCE_GATE.getDefaultState().withProperty(BlockHorizontal.FACING, EnumFacing.EAST);
		mapping[191] = Blocks.TNT.getDefaultState();
		mapping[192] = Blocks.TRIPWIRE_HOOK.getDefaultState().withProperty(BlockTripWireHook.FACING, EnumFacing.WEST).withProperty(BlockTripWireHook.ATTACHED, true);
		mapping[193] = Blocks.TRIPWIRE_HOOK.getDefaultState().withProperty(BlockTripWireHook.FACING, EnumFacing.SOUTH).withProperty(BlockTripWireHook.ATTACHED, true);
		mapping[194] = Blocks.TRIPWIRE_HOOK.getDefaultState().withProperty(BlockTripWireHook.FACING, EnumFacing.NORTH).withProperty(BlockTripWireHook.ATTACHED, true);
		mapping[195] = Blocks.TRIPWIRE_HOOK.getDefaultState().withProperty(BlockTripWireHook.FACING, EnumFacing.EAST).withProperty(BlockTripWireHook.ATTACHED, true);
		mapping[196] = Blocks.TRIPWIRE.getDefaultState().withProperty(BlockTripWire.ATTACHED, true);
		mapping[197] = Blocks.PISTON.getDefaultState()
				.withProperty(BlockPistonBase.EXTENDED, Boolean.valueOf(false))
				.withProperty(BlockDirectional.FACING, EnumFacing.SOUTH);
		mapping[198] = Blocks.PISTON.getDefaultState()
				.withProperty(BlockPistonBase.EXTENDED, Boolean.valueOf(false))
				.withProperty(BlockDirectional.FACING, EnumFacing.UP);
		mapping[199] = Blocks.PISTON.getDefaultState()
				.withProperty(BlockPistonBase.EXTENDED, Boolean.valueOf(false))
				.withProperty(BlockDirectional.FACING, EnumFacing.EAST);
		mapping[200] = Blocks.STONE_PRESSURE_PLATE.getDefaultState();
		mapping[251] = Blocks.CYAN_GLAZED_TERRACOTTA.getBlockState().getBaseState().withProperty(BlockHorizontal.FACING, EnumFacing.WEST);
		mapping[252] = Blocks.CYAN_GLAZED_TERRACOTTA.getBlockState().getBaseState().withProperty(BlockHorizontal.FACING, EnumFacing.NORTH);
		mapping[253] = Blocks.NETHER_WART_BLOCK.getDefaultState();
		mapping[254] = Blocks.BEDROCK.getDefaultState();
		mapping[255] = Blocks.STONE.getDefaultState();
	}

	private void addStairs(IBlockState[] blockstateIn, IBlockState stair) {
		blockstateIn[19] = stair.withProperty(BlockStairs.FACING, EnumFacing.NORTH).withProperty(BlockStairs.HALF,
				EnumHalf.TOP);
		blockstateIn[24] = stair.withProperty(BlockStairs.FACING, EnumFacing.NORTH).withProperty(BlockStairs.HALF,
				EnumHalf.BOTTOM);
		blockstateIn[25] = stair.withProperty(BlockStairs.FACING, EnumFacing.NORTH)
				.withProperty(BlockStairs.HALF, EnumHalf.BOTTOM).withProperty(BlockStairs.SHAPE, EnumShape.INNER_RIGHT);
		blockstateIn[26] = stair.withProperty(BlockStairs.FACING, EnumFacing.NORTH)
				.withProperty(BlockStairs.HALF, EnumHalf.BOTTOM).withProperty(BlockStairs.SHAPE, EnumShape.INNER_LEFT);
		blockstateIn[27] = stair.withProperty(BlockStairs.FACING, EnumFacing.NORTH)
				.withProperty(BlockStairs.HALF, EnumHalf.BOTTOM).withProperty(BlockStairs.SHAPE, EnumShape.OUTER_RIGHT);
		blockstateIn[28] = stair.withProperty(BlockStairs.FACING, EnumFacing.NORTH)
				.withProperty(BlockStairs.HALF, EnumHalf.BOTTOM).withProperty(BlockStairs.SHAPE, EnumShape.OUTER_LEFT);
		blockstateIn[29] = stair.withProperty(BlockStairs.FACING, EnumFacing.SOUTH).withProperty(BlockStairs.HALF,
				EnumHalf.TOP);
		blockstateIn[34] = stair.withProperty(BlockStairs.FACING, EnumFacing.SOUTH).withProperty(BlockStairs.HALF,
				EnumHalf.BOTTOM);
		blockstateIn[39] = stair.withProperty(BlockStairs.FACING, EnumFacing.WEST).withProperty(BlockStairs.HALF,
				EnumHalf.TOP);
		blockstateIn[40] = stair.withProperty(BlockStairs.FACING, EnumFacing.WEST)
				.withProperty(BlockStairs.HALF, EnumHalf.TOP).withProperty(BlockStairs.SHAPE, EnumShape.INNER_RIGHT);
		blockstateIn[41] = stair.withProperty(BlockStairs.FACING, EnumFacing.WEST)
				.withProperty(BlockStairs.HALF, EnumHalf.TOP).withProperty(BlockStairs.SHAPE, EnumShape.INNER_LEFT);
		blockstateIn[44] = stair.withProperty(BlockStairs.FACING, EnumFacing.WEST).withProperty(BlockStairs.HALF,
				EnumHalf.BOTTOM);
		blockstateIn[45] = stair.withProperty(BlockStairs.FACING, EnumFacing.WEST)
				.withProperty(BlockStairs.HALF, EnumHalf.BOTTOM).withProperty(BlockStairs.SHAPE, EnumShape.INNER_RIGHT);
		blockstateIn[46] = stair.withProperty(BlockStairs.FACING, EnumFacing.WEST)
				.withProperty(BlockStairs.HALF, EnumHalf.BOTTOM).withProperty(BlockStairs.SHAPE, EnumShape.INNER_LEFT);
		blockstateIn[49] = stair.withProperty(BlockStairs.FACING, EnumFacing.EAST).withProperty(BlockStairs.HALF,
				EnumHalf.TOP);
		blockstateIn[50] = stair.withProperty(BlockStairs.FACING, EnumFacing.EAST)
				.withProperty(BlockStairs.HALF, EnumHalf.TOP).withProperty(BlockStairs.SHAPE, EnumShape.INNER_RIGHT);
		blockstateIn[51] = stair.withProperty(BlockStairs.FACING, EnumFacing.EAST)
				.withProperty(BlockStairs.HALF, EnumHalf.TOP).withProperty(BlockStairs.SHAPE, EnumShape.INNER_LEFT);
		blockstateIn[54] = stair.withProperty(BlockStairs.FACING, EnumFacing.EAST).withProperty(BlockStairs.HALF,
				EnumHalf.BOTTOM);
		blockstateIn[55] = stair.withProperty(BlockStairs.FACING, EnumFacing.EAST)
				.withProperty(BlockStairs.HALF, EnumHalf.BOTTOM).withProperty(BlockStairs.SHAPE, EnumShape.INNER_RIGHT);
		blockstateIn[56] = stair.withProperty(BlockStairs.FACING, EnumFacing.EAST)
				.withProperty(BlockStairs.HALF, EnumHalf.BOTTOM).withProperty(BlockStairs.SHAPE, EnumShape.INNER_LEFT);
	}

	public int getId(IBlockState bs) {
		for (int i = 0; i < mapping.length; i++) {
			if (mapping[i].equals(bs))
				return i;
		}
		return -1;
	}
}
