package labyrinth.worldgen;

import java.util.List;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

import com.google.common.base.Optional;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import labyrinth.init.LabyrinthBlocks;
import labyrinth.init.LabyrinthEntities;
import labyrinth.util.ModIntegrationUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockBed.EnumPartType;
import net.minecraft.block.BlockBeetroot;
import net.minecraft.block.BlockCarpet;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.BlockFlowerPot.EnumFlowerType;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockHay;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.BlockNewLeaf;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockRedstoneRepeater;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.BlockStairs.EnumShape;
import net.minecraft.block.BlockStem;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockTripWire;
import net.minecraft.block.BlockTripWireHook;
import net.minecraft.block.BlockWall;
import net.minecraft.block.BlockWoodSlab;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;

public class LevelFeaturesStorage {
	public IBlockState[][] blockstateList = new IBlockState[128][256];

	private final Random random = new Random();
	private final List<IBlockState> WALL_CANDIDATES = new ArrayList<IBlockState>();
	private final List<IBlockState> STAIR_CANDIDATES = new ArrayList<IBlockState>();
	private final List<IBlockState> WINDOW_CANDIDATES = new ArrayList<IBlockState>();
	private final List<IBlockState> FENCE_CANDIDATES = new ArrayList<IBlockState>();

	@SuppressWarnings("unchecked")
	private final Class<? extends EntityLiving>[] MOB_CANDIDATES_FIRST = new Class[] { LabyrinthEntities.ZOMBIE,
			LabyrinthEntities.CAVE_SPIDER, LabyrinthEntities.CREEPER, LabyrinthEntities.ENDERMAN,
			LabyrinthEntities.ENDERMITE, LabyrinthEntities.MAGMA_CUBE, LabyrinthEntities.PIG_ZOMBIE,
			LabyrinthEntities.SPIDER, LabyrinthEntities.SLIME, LabyrinthEntities.WITHER_SKELETON };

	@SuppressWarnings("unchecked")
	private final Class<? extends EntityLiving>[] MOB_CANDIDATES_SECOND = new Class[] { LabyrinthEntities.BLAZE,
			LabyrinthEntities.SKELETON, LabyrinthEntities.STRAY, LabyrinthEntities.VINDICATOR,
			LabyrinthEntities.VEX };
	
	@SuppressWarnings("unchecked")
	private final Class<? extends EntityLiving>[] MOB_CANDIDATES_CLAUSTROPHOBIC = new Class[] { 
			LabyrinthEntities.ZOMBIE, LabyrinthEntities.MINI_SPIDER,
			LabyrinthEntities.CREEPER, LabyrinthEntities.ENDERMITE, 
			LabyrinthEntities.MAGMA_CUBE, LabyrinthEntities.PIG_ZOMBIE,
			LabyrinthEntities.SLIME, LabyrinthEntities.WITHER_SKELETON, 
			LabyrinthEntities.BLAZE, LabyrinthEntities.SKELETON, LabyrinthEntities.STRAY,
			LabyrinthEntities.VINDICATOR};

	
	@SuppressWarnings("unchecked")
	final Class<? extends EntityLiving>[][] levelToMob = new Class[128][2];

	public long lastSeed = 0;
	
	@SuppressWarnings("deprecation")
	public void defineCandidates(){
		Block.BLOCK_STATE_IDS.forEach(blockstate -> {
			if(blockstate.getBlock().hasTileEntity(blockstate))
				return;
			if(blockstate.isFullBlock() && 
					blockstate.isOpaqueCube() && 
					blockstate.getMaterial().blocksMovement()){
				WALL_CANDIDATES.add(blockstate);
			}
			Collection<IProperty<?>> properties = blockstate.getPropertyKeys();
			if(properties.contains(BlockStairs.FACING) && 
					properties.contains(BlockStairs.HALF) &&
					properties.contains(BlockStairs.SHAPE)){
				STAIR_CANDIDATES.add(blockstate);
			}
			if(blockstate.getBlock() instanceof BlockPane){
				WINDOW_CANDIDATES.add(blockstate);
			}
			if(blockstate.getBlock() instanceof BlockFence || blockstate.getBlock() instanceof BlockWall){
				FENCE_CANDIDATES.add(blockstate);
			}
		});
	}
	
	public void generateRandom(long seed) {
		this.lastSeed  = seed;
		random.setSeed(seed);
		for (int i = 0; i < levelToMob.length; i++) {
			if (i > 24) {
				levelToMob[i][0] = MOB_CANDIDATES_CLAUSTROPHOBIC[random.nextInt(MOB_CANDIDATES_FIRST.length)];
				levelToMob[i][1] = MOB_CANDIDATES_CLAUSTROPHOBIC[random.nextInt(MOB_CANDIDATES_SECOND.length)];
			} else {
				levelToMob[i][0] = MOB_CANDIDATES_FIRST[random.nextInt(MOB_CANDIDATES_FIRST.length)];
				levelToMob[i][1] = MOB_CANDIDATES_SECOND[random.nextInt(MOB_CANDIDATES_SECOND.length)];
			}
		}
		levelToMob[0][0] = LabyrinthEntities.ZOMBIE;
		levelToMob[0][1] = LabyrinthEntities.ZOMBIE;
		levelToMob[1][0] = LabyrinthEntities.ZOMBIE;
		levelToMob[1][1] = LabyrinthEntities.SLIME;
		levelToMob[2][0] = LabyrinthEntities.ZOMBIE;
		levelToMob[2][1] = LabyrinthEntities.SKELETON;
		levelToMob[3][0] = LabyrinthEntities.CREEPER;
		levelToMob[3][1] = LabyrinthEntities.SKELETON;
		levelToMob[7][0] = LabyrinthEntities.MAGMA_CUBE;
		levelToMob[7][1] = LabyrinthEntities.BLAZE;

		Arrays.fill(blockstateList[0], Blocks.AIR.getDefaultState());
		blockstateList[0][1] = Blocks.QUARTZ_BLOCK.getDefaultState();
		blockstateList[0][2] = Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT,
				BlockQuartz.EnumType.CHISELED);
		blockstateList[0][3] = Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.NORTH);
		blockstateList[0][4] = Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.SOUTH);
		blockstateList[0][5] = Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.WEST);
		blockstateList[0][6] = Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.EAST);
		blockstateList[0][7] = Blocks.FURNACE.getDefaultState().withProperty(BlockFurnace.FACING, EnumFacing.NORTH);
		blockstateList[0][8] = Blocks.FURNACE.getDefaultState().withProperty(BlockFurnace.FACING, EnumFacing.SOUTH);
		blockstateList[0][9] = Blocks.FURNACE.getDefaultState().withProperty(BlockFurnace.FACING, EnumFacing.WEST);
		blockstateList[0][10] = Blocks.FURNACE.getDefaultState().withProperty(BlockFurnace.FACING, EnumFacing.EAST);
		blockstateList[0][11] = Blocks.CRAFTING_TABLE.getDefaultState();
		blockstateList[0][12] = ModIntegrationUtil.getBlockDefaultStateIfNotNull("aov:blocks/blockangelic",
				Blocks.CAULDRON.getBlockState().getBaseState().withProperty(BlockCauldron.LEVEL, Integer.valueOf(3)));
		blockstateList[0][13] = Blocks.BREWING_STAND.getDefaultState();
		blockstateList[0][14] = Blocks.COAL_BLOCK.getDefaultState();
		blockstateList[0][15] = Blocks.WOODEN_SLAB.getDefaultState()
				.withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP)
				.withProperty(BlockWoodSlab.VARIANT, BlockPlanks.EnumType.OAK);
		blockstateList[0][16] = Blocks.LAVA.getDefaultState();
		blockstateList[0][17] = Blocks.WATER.getDefaultState();
		blockstateList[0][18] = Blocks.STAINED_GLASS_PANE.getDefaultState().withProperty(BlockStainedGlassPane.COLOR,
				EnumDyeColor.RED);
		IBlockState stair = Blocks.QUARTZ_STAIRS.getDefaultState().withProperty(BlockStairs.SHAPE, EnumShape.STRAIGHT);
		addStairs(blockstateList[0], stair);
		blockstateList[0][57] = Blocks.WOODEN_SLAB.getDefaultState()
				.withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.BOTTOM)
				.withProperty(BlockWoodSlab.VARIANT, BlockPlanks.EnumType.OAK);

		blockstateList[0][58] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][59] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][60] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][61] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][62] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][63] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][64] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][65] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][66] = Blocks.DIRT.getDefaultState();
		blockstateList[0][67] = Blocks.MELON_BLOCK.getDefaultState();
		blockstateList[0][68] = Blocks.PUMPKIN.getDefaultState().withProperty(BlockHorizontal.FACING, EnumFacing.SOUTH);
		blockstateList[0][69] = Blocks.MELON_STEM.getDefaultState().withProperty(BlockStem.AGE, 7);
		blockstateList[0][70] = Blocks.PUMPKIN_STEM.getDefaultState().withProperty(BlockStem.AGE, 7);
		blockstateList[0][71] = Blocks.SAND.getDefaultState();
		blockstateList[0][72] = Blocks.REEDS.getDefaultState();
		blockstateList[0][73] = Blocks.POTATOES.getDefaultState().withProperty(BlockCrops.AGE, 7);
		blockstateList[0][74] = Blocks.CARROTS.getDefaultState().withProperty(BlockCrops.AGE, 7);
		blockstateList[0][75] = Blocks.LEAVES.getBlockState().getBaseState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.OAK).withProperty(BlockNewLeaf.CHECK_DECAY, Boolean.valueOf(false)).withProperty(BlockNewLeaf.DECAYABLE, Boolean.valueOf(true));
		blockstateList[0][76] = Blocks.CARPET.getBlockState().getBaseState().withProperty(BlockCarpet.COLOR, EnumDyeColor.RED);
		blockstateList[0][77] = Blocks.FLOWER_POT.getDefaultState().withProperty(BlockFlowerPot.CONTENTS, EnumFlowerType.BLUE_ORCHID);
		
		blockstateList[0][78] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][79] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][80] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][81] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][82] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][83] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][84] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][85] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][86] = Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.NORTH).withProperty(BlockBed.PART, EnumPartType.FOOT);
		blockstateList[0][87] = Blocks.BED.getDefaultState().withProperty(BlockBed.FACING, EnumFacing.NORTH).withProperty(BlockBed.PART, EnumPartType.HEAD);
		blockstateList[0][88] = Blocks.LEAVES.getBlockState().getBaseState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.OAK).withProperty(BlockNewLeaf.CHECK_DECAY, Boolean.valueOf(true)).withProperty(BlockNewLeaf.DECAYABLE, Boolean.valueOf(true));
		blockstateList[0][89] = Blocks.STICKY_PISTON.getDefaultState()
				.withProperty(BlockPistonBase.EXTENDED, Boolean.valueOf(true))
				.withProperty(BlockPistonBase.FACING, EnumFacing.EAST);
		blockstateList[0][90] = Blocks.PISTON_HEAD.getDefaultState()
				.withProperty(BlockPistonExtension.TYPE, BlockPistonExtension.EnumPistonType.STICKY)
				.withProperty(BlockPistonExtension.FACING, EnumFacing.EAST)
				.withProperty(BlockPistonExtension.SHORT, Boolean.valueOf(false));
		blockstateList[0][91] = Blocks.LEVER.getDefaultState()
				.withProperty(BlockLever.FACING, BlockLever.EnumOrientation.WEST)
				.withProperty(BlockLever.POWERED, Boolean.valueOf(true));
		blockstateList[0][92] = Blocks.STICKY_PISTON.getDefaultState()
				.withProperty(BlockPistonBase.EXTENDED, Boolean.valueOf(true))
				.withProperty(BlockPistonBase.FACING, EnumFacing.WEST);
		blockstateList[0][93] = Blocks.PISTON_HEAD.getDefaultState()
				.withProperty(BlockPistonExtension.TYPE, BlockPistonExtension.EnumPistonType.STICKY)
				.withProperty(BlockPistonExtension.FACING, EnumFacing.WEST)
				.withProperty(BlockPistonExtension.SHORT, Boolean.valueOf(false));
		blockstateList[0][94] = Blocks.PISTON_HEAD.getDefaultState()
				.withProperty(BlockPistonExtension.TYPE, BlockPistonExtension.EnumPistonType.STICKY)
				.withProperty(BlockPistonExtension.FACING, EnumFacing.SOUTH)
				.withProperty(BlockPistonExtension.SHORT, Boolean.valueOf(false));
		blockstateList[0][95] = Blocks.POWERED_REPEATER.getDefaultState()
				.withProperty(BlockRedstoneRepeater.DELAY, 1)
				.withProperty(BlockRedstoneRepeater.FACING, EnumFacing.WEST)
				.withProperty(BlockRedstoneRepeater.LOCKED, Boolean.valueOf(false));
		blockstateList[0][96] = Blocks.POWERED_REPEATER.getDefaultState()
				.withProperty(BlockRedstoneRepeater.DELAY, 2)
				.withProperty(BlockRedstoneRepeater.FACING, EnumFacing.WEST)
				.withProperty(BlockRedstoneRepeater.LOCKED, Boolean.valueOf(false));
		blockstateList[0][97] = Blocks.POWERED_REPEATER.getDefaultState()
				.withProperty(BlockRedstoneRepeater.DELAY, 3)
				.withProperty(BlockRedstoneRepeater.FACING, EnumFacing.WEST)
				.withProperty(BlockRedstoneRepeater.LOCKED, Boolean.valueOf(false));
		blockstateList[0][98] = Blocks.POWERED_REPEATER.getDefaultState()
				.withProperty(BlockRedstoneRepeater.DELAY, 4)
				.withProperty(BlockRedstoneRepeater.FACING, EnumFacing.WEST)
				.withProperty(BlockRedstoneRepeater.LOCKED, Boolean.valueOf(false));
		blockstateList[0][99] = Blocks.POWERED_REPEATER.getDefaultState()
				.withProperty(BlockRedstoneRepeater.DELAY, 1)
				.withProperty(BlockRedstoneRepeater.FACING, EnumFacing.NORTH)
				.withProperty(BlockRedstoneRepeater.LOCKED, Boolean.valueOf(false));
		blockstateList[0][100] = Blocks.POWERED_REPEATER.getDefaultState()
				.withProperty(BlockRedstoneRepeater.DELAY, 2)
				.withProperty(BlockRedstoneRepeater.FACING, EnumFacing.NORTH)
				.withProperty(BlockRedstoneRepeater.LOCKED, Boolean.valueOf(false));
		blockstateList[0][101] = Blocks.POWERED_REPEATER.getDefaultState()
				.withProperty(BlockRedstoneRepeater.DELAY, 3)
				.withProperty(BlockRedstoneRepeater.FACING, EnumFacing.NORTH)
				.withProperty(BlockRedstoneRepeater.LOCKED, Boolean.valueOf(false));
		blockstateList[0][102] = Blocks.POWERED_REPEATER.getDefaultState()
				.withProperty(BlockRedstoneRepeater.DELAY, 4)
				.withProperty(BlockRedstoneRepeater.FACING, EnumFacing.NORTH)
				.withProperty(BlockRedstoneRepeater.LOCKED, Boolean.valueOf(false));
		blockstateList[0][103] = Blocks.UNLIT_REDSTONE_TORCH.getDefaultState();
		blockstateList[0][104] = Blocks.REDSTONE_WIRE.getDefaultState()
				.withProperty(BlockRedstoneWire.POWER, 15);
		blockstateList[0][105] = Blocks.REDSTONE_WIRE.getDefaultState()
				.withProperty(BlockRedstoneWire.POWER, 14);
		blockstateList[0][106] = Blocks.REDSTONE_WIRE.getDefaultState()
				.withProperty(BlockRedstoneWire.POWER, 13);
		blockstateList[0][107] = Blocks.REDSTONE_WIRE.getDefaultState()
				.withProperty(BlockRedstoneWire.POWER, 12);
		blockstateList[0][108] = Blocks.REDSTONE_WIRE.getDefaultState()
				.withProperty(BlockRedstoneWire.POWER, 11);
		blockstateList[0][109] = Blocks.REDSTONE_WIRE.getDefaultState()
				.withProperty(BlockRedstoneWire.POWER, 10);
		blockstateList[0][110] = Blocks.REDSTONE_WIRE.getDefaultState()
				.withProperty(BlockRedstoneWire.POWER, 9);
		blockstateList[0][111] = Blocks.REDSTONE_WIRE.getDefaultState()
				.withProperty(BlockRedstoneWire.POWER, 8);
		blockstateList[0][112] = Blocks.REDSTONE_WIRE.getDefaultState()
				.withProperty(BlockRedstoneWire.POWER, 7);
		blockstateList[0][113] = Blocks.REDSTONE_WIRE.getDefaultState()
				.withProperty(BlockRedstoneWire.POWER, 6);
		blockstateList[0][114] = Blocks.REDSTONE_WIRE.getDefaultState()
				.withProperty(BlockRedstoneWire.POWER, 5);
		blockstateList[0][115] = Blocks.REDSTONE_WIRE.getDefaultState()
				.withProperty(BlockRedstoneWire.POWER, 0);
		blockstateList[0][116] = Blocks.ANVIL.getDefaultState().withProperty(BlockAnvil.FACING, EnumFacing.NORTH)
				.withProperty(BlockAnvil.DAMAGE, Integer.valueOf(0));
		blockstateList[0][117] = Blocks.ANVIL.getDefaultState().withProperty(BlockAnvil.FACING, EnumFacing.SOUTH)
				.withProperty(BlockAnvil.DAMAGE, Integer.valueOf(0));
		blockstateList[0][118] = Blocks.ANVIL.getDefaultState().withProperty(BlockAnvil.FACING, EnumFacing.WEST)
				.withProperty(BlockAnvil.DAMAGE, Integer.valueOf(0));
		blockstateList[0][119] = Blocks.ANVIL.getDefaultState().withProperty(BlockAnvil.FACING, EnumFacing.EAST)
				.withProperty(BlockAnvil.DAMAGE, Integer.valueOf(0));
		blockstateList[0][120] = Blocks.ACACIA_FENCE.getDefaultState();
		blockstateList[0][121] = LabyrinthBlocks.COUNTER.getDefaultState();
		blockstateList[0][129] = Blocks.STICKY_PISTON.getDefaultState()
				.withProperty(BlockPistonBase.EXTENDED, Boolean.valueOf(true))
				.withProperty(BlockPistonBase.FACING, EnumFacing.NORTH);
		blockstateList[0][130] = Blocks.PISTON_HEAD.getDefaultState()
				.withProperty(BlockPistonExtension.TYPE, BlockPistonExtension.EnumPistonType.STICKY)
				.withProperty(BlockPistonExtension.FACING, EnumFacing.NORTH)
				.withProperty(BlockPistonExtension.SHORT, Boolean.valueOf(false));
		blockstateList[0][131] = Blocks.REDSTONE_TORCH.getDefaultState();
		blockstateList[0][137] = Blocks.LEVER.getDefaultState()
				.withProperty(BlockLever.FACING, BlockLever.EnumOrientation.EAST)
				.withProperty(BlockLever.POWERED, Boolean.valueOf(true));
		blockstateList[0][138] = Blocks.POWERED_REPEATER.getDefaultState()
				.withProperty(BlockRedstoneRepeater.DELAY, 1)
				.withProperty(BlockRedstoneRepeater.FACING, EnumFacing.EAST)
				.withProperty(BlockRedstoneRepeater.LOCKED, Boolean.valueOf(false));
		blockstateList[0][139] = Blocks.POWERED_REPEATER.getDefaultState()
				.withProperty(BlockRedstoneRepeater.DELAY, 2)
				.withProperty(BlockRedstoneRepeater.FACING, EnumFacing.EAST)
				.withProperty(BlockRedstoneRepeater.LOCKED, Boolean.valueOf(false));
		blockstateList[0][140] = Blocks.POWERED_REPEATER.getDefaultState()
				.withProperty(BlockRedstoneRepeater.DELAY, 3)
				.withProperty(BlockRedstoneRepeater.FACING, EnumFacing.EAST)
				.withProperty(BlockRedstoneRepeater.LOCKED, Boolean.valueOf(false));
		blockstateList[0][141] = Blocks.POWERED_REPEATER.getDefaultState()
				.withProperty(BlockRedstoneRepeater.DELAY, 4)
				.withProperty(BlockRedstoneRepeater.FACING, EnumFacing.EAST)
				.withProperty(BlockRedstoneRepeater.LOCKED, Boolean.valueOf(false));
		blockstateList[0][142] = Blocks.POWERED_REPEATER.getDefaultState()
				.withProperty(BlockRedstoneRepeater.DELAY, 1)
				.withProperty(BlockRedstoneRepeater.FACING, EnumFacing.SOUTH)
				.withProperty(BlockRedstoneRepeater.LOCKED, Boolean.valueOf(false));
		blockstateList[0][143] = Blocks.POWERED_REPEATER.getDefaultState()
				.withProperty(BlockRedstoneRepeater.DELAY, 2)
				.withProperty(BlockRedstoneRepeater.FACING, EnumFacing.SOUTH)
				.withProperty(BlockRedstoneRepeater.LOCKED, Boolean.valueOf(false));
		blockstateList[0][144] = Blocks.POWERED_REPEATER.getDefaultState()
				.withProperty(BlockRedstoneRepeater.DELAY, 3)
				.withProperty(BlockRedstoneRepeater.FACING, EnumFacing.SOUTH)
				.withProperty(BlockRedstoneRepeater.LOCKED, Boolean.valueOf(false));
		blockstateList[0][145] = Blocks.POWERED_REPEATER.getDefaultState()
				.withProperty(BlockRedstoneRepeater.DELAY, 4)
				.withProperty(BlockRedstoneRepeater.FACING, EnumFacing.SOUTH)
				.withProperty(BlockRedstoneRepeater.LOCKED, Boolean.valueOf(false));

		blockstateList[0][153] = Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.UP);
		blockstateList[0][154] = Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.NORTH);
		blockstateList[0][155] = Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.SOUTH);
		blockstateList[0][156] = Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.WEST);
		blockstateList[0][157] = Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.EAST);
		blockstateList[0][158] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.NORTH)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][159] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.NORTH)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][160] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.NORTH)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][161] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.NORTH)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][162] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.SOUTH)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][163] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.SOUTH)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][164] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.SOUTH)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][165] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.SOUTH)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][166] = Blocks.BOOKSHELF.getDefaultState();
		blockstateList[0][167] = Blocks.LADDER.getDefaultState().withProperty(BlockLadder.FACING, EnumFacing.SOUTH);
		blockstateList[0][171] = Blocks.ENCHANTING_TABLE.getDefaultState();
		blockstateList[0][172] = Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, EnumType.OAK);
		blockstateList[0][173] = Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH)
				.withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);
		blockstateList[0][174] = Blocks.LOG.getDefaultState().withProperty(BlockLog.LOG_AXIS, EnumAxis.X);
		blockstateList[0][175] = Blocks.LOG.getDefaultState().withProperty(BlockLog.LOG_AXIS, EnumAxis.Y);
		blockstateList[0][176] = Blocks.LOG.getDefaultState().withProperty(BlockLog.LOG_AXIS, EnumAxis.Z);
		blockstateList[0][177] = Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH)
				.withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);
		blockstateList[0][178] = Blocks.FARMLAND.getDefaultState().withProperty(BlockFarmland.MOISTURE, Integer.valueOf(7));
		blockstateList[0][179] = Blocks.REDSTONE_BLOCK.getDefaultState();
		blockstateList[0][180] = Blocks.LIT_REDSTONE_LAMP.getDefaultState();
		blockstateList[0][181] = Blocks.WHEAT.getDefaultState().withProperty(BlockCrops.AGE, 7);
		blockstateList[0][182] = Blocks.BEETROOTS.getDefaultState().withProperty(BlockBeetroot.BEETROOT_AGE, 3);
		blockstateList[0][183] = Blocks.HAY_BLOCK.getDefaultState().withProperty(BlockHay.AXIS, EnumFacing.Axis.X);
		blockstateList[0][184] = Blocks.HAY_BLOCK.getDefaultState().withProperty(BlockHay.AXIS, EnumFacing.Axis.Y);
		blockstateList[0][185] = Blocks.HAY_BLOCK.getDefaultState().withProperty(BlockHay.AXIS, EnumFacing.Axis.Z);
		blockstateList[0][186] = Blocks.GRASS.getDefaultState();
		blockstateList[0][187] = Blocks.ACACIA_FENCE_GATE.getDefaultState().withProperty(BlockHorizontal.FACING, EnumFacing.WEST);
		blockstateList[0][188] = Blocks.ACACIA_FENCE_GATE.getDefaultState().withProperty(BlockHorizontal.FACING, EnumFacing.SOUTH);
		blockstateList[0][189] = Blocks.ACACIA_FENCE_GATE.getDefaultState().withProperty(BlockHorizontal.FACING, EnumFacing.NORTH);
		blockstateList[0][190] = Blocks.ACACIA_FENCE_GATE.getDefaultState().withProperty(BlockHorizontal.FACING, EnumFacing.EAST);
		blockstateList[0][191] = Blocks.TNT.getDefaultState();
		blockstateList[0][192] = Blocks.TRIPWIRE_HOOK.getDefaultState().withProperty(BlockTripWireHook.FACING, EnumFacing.WEST).withProperty(BlockTripWireHook.ATTACHED, true);
		blockstateList[0][193] = Blocks.TRIPWIRE_HOOK.getDefaultState().withProperty(BlockTripWireHook.FACING, EnumFacing.SOUTH).withProperty(BlockTripWireHook.ATTACHED, true);
		blockstateList[0][194] = Blocks.TRIPWIRE_HOOK.getDefaultState().withProperty(BlockTripWireHook.FACING, EnumFacing.NORTH).withProperty(BlockTripWireHook.ATTACHED, true);
		blockstateList[0][195] = Blocks.TRIPWIRE_HOOK.getDefaultState().withProperty(BlockTripWireHook.FACING, EnumFacing.EAST).withProperty(BlockTripWireHook.ATTACHED, true);
		blockstateList[0][196] = Blocks.TRIPWIRE.getDefaultState().withProperty(BlockTripWire.ATTACHED, true);
		blockstateList[0][197] = Blocks.PISTON.getDefaultState()
				.withProperty(BlockPistonBase.EXTENDED, Boolean.valueOf(false))
				.withProperty(BlockPistonBase.FACING, EnumFacing.SOUTH);
		blockstateList[0][198] = Blocks.PISTON.getDefaultState()
				.withProperty(BlockPistonBase.EXTENDED, Boolean.valueOf(false))
				.withProperty(BlockPistonBase.FACING, EnumFacing.UP);
		blockstateList[0][199] = Blocks.PISTON.getDefaultState()
				.withProperty(BlockPistonBase.EXTENDED, Boolean.valueOf(false))
				.withProperty(BlockPistonBase.FACING, EnumFacing.EAST);
		blockstateList[0][200] = Blocks.STONE_PRESSURE_PLATE.getDefaultState();
		blockstateList[0][255] = Blocks.STONE.getDefaultState();

		for (int i = 1; i < blockstateList.length; i++)
			blockstateList[i] = Arrays.copyOf(blockstateList[0], 256);

		blockstateList[1][1] = LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT,
				BlockStone.EnumType.GRANITE_SMOOTH);
		blockstateList[1][2] = Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT,
				BlockQuartz.EnumType.CHISELED);
		blockstateList[1][18] = Blocks.OAK_FENCE.getDefaultState();
		blockstateList[1][120] = Blocks.OAK_FENCE.getDefaultState();

		blockstateList[2][1] = LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT,
				BlockStone.EnumType.DIORITE_SMOOTH);
		blockstateList[2][2] = LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT,
				BlockStone.EnumType.DIORITE);
		blockstateList[2][18] = Blocks.IRON_BARS.getDefaultState();
		blockstateList[2][120] = Blocks.DARK_OAK_FENCE.getDefaultState();
		stair = Blocks.RED_SANDSTONE_STAIRS.getBlockState().getBaseState().withProperty(BlockStairs.SHAPE,
				EnumShape.STRAIGHT);
		addStairs(blockstateList[2], stair);

		blockstateList[3][1] = Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR,
				EnumDyeColor.LIGHT_BLUE);
		blockstateList[3][2] = Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR,
				EnumDyeColor.WHITE);
		blockstateList[3][18] = Blocks.IRON_BARS.getDefaultState();
		blockstateList[3][120] = Blocks.DARK_OAK_FENCE.getDefaultState();

		blockstateList[4][1] = Blocks.STONEBRICK.getDefaultState();
		blockstateList[4][2] = LabyrinthBlocks.STONE.getDefaultState();
		blockstateList[4][18] = Blocks.IRON_BARS.getDefaultState();
		blockstateList[4][120] = Blocks.COBBLESTONE_WALL.getDefaultState();
		stair = Blocks.STONE_BRICK_STAIRS.getBlockState().getBaseState().withProperty(BlockStairs.SHAPE,
				EnumShape.STRAIGHT);
		addStairs(blockstateList[4], stair);

		blockstateList[5][1] = Blocks.COBBLESTONE.getDefaultState();
		blockstateList[5][2] = LabyrinthBlocks.STONE.getDefaultState();
		blockstateList[5][18] = Blocks.IRON_BARS.getDefaultState();
		blockstateList[5][120] = Blocks.COBBLESTONE_WALL.getDefaultState();
		stair = Blocks.STONE_STAIRS.getBlockState().getBaseState().withProperty(BlockStairs.SHAPE, EnumShape.STRAIGHT);
		addStairs(blockstateList[5], stair);

		blockstateList[6][1] = Blocks.NETHER_BRICK.getDefaultState();
		blockstateList[6][2] = LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT,
				BlockStone.EnumType.GRANITE_SMOOTH);
		blockstateList[6][18] = Blocks.IRON_BARS.getDefaultState();
		blockstateList[6][120] = Blocks.NETHER_BRICK_FENCE.getDefaultState();
		stair = Blocks.NETHER_BRICK_STAIRS.getBlockState().getBaseState().withProperty(BlockStairs.SHAPE,
				EnumShape.STRAIGHT);
		addStairs(blockstateList[6], stair);

		blockstateList[7][1] = Blocks.RED_NETHER_BRICK.getDefaultState();
		blockstateList[7][2] = Blocks.OBSIDIAN.getDefaultState();
		blockstateList[7][18] = Blocks.NETHER_BRICK_FENCE.getDefaultState();
		blockstateList[7][120] = Blocks.NETHER_BRICK_FENCE.getDefaultState();
		stair = Blocks.NETHER_BRICK_STAIRS.getBlockState().getBaseState().withProperty(BlockStairs.SHAPE,
				EnumShape.STRAIGHT);
		addStairs(blockstateList[7], stair);

		for (int i = 0; i < blockstateList.length; i = i == 0 ? 8 : ++i) {
			blockstateList[i][1] = WALL_CANDIDATES.get(random.nextInt(WALL_CANDIDATES.size()));
			blockstateList[i][2] = WALL_CANDIDATES.get(random.nextInt(WALL_CANDIDATES.size()));
			blockstateList[i][18] = WINDOW_CANDIDATES.get(random.nextInt(WINDOW_CANDIDATES.size()));
			blockstateList[i][120] = FENCE_CANDIDATES.get(random.nextInt(FENCE_CANDIDATES.size()));
			stair = STAIR_CANDIDATES.get(random.nextInt(STAIR_CANDIDATES.size()));
			addStairs(blockstateList[i], stair);
		}
		
        File folder = new File(".", "config");
        folder.mkdirs();
        File configFile = new File(folder, "labyrinth_level_features_config.json");
        try {
            if (!configFile.exists())
                this.writeConfigToJson(configFile);
            this.readConfigFromJson(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

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

	
	private void writeConfigToJson(File configFile) throws IOException {
		JsonWriter writer = new JsonWriter(new FileWriter(configFile));
		writer.setIndent(" ");
		writer.beginArray();
		for (int level = 0; level < 1; level++) {
			writer.beginObject();
			{
				writer.name("level");
				writer.value(level);
				writer.name("hint");
				writer.value("0 -air, 1 - walls, 2 - floor, 18 - windows, 120 - fence");
				writer.name("entries");
				writer.beginArray();
				for (int num_place = 0; num_place < 57; num_place++) {
					writer.beginObject();
					{
						writer.name("index_space");
						writer.value(num_place);
						IBlockState bstate = this.blockstateList[level][num_place];
						String blockRegistryName = Block.REGISTRY.getNameForObject(bstate.getBlock()).toString();
						writer.name(blockRegistryName);
						writer.beginObject();
						bstate.getProperties().forEach((p, v) -> {
							try {
								writer.name(p.getName());
								writer.value(getValueName(p, v));
							} catch (IOException e) {
								throw new UncheckedIOException("Input error while converting to Json a BlockState instance of block " + blockRegistryName
										+ " for config of flat cube type world.", e);
							}
						});
						writer.endObject();
					}
					writer.endObject();
				}
				writer.endArray();
			}
			writer.endObject();
		}
		writer.endArray();
		writer.close();
	}
    
    @SuppressWarnings({"rawtypes", "unchecked"})
	private void readConfigFromJson(File configFile) throws IOException {
        JsonReader reader = new JsonReader(new FileReader(configFile));
        reader.beginArray();
		while (reader.hasNext()) {
			reader.beginObject();
			{
				reader.nextName();
				int level = reader.nextInt();
				String name = reader.nextName();
				if(name.equals("hint")) {
					reader.skipValue();
					reader.nextName();
				}
				reader.beginArray();
				while (reader.hasNext()) {
					reader.beginObject();
					reader.nextName();
					int index_space = reader.nextInt();
					String blockRegistryName = reader.nextName();
			        Block block = Block.getBlockFromName(blockRegistryName);
					if (block == null) {
						this.blockstateList[level][index_space] = WALL_CANDIDATES.get(random.nextInt(WALL_CANDIDATES.size()));
					} else {
						IBlockState blockState = block.getBlockState().getBaseState();
						reader.beginObject();
						while (reader.hasNext()) {
							IProperty property = block.getBlockState().getProperty(reader.nextName());
							blockState = blockState.withProperty(property, findPropertyValueByName(property, reader.nextString()));
						}
						this.blockstateList[level][index_space] = blockState;
					}
					reader.endObject();
					reader.endObject();
				}
				reader.endArray();
			}
			reader.endObject();
		}
		reader.endArray();
		reader.close();
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
	private String getValueName(IProperty property, Comparable v) {
        return property.getName(v);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
	private Comparable findPropertyValueByName(IProperty property, String valueIn) {
        Optional<Comparable> value = property.parseValue(valueIn);
        if (value.isPresent()) {
            return value.get();
        } else {
            for (Object v : property.getAllowedValues()) {
                if (isValueEqualTo(property, (Comparable) v, valueIn)) {
                    return (Comparable) v;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
	private boolean isValueEqualTo(IProperty property, Comparable value, String valueIn) {
        return getValueName(property, value).equals(valueIn);
    }
}
